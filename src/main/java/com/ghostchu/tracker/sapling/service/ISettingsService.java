package com.ghostchu.tracker.sapling.service;

import com.ghostchu.tracker.sapling.entity.Settings;
import com.github.yulichang.base.MPJBaseService;

import java.util.Optional;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface ISettingsService extends MPJBaseService<Settings> {

    Settings getSetting(String key);

    Optional<String> getValue(String key);

    boolean setValue(String key, String value);

    boolean removeValue(String key);
}
