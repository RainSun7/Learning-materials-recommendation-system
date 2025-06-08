package cn.project.web.controller.common;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/{userId}") // 通过路径参数区分用户ID
@Component
public class ChatWebSocket {
    // 存储所有在线用户的Session（线程安全）
    public static ConcurrentHashMap<String, Session> onlineUsers = new ConcurrentHashMap<>();

    // 连接建立时的逻辑
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        onlineUsers.put(userId, session);
        notifyAdminOnlineStatus(userId, true); // 通知管理端更新在线用户
    }

    // 接收消息的逻辑
    @OnMessage
    public void onMessage(String message, Session session) {
        // 消息格式示例：{"to":"admin123","content":"Hello"}
        JSONObject msgJson = JSON.parseObject(message);
        JSONObject payload = (JSONObject)msgJson.get("payload");
        String content = payload.getString("content");
        Long to = payload.getLong("to");
        Long from = payload.getLong("from");

        // 一对一发送消息
        if (onlineUsers.containsKey(to.toString())) {
            Session targetSession = onlineUsers.get(to.toString());
            if (targetSession != null && targetSession.isOpen()) {
                try {
                    //拼接Json ：currentUserId ,content 两个字段
                    JSONObject json = new JSONObject();
                    json.put("to", to);
                    json.put("content", content);
                    json.put("from", from);
                    json.put("time", new java.util.Date());
                    // 发送消息
                    targetSession.getBasicRemote().sendText(json.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 连接关闭时的逻辑
    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        onlineUsers.remove(userId);
        notifyAdminOnlineStatus(userId, false); // 通知管理端用户下线
    }


    private void sendMessageToUser(String targetUserId, String content,String from) {
        Session targetSession = onlineUsers.get(targetUserId);
        if (targetSession != null && targetSession.isOpen()) {
            try {
                //拼接Json ：currentUserId ,content 两个字段
                JSONObject json = new JSONObject();
                json.put("to", targetUserId);
                json.put("content", content);
                json.put("from", from);
                // 发送消息
                targetSession.getBasicRemote().sendText(json.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 通知管理端用户上下线状态变化
    private void notifyAdminOnlineStatus(String userId, boolean isOnline) {
//        Session adminSession = onlineUsers.get("admin"); // 假设管理端固定ID为"admin"
//        if (adminSession != null) {
//            String msg = String.format("{\"event\":\"status\",\"userId\":\"%s\",\"online\":%b}", userId, isOnline);
//            sendMessageToUser("admin", msg,null);
//        }
    }


}