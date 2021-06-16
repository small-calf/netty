package com.wyl.netty.nio;/**
 * @Auther:calf
 * @Date:2021/6/13
 * @Description:netty
 * @version:1.0
 */

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 服务端
 **/
public class NioServe {
    public static void main(String[] args) throws Exception{
        //创建ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //得到selector对象
        Selector selector = Selector.open();

        //绑定一个端口666，在服务端监听
        serverSocketChannel.socket().bind(new InetSocketAddress(666));

        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        //将serverSocketChannel注册到selector  关心 事件为 OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //循环等待客户端连接
        while (true) {
            //阻塞一秒种，如果没有事件发生则返回
            if (selector.select(1000) == 0) {//没有任何事件发生
                System.out.println("服务器等待了一秒，无连接");
                continue;
            }
            //如果返回的大于0,获取到相关的selectorKey集合
            //selector.selectedKeys();返回关注事件的集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            //selectionKeys 获取反向通道
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                //获取道到SelectionKey
                SelectionKey key = keyIterator.next();
                //根据key,对应的通道发生的事件做相应的处理
                if (key.isAcceptable()) {//如果是OP_ACCEPT，有新的客户端连接
                    //给该客户端生成一个socketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();

                    //将socketChannel设置为非阻塞
                    socketChannel.configureBlocking(false);

                    System.out.println("客户端连接成功，生成了一个socketChannel " + socketChannel.hashCode());

                    //将当前的socketChannel注册到selector,关注事件为OP_READ，同时给他关联一个Buffer
                    socketChannel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                if (key.isReadable()) {//发生OP_READ
                    //通过key 反向获取对应的channel
                    SocketChannel channel = (SocketChannel)key.channel();
                    //获取到该channel关联的buffer
                    ByteBuffer buffer = (ByteBuffer)key.attachment();
                    channel.read(buffer);
                    channel.close();
                    System.out.println("from 客户端 " + new String(buffer.array()));
                }
                //手动从集合中移除当前selectionKey，防止重复操作
                keyIterator.remove();
            }


        }



    }
}
