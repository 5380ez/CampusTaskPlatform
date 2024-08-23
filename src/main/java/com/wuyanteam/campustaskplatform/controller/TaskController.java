package com.wuyanteam.campustaskplatform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wuyanteam.campustaskplatform.entity.Task;
import com.wuyanteam.campustaskplatform.entity.UTT;
import com.wuyanteam.campustaskplatform.mapper.TaskMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@CrossOrigin //允许该控制器跨域
@RequestMapping("/taskinformation") // 添加请求映射的路径前缀
public class TaskController {
    @Resource
    private TaskMapper taskMapper;

    // Method to get task information by task_id
    @GetMapping("/{task_id}") // 使用 {task_id} 表示路径变量
    public List<UTT> taskInformation(@PathVariable("task_id") Integer taskId) {
        List<UTT> list;

        // Create a page object with the current page and a fixed size of 10 records per page.
        list = taskMapper.selectJoinList(
                UTT.class,
                new MPJQueryWrapper<Task>()
                        .select("username", "sex", "`level`", "phone")
                        .select("publisher_id", "taker_id", "publish_time", "take_time", "state", "reward", "start_address", "end_address", "due_time", "title", "description", "campus")
                        .innerJoin("`user` u on t.publisher_id = u.id")
                        .eq("t.id", taskId)); // Use the path variable for the task_id
        return list;
    }
}