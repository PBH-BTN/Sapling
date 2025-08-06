package com.ghostchu.tracker.sapling.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class CurrencyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String color;

    private boolean managed;

}
