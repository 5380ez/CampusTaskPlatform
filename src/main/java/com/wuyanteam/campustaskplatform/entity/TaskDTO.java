package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

@Data
public class TaskDTO {
    private String campus;
    private String sex;
    private int page;
    private String sortOrder;
    private boolean isDesc;
    private String keyword;
}
