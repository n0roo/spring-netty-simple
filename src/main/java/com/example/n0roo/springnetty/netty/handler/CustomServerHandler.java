package com.example.n0roo.springnetty.netty.handler;

import com.example.n0roo.springnetty.netty.ChannelRepository;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
@Slf4j
public class CustomServerHandler extends ChannelInboundHandlerAdapter {

	private final ChannelRepository channelRepository;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Assert.notNull(this.channelRepository, "[Assertion failed] - ChannelRepository is required; it must not be null");

		ctx.fireChannelActive();
		if (log.isDebugEnabled()) {
			log.debug(ctx.channel().remoteAddress() + "");
		}
		String channelKey = ctx.channel().remoteAddress().toString();
		channelRepository.put(channelKey, ctx.channel());

		ctx.writeAndFlush("Your channel key is " + channelKey + "\r\n");

		if (log.isDebugEnabled()) {
			log.debug("Binding Channel Count is {}", this.channelRepository.size());
		}

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		String stringMessage = (String) msg;
		if (log.isDebugEnabled()) {
			log.debug(stringMessage);
		}

		String[] splitMessage = stringMessage.split("::");

		if ( splitMessage.length != 2 ) {
			ctx.channel().writeAndFlush(stringMessage + "\n\r");
			return;
		}

		if ( channelRepository.get(splitMessage[0]) != null ) {
			channelRepository.get(splitMessage[0]).writeAndFlush(splitMessage[1] + "\n\r");
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error(cause.getMessage(), cause);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx){
		Assert.notNull(this.channelRepository, "[Assertion failed] - ChannelRepository is required; it must not be null");
		Assert.notNull(ctx, "[Assertion failed] - ChannelHandlerContext is required; it must not be null");

		String channelKey = ctx.channel().remoteAddress().toString();
		this.channelRepository.remove(channelKey);
		if (log.isDebugEnabled()) {
			log.debug("Binding Channel Count is " + this.channelRepository.size());
		}
	}
}
