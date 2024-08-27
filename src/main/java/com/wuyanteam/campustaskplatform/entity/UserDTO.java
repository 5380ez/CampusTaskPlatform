package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserDTO {
    private int id;
    private String username;
    private String password;
    private String sex;
    private Integer age;
    private Timestamp accCrtTime;
    private int stuId;
    private int exp;
    private int level;
    private int likeCount;
    private String realName;
    private String address;
    private float balance;
    private Timestamp lastLoginTime;
    private int takeNum;
    private int publishNum;
    private String qq;
    private String email;
    private String phone;
    private int finishNum;
    private String avatar_path;
    private String signature;
    private byte[] photo;//照片墙
}