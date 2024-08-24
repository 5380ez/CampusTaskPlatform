package com.wuyanteam.campustaskplatform.controller;

import com.wuyanteam.campustaskplatform.entity.Task;
import com.wuyanteam.campustaskplatform.mapper.TaskMapper;
import com.wuyanteam.campustaskplatform.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@CrossOrigin
@RequestMapping("/creatingtask")
public class CreatingTaskController {
    @Resource
    private TaskMapper taskMapper;
    @Resource
    private UserMapper userMapper;

    @PostMapping("/task")
    public String createTask(@RequestParam Integer publisherId,
                             @RequestParam Float reward,
                             @RequestParam String startAddress,
                             @RequestParam String endAddress,
                             @RequestParam Timestamp dueTime,
                             @RequestParam String title,
                             @RequestParam String description,
                             @RequestParam String campus) {

        // 验证 reward 是否在有效范围内
        if (reward <= 0) {
            return "Failed to create task: Amount too small.";
        }
        if (reward > 10000) {
            return "Failed to create task: Amount too large.";
        }

        // 验证 reward,startAddress, endAddress, dueTime,title 是否为空

        if (title == null || title.isEmpty()) {
            return "Failed to create task: Title cannot be empty.";
        }
        if (startAddress == null || startAddress.isEmpty()) {
            return "Failed to create task: Start address cannot be empty.";
        }
        if (endAddress == null || endAddress.isEmpty()) {
            return "Failed to create task: End address cannot be empty.";
        }


        Task task = new Task();
        task.setPublisherId(publisherId);
        LocalDateTime now = LocalDateTime.now();
        Timestamp publishTime = Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant());
        task.setPublishTime(publishTime);
        task.setReward(reward);
        task.setStartAddress(startAddress);
        task.setEndAddress(endAddress);
        task.setDueTime(dueTime);
        task.setTitle(title);
        task.setDescription(description);
        task.setCampus(campus);
        task.setState("un-taken");
        task.setTakerId(null);
        if (dueTime.compareTo(publishTime) < 0) {
            return "Failed to create task: Due time (" + dueTime + ") cannot be earlier than publishTime (" + publishTime + ").";
        }


        int rows = taskMapper.insert(task);

        if (rows > 0) {
            return "Task created successfully.";
        } else {
            return "Failed to create task.";
        }
    }
}