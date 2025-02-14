package com.ghostchu.tracker.sapling.controller.admin.tracker;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ghostchu.tracker.sapling.dto.TrackerSettingDTO;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.gvar.Setting;
import com.ghostchu.tracker.sapling.service.ISettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/tracker/settings")
@SaCheckPermission(Permission.ADMIN_TRACKER_SETTINGS)
@SaCheckDisable
public class AdminTrackerSettingsController {
    @Autowired
    private ISettingsService settingsService;

    @GetMapping
    public String showSettings(Model model) {
        TrackerSettingDTO trackerSettings = new TrackerSettingDTO();
        trackerSettings.setAnnounceUrl(settingsService.getValue(Setting.TRACKER_ANNOUNCE_URL).orElse(""));
        trackerSettings.setAnnounceIntervalMillis(Long.parseLong(settingsService.getValue(Setting.TRACKER_ANNOUNCE_INTERVAL).orElse("0")));
        trackerSettings.setAnnounceIntervalRandomOffsetMillis(Long.parseLong(settingsService.getValue(Setting.TRACKER_ANNOUNCE_INTERVAL_RANDOM_OFFSET).orElse("0")));
        trackerSettings.setAnnounceMaxReturns(Integer.parseInt(settingsService.getValue(Setting.TRACKER_ANNOUNCE_MAX_RETURNS).orElse("0")));
        trackerSettings.setTrackerId(settingsService.getValue(Setting.TRACKER_ID).orElse(""));
        trackerSettings.setMaintenance(Boolean.parseBoolean(settingsService.getValue(Setting.TRACKER_MAINTENANCE).orElse("false")));
        trackerSettings.setMaintenanceMessage(settingsService.getValue(Setting.TRACKER_MAINTENANCE_MESSAGE).orElse(""));
        model.addAttribute("trackerSettings", trackerSettings);
        return "admin/tracker/settings";
    }

    @PostMapping
    @Transactional
    public String saveSettings(TrackerSettingDTO trackerSettings) {
        settingsService.setValue(Setting.TRACKER_ANNOUNCE_URL, trackerSettings.getAnnounceUrl());
        settingsService.setValue(Setting.TRACKER_ANNOUNCE_INTERVAL, String.valueOf(trackerSettings.getAnnounceIntervalMillis()));
        settingsService.setValue(Setting.TRACKER_ANNOUNCE_INTERVAL_RANDOM_OFFSET, String.valueOf(trackerSettings.getAnnounceIntervalRandomOffsetMillis()));
        settingsService.setValue(Setting.TRACKER_ANNOUNCE_MAX_RETURNS, String.valueOf(trackerSettings.getAnnounceMaxReturns()));
        settingsService.setValue(Setting.TRACKER_ID, trackerSettings.getTrackerId());
        settingsService.setValue(Setting.TRACKER_MAINTENANCE, String.valueOf(trackerSettings.isMaintenance()));
        settingsService.setValue(Setting.TRACKER_MAINTENANCE_MESSAGE, trackerSettings.getMaintenanceMessage());
        return "redirect:/admin/tracker/settings";
    }
}
