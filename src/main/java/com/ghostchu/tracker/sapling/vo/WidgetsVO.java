package com.ghostchu.tracker.sapling.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
public class WidgetsVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String icon;
    private String color;
    private boolean buyable;
    private CurrencyVO buyCurrency;
    private BigDecimal buyBalance;
    private long stock;
    private String restockCronExpression;
    private OffsetDateTime lastRestock;
    private boolean managed;
}
