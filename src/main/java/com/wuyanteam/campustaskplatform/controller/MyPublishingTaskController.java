package com.wuyanteam.campustaskplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wuyanteam.campustaskplatform.entity.*;
import com.wuyanteam.campustaskplatform.mapper.TaskMapper;
import com.wuyanteam.campustaskplatform.mapper.UserMapper;
import com.wuyanteam.campustaskplatform.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin //允许该控制器跨域
@RequestMapping("/mypPublishingTask")
public class MyPublishingTaskController {

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    // 分页查询
    @PostMapping("/{state}")
    public IPage myPublishingTask(HttpServletRequest request, MyTaskDTO myTaskDTO) {
        return getTasks(userService.InfoService(request.getHeader("Authorization")).getId(), myTaskDTO.getPage(), myTaskDTO.getSortRule(), myTaskDTO.isDesc(), myTaskDTO.getState(),null);
    }

    // 搜索
    @PostMapping("/search/{state}")
    public IPage searchPublishingTask(HttpServletRequest request,MyTaskDTO myTaskDTO) {
        return getTasks(userService.InfoService(request.getHeader("Authorization")).getId(), myTaskDTO.getPage(), myTaskDTO.getSortRule(), myTaskDTO.isDesc(), myTaskDTO.getState(), myTaskDTO.getKeyword());
    }

    private IPage getTasks(int myId, int page, String sortRule, boolean isDesc, String state, String keyword) {
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
    @DeleteMapping("/{state}")
    public String DeletingTask(@RequestBody TaskIdDTO taskIdDTO, @PathVariable String state){
        // 假设有一个 QueryWrapper 对象，设置删除条件为 id = 'taskId'
        int id= taskIdDTO.getId();
        if (state.equals("complete")||state.equals("timeout")||state.equals("un-taken")){
            QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", id);
            Task task = taskMapper.selectOne(queryWrapper);
            int result = taskMapper.deleteById(id); // 调用 remove 方法
            if (result==1) {
                return ("Record deleted successfully.");
            } else {
                return ("Failed to delete record.");
            }
        }
        if(state.equals("incomplete")) {
            QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", id);
            Task task = taskMapper.selectOne(queryWrapper);
            int takerId=task.getPublisherId();
//            QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
//            queryWrapper1.eq("id",takerId);
//            User user = userMapper.selectOne(queryWrapper1);
            // 假设要更新 ID 为 1 的用户的邮箱
            User updateUser = new User();
            updateUser.setId(takerId);
            int exp=updateUser.getExp();
            exp=exp-5;
            int flag=1;
            if(exp<0){
                updateUser.setExp(0);
                flag=0;
            }
            updateUser.setExp(exp);
            int rows = userMapper.updateById(updateUser); // 调用 updateById 方法
            if (rows > 0) {
                System.out.println("User updated successfully.");
            } else {
                System.out.println("No user updated.");
            }
            int result = taskMapper.deleteById(id); // 调用 remove 方法
            if (result==1) {
                if(flag==0){
                    return "Record deleted successfully.But your exp is over";
                }
                return ("Record deleted successfully.");
            } else {
                return ("Failed to delete record.");
            }
        }
        return "Cannot find task";
    }
}