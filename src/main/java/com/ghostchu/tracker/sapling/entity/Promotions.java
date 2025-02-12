package com.ghostchu.tracker.sapling.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class Promotions implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private double uploadModifier;

    private double downloadModifier;

    private String condition;

    private String color;
}
