package com.piesat.skywalking.service.mq;

import com.piesat.common.grpc.config.SpringUtil;
import com.piesat.common.server.WebSocketServer;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class MsgListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            WebSocketServer webSocketServer = SpringUtil.getBean(WebSocketServer.class);
            webSocketServer.sendMessage(message.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Message received: " + message.toString());
    }

}
