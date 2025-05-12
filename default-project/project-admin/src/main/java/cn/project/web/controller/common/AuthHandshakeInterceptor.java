package cn.project.web.controller.common;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        // 从请求头或参数获取Token（示例从URL参数获取）
        String token = ((ServletServerHttpRequest) request).getServletRequest()
                           .getParameter("token");
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, 
                              ServerHttpResponse response,
                              WebSocketHandler wsHandler,
                              Exception ex) {}
}