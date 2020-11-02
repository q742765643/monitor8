package com.piesat.sso.client.util.mq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class PubConfig {

    /**
     * 订阅发布的主题
     *
     * @return
     */
    @Bean
    ChannelTopic topic() {
        return new ChannelTopic("PUBSUB.WEBSOCKET");
    }
}
