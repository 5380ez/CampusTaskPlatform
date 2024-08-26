package com.wuyanteam.campustaskplatform.service.ServiceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuyanteam.campustaskplatform.entity.Comment;
import com.wuyanteam.campustaskplatform.mapper.CommentMapper;
import com.wuyanteam.campustaskplatform.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl  extends ServiceImpl<CommentMapper, Comment> implements CommentService {
}