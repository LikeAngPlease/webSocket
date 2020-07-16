package org.ang;

import org.ang.service.NettyServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebsocketApplication {
    @Value("${nettyPort}")
    int nettyPort;

    public static void main(String[] args) {
        SpringApplication.run(WebsocketApplication.class, args);
        System.out.println("<(~︶~)> <(~︶~)> <(~︶~)> <(~︶~)> <(~︶~)> 启动完成");
    }

    @Bean
    public NettyServer getNettyServer() {
        return new NettyServer(nettyPort);
    }

}
