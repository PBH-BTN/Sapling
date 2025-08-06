package com.ghostchu.tracker.sapling.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
public class WidgetsFormDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String icon;
    private String color;
    private boolean buyable;
    private long buyCurrency;
    private BigDecimal buyBalance;
    private long stock;
    private String restockCronExpression;
    private OffsetDateTime lastRestock;
    private boolean managed;
}
