package com.ghostchu.tracker.sapling.event;

import com.ghostchu.tracker.sapling.dto.SiteEventDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.ArrayList;
import java.util.List;

public class UserViewPageEvent extends ApplicationEvent {
    @Getter
    private final List<SiteEventDTO> siteEventDTOList = new ArrayList<>();

    public UserViewPageEvent(Object source) {
        super(source);
    }
}
