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
@RequestMapping("/availabletask")
public class AvailableTaskController {

    @Resource
    private TaskMapper taskMapper;

    @PostMapping
    public IPage availableTask(String campus, String sex, int page, String sortOrder, boolean isDesc) {
        return getTasks(campus, sex, page, sortOrder, isDesc, null);
    }

    // 新增搜索方法
    @PostMapping("/search")
    public IPage searchAvailableTask(String campus, String sex, int page, String sortOrder,  boolean isDesc, @RequestParam String keyword) {
        return getTasks(campus, sex, page, sortOrder, isDesc, keyword);
    }

    private IPage getTasks(String campus, String sex, int page, @RequestParam(defaultValue = "exp")String sortOrder, @RequestParam(defaultValue = "true")boolean isDesc, String keyword) {
        IPage<UTT> iPage;
        MPJQueryWrapper<Task> queryWrapper = new MPJQueryWrapper<Task>()
                .select("username", "sex", "`level`")
                .select("publish_time", "reward", "start_address", "end_address", "due_time", "title", "campus")
                .innerJoin("`user` on publisher_id = `user`.id")
                .eq("state", "un-taken");

        if (keyword != null) {
            queryWrapper = queryWrapper.and(i -> i.like("username", keyword)
                    .or().like("title", keyword)
                    .or().like("description", keyword)
                    .or().like("start_address", keyword)
                    .or().like("end_address", keyword));
        }

        if (campus != null && !campus.isEmpty()) {
            queryWrapper.eq("campus", campus);
        }
        if (sex != null && !sex.isEmpty()) {
            queryWrapper.eq("sex", sex);
        }

        if (isDesc) {
            iPage = taskMapper.selectJoinPage(new Page<>(page, 10), UTT.class, queryWrapper.orderByDesc(sortOrder));
        } else {
            iPage = taskMapper.selectJoinPage(new Page<>(page, 10), UTT.class, queryWrapper.orderByAsc(sortOrder));
        }

        return iPage;
    }
}