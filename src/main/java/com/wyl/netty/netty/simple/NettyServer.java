package com.wyl.netty.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author calf
 * @version 1.0
 * @date
 */
public class NettyServer {
    public static void main(String[] args) {
        //创建BossGroup 和 workGroup
        //说明：
        //1、创建两个线程组 bossGroup 和 workGroup
        //2、bossGroup只处理连接请求，真正和客户端业务处理，会交给workGroup
        //3、两个都是无线循环
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup workGroup = new NioEventLoopGroup();

        //创建服务器端的启动对象，配置参数
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup,workGroup)//创建两个线程组
                 .channel(NioSocketChannel.class)//使用NioSocketChannel作为服务器的通道实现
                 .option(ChannelOption.SO_BACKLOG,128)//设置线程队列得到连接个数
                 .childOption(ChannelOption.SO_KEEPALIVE,true)//设置保持活动连接状态
                 .childHandler()//給我们的workgroup的EventLoop对应的管道
    }
}
