package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Task
{
    private int id;
    private int publisherId;
    private Integer takerId;
    private Timestamp publishTime;
    private String state;
    private Timestamp takeTime;
    private float reward;
    private String startAddress;
    private String endAddress;
    private Timestamp dueTime;
    private String title;
    private String description;
    private Timestamp finishTime;
    private String campus;
}
