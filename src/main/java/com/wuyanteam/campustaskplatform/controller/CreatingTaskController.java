package com.wuyanteam.campustaskplatform.controller;

import com.wuyanteam.campustaskplatform.entity.Task;
import com.wuyanteam.campustaskplatform.mapper.TaskMapper;
import com.wuyanteam.campustaskplatform.mapper.UserMapper;
import com.wuyanteam.campustaskplatform.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@CrossOrigin
@RequestMapping("/creatingTask")
public class CreatingTaskController {
    @Resource
    private TaskMapper taskMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    @PostMapping()
    public String createTask(HttpServletRequest request, @RequestBody Task task) {
        String token = request.getHeader("Authorization");
        // 验证 reward 是否在有效范围内
        if (task.getReward() <= 0) {
            return "Failed to create task: Reward too small.";
        }
        if (task.getReward() > 10000) {
            return "Failed to create task: Reward too large.";
        }

        // 验证 title, startAddress, endAddress, dueTime 是否为空
        if (task.getTitle() == null || task.getTitle().isEmpty()) {
            return "Failed to create task: Title cannot be empty.";
        }
        if (task.getStartAddress() == null || task.getStartAddress().isEmpty()) {
            return "Failed to create task: Start address cannot be empty.";
        }
        if (task.getEndAddress() == null || task.getEndAddress().isEmpty()) {
            return "Failed to create task: End address cannot be empty.";
        }
        Task newTask = new Task();
        newTask.setPublisherId(userService.InfoService(token).getId());
        LocalDateTime now = LocalDateTime.now();
        Timestamp publishTime = Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant());
        newTask.setPublishTime(publishTime);
        newTask.setReward(task.getReward());
        newTask.setStartAddress(task.getStartAddress());
        newTask.setEndAddress(task.getEndAddress());
        newTask.setDueTime(task.getDueTime());
        newTask.setTitle(task.getTitle());
        newTask.setDescription(task.getDescription());
        newTask.setCampus(task.getCampus());
        newTask.setState("un-taken");
        newTask.setTakerId(null);
        if (newTask.getDueTime().compareTo(publishTime) < 0) {
            return "Failed to create task: Due time (" + newTask.getDueTime() + ") cannot be earlier than publishTime (" + publishTime + ").";
        }
        int rows = taskMapper.insert(newTask);

        if (rows > 0) {
            return "Task created successfully.";
        } else {
            return "Failed to create task.";
        }
    }
}