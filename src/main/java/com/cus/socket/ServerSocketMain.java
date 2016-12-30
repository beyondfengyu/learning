package com.cus.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 演示Socket同步接收Timeout异常的场景
 * @author laochunyu
 * @version 1.0
 * @date 2016/12/30
 */
public class ServerSocketMain {

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(15999);
        Socket socket = serverSocket.accept();

        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        byte[] data = new byte[8];
        while (true) {
            inputStream.read(data);
            String mess = new String(data);
            System.out.println("receice mess:" + mess);
            System.out.println("handle mess start ....");
            // 模拟处理任务耗时，耗时必须大于客户端设置的timeout值
            TimeUnit.SECONDS.sleep(12);
            String result = "["+mess+"]: " +  new Date().toString();
            outputStream.write(result.getBytes());
            outputStream.flush();
            System.out.println("handle mess finish !!!  "+result);
        }
    }
}
