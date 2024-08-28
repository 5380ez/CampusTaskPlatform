package com.wuyanteam.campustaskplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wuyanteam.campustaskplatform.Reposity.NotificationDao;
import com.wuyanteam.campustaskplatform.entity.Notification;
import com.wuyanteam.campustaskplatform.mapper.NotificationMapper;
import com.wuyanteam.campustaskplatform.service.NotificationService;
import com.wuyanteam.campustaskplatform.service.UserService;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.sql.Wrapper;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@CrossOrigin //允许该控制器跨域
@RequestMapping("/notification")
public class NotificationController
{
    @Autowired
    private UserService userService;

    @Autowired
    private NotificationMapper notificationMapper;

    private final NotificationDao notificationDao = new NotificationDao();

    @GetMapping("/delete")
    public void deleteNotification(HttpServletRequest request){
        LocalDateTime now = LocalDateTime.now();
        Timestamp currentTime = Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant());
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        //QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<Notification> queryWrapper = Wrappers.<Notification>lambdaQuery()
                        .eq(Notification::getReceiverId,uid)
                        .lt(Notification::getNotifyTime,currentTime);

        List<Notification> list = notificationMapper.selectList(queryWrapper);
        System.out.println("list: "+list);

        for (Notification notification : list) {
            System.out.println("notification:"+notification);
            int notificationId = notification.getNotificationId();
            notificationDao.deleteNotificationById(notificationId);
        }
    }
}
