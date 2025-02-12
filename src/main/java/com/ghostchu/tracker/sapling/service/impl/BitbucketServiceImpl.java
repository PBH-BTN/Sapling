package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.entity.Bitbucket;
import com.ghostchu.tracker.sapling.gvar.Setting;
import com.ghostchu.tracker.sapling.mapper.BitbucketMapper;
import com.ghostchu.tracker.sapling.service.IBitbucketService;
import com.ghostchu.tracker.sapling.service.ISettingsService;
import com.ghostchu.tracker.sapling.vo.BitbucketVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.apache.commons.lang3.Validate;
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
        return settingsService.getValue(Setting.BITBUCKET_PATH).orElseThrow();
    }

    @Override
    @Cacheable(value = "bitbucket", key = "#bitbucketId")
    public Bitbucket getBitBucket(Long bitbucketId) {
        QueryWrapper<Bitbucket> queryWrapper = new QueryWrapper<>();
        queryWrapper = queryWrapper.eq("id", bitbucketId).isNull("deleted_at");
        return getOne(queryWrapper);
    }

    @Override
    public IPage<Bitbucket> listUserFiles(long loginIdAsLong, Page<Bitbucket> pageQuery) {
        QueryWrapper<Bitbucket> queryWrapper = new QueryWrapper<>();
        queryWrapper = queryWrapper
                .eq("owner", loginIdAsLong)
                .eq("managed", false)
                .isNull("deleted_at")
                .orderByDesc("id");
        return page(pageQuery, queryWrapper);
    }

    @Override
    public BitbucketVO toVO(Bitbucket bitbucket) {
        BitbucketVO vo = new BitbucketVO();
        vo.setId(bitbucket.getId());
        vo.setDisplayName(bitbucket.getDisplayName());
        vo.setFilePath(bitbucket.getFilePath());
        vo.setHandler(bitbucket.getHandler());
        vo.setCreatedAt(bitbucket.getCreatedAt());
        vo.setLastAccessAt(bitbucket.getLastAccessAt());
        vo.setDirectAccess(bitbucket.isDirectAccess());
        vo.setFileSize(bitbucket.getFileSize());
        vo.setMime(bitbucket.getMime());
        return vo;

    }

    @Override
    public void deleteById(Long id) {
        Bitbucket bitbucket = getById(id);
        bitbucket.setDeletedAt(OffsetDateTime.now());
        updateById(bitbucket);
    }

    @Override
    public Bitbucket uploadToBitbucket(MultipartFile file, Long owner, String mime, boolean directAccess, boolean managed) throws IOException {
        Validate.notBlank(mime, "BitBucket 的 MIME 参数不能为空");
        Validate.notNull(file, "BitBucket 的 MultipartFile 不能为空");
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
        bitbucket.setManaged(managed);
        this.save(bitbucket);
        return bitbucket;
    }

    @Override
    public Bitbucket uploadToBitbucket(byte[] bytes, String fileName, Long owner, String mime, boolean directAccess, boolean managed) throws IOException {
        Validate.notBlank(mime, "BitBucket 的 MIME 参数不能为空");
        Validate.notNull(bytes, "BitBucket 的 bytes 不能为空");
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
        bitbucket.setManaged(managed);
        this.save(bitbucket);
        return bitbucket;
    }

    @Override
    public Bitbucket uploadToBitbucket(File file, Long owner, String mime, boolean directAccess, boolean managed) throws IOException {
        Validate.notBlank(mime, "BitBucket 的 MIME 参数不能为空");
        Validate.notNull(file, "BitBucket 的 File 不能为空");
        Validate.isTrue(file.exists(), "BitBucket 的 File 的文件不存在");
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
        bitbucket.setManaged(managed);
        this.save(bitbucket);
        return bitbucket;
    }

    @Override
    @Deprecated() // 不推荐使用，因为无法缓存
    public InputStream readBitBucket(Long bitbucketId) throws IOException {
        QueryWrapper<Bitbucket> queryWrapper = new QueryWrapper<>();
        queryWrapper = queryWrapper.eq("id", bitbucketId).isNull("deleted_at");
        Bitbucket bitbucket = getOne(queryWrapper);
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
