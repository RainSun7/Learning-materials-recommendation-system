package cn.project.web.controller.system;

import java.io.Serializable;

public class AIMessageModel implements Serializable {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AIMessageModel{" +
                "message='" + message + '\'' +
                '}';
    }
}
