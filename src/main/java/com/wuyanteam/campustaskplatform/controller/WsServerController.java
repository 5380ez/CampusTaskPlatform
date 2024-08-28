package com.wuyanteam.campustaskplatform.controller;


import com.github.yulichang.query.MPJQueryWrapper;
import com.wuyanteam.campustaskplatform.entity.*;
import com.wuyanteam.campustaskplatform.mapper.MessageMapper;
import com.wuyanteam.campustaskplatform.service.TaskService;
import com.wuyanteam.campustaskplatform.service.UserService;
import com.wuyanteam.campustaskplatform.config.WsServer;
import com.wuyanteam.campustaskplatform.mapper.CommentMapper;
import com.wuyanteam.campustaskplatform.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

//@EnableScheduling
@RestController
@CrossOrigin
public class WsServerController {
    @Autowired
    WsServer wsServer;

    @Autowired
    NotificationMapper notificationMapper;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private MessageMapper messageMapper;


    @GetMapping("/sendNotification")
    public void sendToClient(HttpServletRequest request){
        List<NTT> notePosting;
        notePosting = notificationMapper.selectJoinList(
                NTT.class,
                new MPJQueryWrapper<Notification>().select("c.id as commentId","m.id as messageId","t.task_id as taskId","t.type as type","t.receiver_id as receiverId","t.notify_time as notifyTime")
                        .leftJoin("`task` t1 on t.task_id = t1.id")
                        .leftJoin("`comment` c on t.comment_publish_time = c.publish_time")
                        .leftJoin("`message` m on t.message_publish_time = m.send_time")
                        .eq("t.receiver_id",userService.InfoService(request.getHeader("Authorization")).getId())
                        .eq("t.is_read",false));

        for(int i=0;i<notePosting.size();i++)
        {
            NTT ntt = notePosting.get(i);
            if(ntt.getType().equals("comment") )
            {
                Comment comment = commentMapper.selectById(ntt.getCommentId());
                System.out.println("comment:"+comment);
                User user = userService.getById(comment.getCommentatorId());
                int myId = userService.InfoService(request.getHeader("Authorization")).getId();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String notifyTime = ntt.getNotifyTime().format(formatter);
                String message = myId+"|"+notifyTime+"您收到一条来自用户："+user.getUsername()+",关于任务"+comment.getTaskId()+"的新回复:"+comment.getContent();
                wsServer.sendMessageToSomeone(message);
            }else if(ntt.getType().equals("message"))
            {
                Message message = messageMapper.selectById(ntt.getMessageId());
                User user = userService.getById(message.getSenderId());
                int myId = userService.InfoService(request.getHeader("Authorization")).getId();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String notifyTime = ntt.getNotifyTime().format(formatter);
                String message1 = myId+"|"+notifyTime+"您收到一条来自用户："+user.getUsername()+"的新消息："+message.getContent();
                wsServer.sendMessageToSomeone(message1);
            }else if(ntt.getType().equals("task"))
            {
                String message;
                Task task = taskService.getById(ntt.getTaskId());
                System.out.println("task:"+task);
                User user = userService.getById(task.getTakerId());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String notifyTime = ntt.getNotifyTime().format(formatter);
                switch (task.getState()){
                    case "incomplete":
                        message = task.getPublisherId()+"|"+notifyTime+"您的订单已被用户："+user.getUsername()+"接单";
                        break;
                    case "complete":
                        System.out.println("task:"+task);
                        message = task.getTakerId()+"|"+notifyTime+"您的id为："+task.getId()+"的订单已确认完成，订单金额为："+task.getReward();
                        break;
                    case "un-taken":
                        message = task.getPublisherId()+"|"+notifyTime+"您的订单状态变更为：未接单";
                        break;
                    case "unconfirmed":
                        message = task.getPublisherId()+"|"+notifyTime+"您id为"+task.getId()+"订单已送达，等待确认";
                    default:
                        message = task.getPublisherId()+"|"+notifyTime+"您的订单未被接单，请稍等~";
                        break;
                }
                wsServer.sendMessageToSomeone(message);
            }else if(ntt.getType().equals("cancel")){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String notifyTime = ntt.getNotifyTime().format(formatter);
                Task task = taskService.getById(ntt.getTaskId());
                String message = task.getTakerId()+"|"+notifyTime+"您的id为："+task.getId()+"的任务已被取消，请联系发布者协商";
                wsServer.sendMessageToSomeone(message);
            }else if(ntt.getType().equals("reject")){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String notifyTime = ntt.getNotifyTime().format(formatter);
                Task task = taskService.getById(ntt.getTaskId());
                String message = task.getTakerId()+"|"+notifyTime+"您的id为："+task.getId()+"的订单被拒绝确认，请继续配送";
                wsServer.sendMessageToSomeone(message);
            }
        }



        //wsServer.sendMessageToAll(message);

        //wsServer.sendCommentNotification("test notification");
    }

}
