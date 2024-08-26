package com.wuyanteam.campustaskplatform.controller;

import com.wuyanteam.campustaskplatform.service.ServiceImpl.MailServiceImpl;
import com.wuyanteam.campustaskplatform.utils.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.mail.MessagingException;
@RestController
@CrossOrigin //允许该控制器跨域

public class EmailController {
    @Resource
    private MailServiceImpl mailService;

    @GetMapping(value = "sendEmail/{email}")
    public Result<Object> sendCode(@PathVariable String email) {
        try {
            mailService.mail(email);
            return Result.success("验证码发送成功！");
        } catch (MessagingException e) {
            return Result.error("发送邮件失败，请稍后再试");
        }
    }
}
