package com.cmze.internal.websocket;

import org.springframework.stereotype.Component;

@Component
public class RoomHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler handler, Map<String, Object> attributes) {
        // Minimalnie: z nagłówka X-User-Id (później podmień na JWT)
        List<String> ids = request.getHeaders().get("X-User-Id");
        if (ids != null && !ids.isEmpty()) {
            attributes.put("userId", ids.get(0));
        }
        // TODO: jeśli masz JWT w Authorization: Bearer xxx – tutaj zdekoduj i włóż sub -> attributes.put("userId", sub)
        return true;
    }

    @Override public void afterHandshake(ServerHttpRequest req, ServerHttpResponse res,
                                         WebSocketHandler handler, Exception ex) { }
}
