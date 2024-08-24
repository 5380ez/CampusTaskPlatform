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
@RequestMapping("/mytakingtask")
public class MyTakingTaskController {

    @Resource
    private TaskMapper taskMapper;

    // 分页查询
    @PostMapping("/{state}")
    public IPage myTakingTask(UserDTO userDTO) {
        return getTasks(userDTO.getMyId(), userDTO.getPage(),userDTO.getSortRule(),userDTO.isDesc(),userDTO.getState(),null);
    }

    // 搜索
    @PostMapping("/search/{state}")
    public IPage searchTakingTask(UserDTO userDTO) {
        return getTasks(userDTO.getMyId(), userDTO.getPage(),userDTO.getSortRule(),userDTO.isDesc(),userDTO.getState(),userDTO.getKeyword());
    }

    private IPage getTasks(Integer myId, Integer page,String sortRule,boolean isDesc, String state, String keyword) {
        IPage<UTT> iPage;
        MPJQueryWrapper<Task> queryWrapper = new MPJQueryWrapper<Task>()
                .select("reward", "start_address", "end_address", "due_time", "title")
                .select("username", "sex")
                .innerJoin("`user` on publisher_id = `user`.id")
                .eq("taker_id", myId)
                .eq("state", state);

        if (keyword != null) {
            queryWrapper = queryWrapper.and(i -> i.like("username", keyword)
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