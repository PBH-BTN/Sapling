package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ghostchu.tracker.sapling.entity.Settings;
import com.ghostchu.tracker.sapling.mapper.SettingsMapper;
import com.ghostchu.tracker.sapling.service.ISettingsService;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class SettingsServiceImpl extends MPJBaseServiceImpl<SettingsMapper, Settings> implements ISettingsService {

    @Override
    @Cacheable(value = "settings", key = "#key")
    public Settings getSetting(String key) {
        return getOne(new QueryWrapper<Settings>().eq("name", key));
    }

    @Override
    @Cacheable(value = "settings", key = "'key:'+#key+';defvalue:'+#defaultValue")
    public String getValue(String key, String defaultValue) {
        Settings value = getSetting(key);
        return value == null ? defaultValue : value.getValue();
    }

    @Override
    @Cacheable(value = "settings", key = "#key")
    public String getValue(String key) {
        return getValue(key, null);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "settings", key = "#key")
            }
    )
    public boolean setValue(String key, String value) {
        Settings setting = getOne(new QueryWrapper<Settings>().eq("name", key));
        if (setting == null) {
            setting = new Settings();
            setting.setName(key);
            setting.setValue(value);
            return save(setting);
        } else {
            setting.setValue(value);
            return updateById(setting);
        }
    }

    @Override
    public boolean removeValue(String key) {
        return remove(new QueryWrapper<Settings>().eq("name", key));
    }


}
