package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

import javax.annotation.sql.DataSourceDefinition;
import java.sql.Timestamp;

//Comments and Task
@Data
public class CT {
    int id;
    String content;
    Timestamp publishTime;
    Integer likeNum;
    Integer publisherId;
    Integer receiverId;
    Integer parentId;
    String publisherUsername;
    String receiverUsername;
    Timestamp ancestorPublishTime;
    String avatarPath;
}
