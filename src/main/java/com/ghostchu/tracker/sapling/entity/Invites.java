package com.ghostchu.tracker.sapling.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
public class Invites {

    @TableId(type = IdType.AUTO)
    private Long id;

    private long inviteBy;

    private String inviteEmail;

    private String inviteUsername;

    private OffsetDateTime createdAt;

    private OffsetDateTime expireAt;

    private Long invitedUser;

    private OffsetDateTime consumeAt;

    private String inviteCode;

    public boolean isInviteValid() {
        return expireAt.isAfter(OffsetDateTime.now()) && invitedUser == null;
    }
}
