package com.wuyanteam.campustaskplatform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wuyanteam.campustaskplatform.entity.Task;
import com.wuyanteam.campustaskplatform.entity.TaskDTO;
import com.wuyanteam.campustaskplatform.entity.UTT;
import com.wuyanteam.campustaskplatform.mapper.TaskMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@CrossOrigin //允许该控制器跨域
@RequestMapping("/availableTask")
public class AvailableTaskController {

    @Resource
    private TaskMapper taskMapper;
    @PostMapping()
    public IPage availableTask(@RequestBody TaskDTO taskDTO) {
        System.out.println(taskDTO);
        return getTasks(taskDTO.getCampus(), taskDTO.getSex(), taskDTO.getPage(), taskDTO.getSortOrder(), taskDTO.getIsDesc(), null);
    }

    // 新增搜索方法
    @PostMapping("/search")
    public IPage searchAvailableTask(@RequestBody TaskDTO taskDTO) {
        return getTasks(taskDTO.getCampus(), taskDTO.getSex(), taskDTO.getPage(), taskDTO.getSortOrder(), taskDTO.getIsDesc(), taskDTO.getKeyword());
    }

    private IPage getTasks(String campus, String sex, int page, String sortOrder, Boolean isDesc, String keyword) {
        IPage<UTT> iPage;
        MPJQueryWrapper<Task> queryWrapper = new MPJQueryWrapper<Task>()
                .select("username", "sex", "`level`", "`user`.id as uid")
                .select("t.id as taskId","publish_time", "reward", "start_address", "end_address", "due_time", "title", "campus")
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

        // 直接使用 isDesc 的值来确定排序方式
        if (isDesc) {
            iPage = taskMapper.selectJoinPage(new Page<>(page, 10), UTT.class, queryWrapper.orderByDesc(sortOrder).orderByDesc("exp"));
        } else {
            iPage = taskMapper.selectJoinPage(new Page<>(page, 10), UTT.class, queryWrapper.orderByAsc(sortOrder).orderByDesc("exp"));
        }

        return iPage;
    }
}