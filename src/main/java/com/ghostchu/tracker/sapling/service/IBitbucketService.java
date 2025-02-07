package com.ghostchu.tracker.sapling.service;

import com.ghostchu.tracker.sapling.entity.Bitbucket;
import com.github.yulichang.base.MPJBaseService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface IBitbucketService extends MPJBaseService<Bitbucket> {
    Bitbucket uploadToBitbucket(MultipartFile file, Long owner, String mime, boolean directAccess, boolean managed) throws IOException;

    Bitbucket uploadToBitbucket(byte[] bytes, String fileName, Long owner, String mime, boolean directAccess, boolean managed) throws IOException;

    Bitbucket uploadToBitbucket(File file, Long owner, String mime, boolean directAccess, boolean managed) throws IOException;

    InputStream readBitBucket(Long bitbucketId) throws IOException;

    InputStream readBitBucket(Bitbucket bitbucket) throws IOException;

    Bitbucket getBitBucket(Long bitbucketId);
}
