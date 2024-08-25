package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

@Data
public class MyTaskDTO {
    private int page;
    private String sortRule;
    private Boolean isDesc;
    private String keyword;
    private String state;
    }

