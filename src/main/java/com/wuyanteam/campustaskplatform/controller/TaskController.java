package com.wuyanteam.campustaskplatform.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wuyanteam.campustaskplatform.entity.*;
import com.wuyanteam.campustaskplatform.mapper.CommentMapper;
import com.wuyanteam.campustaskplatform.mapper.TaskMapper;
import com.wuyanteam.campustaskplatform.mapper.UserMapper;
import com.wuyanteam.campustaskplatform.service.CommentService;
import com.wuyanteam.campustaskplatform.service.TaskService;
import com.wuyanteam.campustaskplatform.service.UserService;
import com.wuyanteam.campustaskplatform.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
                                "t.taker_id", "t.publish_time", "t.state", "t.take_time", "t.reward", "t.campus",
                                "t.start_address", "t.end_address", "t.due_time", "t.title", "t.description")
                        .innerJoin("`user` u1 on t.publisher_id = u1.id")
                        .leftJoin("`user` u2 on t.taker_id = u2.id")
                        .eq("t.id", taskId));
        return list;
    }
    @PostMapping("/{task_id}")
    public Result taskUpdate(HttpServletRequest request, @PathVariable("task_id") Integer taskId)
    {
        System.out.println(taskService);
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        UpdateWrapper<Task> updateWrapper = new UpdateWrapper<>();
        Task task = taskService.getById(taskId);
        if (task == null)
        {
            System.out.println("null");
        }
        System.out.println(task);
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
        return Result.success(task,"成功接单！");
    }
    //查看评论
    @GetMapping("/{task_id}/comment/{page}")
    public Map<String, Object> commentInformation(@PathVariable("task_id") int taskId, @PathVariable(value = "page") int currentPage)
    {
        IPage<CT> pagedComments;
        pagedComments = commentMapper.selectJoinPage(new Page<>(currentPage, 5),
                CT.class,
                new MPJQueryWrapper<Comment>()
                        .select("com.content", "com.publish_time", "com.like_num", "u.username as publisherUsername","u1.username as receiverUsername")
                        .innerJoin("`comment` com on com.task_id = t.id")
                        .innerJoin("`user` u on com.commentator_id = u.id")
                        .innerJoin("`user` u1 on com.receiver_id = u1.id")
                        .eq("t.id", taskId));
        Map<String, Object> result = new HashMap<>();
        result.put("comments", pagedComments.getRecords());
        result.put("totalPages", pagedComments.getPages());
        result.put("currentPage", currentPage);
        result.put("pageSize", 5);
        return result;
    }
    @PostMapping("/{task_id}/comment/create")
    public Map<String,Object> createComment(@PathVariable("task_id")int taskId,@RequestBody Comment comment){
        comment.setTaskId(taskId);
        comment.setCommentatorId(comment.getCommentatorId());
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
        Map<String, Object> result = new HashMap<>();
        if (row > 0) {
            result.put("message", "评论成功！");
            result.put("commentatorId", comment.getCommentatorId()); // 添加评论者ID
        } else {
            result.put("message", "评论失败，请稍后重试！");
        }
        return result;
    }
    @PostMapping("/{task_id}/comment/createNested")
    public Map<String,Object> createNestedComment(@PathVariable("task_id")int taskId, @RequestBody NestedCommentDTO nestedCommentDTO){
        Comment comment = new Comment();
        comment.setTaskId(taskId);
        comment.setCommentatorId(nestedCommentDTO.getCommentatorId());
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
        Map<String, Object> result = new HashMap<>();
        if (row > 0) {
            result.put("message", "评论成功！");
            result.put("commentatorId", nestedCommentDTO.getCommentatorId()); // 添加评论者ID
        } else {
            result.put("message", "评论失败，请稍后重试！");
        }
        return result;
    }
    @PostMapping("/{task_id}/comment/delete")
    public String deleteComment(HttpServletRequest request, @RequestBody Comment comment){
        Comment commentSearch = commentMapper.selectById(comment.getId());
        if(Objects.equals(userService.InfoService(request.getHeader("Authorization")).getId(), commentSearch.getCommentatorId()))
        {
            commentMapper.deleteById(comment.getId());
            return "评论删除成功！";
        }else {
            return "删除失败！您无此权限！";
        }
    }
    //任务点赞功能
    @PostMapping("/task_id/is_like")
    public Result isLikeUpdate(HttpServletRequest request, @RequestBody Task task)
    {
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();;
        UpdateWrapper<Task> updateWrapper = new UpdateWrapper<>();
        //根据taskId获得任务
        int taskId = task.getId();
        Task task1 = taskService.getById(taskId);
        Boolean isLike = task1.getIsLike();
        //Task task = taskMapper.selectById(taskId);
        //获得taskerId,再根据takerId增加taker的点赞数
        Integer taskerId = task1.getTakerId();
        System.out.println(taskerId);
        System.out.println(uid);
        System.out.println(isLike);
        if(taskerId==uid){
            return Result.error("404","你不能给自己点赞");
        }
        if(taskerId == null){
            return Result.error("405","任务未被接受");
        }
        if(taskerId!=uid && isLike==true){
            return Result.error("414","你不能重复点赞");
        }
        if(taskerId!=uid && isLike==false) {
            User user = userMapper.selectById(taskerId);
            int like = user.getLikeCount();
            System.out.println(like);
            like = like + 1;
            System.out.println(like);
            User updateUser = new User();
            updateUser.setId(taskerId);
            updateUser.setLikeCount(like);
            int rows = userMapper.updateById(updateUser); // 调用 updateById 方法
            if (rows > 0) {
                System.out.println("User updated successfully.");
            } else {
                System.out.println("No user updated.");
            }
            isLike = true;
            UpdateWrapper<Task> updateWrapper1 = new UpdateWrapper<>();
            updateWrapper1.eq("id", taskId).set("is_like", true);
            boolean result = taskService.update(updateWrapper1);
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
    //任务取消點贊
    @PostMapping("/task_id/not_like")
    public Result notLikeUpdate(HttpServletRequest request, @RequestBody Task task)
    {
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        UpdateWrapper<Task> updateWrapper = new UpdateWrapper<>();
        //根据taskId获得任务
        int taskId = task.getId();
        Task task1 = taskService.getById(taskId);
        Boolean isLike = task1.getIsLike();
        //Task task = taskMapper.selectById(taskId);
        //获得taskerId,再根据takerId增加taker的点赞数
        Integer taskerId = task1.getTakerId();
        System.out.println(taskerId);
        System.out.println(uid);
        if(taskerId==uid){
            return Result.error("404","你不能对自己操作");
        }
        if(taskerId == null){
            return Result.error("405","任务未被接受");
        }
        if (isLike == false){
            return Result.error("415","错误请求");
        }
        if(taskerId!=uid && isLike == true) {
            User user = userMapper.selectById(taskerId);
            int like = user.getLikeCount();
            System.out.println(like);
            like = like - 1;
            System.out.println(like);
            User updateUser = new User();
            updateUser.setId(taskerId);
            updateUser.setLikeCount(like);
            int rows = userMapper.updateById(updateUser); // 调用 updateById 方法
            if (rows > 0) {
                System.out.println("User updated successfully.");
            } else {
                System.out.println("No user updated.");
            }
            isLike = false;
            UpdateWrapper<Task> updateWrapper1 = new UpdateWrapper<>();
            updateWrapper1.eq("id", taskId).set("is_like", false);
            boolean result = taskService.update(updateWrapper1);
            List<Object>list = new ArrayList<>();
            list.add(false);
            list.add(like);
            return Result.success(list,"取消点赞成功");
        }
        return Result.error("403","未检测到操作");
    }
    //任务发布者和任务接收者删除或取消任务
    @PostMapping("/{task_id}/deleteTask")
    public Result deleteTask(HttpServletRequest request, @RequestBody Task task)
    {
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        UpdateWrapper<Task> updateWrapper = new UpdateWrapper<>();
        //根据taskId获得任务
        int taskId = task.getId();
        Task task1 = taskService.getById(taskId);
        //Task task = taskMapper.selectById(taskId);
        //获得taskerId,再根据takerId增加taker的点赞数
        Integer publisherId = task1.getPublisherId();
        Integer taskerId = task1.getTakerId();
        System.out.println(publisherId);
        System.out.println(uid);
        System.out.println(taskerId);
        if(publisherId==uid){
            QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", taskId);
            String state = task1.getState();
            boolean result = taskService.remove(queryWrapper); // 调用 remove 方法
            if (result) {
                if(state.equals("incomplete")){
                    // 假设有一个 User 实体对象，设置更新字段为 email，根据 ID 更新
                    User user = userMapper.selectById(publisherId);
                    int flag=1;
                    int exp=user.getExp();
                    System.out.println(exp);
                    exp -=5;
                    User updateEntity = new User();
                    updateEntity.setId(publisherId);
                    if(exp<0){
                        updateEntity.setExp(0);
                        int rows1 = userMapper.updateById(updateEntity);
                        flag=0;
                    }
                    System.out.println(exp+"  falg");
                    if(flag==1) {
                        int rows2 = userMapper.updateById(updateEntity);
                        updateEntity.setExp(exp);
                    }
                    QueryWrapper<User> whereWrapper = new QueryWrapper<>();
                    whereWrapper.eq("id", publisherId);
                    boolean result2 = userService.update(updateEntity, whereWrapper);
                    if (result2) {
                        System.out.println("Record updated successfully.");
                        if(flag==0){
                            return  Result.success("你发布的任务已删除,但由于删除已接受任务，扣5经验，且你的经验值已经为0");
                        }
                        if(flag==1){
                            return Result.success("你发布的任务已删除,但由于删除已接受任务，扣5经验");
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
        if(taskerId== uid){
            UpdateWrapper<Task> updateWrapper1 = new UpdateWrapper<>();
            updateWrapper1.eq("id", taskId).set("state", "un-taken");
            boolean result1 = taskService.update(updateWrapper1); // 调用 update 方法
            User user = userMapper.selectById(taskerId);
            int flag=1;
            int exp=user.getExp();
            System.out.println(exp);
            exp -=5;
            User updateEntity = new User();
            System.out.println(exp+"  falg");
            updateEntity.setId(taskerId);
            updateEntity.setExp(exp);
            int rows1 = userMapper.updateById(updateEntity);
            if(exp<0){
                updateEntity.setExp(0);
                int rows2 = userMapper.updateById(updateEntity);
                flag=0;
            }
            if (result1) {
                System.out.println("Record updated successfully.");
            } else {
                System.out.println("Failed to update record.");
            }
            if (flag==0){
                return Result.success("你接受的任务已取消,经验值扣除5,经验值已经为0");
            }
            return Result.success("你接受的任务已取消,经验值扣除5");

        }
        return Result.error("403","未检测到操作");
    }
    //评论点赞功能
    //点赞功能
    @PostMapping("/{task_id}/comment/isLike")
    public Result commentIsLike(HttpServletRequest request, @PathVariable("task_id") int taskId,@RequestBody Comment c )
    {
        int id = c.getId();
        System.out.println(id);
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        System.out.println(uid);
        //根据commentId获得评论
        Comment comment = commentMapper.selectById(id);
        //Task task = taskMapper.selectById(taskId);
        //获得taskerId,再根据takerId增加taker的点赞数
        int like = comment.getLikeNum();
        System.out.println(like);
        like = like + 1;
        System.out.println(like);
//        Comment updateComment = new Comment();
//        updateComment.setId(id);
//        System.out.println("运行标记1");
//        updateComment.setLikeNum(like);
//        System.out.println("运行标记2");
//        int rows = commentMapper.updateById(updateComment); // 调用 updateById 方法
//        if (rows > 0) {
//            System.out.println("User updated successfully.");
//            return Result.success("点赞成功");
//        } else {
//            System.out.println("No user updated.");
//            return Result.error("414","点赞失败");
//        }
        UpdateWrapper<Comment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("like_num", like);
        boolean result = commentService.update(updateWrapper); // 调用 update 方法
        if (result) {
            System.out.println("Record updated successfully.");
            return Result.success("点赞成功");
        } else {
            System.out.println("Failed to update record.");
            return Result.error("414","点赞失败");
        }
    }
    //取消点赞功能
    @PostMapping("/{task_id}/comment/notLike")
    public Result commentNotLike(HttpServletRequest request, @PathVariable("task_id") int taskId,@RequestBody Comment c )
    {
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        //根据commentId获得评论
        int id = c.getId();
        Comment comment = commentMapper.selectById(id);
        int like = comment.getLikeNum();
        System.out.println(like);
        like = like - 1;
        System.out.println(like);
        UpdateWrapper<Comment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("like_num", like);
        boolean result = commentService.update(updateWrapper); // 调用 update 方法
        if (result) {
            System.out.println("Record updated successfully.");
            return Result.success("取消点赞成功");
        } else {
            System.out.println("Failed to update record.");
            return Result.error("414","取消点赞失败");
        }
    }
}
