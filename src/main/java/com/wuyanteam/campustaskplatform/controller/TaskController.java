package com.wuyanteam.campustaskplatform.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wuyanteam.campustaskplatform.entity.*;
import com.wuyanteam.campustaskplatform.mapper.CommentMapper;
import com.wuyanteam.campustaskplatform.mapper.TaskMapper;
import com.wuyanteam.campustaskplatform.service.UserService;
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
@RequestMapping("/taskInformation")
public class TaskController {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserService userService;

    @GetMapping("/{task_id}")
    public Map<String, Object> taskInformation(@PathVariable("task_id") int taskId,
                                               @RequestParam(value = "page",defaultValue = "1") int currentPage) {
        // 查询任务信息
        List<UTT> taskInformation;
        taskInformation = taskMapper.selectJoinList(
                UTT.class,
                new MPJQueryWrapper<Task>().select("u1.username as publisherUsername", "u1.sex as publisherSex",
                                "u1.phone as publisherPhone ", "u1.level as publisherLevel", "u2.username as takerUsername",
                                "u2.sex as takerSex", "u2.level as takerLevel", "u2.phone as takerPhone", "t.publisher_id",
                                "t.taker_id", "t.publish_time", "t.state", "t.take_time", "t.reward", "t.campus",
                                "t.start_address", "t.end_address", "t.due_time", "t.title", "t.description")
                        //          "com.commentator_username","com.content","com.publish_time","com.like_num")
                        .innerJoin("`user` u1 on t.publisher_id = u1.id")
                        .leftJoin("`user` u2 on t.taker_id = u2.id")
                        //.leftJoin("`comment` com on t.id = com.task_id")
                        .eq("t.id", taskId));


        IPage<CT> pagedComments;
        pagedComments = commentMapper.selectJoinPage(new Page<>(currentPage,5),
                CT.class,
                new MPJQueryWrapper<Comment>()
                        .select("com.content", "com.publish_time", "com.like_num", "u.username")
                        .innerJoin("`comment` com on com.task_id = t.id")
                        .innerJoin("`user` u on com.commentator_id = u.id")
                        .eq("t.id",taskId));
        Map<String,Object> result = new HashMap<>();
        result.put("taskInformation",taskInformation);
        result.put("comments",pagedComments.getRecords());
        result.put("totalPages",pagedComments.getPages());
        result.put("currentPage",currentPage);
        result.put("pageSize",5);

        return result;
    }

    @PostMapping("/{task_id}")
    public String createComment(@PathVariable("task_id")int taskId,@RequestBody CommentDTO commentDTO/*@RequestParam String content, @RequestParam Integer commentatorId*/){
        //
        //String token = request.getHeader("Authorization");
        Comment comment = new Comment();
        comment.setTaskId(taskId);
        comment.setCommentatorId(commentDTO.getCommentatorId());
        comment.setContent(commentDTO.getContent());
        LocalDateTime now = LocalDateTime.now();
        Timestamp publishTime = Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant());
        comment.setPublishTime(publishTime);
        comment.setAncestorPublishTime(publishTime);
        comment.setParentId(0);
        Task task = taskMapper.selectById(taskId);
        comment.setReceiverId(task.getPublisherId());
        comment.setLikeNum(0);
        int row = commentMapper.insert(comment);

        if(row > 0)
        {
            return "评论成功！";
        }else {
            return "评论失败，请稍后重试！";
        }

    }

    @PutMapping("/{task_id}")
    //presentCommentId refers to the comment user wants to reply
    public String createNestedComment(@PathVariable("task_id")int taskId, @RequestBody NestedCommentDTO nestedCommentDTO){
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

        if(row > 0)
        {
            return "回复成功！";
        }else {
            return "回复失败，请稍后重试！";
        }

    }

    @DeleteMapping("/{task_id}")
    public String deleteComment(HttpServletRequest request, @RequestBody DeleteCommentDTO deleteCommentDTO){

        Comment commentSearch = commentMapper.selectById(deleteCommentDTO.getCommentId());
        if(Objects.equals(userService.InfoService(request.getHeader("Authorization")).getId(), commentSearch.getCommentatorId()))
        {
            commentMapper.deleteById(deleteCommentDTO.getCommentId());
            return "评论删除成功！";
        }else {
            return "删除失败！您无此权限！";
        }
    }

}
