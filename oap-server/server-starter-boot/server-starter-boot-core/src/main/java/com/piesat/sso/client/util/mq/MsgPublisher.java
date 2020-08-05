package com.piesat.sso.client.util.mq;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;

@Component
public class MsgPublisher {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ChannelTopic topic;

    public void sendMsg(String msg){
        redisTemplate.convertAndSend( topic.getTopic(), msg);
    }
}
