package com.wuyanteam.campustaskplatform.entity;

import com.sun.jmx.snmp.Timestamp;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class NTT {
    private int notificationId;
    private int receiverId;
    private int commentId;
    private int messageId;
    private int taskId;
    private boolean isRead;
    private String type;
    private LocalDateTime notifyTime;
}
