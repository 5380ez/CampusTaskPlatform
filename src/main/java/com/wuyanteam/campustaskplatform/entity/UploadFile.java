package com.wuyanteam.campustaskplatform.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@TableName("upload_file")
public class UploadFile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String primaryName;//文件主名称

    private String extension;//文件扩展名

    private String path;//存放路径

    private String type;//文件类型

    private Long size;

    private int uploaderId;

    private Timestamp createTime;

}