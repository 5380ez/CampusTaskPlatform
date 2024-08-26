package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

import javax.persistence.*;

@Table(name="vcode")
@Entity
@Data
public class Vcode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String code;
}