<%--
  Created by IntelliJ IDEA.
  User: 0
  Date: 2021/8/8
  Time: 17:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Java 后端 WebSocket 的 Tomcat 实现</title>
</head>
<body>
Welcome <br/>
<input id="text" type="text"/>
<button onclick="send()">发送消息</button>
<hr/>
<button onclick="closeWebSocket()">关闭 WebSocket 连接</button>
<hr/>
<div id="message"></div>
</body>
<script type="text/javascript">
    var websocket = null;
    // 判断 当前浏览器是否支持 WebSocket
    if ('WebSocket' in window) {
        websocket = new WebSocket("ws://localhost:7001/websocket");
    }
    else {
        alert('当前浏览器 Not support websocket ')
    }

    // 连接发生错误的回调方法
    websocket.onerror = function (event) {
        setMessageInnerHTML(" WebSocket 连接发生错误");
    };

    // 连接成功建立的回调方法
    websocket.onopen = function (event) {
        setMessageInnerHTML(" WebSocket 连接成功");
    }

    // 接收到消息的回调方法
    websocket.onmessage = function (event) {
        setMessageInnerHTML(event.data);
    }

    // 连接关闭的回调方法
    websocket.onclose = function (event) {
        setMessageInnerHTML(" WebSocket 连接关闭");
    }

    // 监听窗口关闭事件，当窗口关闭时，主动去关闭 websocket 连接，防止连接还没断开就关闭窗口，server 端会抛异常
    window.onbeforeunload = function (event) {
        closeWebSocket();
    }

    // 将消息显示在网页上
    function setMessageInnerHTML(innerHTML) {
        document.getElementById('message').innerHTML += innerHTML + '<br/>';
    }

    // 关闭 WebSocket 连接
    function closeWebSocket() {
        websocket.close();
    }

    // 发送消息
    function send() {
        var message = document.getElementById('text').value;
        websocket.send(message);
    }
</script>
</html>
