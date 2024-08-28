package com.wuyanteam.campustaskplatform.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wuyanteam.campustaskplatform.config.WsServer;
import com.wuyanteam.campustaskplatform.entity.*;
import com.wuyanteam.campustaskplatform.mapper.*;
import com.wuyanteam.campustaskplatform.service.CommentService;
import com.wuyanteam.campustaskplatform.service.TaskService;
import com.wuyanteam.campustaskplatform.service.UserService;
import com.wuyanteam.campustaskplatform.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentLikeMapper commentLikeMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @GetMapping("/{task_id}")
    public List<UTT> taskInformation(HttpServletRequest request, @PathVariable("task_id") int taskId) {
        // 查询任务信息
        List<UTT> list;
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        list = taskMapper.selectJoinList(
                UTT.class,
                new MPJQueryWrapper<Task>().select("u1.username as publisherUsername", "u1.sex as publisherSex",
                                "u1.phone as publisherPhone ", "u1.level as publisherLevel", "u2.username as takerUsername",
                                "u2.sex as takerSex", "u2.level as takerLevel", "u2.phone as takerPhone", "t.publisher_id",
                                "t.taker_id", "t.publish_time", "t.state", "t.take_time", "t.reward", "t.campus","u1.avatar_path",
                                "t.start_address", "t.end_address", "t.due_time", "t.title", "t.description")
                        .innerJoin("`user` u1 on t.publisher_id = u1.id")
                        .leftJoin("`user` u2 on t.taker_id = u2.id")
                        .eq("t.id", taskId));
        return list;
    }
    @PostMapping("/{task_id}")
    public Result taskUpdate(HttpServletRequest request, @PathVariable("task_id") int taskId)
    {
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        UpdateWrapper<Task> updateWrapper = new UpdateWrapper<>();
        Task task = taskService.getById(taskId);
        updateWrapper.eq("id", taskId);
        if (task.getPublisherId() == uid) {
            return Result.error("401","不能接受自己发布的任务！");
        }
        LocalDateTime now = LocalDateTime.now();
        if(task.getDueTime().compareTo(Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant())) < 0)
        {
            return Result.error("402","任务已超时！");
        }
        updateWrapper.set("state", "incomplete").set("take_time",Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant())).set("taker_id",uid);
        int row = taskMapper.update(null,updateWrapper);
        if(row > 0) {

            //新增notification记录——》向发布者发送接单消息
            Notification notification = new Notification();
            notification.setType("task");
            LocalDateTime specificDataTime = LocalDateTime.of(2000,1,1,0,0,0);
            Timestamp specificTimeStamp = Timestamp.valueOf(specificDataTime);
            notification.setMessagePublishTime(specificTimeStamp);
            notification.setMessagePublishTime(specificTimeStamp);
            notification.setRead(false);
            notification.setTaskId(taskId);
            Timestamp publishTime = Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant());
            notification.setNotify_time(publishTime);

            //notification实时通知
            WsServer wsServer = new WsServer();
            User user = userService.getById(task.getTakerId());
            wsServer.sendMessageToSomeone(task.getPublisherId()+"|您的订单已被用户："+user.getUsername()+"接单");


            return Result.success(task, "成功接单！");
        }
        return Result.error("403","接单失败");
    }
    //查看评论
    @GetMapping("/{task_id}/comment/{page}")
    public Map<String, Object> commentInformation(HttpServletRequest request,@PathVariable("task_id") int taskId, @PathVariable(value = "page") int currentPage)
    {
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        IPage<CT> pagedComments;
        pagedComments = commentMapper.selectJoinPage(new Page<>(currentPage, 5),
                CT.class,
                new MPJQueryWrapper<Comment>()
                        .select("t.id","u.avatar_path","t.content", "t.commentator_id as publisherId","t.publish_time", "t.like_num", "u.username as publisherUsername","u1.username as receiverUsername","t.parent_id")
                        .innerJoin("`task` t1 on t.task_id = t1.id")
                        .innerJoin("`user` u on t.commentator_id = u.id")
                        .innerJoin("`user` u1 on t.receiver_id = u1.id")
                        .eq("t1.id", taskId)
                        .orderByAsc("t.ancestor_publish_time")
                        .orderByAsc("t.publish_time"));

        Map<String, Object> result = new HashMap<>();
        result.put("comments", pagedComments.getRecords());
        result.put("totalPages", pagedComments.getPages());
        result.put("currentPage", currentPage);
        result.put("pageSize", 5);
        return result;
    }

    @PostMapping("/{task_id}/comment/create")
    public Map<String,Object> createComment(HttpServletRequest request,@PathVariable("task_id")int taskId,@RequestBody Comment comment){
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        comment.setTaskId(taskId);
        comment.setCommentatorId(uid);
        comment.setContent(comment.getContent());
        LocalDateTime now = LocalDateTime.now();
        Timestamp publishTime = Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant());
        comment.setPublishTime(publishTime);
        comment.setAncestorPublishTime(publishTime);
        comment.setParentId(0);
        Task task = taskMapper.selectById(taskId);
        comment.setReceiverId(task.getPublisherId());
        comment.setLikeNum(0);
        int row = commentMapper.insert(comment);
        comment.setAncestorPublishTime(publishTime);
        Map<String, Object> result = new HashMap<>();
        if (row > 0) {

            //添加至notification表
            Notification notification = new Notification();
            notification.setRead(false);
            notification.setCommentPublishTime(publishTime);
            notification.setReceiverId(task.getPublisherId());
            LocalDateTime specificDataTime = LocalDateTime.of(2000,1,1,0,0,0);
            Timestamp specificTimeStamp = Timestamp.valueOf(specificDataTime);
            notification.setMessagePublishTime(specificTimeStamp);
            notification.setTaskId(0);
            notification.setType("comment");
            notification.setNotify_time(publishTime);
            int row1 = notificationMapper.insert(notification);

            WsServer wsServer = new WsServer();
            User user = userService.getById(uid);
            String message = comment.getReceiverId()+"|您收到一条来自用户:"+user.getUsername()+",关于任务"+comment.getTaskId()+"的新回复:"+comment.getContent();
            wsServer.sendMessageToSomeone(message);

            result.put("message", "评论成功！");
            result.put("commentatorId", comment.getCommentatorId()); // 添加评论者ID
        } else {
            result.put("message", "评论失败，请稍后重试！");
        }
        return result;
    }
    @PostMapping("/{task_id}/comment/createNested")
    public Map<String,Object> createNestedComment(HttpServletRequest request,@PathVariable("task_id")int taskId, @RequestBody NestedCommentDTO nestedCommentDTO){
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        Comment comment = new Comment();
        comment.setTaskId(taskId);
        comment.setCommentatorId(uid);
        comment.setContent(nestedCommentDTO.getContent());
        LocalDateTime now = LocalDateTime.now();
        Timestamp publishTime = Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant());
        comment.setPublishTime(publishTime);
        comment.setParentId(nestedCommentDTO.getPresentCommentId());
        Comment comment1 = commentMapper.selectById(nestedCommentDTO.getPresentCommentId());
        comment.setReceiverId(comment1.getCommentatorId());
        comment.setLikeNum(0);
        comment.setAncestorPublishTime(comment1.getAncestorPublishTime());
        int row = commentMapper.insert(comment);
        comment.setAncestorPublishTime(comment1.getAncestorPublishTime());
        Map<String, Object> result = new HashMap<>();
        if (row > 0) {

            //添加至notification表
            Notification notification = new Notification();
            notification.setRead(false);
            notification.setCommentPublishTime(publishTime);
            notification.setReceiverId(comment1.getCommentatorId());
            LocalDateTime specificDataTime = LocalDateTime.of(2000,1,1,0,0,0);
            Timestamp specificTimeStamp = Timestamp.valueOf(specificDataTime);
            notification.setMessagePublishTime(specificTimeStamp);
            notification.setTaskId(0);
            notification.setNotify_time(publishTime);
            notification.setType("comment");
            int row1 = notificationMapper.insert(notification);

            //发送消息提示
            WsServer wsServer = new WsServer();
            User user = userService.getById(uid);
            String message = comment1.getCommentatorId()+"|您收到一条来自用户:"+user.getUsername()+",关于任务"+comment.getTaskId()+"的新回复:"+comment.getContent();
            wsServer.sendMessageToSomeone(message);

            result.put("message", "评论成功！");
            result.put("commentatorId", nestedCommentDTO.getCommentatorId()); // 添加评论者ID
        } else {
            result.put("message", "评论失败，请稍后重试！");
        }
        return result;
    }
    @PostMapping("/{task_id}/comment/delete")
    public String deleteComment(HttpServletRequest request,@RequestBody Comment comment){
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        Comment commentSearch = commentMapper.selectById(comment.getId());
        if(Objects.equals(uid, commentSearch.getCommentatorId()))
        {
            commentMapper.deleteById(comment.getId());
            return "评论删除成功！";
        }else {
            return "删除失败！您无此权限！";
        }
    }
    //任务点赞功能
    @GetMapping("/{task_id}/like")
    public boolean Like(@PathVariable("task_id")int taskId)
    {
        Task task = taskService.getById(taskId);
        return task.getIsLike();
    }
    @PostMapping("/{task_id}/like")
    public Result isLikeUpdate(HttpServletRequest request,@PathVariable("task_id")int taskId)
    {
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        //根据taskId获得任务
        Task task = taskService.getById(taskId);
        boolean isLike = task.getIsLike();
        //获得takerId,再根据takerId增加taker的点赞数
        Integer takerId = task.getTakerId();
        if(takerId==uid){
            return Result.error("404","你不能给自己点赞");
        }
        if(takerId == null){
            return Result.error("405","任务未被接受");
        }
        if(takerId!=uid && isLike){
            return Result.error("414","你不能重复点赞");
        }
        if(takerId!=uid && isLike==false) {
            User user = userMapper.selectById(takerId);
            int like = user.getLikeCount();
            like = like + 1;
            User updateUser = new User();
            updateUser.setId(takerId);
            updateUser.setLikeCount(like);
            userMapper.updateById(updateUser); // 调用 updateById 方法
            UpdateWrapper<Task> updateWrapper1 = new UpdateWrapper<>();
            updateWrapper1.eq("id", taskId).set("is_like", true);
            taskService.update(updateWrapper1);
            List<Object>list = new ArrayList<>();
            list.add(true);
            list.add(like);
            Task test =new Task();
            test.setTakerId(like);
            test.setIsLike(false);
            return Result.success(list,"点赞成功");
        }
        return Result.error("403","未检测到操作");
    }
    @PostMapping("/{task_id}/not_like")
    public Result notLikeUpdate(HttpServletRequest request, @PathVariable("task_id")int taskId)
    {
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        //根据taskId获得任务
        Task task = taskService.getById(taskId);
        Integer takerId = task.getTakerId();
        Boolean isLike = task.getIsLike();
        //获得takerId,再根据takerId增加taker的点赞数
        if(takerId==uid){
            return Result.error("404","你不能对自己操作");
        }
        if(takerId == null){
            return Result.error("405","任务未被接受");
        }
        if (!isLike){
            return Result.error("415","错误请求");
        }
        if(takerId!=uid && isLike) {
            User user = userMapper.selectById(takerId);
            int like = user.getLikeCount();
            like = like - 1;
            User updateUser = new User();
            updateUser.setId(takerId);
            updateUser.setLikeCount(like);
            userMapper.updateById(updateUser); // 调用 updateById 方法
            UpdateWrapper<Task> updateWrapper1 = new UpdateWrapper<>();
            updateWrapper1.eq("id", taskId).set("is_like", false);
            taskService.update(updateWrapper1);
            List<Object>list = new ArrayList<>();
            list.add(false);
            list.add(like);
            return Result.success(list,"取消点赞成功");
        }
        return Result.error("403","未检测到操作");
    }
    //任务发布者和任务接收者删除或取消任务
    @PostMapping("/{task_id}/delete")
    public Result deleteTask(HttpServletRequest request, @PathVariable("task_id")int taskId)
    {
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        //根据taskId获得任务
        Task task1 = taskService.getById(taskId);
        //Task task = taskMapper.selectById(taskId);
        //获得takerId,再根据takerId增加taker的点赞数
        Integer publisherId = task1.getPublisherId();
        Integer takerId = task1.getTakerId();
        System.out.println(publisherId);
        System.out.println(uid);
        System.out.println(takerId);
        if(publisherId==uid){
            QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", taskId);
            String state = task1.getState();
            boolean result = taskService.remove(queryWrapper); // 调用 remove 方法
            if (result) {
                //如果task状态为incomplete会扣除任务发布者5经验值惩罚
                if(state.equals("incomplete")){
                    // 假设有一个 User 实体对象，设置更新字段为 email，根据 ID 更新
                    User user = userMapper.selectById(publisherId);
                    int flag=1;
                    int exp=user.getExp();
                    System.out.println(exp);
                    exp -=5;
                    User updateEntity = new User();
                    if(exp<0){
                        exp=0;
                        updateEntity.setExp(exp);
                        flag=0;
                    }
                    System.out.println(exp+"  falg");
                    if(flag==1) {
                        updateEntity.setExp(exp);
                    }
                    QueryWrapper<User> whereWrapper = new QueryWrapper<>();
                    whereWrapper.eq("id", publisherId);
                    boolean result2 = userService.update(updateEntity, whereWrapper);
                    if (result2) {
                        System.out.println("Record updated successfully.");
                        HashMap<Integer,Integer>map =new HashMap<Integer,Integer>();
                        map.put(publisherId,exp);
                        if(flag==0){

                            //新增notification记录——》向接单者发送取消消息
                            Notification notification = new Notification();
                            notification.setType("cancel");
                            LocalDateTime specificDataTime = LocalDateTime.of(2000,1,1,0,0,0);
                            Timestamp specificTimeStamp = Timestamp.valueOf(specificDataTime);
                            notification.setMessagePublishTime(specificTimeStamp);
                            notification.setMessagePublishTime(specificTimeStamp);
                            notification.setRead(false);
                            notification.setTaskId(taskId);
                            LocalDateTime now = LocalDateTime.now();
                            Timestamp publishTime = Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant());
                            notification.setNotify_time(publishTime);
                            notificationMapper.insert(notification);

                            //notification实时通知
                            WsServer wsServer = new WsServer();
                            wsServer.sendMessageToSomeone(task1.getTakerId()+"|您的id为："+taskId+"的任务已被取消，请联系发布者协商");


                            return  Result.success(map,"你发布的任务已删除,但由于删除已接受任务，扣5经验，且你的经验值已经为0");
                        }
                        if(flag==1){
                            return Result.success(map,"你发布的任务已删除,但由于删除已接受任务，扣5经验");
                        }
                    } else {
                        System.out.println("Failed to update record.");
                    }
                }
                System.out.println("Record deleted successfully.");
                return Result.success("你发布的任务已删除");
            } else {
                System.out.println("Failed to delete record.");
                return Result.error("406","删除失败");
            }

        }
        if (takerId == uid) {
            User user = userMapper.selectById(takerId);
            int exp = user.getExp() - 5;

            User updateEntity = new User();
            updateEntity.setId(takerId);
            updateEntity.setExp(Math.max(0, exp)); // 若经验值小于0，则设置为0

            int flag = exp < 0 ? 0 : 1; // 根据经验值是否小于0来设置flag
            //设置exp小于0为0，可以让前台接受
            if(exp<0){
                exp = 0;
            }
            UpdateWrapper<Task> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", taskId)
                    .set("state", "un-taken")
                    .set("take_time",null)
                    .set("taker_id", null);

            //新增notification记录——》向发布者发送状态变更消息
            Notification notification = new Notification();
            notification.setType("task");
            LocalDateTime specificDataTime = LocalDateTime.of(2000,1,1,0,0,0);
            Timestamp specificTimeStamp = Timestamp.valueOf(specificDataTime);
            notification.setMessagePublishTime(specificTimeStamp);
            notification.setMessagePublishTime(specificTimeStamp);
            notification.setRead(false);
            notification.setTaskId(taskId);
            LocalDateTime now = LocalDateTime.now();
            Timestamp publishTime = Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant());
            notification.setNotify_time(publishTime);
            notificationMapper.insert(notification);

            //notification实时通知
            WsServer wsServer = new WsServer();
            wsServer.sendMessageToSomeone(task1.getPublisherId()+"|您的订单状态变更为：未接单");


            boolean result = taskService.update(updateWrapper);

            if (result) {
                // 更新用户经验值
                userMapper.updateById(updateEntity); // 不论flag为何值都更新数据库中的用户信息
                HashMap<Integer,Integer>map =new HashMap<Integer,Integer>();
                map.put(takerId,exp);
                String message;
                if (flag == 0) {
                    message = "你接受的任务已取消，经验值减5，且当前经验值已经为0";
                } else {
                    message = "你接受的任务已取消，经验值减5";
                }

                System.out.println("Record updated successfully.");
                return Result.success(map,message);
            } else {
                System.out.println("Failed to update record.");
                return Result.error("更新失败", "未能成功取消任务");
            }
        }
        return Result.error("403","未检测到操作");
    }
    @PostMapping("/{task_id}/requestConfirm")
    public Result requestConfirm(HttpServletRequest request, @PathVariable("task_id") int taskId)
    {
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        //根据taskId获得任务
        Task task = taskService.getById(taskId);
        if(uid == task.getTakerId() )
        {
            LocalDateTime now = LocalDateTime.now();
            Timestamp nowTime = Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant());
            if(task.getDueTime().compareTo(nowTime) < 0)
            {
                return Result.error("402","任务已超时！");
            }
            UpdateWrapper<Task> updateWrapper= new UpdateWrapper<>();
            updateWrapper.eq("id",taskId).set("state","unconfirmed");
            int row = taskMapper.update(null,updateWrapper);
            if(row > 0)
            {

                //新增notification记录——》向发布者发送确认送达信息
                Notification notification = new Notification();
                notification.setType("task");
                LocalDateTime specificDataTime = LocalDateTime.of(2000,1,1,0,0,0);
                Timestamp specificTimeStamp = Timestamp.valueOf(specificDataTime);
                notification.setMessagePublishTime(specificTimeStamp);
                notification.setMessagePublishTime(specificTimeStamp);
                notification.setRead(false);
                notification.setTaskId(taskId);
                Timestamp publishTime = Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant());
                notification.setNotify_time(publishTime);
                notificationMapper.insert(notification);

                //notification实时通知
                WsServer wsServer = new WsServer();
                wsServer.sendMessageToSomeone(task.getPublisherId()+"|您id为"+task.getId()+"订单已送达，等待确认，订单金额为："+task.getReward());


                return Result.success("已提交完成申请");
            }
            return Result.error("403","提交失败");
        }
        return Result.error("401","无权限");
    }
    @PostMapping("/{task_id}/confirm")
    public Result confirm(HttpServletRequest request,@PathVariable("task_id") int taskId)
    {
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        Task task = taskService.getById(taskId);
        User user = userService.getById(uid);
        int takerId = task.getTakerId();
        if(uid == task.getPublisherId())
        {
            if(!Objects.equals(task.getState(), "unconfirmed"))
            {
                return Result.error("402","任务状态异常");
            }
            UpdateWrapper<Task> taskUpdateWrapper= new UpdateWrapper<>();
            taskUpdateWrapper.eq("id",taskId).set("state","complete");
            UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
            int exp = user.getExp();
            float balance = user.getBalance();
            exp += 8;
            balance += task.getReward();
            userUpdateWrapper.eq("id",takerId).set("exp",exp).set("balance",balance);
            int row = taskMapper.update(null,taskUpdateWrapper);
            if(row > 0)
            {

                //新增notification记录——》向接单者发送确认消息
                Notification notification = new Notification();
                notification.setType("complete");
                LocalDateTime specificDataTime = LocalDateTime.of(2000,1,1,0,0,0);
                Timestamp specificTimeStamp = Timestamp.valueOf(specificDataTime);
                notification.setMessagePublishTime(specificTimeStamp);
                notification.setMessagePublishTime(specificTimeStamp);
                notification.setRead(false);
                notification.setTaskId(taskId);
                LocalDateTime now = LocalDateTime.now();
                Timestamp publishTime = Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant());
                notification.setNotify_time(publishTime);
                notificationMapper.insert(notification);

                //notification实时通知
                WsServer wsServer = new WsServer();
                wsServer.sendMessageToSomeone(task.getTakerId()+"|您的id为："+task.getId()+"的订单已确认完成，订单金额为："+task.getReward());


                return Result.success("已确认任务完成");
            }
            return Result.error("403","确认失败");
        }
        return Result.error("401","无权限");
    }
    @PostMapping("/{task_id}/refuse")
    public Result refuse(HttpServletRequest request,@PathVariable("task_id") int taskId,@RequestBody Task t)
    {
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        Task task = taskService.getById(taskId);
        if(uid == task.getPublisherId())
        {
            if(!Objects.equals(task.getState(), "unconfirmed"))
            {
                return Result.error("402","任务状态异常");
            }
            UpdateWrapper<Task> updateWrapper= new UpdateWrapper<>();
            updateWrapper.eq("id",taskId).set("state","incomplete").set("dueTime",t.getDueTime());
            int row = taskMapper.update(null,updateWrapper);
            if(row > 0)
            {

                //新增notification记录——》向接单者发送拒绝确认消息
                Notification notification = new Notification();
                notification.setType("reject");
                LocalDateTime specificDataTime = LocalDateTime.of(2000,1,1,0,0,0);
                Timestamp specificTimeStamp = Timestamp.valueOf(specificDataTime);
                notification.setMessagePublishTime(specificTimeStamp);
                notification.setMessagePublishTime(specificTimeStamp);
                notification.setRead(false);
                notification.setTaskId(taskId);
                LocalDateTime now = LocalDateTime.now();
                Timestamp publishTime = Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant());
                notification.setNotify_time(publishTime);
                notificationMapper.insert(notification);

                //notification实时通知
                WsServer wsServer = new WsServer();
                wsServer.sendMessageToSomeone(task.getTakerId()+"|您的id为："+task.getId()+"的订单被拒绝确认，请继续配送");


                return Result.success("已拒绝任务完成申请，任务将继续");
            }
            return Result.error("403","拒绝失败");
        }
        return Result.error("401","无权限");
    }
    @PostMapping("/{task_id}/comment/like")
    public Result commentIsLike(HttpServletRequest request, @RequestBody Comment c) {
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        int commentId = c.getId();
        //flag用于最后判断是点赞还是取消点赞，1为点赞，0为取消
        int flag=1;
        // 查询是否已有对应的点赞记录
        QueryWrapper<CommentLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("comment_id", commentId)
                .eq("user_id", uid);
        CommentLike existingLike = commentLikeMapper.selectOne(queryWrapper);
        if (existingLike == null) {
            // 如果记录不存在，则插入新记录
            // 创建CommentLike对象
            CommentLike commentLike = new CommentLike();
            commentLike.setUserId(uid);
            commentLike.setCommentId(commentId);
            commentLike.setIsLike(true); // 设置为true表示点赞
            commentLikeMapper.insert(commentLike);
        } else {
            // 如果记录已存在，则检查是否已经点赞过
            if (existingLike.getIsLike()) {
                // 如果已经点赞，则更新isLike为false
                existingLike.setIsLike(false);
                commentLikeMapper.updateById(existingLike);
                flag = 0;
            } else {
                // 如果之前没有点赞，则更新isLike为true
                existingLike.setIsLike(true);
                commentLikeMapper.updateById(existingLike);
            }
        }
        // 根据commentId获取评论，并更新评论的点赞数,根据flag判断点赞或取消点赞,isLike用于返回给前端点赞或取消点赞操作
        Comment comment = commentMapper.selectById(commentId);
        int like = comment.getLikeNum();
        Boolean isLike = true;
        if (flag==1) {
            like++;
        }
        else {
            isLike = false;
            like--;
        }
        // 更新点赞数到Comment表中
        UpdateWrapper<Comment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", commentId).set("like_num", like);
        boolean result = commentService.update(updateWrapper);
        if (result) {
            // 点赞成功后，返回给前端true和点赞数，以及该评论对应的Id
            List<Object> list = new ArrayList<>();
            list.add(isLike);
            list.add(like);
            HashMap<Integer, List<Object>> map = new HashMap<>();
            map.put(commentId, list);
            return Result.success(map, "操作成功");
        } else {
            return Result.error("414", "操作失败");
        }
    }
}