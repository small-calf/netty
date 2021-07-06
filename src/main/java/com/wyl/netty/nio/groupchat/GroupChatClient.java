package com.wyl.netty.nio.groupchat;

/**
 * @author calf
 * @version 1.0
 * @date
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * 群聊系统客户端
 */
public class GroupChatClient {
    //定义相关的属性
    private final String HOST = "127.0.0.1";//服务器ip
    private final int PORT = 6667;//服务器端口
    private Selector selector;
    private SocketChannel socketChannel;
    private String username;

    //构造器 完成初始化
    public GroupChatClient () throws IOException {
        selector = Selector.open();
        //连接服务器

        socketChannel = socketChannel.open(new InetSocketAddress(HOST,PORT));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        //得到username
        username = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println(username + "is ok ...");
    }

    //像服务器发送消息
    private void sendInfo(String info) {
        info = username + " 说 " + info;
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    //从服务器端回复的消息
    public void readInfo() {
        try {
            int readChannels = selector.select();
            if (readChannels > 0) {
                //有可用通道，可以读
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        //得到相关通道
                        SocketChannel sc = (SocketChannel) key.channel();
                        //得到buffer
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        //读取
                        sc.read(buffer);
                        //将缓存区的书数据转换成字符串
                        String msg = new String(buffer.array());
                        System.out.println(msg.trim());

                    }

                }
                iterator.remove();//删除当前的key
            }else  {
                //System.out.println("没有可用通道...");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception{
        //启动我们的客户端
        GroupChatClient chatClient = new GroupChatClient();
        //启动一个线程  每隔3秒  读取从服务器发送的数据
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    chatClient.readInfo();
                    try {
                        Thread.currentThread().sleep(3000);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        //客户端发送数据给服务器
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            chatClient.sendInfo(s);
        }
    }
}
