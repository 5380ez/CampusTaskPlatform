package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserDTO {
    private int id;
    private String username;
    private String sex;
    private Integer age;
    private int stuId;
    private int exp;
    private int level;
    private int likeCount;
    private int publishNum;
    private String qq;
    private String email;
    private String phone;
    private byte[] avatar;//头像
    private String signature;
    private byte[] photo;//照片墙
}