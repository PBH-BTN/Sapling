package com.ghostchu.tracker.sapling.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class UserBalancesVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private long owner;
    private long currency;
    private BigDecimal balance;
}
