package com.ghostchu.tracker.sapling.controller;

import com.dampcake.bencode.Bencode;
import com.ghostchu.tracker.sapling.entity.Peers;
import com.ghostchu.tracker.sapling.entity.Torrents;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.gvar.Setting;
import com.ghostchu.tracker.sapling.model.AnnounceRequest;
import com.ghostchu.tracker.sapling.service.IPeersService;
import com.ghostchu.tracker.sapling.service.ISettingsService;
import com.ghostchu.tracker.sapling.service.ITorrentsService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.tracker.PeerEvent;
import com.ghostchu.tracker.sapling.util.ExternalSwitch;
import com.ghostchu.tracker.sapling.util.ServletUtil;
import com.google.common.net.HostAndPort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@Slf4j
public class TrackerController {
    private static final Random random = new Random();
    @Autowired
    private HttpServletRequest req;
    @Autowired
    private ISettingsService settingsService;
    @Autowired
    private IPeersService peersService;
    @Autowired
    private IUsersService usersService;
    @Autowired
    private ITorrentsService torrentsService;
    @Autowired
    @Qualifier("bencodeUTF8")
    private Bencode bencodeUTF8;

    public static String compactPeers(List<Peers> peers, boolean isV6) throws IllegalStateException {
        ByteBuffer buffer = ByteBuffer.allocate((isV6 ? 18 : 6) * peers.size());
        for (Peers peer : peers) {
            buffer.put(peer.getIp().getAddress());
            int in = peer.getPort();
            buffer.put((byte) ((in >>> 8) & 0xFF));
            buffer.put((byte) (in & 0xFF));
        }
        return new String(buffer.array(), StandardCharsets.ISO_8859_1);
    }

    /**
     * 套他猴子的 BitTorrent 总给我整花活
     *
     * @param queryString 查询字符串
     * @return 使用 ISO_8859_1 进行 URL 解码的 Info Hash 集合
     */
    public static List<byte[]> extractInfoHashes(String queryString) {
        List<byte[]> infoHashes = new ArrayList<>(1);
        if (queryString == null)
            throw new IllegalArgumentException("No queryString provided");
        String[] params = queryString.split("&");
        for (String param : params) {
            if (param.startsWith("info_hash=")) {
                String encodedHash = param.substring(10);
                byte[] decodedHash = URLDecoder.decode(encodedHash, StandardCharsets.ISO_8859_1).getBytes(StandardCharsets.ISO_8859_1);
                infoHashes.add(decodedHash);
            }
        }

        return infoHashes;
    }


    /**
     * 套他猴子的 BitTorrent 总给我整花活
     *
     * @param queryString 查询字符串
     * @return 使用 ISO_8859_1 进行 URL 解码的 PeerID 集合
     */
    public static List<byte[]> extractPeerId(String queryString) {
        List<byte[]> infoHashes = new ArrayList<>(1);
        if (queryString == null)
            throw new IllegalArgumentException("No queryString provided");
        String[] params = queryString.split("&");
        for (String param : params) {
            if (param.startsWith("peer_id=")) {
                String encodedHash = param.substring(10);
                byte[] decodedHash = URLDecoder.decode(encodedHash, StandardCharsets.ISO_8859_1).getBytes(StandardCharsets.ISO_8859_1);
                infoHashes.add(decodedHash);
            }
        }

        return infoHashes;
    }

    @GetMapping("/announce")
    @ResponseBody
    public ResponseEntity<?> announce() throws URISyntaxException, UnknownHostException {
        if (Boolean.parseBoolean(settingsService.getValue(Setting.TRACKER_MAINTENANCE))) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(generateFailureResponse(settingsService.getValue(Setting.TRACKER_MAINTENANCE_MESSAGE), 86400));
        }
        if (req.getQueryString() == null) {
            return redirectTo(settingsService.getValue(Setting.SITE_URL));
        }
        String userAgent = ServletUtil.ua(req);
        if (userAgent != null) {
            if (userAgent.contains("Mozilla")) {
                return redirectTo(settingsService.getValue(Setting.SITE_URL));
            }
        }
        var passkey = req.getParameter("passkey");
        Validate.notBlank(passkey, "[非法请求] passkey 参数不可为空");
        Users users = usersService.getUserByPasskey(passkey);
        Validate.notNull(users, "[非法请求] passkey 不存在或者已失效");
        if (users.getBannedId() != null) {
            throw new IllegalStateException("[停权] passkey 对应的用户已被停权，请登录站点查看详情");
        }
        // 截取infohash
        var infoHashes = extractInfoHashes(req.getQueryString());
        Validate.isTrue(infoHashes.size() == 1, "[非法请求] 单个 announce 请求中最多只能包含 1 个 info_hash 参数");
        byte[] infoHash = infoHashes.getFirst();
        Validate.isTrue(infoHash.length == 20);
        Torrents torrents = torrentsService.getTorrentByInfoHash(infoHash);
        if (torrents == null || !torrents.isVisible() || torrents.isDeleted()) {
            // 有些客户端会识别这个固定字符串，所以不要改成别的
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body(generateFailureResponse("torrent not registered with this tracker", 86400));
        }
        // 截取peerid
        var peerIds = extractPeerId(req.getQueryString());
        Validate.isTrue(peerIds.size() == 1, "[非法请求] 单个 announce 请求中最多只能包含 1 个 peer_id 参数");
        byte[] peerId = peerIds.getFirst();

