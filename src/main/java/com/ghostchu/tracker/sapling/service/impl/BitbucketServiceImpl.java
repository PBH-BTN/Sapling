package com.ghostchu.tracker.sapling.service.impl;

import com.ghostchu.tracker.sapling.entity.Bitbucket;
import com.ghostchu.tracker.sapling.mapper.BitbucketMapper;
import com.ghostchu.tracker.sapling.service.IBitbucketService;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${sapling.bitbucket.path}")
    private String bitbucketPath;

    @Override
    public Bitbucket uploadToBitbucket(MultipartFile file, Long owner, boolean directAccess) throws IOException {
        String filePath = UUID.randomUUID().toString();
        File dest = new File(bitbucketPath, filePath);
        file.transferTo(dest);
        Bitbucket bitbucket = new Bitbucket();
        bitbucket.setDisplayName(file.getOriginalFilename());
        bitbucket.setHandler("local");
        bitbucket.setFilePath(filePath);
        bitbucket.setOwner(owner);
        bitbucket.setCreatedAt(OffsetDateTime.now());
        bitbucket.setDirectAccess(directAccess);
        bitbucket.setFileSize(file.getSize());
        this.save(bitbucket);
        return bitbucket;
    }

    @Override
    public Bitbucket uploadToBitbucket(byte[] bytes, String fileName, Long owner, boolean directAccess) throws IOException {
        String filePath = UUID.randomUUID().toString();
        File dest = new File(bitbucketPath, filePath);
        Files.write(dest.toPath(), bytes);
        Bitbucket bitbucket = new Bitbucket();
        bitbucket.setDisplayName(fileName);
        bitbucket.setHandler("local");
        bitbucket.setFilePath(filePath);
        bitbucket.setOwner(owner);
        bitbucket.setCreatedAt(OffsetDateTime.now());
        bitbucket.setDirectAccess(directAccess);
        bitbucket.setFileSize((long) bytes.length);
        this.save(bitbucket);
        return bitbucket;
    }

    @Override
    public Bitbucket uploadToBitbucket(File file, Long owner, boolean directAccess) throws IOException {
        String filePath = UUID.randomUUID().toString();
        File dest = new File(bitbucketPath, filePath);
        Files.copy(file.toPath(), dest.toPath());
        Bitbucket bitbucket = new Bitbucket();
        bitbucket.setDisplayName(file.getName());
        bitbucket.setHandler("local");
        bitbucket.setFilePath(filePath);
        bitbucket.setOwner(owner);
        bitbucket.setCreatedAt(OffsetDateTime.now());
        bitbucket.setDirectAccess(directAccess);
        bitbucket.setFileSize(file.length());
        this.save(bitbucket);
        return bitbucket;
    }

    @Override
    public InputStream readBitBucket(Long bitbucketId) throws IOException {
        Bitbucket bitbucket = this.getById(bitbucketId);
        if (bitbucket == null) {
            throw new IOException("Given BitBucket not exists.");
        }
        String path = bitbucket.getFilePath();
        File fsFile = new File(bitbucketPath, path);
        if (!fsFile.exists()) {
            throw new IOException("Given BitBucket exists but the file is missing.");
        }
        return new FileInputStream(fsFile);
    }
}
