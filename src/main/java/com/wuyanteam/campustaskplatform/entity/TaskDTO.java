package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

@Data
public class TaskDTO {
    private String campus;
    private String sex;
    private Integer page;
    private String sortOrder;
    private Boolean isDesc;
}
