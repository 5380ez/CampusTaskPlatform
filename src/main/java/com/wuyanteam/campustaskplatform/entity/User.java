package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Table(name="user")
@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
