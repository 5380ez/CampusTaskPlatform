package com.wuyanteam.campustaskplatform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wuyanteam.campustaskplatform.entity.Task;
import com.wuyanteam.campustaskplatform.entity.UTT;
import com.wuyanteam.campustaskplatform.entity.UserDTO;
import com.wuyanteam.campustaskplatform.mapper.TaskMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@CrossOrigin //允许该控制器跨域
@RequestMapping("/mypublishingtask")
public class MyPublishingTaskController {

    @Resource
    private TaskMapper taskMapper;

    // 分页查询
    @PostMapping("/{state}")
    public IPage myPublishingTask(UserDTO userDTO) {
        return getTasks(userDTO.getMyId(), userDTO.getPage(),userDTO.getSortRule(),userDTO.isDesc(),userDTO.getState(),null);
    }

    // 搜索
    @PostMapping("/search/{state}")
    public IPage searchPublishingTask(UserDTO userDTO) {
        return getTasks(userDTO.getMyId(), userDTO.getPage(),userDTO.getSortRule(),userDTO.isDesc(),userDTO.getState(),userDTO.getKeyword());
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