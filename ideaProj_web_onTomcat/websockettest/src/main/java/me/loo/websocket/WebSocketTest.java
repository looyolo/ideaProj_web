package me.loo.websocket;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一 websocket 服务器端,
 * 注解的值将被用于监听用户连接的终端访问 URL 地址,客户端可以通过这个 URL 来连接到 WebSocket 服务器端
 */
@ServerEndpoint("/websocket")
public class WebSocketTest {
    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的
    private static int onlineCount = 0;

    // concurrent 包的线程安全 Set ，用来存放每个客户端对应的 MyWebSocket 对象。
    // 若要实现服务端与单一客户端通信的话，可以使用 Map 来存放，其中 Key 可以为用户标识
    private static CopyOnWriteArraySet<WebSocketTest> webSocketSet = new CopyOnWriteArraySet<WebSocketTest>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法
     * @param session  可选的参数。session 为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSocketSet.add(this);     // 加入 set 中
        addOnlineCount();           // 在线数加 1
        System.out.println("有一个新连接打开，已加 1 , 当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);  // 从 set 中移除
        subOnlineCount();           // 在线数减 1
        System.out.println("有一个连接关闭，已减 1 , 当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);
        // 群发消息
        for(WebSocketTest item: webSocketSet){
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException{
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        // 获取 在线连接数
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        // 在线连接数 加(add) 1
        WebSocketTest.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        // 在线连接数 减(subtract) 1
        WebSocketTest.onlineCount--;
    }

}