package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;

@Data
public class UTT
{
    private int uid;
    private byte[] avatar;
    private String username;
    private String publisherUsername;
    private String takerUsername;
    private String password;
    private String sex;
    private String publisherSex;
    private String takerSex;
    private int age;
    private Timestamp accCrtTime;
    private int stuId;
    private int exp;
    private int publisherLevel;
    private int takerLevel;
    private String campus;
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
    private String publisherPhone;
    private String takerPhone;
    private int finishNum;
    private int taskId;
    private int publisherId;
    private int takerId;
    private Timestamp publishTime;
    private String state;
    private Timestamp takeTime;
    private float reward;
    private String startAddress;
    private String endAddress;
    private Timestamp dueTime;
    private String title;
    private String description;
    private Timestamp finishTime;
    private Boolean isLike;
}
