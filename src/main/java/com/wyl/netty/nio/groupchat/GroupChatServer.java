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
                int count = selector.select(2000);
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
                            SocketChannel channel = (SocketChannel)key.channel();
                            ByteBuffer buffer = (ByteBuffer)key.attachment();
                            channel.read(buffer);
                            channel.close();;
                        }
                        selectionKeys.remove();
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {

        }
    }

    public static void main(String[] args) {

    }
}
