package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

@Data
public class NestedCommentDTO {
    private Integer presentCommentId;
    private String content;
    private Integer commentatorId;
}
