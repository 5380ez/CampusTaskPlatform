package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

import javax.annotation.sql.DataSourceDefinition;
import java.sql.Timestamp;

//Comments and Task
@Data
public class CT {
    String userName;
    String content;
    Timestamp publishTime;
    int likeNum;
}
