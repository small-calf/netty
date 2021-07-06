package com.wyl.netty.nio.groupchat;/**
 * @Auther:calf
 * @Date:2021/6/14
 * @Description:netty
 * @version:1.0
 */

import org.springframework.expression.spel.ast.Selection;

import java.awt.print.Pageable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 群聊系统服务端
 **/
public class GroupChatServer {

    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static final int PORT = 6667;

    //初始化构造方法
    public GroupChatServer() {
        try {
            selector = Selector.open();
            listenChannel = ServerSocketChannel.open();
            listenChannel.configureBlocking(false);
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    //监听
    public void listen() {
        try {
            while (true) {
                int count = selector.select();
                if (count > 0) {
                    Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
                    while (selectionKeys.hasNext()) {
                        SelectionKey key = selectionKeys.next();

                        if (key.isAcceptable()) {
                            SocketChannel sc = listenChannel.accept();
                            sc.configureBlocking(false);
                            sc.register(selector, SelectionKey.OP_READ);
                            //提示：
                            System.out.println(sc.getRemoteAddress() + " 上线 ");

                        }
                        if (key.isReadable()) {
                           readData(key);
                        }
                        selectionKeys.remove();
                    }
                }else {
                    System.out.println("等待...");
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
        }
    }
    //读取客户端消息
    private void readData(SelectionKey key) {
        //定义socketChannel
        SocketChannel channel = null;
        try {
            //取到关联的channel
            channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count = channel.read(buffer);
            if (count > 0) {
                //把缓存区的数据转成字符串
                String msg = new String(buffer.array());
                //输出该消息
                System.out.println("from 客户端 " + msg);

                //向其他客户端转发消息(去掉自己)，单独封装成一个方法
                sendInfoToOtherClients(msg,channel);
            }
        }catch (IOException e) {
            try {
                System.out.println(channel.getRemoteAddress() + " 离线");
                //取消注册
                key.cancel();
                //关闭通道
                channel.close();
            }catch (IOException e2) {
                e2.printStackTrace();
            }

        }
    }

    //转发消息给其他客户端
    private void sendInfoToOtherClients(String msg, SocketChannel self) throws IOException{
        System.out.println("服务器转变消息中...");
        //遍历 所有注册到selector 上的 SocketChannel, 并排除self
        for (SelectionKey key: selector.keys()) {
            //通过key获取对应的socketChannel
            Channel targetChannel = key.channel();

            //排除自己
            if (targetChannel instanceof SocketChannel && targetChannel != self) {
                //转型
                SocketChannel dest = (SocketChannel) targetChannel;
                //将msg存储到buffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                //将buffer的数据写入到通道
                dest.write(buffer);
            }

        }
    }


    public static void main(String[] args) {
        //创建服务器对象
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();

    }
}