        Validate.isTrue(StringUtils.isNumeric(req.getParameter("port")), "[非法请求] port 参数必须为数字且不可为空");
        int port = Integer.parseInt(req.getParameter("port"));
        Validate.isTrue(StringUtils.isNumeric(req.getParameter("uploaded")), "[非法请求] uploaded 参数必须为数字且不可为空");
        long uploaded = Long.parseLong(req.getParameter("uploaded"));
        Validate.isTrue(StringUtils.isNumeric(req.getParameter("downloaded")), "[非法请求] downloaded 参数必须为数字且不可为空");
        long downloaded = Long.parseLong(req.getParameter("downloaded"));
        var leftBi = new BigInteger(req.getParameter("left") != null ? req.getParameter("left") : "-1");
        long left = leftBi.longValue();
        boolean compact = "1".equals(req.getParameter("compact")) || "1".equals(req.getParameter("no_peer_id"));
        PeerEvent peerEvent = PeerEvent.EMPTY;
        String event = req.getParameter("event");
        if (event != null) {
            peerEvent = PeerEvent.fromString(event);
        }
        int numWant = Integer.parseInt(Optional.ofNullable(req.getParameter("num_want")).orElse("100"));
        InetAddress reqIpInetAddress = InetAddress.getByName(req.getRemoteAddr());
        try {
            reqIpInetAddress = InetAddress.getByName(ServletUtil.ip(req));
        } catch (Exception ignored) {
        }
        List<InetAddress> peerIps = getPossiblePeerIps(req)
                .stream()
                .map(s -> {
                    try {
                        return InetAddress.getByName(s.getHost());
                    } catch (UnknownHostException e) {
                        //log.warn("Failed to parse peer IP: {}", s.getHost());
                        return null;
                    }
                })
                .distinct()
                .filter(Objects::nonNull)
                .filter(ip -> {
                    if (ExternalSwitch.parseBoolean("sapling.tracker.ignore-local-address", true)) {
                        return !ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.isAnyLocalAddress() && !ip.isMulticastAddress();
                    } else {
                        return true;
                    }
                })
                .toList();
        List<AnnounceRequest> requests = new ArrayList<>(8);
        for (InetAddress ip : peerIps) {
            requests.add(new AnnounceRequest(
                    torrents.getId(),
                    users.getId(),
                    peerId,
                    reqIpInetAddress,
                    ip,
                    port,
                    uploaded,
                    downloaded,
                    left,
                    peerEvent,
                    ServletUtil.ua(req)));
        }
        peersService.announce(requests);
        TrackedPeers peers;
        if (peerEvent != PeerEvent.STOPPED) {
            peers = new TrackedPeers(peersService.fetchPeers(users.getId(), torrents.getId(), Math.min(Integer.parseInt(settingsService.getValue(Setting.TRACKER_ANNOUNCE_MAX_RETURNS)), numWant), true, null).getRecords());
        } else {
            peers = new TrackedPeers(Collections.emptyList());
        }
        long intervalMillis = generateInterval();
        // 合成响应
        Map<String, Object> map = new HashMap<>(8);
        map.put("interval", intervalMillis / 1000);
        map.put("complete", peers.getSeeds());
        map.put("incomplete", peers.getLeeches());
        map.put("external ip", ServletUtil.ip(req));
        map.put("tracker id", settingsService.getValue(Setting.TRACKER_ID));

        if (compact) {
            map.put("peers", compactPeers(peers.getIpv4(), false));
            if (!peers.getIpv6().isEmpty())
                map.put("peers6", compactPeers(peers.getIpv6(), true));
        } else {
            List<Peers> allPeers = new ArrayList<>(peers.getIpv4());
            allPeers.addAll(peers.getIpv6());
            map.put("peers", new HashMap<>() {{
                for (Peers p : allPeers) {
                    put("peer id", new String(p.getPeerId(), StandardCharsets.ISO_8859_1));
                    put("ip", p.getIp());
                    put("port", p.getPort());
                }
            }});
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body(bencodeUTF8.encode(map));
    }

