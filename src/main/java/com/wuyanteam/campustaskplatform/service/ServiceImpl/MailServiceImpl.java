package com.wuyanteam.campustaskplatform.service.ServiceImpl;

import com.wuyanteam.campustaskplatform.Reposity.VcodeDao;
import com.wuyanteam.campustaskplatform.entity.Vcode;
import com.wuyanteam.campustaskplatform.utils.CodeGeneratorUtil;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailServiceImpl {

    @Resource
    private JavaMailSenderImpl mailSender;
    @Resource
    private VcodeDao vcode;
    public boolean mail(String email) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        //生成随机验证码
        String code= CodeGeneratorUtil.generateCode(6);
        while(vcode.findByCode(code)!=null){
            code= CodeGeneratorUtil.generateCode(6);
        }
        Vcode vcode1 = new Vcode();
        vcode1.setCode(code);
        vcode.save(vcode1);
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        //邮件信息
        helper.setText("<p style='color: black'>三秦锅，你在搞什么飞机！你的验证码为：" + code + "(有效期为一分钟)</p>", true);
        //主题名
        helper.setSubject("campustaskplatform验证码----验证码");
        //邮箱地址
        helper.setTo(email);
        //发送人邮箱
        helper.setFrom("2065343055@qq.com");
        mailSender.send(mimeMessage);
        return true;
    }
}