package com.wuyanteam.campustaskplatform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wuyanteam.campustaskplatform.entity.Task;
import com.wuyanteam.campustaskplatform.entity.UTT;
import com.wuyanteam.campustaskplatform.mapper.TaskMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/mypublishingtask")
public class MyPublishingTaskController {

    @Resource
    private TaskMapper taskMapper;

    // 分页查询
    @PostMapping("/{state}")
    public IPage myPublishingTask(int myId, int page,  String sortRule, boolean isDesc, @PathVariable String state) {
        return getTasks(myId, page, sortRule, isDesc, state, null);
    }

    // 搜索
    @PostMapping("/search/{state}")
    public IPage searchPublishingTask(int myId, int page, String sortRule, boolean isDesc, @PathVariable String state, String keyword) {
        return getTasks(myId, page, sortRule, isDesc, state, keyword);
    }

    private IPage getTasks(int myId, int page, @RequestParam(defaultValue = "publish_time")String sortRule, @RequestParam(defaultValue = "true")boolean isDesc, String state, String keyword) {
        IPage<UTT> iPage;
        MPJQueryWrapper<Task> queryWrapper = new MPJQueryWrapper<Task>()
                .select("take_time", "publish_time", "finish_time", "due_time", "title")
                .select("username", "sex")
                .leftJoin("`user` on taker_id = `user`.id")
                .eq("publisher_id", myId)
                .eq("state", state);

        if (keyword != null) {
            queryWrapper = queryWrapper.and(i -> i.like("`user`.username", keyword)
                    .or().like("title", keyword)
                    .or().like("description", keyword)
                    .or().like("start_address", keyword)
                    .or().like("end_address", keyword));
        }

        if (isDesc) {
            iPage = taskMapper.selectJoinPage(new Page<>(page, 10), UTT.class, queryWrapper.orderByDesc(sortRule));
        } else {
            iPage = taskMapper.selectJoinPage(new Page<>(page, 10), UTT.class, queryWrapper.orderByAsc(sortRule));
        }

        return iPage;
    }
}