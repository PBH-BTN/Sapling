package com.ghostchu.tracker.sapling.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
public class InvitesVO {
    private Long id;

    private UserVO inviteBy;

    private String inviteEmail;

    private String inviteUsername;

    private OffsetDateTime createdAt;

    private OffsetDateTime expireAt;

    private UserVO invitedUser;

    private OffsetDateTime consumeAt;

    private String inviteCode;

    public boolean isInviteValid() {
        return expireAt.isAfter(OffsetDateTime.now()) && invitedUser == null;
    }
}
