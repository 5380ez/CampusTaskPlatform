package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Message
{
    private int id;
    private int senderId;
    private int receiverId;
    private String content;
    private Timestamp sendTime;
    private int taskId;
}
