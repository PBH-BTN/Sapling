package com.ghostchu.tracker.sapling.service.impl;

import com.ghostchu.tracker.sapling.entity.Bitbucket;
import com.ghostchu.tracker.sapling.gvar.Setting;
import com.ghostchu.tracker.sapling.mapper.BitbucketMapper;
import com.ghostchu.tracker.sapling.service.IBitbucketService;
import com.ghostchu.tracker.sapling.service.ISettingsService;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class BitbucketServiceImpl extends MPJBaseServiceImpl<BitbucketMapper, Bitbucket> implements IBitbucketService {
    @Autowired
    private ISettingsService settingsService;

    private String getBitbucketPath() {
        return settingsService.getValue(Setting.BITBUCKET_PATH);
    }

    @Override
    @Cacheable(value = "bitbucket", key = "#bitbucketId")
    public Bitbucket getBitBucket(Long bitbucketId) {
        return this.getById(bitbucketId);
    }

    @Override
    public Bitbucket uploadToBitbucket(MultipartFile file, Long owner, String mime, boolean directAccess) throws IOException {
        String filePath = UUID.randomUUID().toString();
        File dest = new File(getBitbucketPath(), filePath);
        file.transferTo(dest);
        Bitbucket bitbucket = new Bitbucket();
        bitbucket.setDisplayName(file.getOriginalFilename());
        bitbucket.setHandler("local");
        bitbucket.setFilePath(filePath);
        bitbucket.setOwner(owner);
        bitbucket.setCreatedAt(OffsetDateTime.now());
        bitbucket.setDirectAccess(directAccess);
        bitbucket.setFileSize(file.getSize());
        bitbucket.setMime(mime);
        this.save(bitbucket);
        return bitbucket;
    }

    @Override
    public Bitbucket uploadToBitbucket(byte[] bytes, String fileName, Long owner, String mime, boolean directAccess) throws IOException {
        String filePath = UUID.randomUUID().toString();
        File dest = new File(getBitbucketPath(), filePath);
        Files.write(dest.toPath(), bytes);
        Bitbucket bitbucket = new Bitbucket();
        bitbucket.setDisplayName(fileName);
        bitbucket.setHandler("local");
        bitbucket.setFilePath(filePath);
        bitbucket.setOwner(owner);
        bitbucket.setCreatedAt(OffsetDateTime.now());
        bitbucket.setDirectAccess(directAccess);
        bitbucket.setFileSize(bytes.length);
        bitbucket.setMime(mime);
        this.save(bitbucket);
        return bitbucket;
    }

    @Override
    public Bitbucket uploadToBitbucket(File file, Long owner, String mime, boolean directAccess) throws IOException {
        String filePath = UUID.randomUUID().toString();
        File dest = new File(getBitbucketPath(), filePath);
        Files.copy(file.toPath(), dest.toPath());
        Bitbucket bitbucket = new Bitbucket();
        bitbucket.setDisplayName(file.getName());
        bitbucket.setHandler("local");
        bitbucket.setFilePath(filePath);
        bitbucket.setOwner(owner);
        bitbucket.setCreatedAt(OffsetDateTime.now());
        bitbucket.setDirectAccess(directAccess);
        bitbucket.setFileSize(file.length());
        bitbucket.setMime(mime);
        this.save(bitbucket);
        return bitbucket;
    }

    @Override
    @Deprecated() // 不推荐使用，因为无法缓存
    public InputStream readBitBucket(Long bitbucketId) throws IOException {
        Bitbucket bitbucket = this.getById(bitbucketId);
        if (bitbucket == null) {
            throw new IOException("Given BitBucket not exists.");
        }
        String path = bitbucket.getFilePath();
        File fsFile = new File(getBitbucketPath(), path);
        if (!fsFile.exists()) {
            throw new IOException("Given BitBucket exists but the file is missing.");
        }
        return new FileInputStream(fsFile);
    }

    @Override
    public InputStream readBitBucket(Bitbucket bitbucket) throws IOException {
        if (bitbucket == null) {
            throw new IOException("Given BitBucket not exists.");
        }
        String path = bitbucket.getFilePath();
        File fsFile = new File(getBitbucketPath(), path);
        if (!fsFile.exists()) {
            throw new IOException("Given BitBucket exists but the file is missing.");
        }
        return new FileInputStream(fsFile);
    }
}
