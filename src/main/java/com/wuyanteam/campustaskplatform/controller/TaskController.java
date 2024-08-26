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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    public Result taskUpdate(HttpServletRequest request, @PathVariable("task_id") int taskId)
    {
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
                        .select("com.content", "com.publish_time", "com.like_num", "u.username as publisherUsername","u1.username as receiverUsername","com.parent_id")
                        .innerJoin("`comment` com on com.task_id = t.id")
                        .innerJoin("`user` u on com.commentator_id = u.id")
                        .innerJoin("`user` u1 on com.receiver_id = u1.id")
                        .eq("t.id", taskId)
                        .orderByAsc("com.ancestor_publish_time")
                        .orderByAsc("com.publish_time"));

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
    //点赞功能
    @PostMapping("/{task_id}/like")
    public Result isLikeUpdate(HttpServletRequest request,@PathVariable("task_id")int taskId)
    {
        System.out.println(taskService);
        int uid = userService.InfoService(request.getHeader("Authorization")).getId();
        //根据taskId获得任务
        Task task1 = taskService.getById(taskId);
        //获得takerId,再根据takerId增加taker的点赞数
        Integer takerId = task1.getTakerId();
        System.out.println(takerId);
        System.out.println(uid);
        if(takerId==uid){
            return Result.error("404","你不能给自己点赞");
        }
        if(takerId == null){
            return Result.error("405","任务未被接受");
        }
        if(takerId!=uid) {
            User user = userMapper.selectById(takerId);
            int like = user.getLikeCount();
            System.out.println(like);
            like = like + 1;
            System.out.println(like);
            User updateUser = new User();
            updateUser.setId(takerId);
            updateUser.setLikeCount(like);
            int rows = userMapper.updateById(updateUser); // 调用 updateById 方法
            if (rows > 0) {
                System.out.println("User updated successfully.");
            } else {
                System.out.println("No user updated.");
            }
            return Result.success("点赞成功");
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
                if(state.equals("incomplete")){
                    // 假设有一个 User 实体对象，设置更新字段为 email，根据 ID 更新
                    User user = userMapper.selectById(publisherId);
                    int flag=1;
                    int exp=user.getExp();
                    System.out.println(exp);
                    exp -=5;
                    User updateEntity = new User();
                    if(exp<0){
                        updateEntity.setExp(0);
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
        if(takerId == uid){
            UpdateWrapper<Task> updateWrapper1 = new UpdateWrapper<>();
            updateWrapper1.eq("id", taskId).set("state", "un-taken");
            boolean result1 = taskService.update(updateWrapper1); // 调用 update 方法
            if (result1) {
                System.out.println("Record updated successfully.");
            } else {
                System.out.println("Failed to update record.");
            }
            return Result.success("你接受的任务已取消");
        }
        return Result.error("403","未检测到操作");
    }

}
