package com.cmze.configuration;

import com.cmze.internal.websocket.RoomChannelInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketInboundConfiguration implements WebSocketMessageBrokerConfigurer {

    private final RoomChannelInterceptor interceptor;

    public WebSocketInboundConfiguration(RoomChannelInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(interceptor);
    }
}

