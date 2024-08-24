package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class CommentDTO {
    private String content;
    private int commentatorId;
}
