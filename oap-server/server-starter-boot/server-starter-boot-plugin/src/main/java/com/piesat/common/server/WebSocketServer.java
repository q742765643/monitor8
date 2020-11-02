package com.piesat.common.server;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint("/webSocket/{userId}")
public class WebSocketServer {
    //记录在线人数
    private static AtomicInteger onlineNum = new AtomicInteger();
    private static ConcurrentHashMap<Session, String> sessionPools = new ConcurrentHashMap<>();
    private Session session;

    public static void addOnlineCount() {
        onlineNum.incrementAndGet();
    }

    public static void subOnlineCount() {
        onlineNum.decrementAndGet();
    }

    @OnOpen
    public void open(Session session, @PathParam(value = "userId") String userId) {
        this.session = session;
        sessionPools.put(session, userId);
        addOnlineCount();
        System.out.println("当前在线人数为" + onlineNum);

    }

    public void sendMessage(String content) throws IOException {
        sessionPools.forEach((k, v) -> {
            try {
                k.getBasicRemote().sendObject(content);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncodeException e) {
                e.printStackTrace();
            }
        });

    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {

    }

    @OnClose
    public void onClose() {
        sessionPools.remove(session);
        subOnlineCount();
        System.out.println("当前在线人数为" + onlineNum);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("发生错误");
        throwable.printStackTrace();
    }

}
