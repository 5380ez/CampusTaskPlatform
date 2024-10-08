package com.wuyanteam.campustaskplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wuyanteam.campustaskplatform.entity.*;
import com.wuyanteam.campustaskplatform.mapper.CommentMapper;
import com.wuyanteam.campustaskplatform.mapper.TaskMapper;
import com.wuyanteam.campustaskplatform.mapper.UserMapper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;

@RestController
@CrossOrigin
public class BigScreenController {
    @Resource
    UserMapper userMapper;
    @Resource
    TaskMapper taskMapper;
    @Resource
    CommentMapper commentMapper;
    @GetMapping("/bigScreenData")
    public BigScreenData data() {
        BigScreenData data = new BigScreenData();
        // 用户总数量
        Long userNum;
        QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
        userNum = userMapper.selectCount(queryWrapper1);

        data.setUserNum(userNum);

        // 任务所在校区
        String[] campuses = {"A", "B", "C", "D"};
        int[] campusNum = new int[4];

        for (int i = 0; i < campuses.length; i++) {
            QueryWrapper<Task> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("campus", campuses[i]);
            campusNum[i] = Math.toIntExact(taskMapper.selectCount(queryWrapper2));
        }

        data.setCampusNum(campusNum); // 假设 BigScreenData 类有一个 setCampusNum 方法来设置数组

        // 总点赞数
        int totalLike = 0;
        for (int i=1;i<=userNum;i++) {
            QueryWrapper<User> queryWrapper3 = new QueryWrapper<>();
            queryWrapper3.eq("id", i);
            User existingUser = userMapper.selectOne(queryWrapper3);
            if (existingUser != null){
                int like = existingUser.getLikeCount();
                totalLike += like;
            }
        }
        data.setLikeNum(totalLike);

        //总评论数
        int totalComment;
        QueryWrapper<Comment> queryWrapper4 = new QueryWrapper<>();
        totalComment = Math.toIntExact(commentMapper.selectCount(queryWrapper4));
        data.setCommentNum(totalComment);

        //男女比例
        String[] sexes = {"male", "female"};
        int[] sexNum = new int[2];

        for (int i = 0; i < sexes.length; i++) {
            QueryWrapper<User> queryWrapper5 = new QueryWrapper<>();
            queryWrapper5.eq("sex", sexes[i]);
            sexNum[i] = Math.toIntExact(userMapper.selectCount(queryWrapper5));
        }
        int maleRate = sexNum[0];
        int femaleRate = sexNum[1];
        data.setMaleRate(maleRate);
        data.setFemaleRate(femaleRate);

        // 用户总数（一周变化）
        int todayUserNum ;
        LocalDate now = LocalDate.now();
        LocalDate Date = now.minusDays(0);
        QueryWrapper<User> queryWrapper6 = new QueryWrapper<>();
        queryWrapper6.between("last_login_time", Date.atStartOfDay(), Date.plusDays(1).atStartOfDay());
        todayUserNum = Math.toIntExact(userMapper.selectCount(queryWrapper6));
        data.setTodayUserNum(todayUserNum);

        // 不同状态任务数
        int untakenNum =0;
       int incompleteNum =0;
        int completeNum =0;
        int timeoutNum =0;
        int unconfirmedNum=0;
        String[] states = {"un-taken", "incomplete", "complete", "timeout", "unconfirmed"};
        for (int j = 0; j < states.length; j++) {
            QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("state", states[j]);
            switch (states[j]) {
                case "un-taken":
                    untakenNum = Math.toIntExact(taskMapper.selectCount(queryWrapper));
                    break;
                case "incomplete":
                    incompleteNum = Math.toIntExact(taskMapper.selectCount(queryWrapper));
                    break;
                case "complete":
                    completeNum = Math.toIntExact(taskMapper.selectCount(queryWrapper));
                    break;
                case "timeout":
                    timeoutNum = Math.toIntExact(taskMapper.selectCount(queryWrapper));
                    break;
                case "unconfirmed":
                    unconfirmedNum = Math.toIntExact(taskMapper.selectCount(queryWrapper));
                    break;
                default:
                        // 不做任何处理
                    break;
                }
            }

        data.setUntakenNum(untakenNum);
        data.setIncompleteNum(incompleteNum);
        data.setCompleteNum(completeNum);
        data.setTimeoutNum(timeoutNum);
        data.setUncomfirmedNum(unconfirmedNum);

        //七天用户活跃度（七天登录/用户总数）
        LocalDate sevenDaysAgo = now.minusDays(7);
        QueryWrapper<User> queryWrapper7 = new QueryWrapper<>();
        queryWrapper7.ge("last_login_time", sevenDaysAgo.atStartOfDay());
        int activeUsersSevenDays = Math.toIntExact(userMapper.selectCount(queryWrapper7));
        float oneWeekActiveRate = (float) activeUsersSevenDays / userNum;

        data.setOneWeekActiveRate(oneWeekActiveRate);


        //任务总数
        Long taskNum;
        QueryWrapper<Task> queryWrapper8 = new QueryWrapper<>();
        taskNum = taskMapper.selectCount(queryWrapper8);
        data.setTaskNum(Math.toIntExact(taskNum));

        // 七天内每天的登录人数和活跃度（每天的登录人数/当天总人数）
        int[] dailyActiveUsers = new int[7];
        float[] dailyActiveRates = new float[7];

        for (int i = 0; i < 7; i++) {
            LocalDate date = now.minusDays(i);
            QueryWrapper<User> queryWrapperDaily = new QueryWrapper<>();
            queryWrapperDaily.between("last_login_time", date.atStartOfDay(), date.plusDays(1).atStartOfDay());
            int dailyActiveUsersCount = Math.toIntExact(userMapper.selectCount(queryWrapperDaily));
            dailyActiveUsers[i] = dailyActiveUsersCount;

            // 当天总人数为用户总数，因为活跃度是相对于总人数的
            float dailyActiveRate = (float) dailyActiveUsersCount / userNum;
            dailyActiveRates[i] = dailyActiveRate;
        }

        data.setDailyActiveUsers(dailyActiveUsers); // 假设 BigScreenData 类有一个 setDailyActiveUsers 方法来设置数组
        data.setDailyActiveRates(dailyActiveRates); // 假设 BigScreenData 类有一个 setDailyActiveRates 方法来设置数组

        // 用户总数（当天注册数）
        int todayRegisterNum ;
        QueryWrapper<User> queryWrapper9 = new QueryWrapper<>();
        queryWrapper9.between("acc_crt_time", Date.atStartOfDay(), Date.plusDays(1).atStartOfDay());
        todayRegisterNum = Math.toIntExact(userMapper.selectCount(queryWrapper9));
        data.setTodayRegisterNum(todayRegisterNum);


        return data;
    }
}
