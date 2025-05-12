package cn.project.web.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//    @Autowired
//    private AuthHandshakeInterceptor authInterceptor;
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws/chat") // 暴露的WebSocket端点
//                .addInterceptors(authInterceptor) // 添加认证拦截器[6](@ref)
//                .setAllowedOriginPatterns("*")
//                .withSockJS();
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/topic", "/queue"); // 消息代理前缀
//        config.setApplicationDestinationPrefixes("/app"); // 客户端发送前缀
//        config.setUserDestinationPrefix("/user"); // 用户目标前缀[5](@ref)
//    }
}