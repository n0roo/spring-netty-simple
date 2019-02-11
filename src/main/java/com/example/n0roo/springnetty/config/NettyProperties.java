package com.example.n0roo.springnetty.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "netty")
public class NettyProperties {
	private int port;

	private int bossCount;

	private int workerCount;

	private boolean keepAlive;

	private int backlog;
}