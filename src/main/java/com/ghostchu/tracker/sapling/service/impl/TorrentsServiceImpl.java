package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.dto.TorrentDTO;
import com.ghostchu.tracker.sapling.entity.Bitbucket;
import com.ghostchu.tracker.sapling.entity.Torrents;
import com.ghostchu.tracker.sapling.mapper.TorrentsMapper;
import com.ghostchu.tracker.sapling.service.IBitbucketService;
import com.ghostchu.tracker.sapling.service.ICategoriesService;
import com.ghostchu.tracker.sapling.service.ITorrentsService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.util.TorrentParser;
import com.github.yulichang.base.MPJBaseServiceImpl;
import lombok.Cleanup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HexFormat;

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

    @Override
    public IPage<Torrents> getTorrentsByPage(long page, int size, boolean includeInvisible, boolean includeDeleted) {
        IPage<Torrents> iPage = new Page<>(page, size);
        return baseMapper.selectPage(iPage, new QueryWrapper<Torrents>()
                .eq(!includeInvisible, "visible", true)
                .isNull(!includeDeleted, "deleted_at"));

    }

    @Override
    public TorrentDTO toDTO(Torrents torrent) {
        TorrentDTO torrentDTO = new TorrentDTO();
        torrentDTO.setId(torrent.getId());
        torrentDTO.setOwner(torrent.getOwner());
        var user = usersService.getUserById(torrent.getOwner());

        torrentDTO.setOwnerName(user != null ? user.getName() : "(deleted)");
        torrentDTO.setHashV1(torrent.getHashV1() != null ? HexFormat.of().formatHex(torrent.getHashV1()) : null);
        torrentDTO.setHashV2(torrent.getHashV2() != null ? HexFormat.of().formatHex(torrent.getHashV2()) : null);
        torrentDTO.setTitle(torrent.getTitle());
        torrentDTO.setSubtitle(torrent.getSubtitle());
        torrentDTO.setDescription(torrent.getDescription());
        torrentDTO.setSize(torrent.getSize());
        var category = categoriesService.getCategoryById(torrent.getCategory());
        torrentDTO.setCategory(torrent.getCategory());
        torrentDTO.setCategoryName(category != null ? category.getName() : "(category deleted)");
        torrentDTO.setNumFiles(torrent.getNumFiles());
        torrentDTO.setCreatedAt(torrent.getCreatedAt());
        return torrentDTO;
    }

    @Override
    public Torrents createTorrent(Long owner, MultipartFile file, Long categoryId, String title, String subtitle, String description, boolean anonymous) throws IOException {
        Bitbucket uploadedFile = bitbucketService.uploadToBitbucket(file, owner, false);
        @Cleanup
        var inputStream = bitbucketService.readBitBucket(uploadedFile.getId());
        var torrentInfo = TorrentParser.parse(inputStream.readAllBytes());
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
        torrents.setVisible(true); // 默认不审核
        this.save(torrents);
        return torrents;
    }


}
