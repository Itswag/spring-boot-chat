package cn.srblog.chat.controller;


import cn.srblog.chat.config.WebSocketConfig;
import cn.srblog.chat.entity.User;
import cn.srblog.chat.service.IUserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/websocket/",configurator = WebSocketConfig.class)
@Slf4j
@Component
public class WebSocketServer {

	private static Map<User,Session> clients = new  ConcurrentHashMap<>();
	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static Gson gson = new Gson();
	private Session session;
	private User user;
	private String RobotName="@小图图";
	/**
	 * 图灵机器人配置(V1.0)
	 */
	private final static String API_KEY  = "*";

	public static IUserService userService;
	/**
	 * 连接建立成功调用
	 * @param session
	 */
	@OnOpen
	public void onOpen(Session session, EndpointConfig config) throws IOException {
		session.setMaxTextMessageBufferSize(5242800);
		this.session = session;
		user = (User) config.getUserProperties().get("user");
		log.info(user.toString());
		clients.put(user, session);
		showOnLine(gson.toJson(clients.keySet()));

	}

	/**
	 * 连接关闭调用
	 */
	@OnClose
	public void onClose() throws IOException {
		if (user != null){
			clients.remove(user);
			showOnLine(gson.toJson(clients.keySet()));
		}
	}


	/**
	 * 收到客户端消息后调用的方法
	 * @param message 客户端发送过来的消息
	 * @throws IOException
	 */
	@OnMessage
	public void onMessage(String message) throws IOException {
		log.info(message);
		JsonObject result = new JsonParser().parse(message).getAsJsonObject();
		int type = result.get("type").getAsInt();
		String msg = result.get("msg").getAsString();
		if(type == 1) {
			String userId=result.get("userId").getAsString();
			User chaUser= userService.getById(userId);
			log.info(chaUser.equals(user)+"");
			Session chatSession=clients.get(chaUser);
			result.addProperty("date", DATE_FORMAT.format(new Date()));
			result.addProperty("name", user.getNickname());
			chatSession.getBasicRemote().sendText(result.toString());
		}else{
			result.addProperty("senderId",user.getId());
			result.addProperty("date", DATE_FORMAT.format(new Date()));
			result.addProperty("name", user.getNickname());
			//去除HTML标签
			String paramStr=msg.replaceAll("</?[^>]+>","");
			log.info("去除HTML标签后的内容:{}",paramStr);
			if (paramStr.startsWith(RobotName)) {
				msg= paramStr.substring(RobotName.length(), paramStr.length());
				log.info("截取后的内容:{}",msg);
				JsonObject jsonObject = robotSend(msg);
				result.add("robotMsg",jsonObject);
			}
				for (Map.Entry<User, Session> entry : clients.entrySet()) {
					result.addProperty("isSelf",entry.getValue().equals(clients.get(user)));
					entry.getValue().getAsyncRemote().sendText(result.toString());
				}

	    }
	}


	/**
	 * 获取机器人返回信息
	 * @param message
	 * @return
	 */
	public JsonObject robotSend(String message) {

		MultiValueMap<String,Object> map = new LinkedMultiValueMap<>();
		String url = "http://www.tuling123.com/openapi/api";
		map.add("key", API_KEY);
		map.add("info", message);
		map.add("userId",user.getId());
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		String body = restTemplate.postForObject(url, map, String.class);
		JsonObject robotResult = new JsonParser().parse(body).getAsJsonObject();
		return robotResult;
	}

	/**
	 * 显示在线列表
	 * @param message
	 * @throws IOException
	 */
	public static void showOnLine(String message) throws IOException{
		for (Map.Entry<User, Session> entry : clients.entrySet()) {
			entry.getValue().getBasicRemote().sendText(message);
		}

	}


	/**
	 * 发送信息
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String message) throws IOException {
		this.session.getBasicRemote().sendText(message);
	}

	/**
	 * 发生错误时调用
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) throws IOException {
		log.info("发生错误");
		onClose();
		error.printStackTrace();		
	}


}
