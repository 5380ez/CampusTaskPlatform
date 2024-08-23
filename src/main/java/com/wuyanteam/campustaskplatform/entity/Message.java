package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;
@Data
public class Message
{
    private int id;
    private int senderId;
    private int receiverId;
    private String content;
    private String sendTime;
    private int taskId;
}