    private byte[] generateFailureResponse(String reason, long retryAfterSeconds) {
        var map = new HashMap<>(2);
        map.put("failure reason", reason);
        map.put("retry in", retryAfterSeconds == -1 ? "never" : retryAfterSeconds);
        return bencodeUTF8.encode(map);
    }

    private long generateInterval() {
        var offset = random.nextLong(Long.parseLong(settingsService.getValue(Setting.TRACKER_ANNOUNCE_INTERVAL_RANDOM_OFFSET)));
        if (random.nextBoolean()) {
            offset = -offset;
        }
        return Long.parseLong(settingsService.getValue(Setting.TRACKER_ANNOUNCE_INTERVAL)) + offset;
    }

    public ResponseEntity<?> redirectTo(String url) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(url));
        return new ResponseEntity<>(null, headers, HttpStatus.TEMPORARY_REDIRECT);
    }

    @GetMapping("/scrape")
    @ResponseBody
    public ResponseEntity<?> scrape() throws URISyntaxException {
        if (Boolean.parseBoolean(settingsService.getValue(Setting.TRACKER_MAINTENANCE))) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(generateFailureResponse(settingsService.getValue(Setting.TRACKER_MAINTENANCE_MESSAGE), 86400));
        }
        if (req.getQueryString() == null) {
            return redirectTo(settingsService.getValue(Setting.SITE_URL));
        }
        String userAgent = ServletUtil.ua(req);
        if (userAgent != null) {
            if (userAgent.contains("Mozilla")) {
                return redirectTo(settingsService.getValue(Setting.SITE_URL));
            }
        }
        var passkey = req.getParameter("passkey");
        Validate.notBlank(passkey, "[非法请求] passkey 参数不可为空");
        Users users = usersService.getUserByPasskey(passkey);
        Validate.notNull(users, "[非法请求] passkey 不存在或者已失效");
        if (users.getBannedId() != null) {
            throw new IllegalStateException("[停权] passkey 对应的用户已被停权，请登录站点查看详情");
        }
        var infoHashes = extractInfoHashes(req.getQueryString());
        var map = new HashMap<>();
        var files = new HashMap<>();
        for (byte[] infoHash : infoHashes) {
            Torrents torrents = torrentsService.getTorrentByInfoHash(infoHash);
            if (torrents != null && torrents.isVisible() && !torrents.isDeleted()) {
                files.put(new String(infoHash, StandardCharsets.ISO_8859_1), new HashMap<>() {{
                    var peers = peersService.scrape(torrents.getId());
                    put("complete", peers.seeders());
                    put("incomplete", peers.leeches());
                }});
            }
        }
        map.put("files", files);
        map.put("external ip", ServletUtil.ip(req));
        //auditService.log(req, "TRACKER_SCRAPE", true, Map.of("hash", infoHashes, "user-agent", ua(req)));
        return ResponseEntity.ok(bencodeUTF8.encode(map));
    }

    public List<HostAndPort> getPossiblePeerIps(HttpServletRequest req) {
        List<String> found = new ArrayList<>(12);
        found.add(ServletUtil.ip(req));
        var ips = req.getParameterValues("ip");
        if (ips != null) {
            found.addAll(List.of(ips));
        }
        var ipv4 = req.getParameterValues("ipv4");
        if (ipv4 != null) {
            found.addAll(List.of(ipv4));
        }
        var ipv6 = req.getParameterValues("ipv6");
        if (req.getParameter("ipv6") != null) {
            found.addAll(List.of(ipv6));
        }
        List<HostAndPort> hap = new ArrayList<>(found.size());
        for (String s : found) {
            try {
                hap.add(HostAndPort.fromString(s));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return hap;
    }

    @Getter
    static class TrackedPeers {
        private final List<Peers> ipv4 = new ArrayList<>();
        private final List<Peers> ipv6 = new ArrayList<>();
        private int seeds;
        private int leeches;

        public TrackedPeers(List<Peers> peers) {
            for (Peers peer : peers) {
                if (peer.getIp() instanceof Inet4Address) {
                    ipv4.add(peer);
                } else if (peer.getIp() instanceof Inet6Address) {
                    ipv6.add(peer);
                }
                if (peer.getToGo() == 0) {
                    seeds++;
                } else {
                    leeches++;
                }
            }
        }
    }
}
