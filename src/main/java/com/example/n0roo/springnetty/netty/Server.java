package com.example.n0roo.springnetty.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

@Data
@RequiredArgsConstructor
@Component
public class Server {

	private final ServerBootstrap serverBootstrap;
	private final InetSocketAddress inetSocketAddress;

	private Channel channel;

	public void start() throws Exception {
		channel = serverBootstrap.bind(inetSocketAddress).sync().channel().closeFuture().sync().channel();
	}

	@PreDestroy
	public void stop() {
		if (channel != null) {
			channel.close();
			channel.parent().close();
		}
	}
}
