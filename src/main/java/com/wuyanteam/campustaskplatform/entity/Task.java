package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
@Table(name="task")
@Entity
@Data
public class Task
{
    @Id
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
