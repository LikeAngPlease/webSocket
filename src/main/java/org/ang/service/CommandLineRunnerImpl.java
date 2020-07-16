package org.ang.service;


import io.netty.channel.Channel;
import org.ang.pool.Channelpool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 项目回调
 * 所有定时任务一律在此回调
 *
 * @author Liang
 */
@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
    private static final Logger logger = LogManager.getLogger(CommandLineRunnerImpl.class);
    @Autowired
    private NettyServer nettyServer;
    @Autowired
    private TaskExecutor threadPoolDefault;


    @Override
    public void run(String... args) {
        logger.info("|･ω･｀)|･ω･｀)|･ω･｀)|･ω･｀)|･ω･｀)|･ω･｀)|･ω･｀)|･ω･｀)|･ω･｀)项目加载后自动调用开始");
        threadPoolDefault.execute(nettyServer);
        Channelpool channelpool = new Channelpool();
        threadPoolDefault.execute(channelpool.runnable);

    }


}