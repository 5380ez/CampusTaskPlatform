package com.wuyanteam.campustaskplatform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wuyanteam.campustaskplatform.entity.Task;
import com.wuyanteam.campustaskplatform.entity.UTT;
import com.wuyanteam.campustaskplatform.entity.MyTaskDTO;
import com.wuyanteam.campustaskplatform.mapper.TaskMapper;
import com.wuyanteam.campustaskplatform.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin //允许该控制器跨域
@RequestMapping("/myTakingTask")
public class MyTakingTaskController {

    @Resource
    private TaskMapper taskMapper;
    @Resource
    private UserService userService;
    // 分页查询
    @PostMapping()
    public IPage myTakingTask(HttpServletRequest request,@RequestBody MyTaskDTO myTaskDTO) {
        return getTasks(userService.InfoService(request.getHeader("Authorization")).getId(), myTaskDTO.getPage(), myTaskDTO.getSortRule(), myTaskDTO.getIsDesc(), myTaskDTO.getState(),null);
    }

    // 搜索
    @PostMapping("/search")
    public IPage searchTakingTask(HttpServletRequest request,@RequestBody MyTaskDTO myTaskDTO) {
        return getTasks(userService.InfoService(request.getHeader("Authorization")).getId(), myTaskDTO.getPage(), myTaskDTO.getSortRule(), myTaskDTO.getIsDesc(), myTaskDTO.getState(), myTaskDTO.getKeyword());
    }

    private IPage getTasks(int myId, Integer page, String sortRule, boolean isDesc,String state, String keyword) {
        IPage<UTT> iPage;
        MPJQueryWrapper<Task> queryWrapper = new MPJQueryWrapper<Task>()
                .select("t.id as taskId","reward", "start_address", "end_address", "due_time", "title")
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