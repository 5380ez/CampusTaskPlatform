package com.wuyanteam.campustaskplatform.entity;

import com.wuyanteam.campustaskplatform.mapper.MessageMapper;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Message
{
    public static MessageMapper messageMapper;
    private int id;
    private int senderId;
    private int receiverId;
    private String content;
    private Timestamp sendTime;
}
