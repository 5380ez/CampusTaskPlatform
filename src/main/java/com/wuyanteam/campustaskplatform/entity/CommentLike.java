package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

@Data
public class CommentLike {
    private int id;
    private int commentId;
    private int userId;
    private Boolean isLike;
}
