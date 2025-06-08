package cn.project.web.controller.system;

import cn.project.common.core.domain.AjaxResult;
import com.alibaba.fastjson.JSONObject;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class CustomerServiceController {


    @Autowired
    private RestTemplate restTemplate;


    @PostMapping("/message")
    public AjaxResult receiveMessage (@RequestBody AIMessageModel  aiMessageModel) {
        System.out.println("用户新闻的"+aiMessageModel);
//       请求体数据
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("message",  "请用中文回答，并且控制字数在50字以内："+aiMessageModel.getMessage());
        requestBody.put("mode", "chat");
        requestBody.put("userId", 1);
//  请求头数据
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.set("Authorization","Bearer 9Y56BH2-ZQ6MAK0-HG692Y6-B2MH68E");
        requestHeaders.set("accept", "application/json");
        HttpEntity<Map<String, Object>> r = new HttpEntity<Map<String, Object>>(requestBody, requestHeaders);

        String url = "http://localhost:3001/api/v1/workspace/ai/thread/679108c6-2db6-4dd8-acda-c21b174d07cf/chat";
        String content = restTemplate.postForObject(url, r, String.class);


        JSONObject jsonObject = JSONObject.parseObject(content);
        String aiAnswer = (String)jsonObject.get("textResponse");
        boolean contains = aiAnswer.contains("</think>");
        if(contains){
            aiAnswer = aiAnswer.split("</think>")[1];
        }else{
            aiAnswer = aiAnswer.split("<think>")[1];
        }
        return  AjaxResult.success(aiAnswer);
    }

}