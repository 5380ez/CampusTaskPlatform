package com.wuyanteam.campustaskplatform.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wuyanteam.campustaskplatform.entity.*;
import com.wuyanteam.campustaskplatform.mapper.CommentMapper;
import com.wuyanteam.campustaskplatform.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin
@RequestMapping("/taskinformation")
public class TaskController {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private CommentMapper commentMapper;

    @GetMapping("/{task_id}")
    public Map<String, Object> taskInformation(@PathVariable("task_id") int taskId,
                                               @RequestParam(value = "page",defaultValue = "1") int currentPage) {


        // 查询任务信息
        List<UTT> taskInformation;
        taskInformation = taskMapper.selectJoinList(
                UTT.class,
                new MPJQueryWrapper<Task>()
                        .select("u1.username as publisherUsername", "u1.sex as publisherSex", "u1.phone as publisherPhone ", "u1.level as publisherLevel", "u2.username as takerUsername", "u2.sex as takerSex", "u2.level as takerLevel", "u2.phone as takerPhone",
                                "t.publisher_id", "t.taker_id", "t.publish_time", "t.state", "t.take_time", "t.reward", "t.campus",
                                "t.start_address", "t.end_address", "t.due_time", "t.title", "t.description")
                        //          "com.commentator_username","com.content","com.publish_time","com.like_num")
                        .innerJoin("`user` u1 on t.publisher_id = u1.id")
                        .leftJoin("`user` u2 on t.taker_id = u2.id")
                        //.leftJoin("`comment` com on t.id = com.task_id")
                        .eq("t.id", taskId)
        );


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


    @PostMapping("/{task_id}/comment")
    public Comment createComment(@PathVariable("task_id")int taskId, @RequestParam Integer parentId, @RequestParam String content, @RequestParam Integer commentatorId, @RequestParam Integer receiverId, @RequestParam Integer likeNum, @RequestParam Integer floorNum ){
        Comment comment = new Comment();
        comment.setTaskId(taskId);
        comment.setCommentatorId(commentatorId);
        comment.setContent(content);
        //comment.setPublishTime(publishTime);
        LocalDateTime now = LocalDateTime.now();
        Timestamp publishTime = Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant());
        comment.setPublishTime(publishTime);
       // comment.setParentId(parentId);
        comment.setReceiverId(receiverId);
        comment.setLikeNum(likeNum);
        int row = commentMapper.insert(comment);

        return comment;
    }

    @PostMapping("/{task_id}/comment/delete")
    public void deleteComment(){

    }
}
