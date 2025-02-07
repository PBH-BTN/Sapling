package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;
import com.ghostchu.tracker.sapling.entity.Bitbucket;
import com.ghostchu.tracker.sapling.entity.Torrents;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.gvar.Setting;
import com.ghostchu.tracker.sapling.mapper.TorrentsMapper;
import com.ghostchu.tracker.sapling.service.*;
import com.ghostchu.tracker.sapling.util.TorrentParser;
import com.ghostchu.tracker.sapling.vo.TorrentDetailsVO;
import com.ghostchu.tracker.sapling.vo.TorrentVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import lombok.Cleanup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class TorrentsServiceImpl extends MPJBaseServiceImpl<TorrentsMapper, Torrents> implements ITorrentsService {
    @Autowired
    private IBitbucketService bitbucketService;
    @Autowired
    private IUsersService usersService;
    @Autowired
    private ICategoriesService categoriesService;
    @Autowired
    private ISettingsService settingsService;

    @Override
    @Cacheable(value = "torrents", key = "'id:' + #id")
    public Torrents getTorrentById(Long id) {
        return getOne(new QueryWrapper<Torrents>()
                .eq("id", id));
    }

    @Override
    public IPage<Torrents> getTorrentsByPage(long page, int size, boolean includeInvisible, boolean includeDeleted) {
        IPage<Torrents> iPage = new Page<>(page, size);
        return baseMapper.selectPage(iPage, new QueryWrapper<Torrents>()
                .orderBy(true, false, "created_at"));
    }

    @Override
    public TorrentVO toVO(Torrents torrent) {
        TorrentVO vo = new TorrentVO();
        vo.setId(torrent.getId());
        vo.setOwner(usersService.toVO(usersService.getUserById(torrent.getOwner())));
        vo.setTitle(torrent.getTitle());
        vo.setSubtitle(torrent.getSubtitle());
        vo.setSize(torrent.getSize());
        vo.setCategory(categoriesService.toVO(categoriesService.getCategoryById(torrent.getCategory())));
        vo.setNumFiles(torrent.getNumFiles());
        vo.setCreatedAt(torrent.getCreatedAt());
        vo.setAnonymous(torrent.isAnonymous());
        vo.setVisible(torrent.isVisible());
        vo.setDeleted(torrent.getDeletedAt() != null);
        return vo;
    }

    @Override
    public TorrentDetailsVO toDetailsVO(Torrents torrent) {
        TorrentDetailsVO vo = new TorrentDetailsVO();
        vo.setId(torrent.getId());
        vo.setOwner(usersService.toVO(usersService.getUserById(torrent.getOwner())));
        vo.setTitle(torrent.getTitle());
        vo.setSubtitle(torrent.getSubtitle());
        vo.setDescription(torrent.getDescription());
        vo.setSize(torrent.getSize());
        vo.setCategory(categoriesService.toVO(categoriesService.getCategoryById(torrent.getCategory())));
        vo.setNumFiles(torrent.getNumFiles());
        vo.setCreatedAt(torrent.getCreatedAt());
        vo.setAnonymous(torrent.isAnonymous());
        vo.setInfo(torrent.getInfo());
        vo.setVisible(torrent.isVisible());
        vo.setDeleted(torrent.getDeletedAt() != null);
        return vo;
    }

    @Override
    public boolean isTorrentExists(byte[] infoHash) {
        return baseMapper.selectCount(new QueryWrapper<Torrents>().eq("hash_v1", infoHash).or().eq("hash_v2", infoHash)) > 0;
    }

    @Override
    public byte[] downloadTorrentForUser(Torrents torrents, Users user) throws IOException {
        @Cleanup
        var in = bitbucketService.readBitBucket(torrents.getFile());
        byte[] content = in.readAllBytes();
        Bencode bencode = new Bencode(StandardCharsets.ISO_8859_1);
        Map<String, Object> decoded = bencode.decode(content, Type.DICTIONARY);
        decoded.remove("announce");
        decoded.remove("announce-list");
        decoded.put("announce", settingsService.getValue(Setting.TRACKER_ANNOUNCE_URL) + "?passkey=" + user.getPasskey());
        return bencode.encode(decoded);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "torrents", key = "'id:' + #id"),
            @CacheEvict(value = "torrents", key = "'infoHash:' + #result.hashV1"),
            @CacheEvict(value = "torrents", key = "'infoHash:' + #result.hashV2"),
    })
    public Torrents createTorrent(Long owner, MultipartFile file, Long categoryId, String title, String subtitle, String description, boolean anonymous, boolean visible) throws IOException {
        byte[] content = file.getInputStream().readAllBytes();
        /* 使种子私有化，去除缓存信息 */
        Bencode bencode = new Bencode(StandardCharsets.ISO_8859_1);
        Map<String, Object> decoded = bencode.decode(content, Type.DICTIONARY);
        decoded.remove("nodes");
        Map<String, Object> infoMap = (Map<String, Object>) decoded.get("info");
        infoMap.put("private", 1);
        infoMap.put("source", "[" + settingsService.getValue(Setting.SITE_NAME) + "]" + settingsService.getValue(Setting.SITE_URL));
        byte[] processed = bencode.encode(decoded);
        /* 数据库检查是否有重复种子 */
        var torrentInfo = TorrentParser.parse(processed);
        if (isTorrentExists(torrentInfo.infoHashV1Bytes()) || isTorrentExists(torrentInfo.infoHashV2Bytes())) {
            throw new IllegalArgumentException("要上传的种子已经存在");
        }
        Bitbucket uploadedFile = bitbucketService.uploadToBitbucket(processed, file.getOriginalFilename(), owner, "application/x-bittorrent", false, true);
        Torrents torrents = new Torrents();
        torrents.setOwner(owner);
        torrents.setHashV1(torrentInfo.infoHashV1Bytes());
        torrents.setHashV2(torrentInfo.infoHashV2Bytes());
        if (torrentInfo.infoHashV2Bytes() != null) {
            byte[] truncatedBytes = new byte[20];
            System.arraycopy(torrentInfo.infoHashV2Bytes(), 0, truncatedBytes, 0, 20);
            torrents.setHashV2Short(truncatedBytes);
        }
        torrents.setAnonymous(anonymous);
        torrents.setFile(uploadedFile.getId());
        torrents.setTitle(title);
        torrents.setSubtitle(subtitle);
        torrents.setDescription(description);
        torrents.setSize(torrentInfo.totalSize());
        torrents.setCategory(categoryId);
        torrents.setNumFiles(torrentInfo.files());
        torrents.setCreatedAt(OffsetDateTime.now());
        torrents.setVisible(visible); // 默认不审核
        torrents.setInfo(torrentInfo);
        this.save(torrents);
        return torrents;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "torrents", key = "'id:' + #id"),
            @CacheEvict(value = "torrents", key = "'infoHash:' + #result.hashV1"),
            @CacheEvict(value = "torrents", key = "'infoHash:' + #result.hashV2"),
    })
    public Torrents updateTorrent(Long id, Long userId, Long categoryId, String title, String subtitle, String description) {
        if (categoriesService.getCategoryById(categoryId) == null) {
            throw new IllegalArgumentException("指定的分类不存在");
        }
        Torrents torrents = baseMapper.selectById(id);
        if (torrents.getOwner() != userId) {
            throw new IllegalArgumentException("只有种子的所有者才能编辑种子");
        }
        torrents.setCategory(categoryId);
        torrents.setTitle(title);
        torrents.setSubtitle(subtitle);
        torrents.setDescription(description);
        baseMapper.updateById(torrents);
        return torrents;
    }

    @Override
    @Cacheable(value = "torrents", key = "'infoHash:' + #infoHash")
    public Torrents getTorrentByInfoHash(byte[] infoHash) {
        return getOne(new QueryWrapper<Torrents>()
                .eq("hash_v1", infoHash)
                .or()
                .eq("hash_v2_short", infoHash));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "torrents", key = "'id:' + #id"),
            @CacheEvict(value = "torrents", key = "'infoHash:' + #result.hashV1"),
            @CacheEvict(value = "torrents", key = "'infoHash:' + #result.hashV2"),
    })
    public Torrents deleteTorrent(long id, long deleteBy) {
        Torrents torrents = baseMapper.selectById(id);
        torrents.setDeletedAt(OffsetDateTime.now());
        torrents.setDeletedBy(deleteBy);
        baseMapper.updateById(torrents);
        return torrents;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "torrents", key = "'id:' + #id"),
            @CacheEvict(value = "torrents", key = "'infoHash:' + #result.hashV1"),
            @CacheEvict(value = "torrents", key = "'infoHash:' + #result.hashV2"),
    })
    public Torrents unDeleteTorrent(long id) {
        Torrents torrents = baseMapper.selectById(id);
        torrents.setDeletedAt(null);
        baseMapper.updateById(torrents);
        return torrents;
    }
}
