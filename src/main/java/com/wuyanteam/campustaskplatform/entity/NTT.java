package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

@Data
public class NTT {
    private int notificationId;
    private int receiverId;
    private int commentId;
    private int messageId;
    private int taskId;
    private boolean isRead;
    private String type;
}
