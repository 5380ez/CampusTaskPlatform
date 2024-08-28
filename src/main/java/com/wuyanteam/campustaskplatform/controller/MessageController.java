package com.wuyanteam.campustaskplatform.controller;

import com.wuyanteam.campustaskplatform.entity.Message;
import com.wuyanteam.campustaskplatform.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController

@RequestMapping("/message")
public class MessageController
{
    @Autowired
    private MessageMapper messageMapper;

    @GetMapping("/createMessage")
    private void createMessage1(){
        Message message = new Message();
        LocalDateTime now = LocalDateTime.now();
        Timestamp publishTime = Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant());
        message.setSendTime(publishTime);
        message.setContent("test");
        message.setSenderId(10);
        message.setReceiverId(9);

        messageMapper.insert(message);

    }
}
