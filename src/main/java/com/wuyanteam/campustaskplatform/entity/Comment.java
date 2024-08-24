package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Comment
{
    private int parentId;
    private String content;
    private int id;
    private int commentatorId;
    private int taskId;
    private int receiverId;
    private Timestamp publishTime;
    private int likeNum;
    private Timestamp ancestorPublishTime;
}
