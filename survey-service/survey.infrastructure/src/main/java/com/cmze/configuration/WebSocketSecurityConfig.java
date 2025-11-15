package com.cmze.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {
    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {

        messages
                .simpDestMatchers("/app/**").authenticated()

                .simpSubscribeDestMatchers("/topic/**", "/queue/**").authenticated()

                // Domyślnie wiadomości systemowe (CONNECT, DISCONNECT) są dozwolone,
                // ale nasz interceptor i tak sprawdzi token przy CONNECT.
                // Można też jawnie zablokować:
                // .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.UNSUBSCRIBE, SimpMessageType.DISCONNECT).permitAll()
                .anyMessage().authenticated();

        return messages.build();
    }
}
