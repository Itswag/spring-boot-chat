package cn.srblog.chat.config;

import cn.srblog.chat.controller.WebSocketServer;
import cn.srblog.chat.entity.User;
import cn.srblog.chat.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;

@Configuration
public class WebSocketConfig extends Configurator {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        User user=(User)httpSession.getAttribute("currentUser");
        sec.getUserProperties().put("user",user);
        super.modifyHandshake(sec, request, response);
    }

    @Autowired
    public void setUserService(IUserService iUserService) {
        WebSocketServer.userService = iUserService;
    }

}
