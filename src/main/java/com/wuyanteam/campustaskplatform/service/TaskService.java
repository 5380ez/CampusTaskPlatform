package com.wuyanteam.campustaskplatform.service;

import com.github.yulichang.extension.mapping.base.MPJDeepService;
import com.wuyanteam.campustaskplatform.entity.Task;

public interface TaskService extends MPJDeepService<Task> {
    void checkAndSetTimeoutTasks();
}
