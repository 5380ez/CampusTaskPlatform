package com.wuyanteam.campustaskplatform.config;


import com.wuyanteam.campustaskplatform.entity.*;
import com.wuyanteam.campustaskplatform.mapper.MessageMapper;
import com.wuyanteam.campustaskplatform.mapper.NotificationMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static com.wuyanteam.campustaskplatform.entity.Message.messageMapper;
import static com.wuyanteam.campustaskplatform.entity.Notification.notificationMapper;

@Slf4j
@Component
@ServerEndpoint(value = "/websocket/{userId}")
//@ServerEndpoint("/")
public class WsServer{

    private Session session;

    private int userId;

    @Autowired
    public void setMessageMapper(MessageMapper messageMapper) {
        Message.messageMapper = messageMapper;
    }


    @Autowired
    public void setNotificationMapper(NotificationMapper notificationMapper){ Notification.notificationMapper = notificationMapper; }

    /**
     * 记录在线连接客户端数量
     */
    private static AtomicInteger onlineCount = new AtomicInteger(0);
    /**
     * 存放每个连接进来的客户端对应的websocketServer对象，用于后面群发消息
     */
    private static CopyOnWriteArrayList<WsServer> wsServers = new CopyOnWriteArrayList<>();

    private static ConcurrentHashMap<Integer,WsServer> clientConnections = new ConcurrentHashMap<>();

    /*
      服务端与客户端连接成功时执行
      @param session 会话
     */
    @OnOpen
    public void onOpen(Session session,@PathParam("userId") int userId){
        this.session = session;
        //接入的客户端+1
        int count = onlineCount.incrementAndGet();
        //集合中存入客户端对象+1
        wsServers.add(this);
        this.userId = userId;

        clientConnections.put(userId,this);


        log.info("与客户端连接成功，当前连接的客户端数量为：{}", count);
    }



    /*
     * 收到客户端的消息时执行
     * @param message 消息
     * @param session 会话
     */

    @OnMessage
    public void getMessage(String message) throws IOException {
        System.out.println("收到"+":"+message);
        //接收者
        String receiverId = message.split("[|]")[1];
        //消息
        String sendMessage = message.split("[|]")[0];

        createMessage(receiverId,sendMessage);



    }




    private void createMessage(String receiverId,String sendMessage) throws IOException {
        //将消息存入message表
        Message message = new Message();
        message.setSenderId(userId);
        message.setReceiverId(Integer.parseInt(receiverId));
        message.setContent(sendMessage);
        LocalDateTime now = LocalDateTime.now();
        Timestamp publishTime = Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant());
        message.setSendTime(publishTime);

        Notification notification = new Notification();
        notification.setTaskId(0);
        notification.setRead(false);
        notification.setType("message");
        LocalDateTime specificDataTime = LocalDateTime.of(2000,1,1,0,0,0);
        Timestamp specificTimeStamp = Timestamp.valueOf(specificDataTime);
        notification.setMessagePublishTime(publishTime);
        notification.setCommentPublishTime(specificTimeStamp);
        notification.setReceiverId(message.getReceiverId());
        notification.setNotify_time(publishTime);

        notificationMapper.insert(notification);
        messageMapper.insert(message);

        //将存储的消息转发给指定用户
        //forwardMessageToUser(Integer.parseInt(receiverId), message);
        Set<Map.Entry<Integer,WsServer>> entries = clientConnections.entrySet();
        for(Map.Entry<Integer,WsServer> entry : entries){
            if(entry.getKey().equals(Integer.parseInt(receiverId))){
                entry.getValue().send("用户"+ receiverId + ":" + sendMessage);
            }
        }

    }

    public void send(String message) throws IOException {
        if(session.isOpen()){
            session.getBasicRemote().sendText(message);
        }
    }




    @OnError
    public void onError(Session session, @NonNull Throwable throwable){
        log.error("连接发生报错");
        throwable.printStackTrace();
    }

    /*
     * 连接断开时执行
     */
    @OnClose
    public void onClose(){
        //接入客户端连接数-1
        int count = onlineCount.decrementAndGet();
        //集合中的客户端对象-1
        wsServers.remove(this);
        log.info("服务端断开连接，当前连接的客户端数量为：{}", count);
    }



    public void sendMessage(String message){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = now.format(formatter);

        if (this.session != null && this.session.isOpen()) {
            String message1 = currentTime +"    "+ message;
            this.session.getAsyncRemote().sendText(message1);
            log.info("推送消息给客户端:{}，回复内容为：{}", this.session.getMessageHandlers(), message);
        } else {
            log.warn("WebSocket session is not valid or has been closed.");
        }
    }


    /*
     * 群发消息
     * @param message 消息
     */

    public void sendMessageToSomeone(String message){
        String receiverId = message.split("[|]")[0];
        String sendMessage = message.split("[|]")[1];
        CopyOnWriteArrayList<WsServer> ws = wsServers;
        for (WsServer wsServer : ws){
            if(Integer.parseInt(receiverId) == wsServer.userId){
                wsServer.sendMessage(sendMessage);
            }
        }
    }



}

