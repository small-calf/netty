package com.wyl.netty.nio;/**
 * @Auther:calf
 * @Date:2021/6/14
 * @Description:netty
 * @version:1.0
 */

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 客户端
 **/
public class NioClient {
    public static void main(String[] args) throws Exception{
        //得到一个网络通道
        SocketChannel socketChannel = SocketChannel.open();

        //设置非阻塞
        socketChannel.configureBlocking(false);
        //提供服务器端的ip和端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 666);
        //连接服务器
        if (!socketChannel.connect(inetSocketAddress)) {
            while (!socketChannel.finishConnect()) {
                System.out.println("因为连接需要时间，客户端不会阻塞，可以做其它工作");
            }
        }

        //如果连接成功就发送数据
        String str = "hello,calf第一次学习netty";
        //
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());

        //将buffer数据写入channel
        socketChannel.write(buffer);

        //socketChannel.close();

        System.in.read();

    }
}
