package com.cmze.configuration;

import com.cmze.security.JwtTokenReader;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import org.springframework.messaging.simp.config.ChannelRegistration;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenReader jwtTokenReader;

    public WebSocketConfig(@Lazy JwtTokenReader jwtTokenReader) {
        this.jwtTokenReader = jwtTokenReader;
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    String authHeader = accessor.getFirstNativeHeader("Authorization");

                    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);

                        if (jwtTokenReader.validateToken(token)) {
                            UUID userId = jwtTokenReader.getUserId(token);
                            List<String> roles = jwtTokenReader.getRoles(token);

                            List<GrantedAuthority> authorities = roles.stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList());

                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

                            accessor.setUser(authentication);
                        }
                    }
                }
                return message;
            }
        });
    }
}
