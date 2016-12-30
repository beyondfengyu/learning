package com.cus.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

/**
 * 演示Socket同步接收Timeout异常的场景
 *
 * @author laochunyu
 * @version 1.0
 * @date 2016/12/30
 */
public class SocketMain {

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("127.0.0.1", 15999);
        socket.setSoTimeout(10*1000);
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();

        String request = "gettime1";
        outputStream.write(request.getBytes());
        outputStream.flush();
        System.out.println("client send request: gettime1");

        byte[] data = new byte[1024];
        try {
            System.out.println("client wait for result ...");
            inputStream.read(data);
        } catch (SocketTimeoutException e) {
            System.out.println("client read timeout, the socket is:"+(socket.isConnected() && !socket.isClosed()));
        }
        //等待一段时间，然后使用原Socket通道发送下一个请求
        TimeUnit.SECONDS.sleep(5);
        request = "gettime2";
        outputStream.write(request.getBytes());
        outputStream.flush();
        System.out.println("client send request: gettime2");
        //接收消息，应该是上一次请求的处理结果
        inputStream.read(data);
        System.out.println("client receive mess: " + new String(data));
        try {
            inputStream.read(data);
            System.out.println("client receive mess: " + new String(data));
        } catch (Exception e) {
            inputStream.read(data);
            System.out.println("client receive mess: " + new String(data));
        }
        TimeUnit.SECONDS.sleep(20);
    }
}
