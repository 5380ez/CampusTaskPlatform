package com.wuyanteam.campustaskplatform.controller;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wuyanteam.campustaskplatform.entity.*;
import com.wuyanteam.campustaskplatform.mapper.CommentMapper;
import com.wuyanteam.campustaskplatform.mapper.TaskMapper;
import com.wuyanteam.campustaskplatform.service.TaskService;
import com.wuyanteam.campustaskplatform.service.UserService;
import com.wuyanteam.campustaskplatform.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/{task_id}/comment")
    public Map<String, Object> commentInformation(@PathVariable("task_id") int taskId, @RequestParam(value = "page",defaultValue = "1") int currentPage)
    {
        IPage<CT> pagedComments;
        pagedComments = commentMapper.selectJoinPage(new Page<>(currentPage, 5),
                CT.class,
                new MPJQueryWrapper<Comment>()
                        .select("com.content", "com.publish_time", "com.like_num", "u.username")
                        .innerJoin("`comment` com on com.task_id = t.id")
                        .innerJoin("`user` u on com.commentator_id = u.id")
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
}
