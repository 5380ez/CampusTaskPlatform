package com.wuyanteam.campustaskplatform.entity;

import com.wuyanteam.campustaskplatform.mapper.NotificationMapper;
import lombok.Data;


import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;


@Data
@Entity
public class Notification {
    @Id
    private int notificationId;

    private int receiverId;
    private Timestamp commentPublishTime;
    private Timestamp messagePublishTime;
    private int taskId;
    private boolean isRead;
    private String type;
    private Timestamp notifyTime;

    public static NotificationMapper notificationMapper;
}
