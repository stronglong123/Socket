package bhz.netty.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
    public static void main(String[] args) throws Exception {

        // 1 第一个线程组 用来接受客户端的
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 2 第二个线程组 处理实际业务
        EventLoopGroup workGroup = new NioEventLoopGroup();

        // 串讲一个辅助类bootstrap ，对我们的server进行一系列处理
        ServerBootstrap b = new ServerBootstrap();

        // 把两个工作线程组加入进来 指定使用nioserversocketchannel这种类型的通道
        b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                // 一定要使用childhandler去绑定具体的事务处理器
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc)
                            throws Exception {
                        sc.pipeline().addLast(new ServerHandler());
                    }
                    // 设置缓冲区
                }).option(ChannelOption.SO_BACKLOG, 128)
                // 保持连接
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        // 绑定接口，进行监听
        ChannelFuture f = b.bind(8765).sync();

        f.channel().closeFuture().sync();

        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
