package com.example.n0roo.springnetty;

import com.example.n0roo.springnetty.config.NettyProperties;
import com.example.n0roo.springnetty.netty.ChannelRepository;
import com.example.n0roo.springnetty.netty.Server;
import com.example.n0roo.springnetty.netty.handler.CustomChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
@EnableConfigurationProperties(NettyProperties.class)
public class SpringNettyApplication {

	@Autowired
	private NettyProperties nettyProperties;


	@Autowired
	private CustomChannelInitializer customChannelInitializer;

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(SpringNettyApplication.class, args);
		Server server = configurableApplicationContext.getBean(Server.class);
		server.start();
	}

	@Bean(name = "serverBootstrap")
	public ServerBootstrap bootstrap() {
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup(), workerGroup())
			.channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.DEBUG))
			.childHandler(customChannelInitializer);
		Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();
		Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();
		for (@SuppressWarnings("rawtypes") ChannelOption option : keySet) {
			b.option(option, tcpChannelOptions.get(option));
		}
		return b;
	}

	@Bean
	public Map<ChannelOption<?>, Object> tcpChannelOptions() {
		Map<ChannelOption<?>, Object> options = new HashMap<>();
		options.put(ChannelOption.SO_BACKLOG, nettyProperties.getBacklog());
		return options;
	}

	@Bean(destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup bossGroup() {
		return new NioEventLoopGroup(nettyProperties.getBossCount());
	}

	@Bean(destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup workerGroup() {
		return new NioEventLoopGroup(nettyProperties.getWorkerCount());
	}

	@Bean
	public InetSocketAddress tcpSocketAddress() {
		return new InetSocketAddress(nettyProperties.getPort());
	}

	@Bean
	public ChannelRepository channelRepository() {
		return new ChannelRepository();
	}
}

