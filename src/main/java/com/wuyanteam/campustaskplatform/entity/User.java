package com.wuyanteam.campustaskplatform.entity;

import javax.persistence.*;

@Table(name="user")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String sex;
    private int age;
    private String accCrtTime;
    private int stuId;
    private int exp;
    private int level;
    private int likeCount;
    private String realName;
    private String address;
    private int balance;
    private String lastLoginTime;
    private int takeNum;
    private int publishNum;
    private String qq;

    private String email;

    private String phone;

    private int finishNum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAccCrtTime() {
        return accCrtTime;
    }

    public void setAccCrtTime(String accCrtTime) {
        this.accCrtTime = accCrtTime;
    }

    public int getStuId() {
        return stuId;
    }

    public void setStuId(int stuId) {
        this.stuId = stuId;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public int getTakeNum() {
        return takeNum;
    }

    public void setTakeNum(int takeNum) {
        this.takeNum = takeNum;
    }

    public int getPublishNum() {
        return publishNum;
    }

    public void setPublishNum(int publishNum) {
        this.publishNum = publishNum;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getFinishNum() {
        return finishNum;
    }

    public void setFinishNum(int finishNum) {
        this.finishNum = finishNum;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                ", accCrtTime='" + accCrtTime + '\'' +
                ", stuId=" + stuId +
                ", exp=" + exp +
                ", level=" + level +
                ", likeCount=" + likeCount +
                ", realName='" + realName + '\'' +
                ", address='" + address + '\'' +
                ", balance=" + balance +
                ", lastLoginTime='" + lastLoginTime + '\'' +
                ", takeNum=" + takeNum +
                ", publishNum=" + publishNum +
                ", qq='" + qq + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", finishNum=" + finishNum +
                '}';
    }
}
