package com.wuyanteam.campustaskplatform.controller;

import com.wuyanteam.campustaskplatform.service.TaskService;
import com.wuyanteam.campustaskplatform.service.UserService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@EnableScheduling
@Component
public class TaskScheduler111 {

    @Resource
    private TaskService taskService;

    @Resource
    private UserService userService;

    /**
     * 定时任务，每2秒检查一次任务状态.
     */
    @Scheduled(fixedRate = 2000)
    public void checkTasksForTimeout() {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        taskService.checkAndSetTimeoutTasks();
        userService.UpdateLevelService();
    }
}