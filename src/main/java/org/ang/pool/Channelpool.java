package org.ang.pool;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class Channelpool {
    private static Map<String, Channel> channelpool = new HashMap<>();
    /**
     * 采用队列缓存处理高并发 防止HashMap 底层数组扩容时出现线程不安全事件
     */
    private static LinkedBlockingQueue<Map<String, Channel>> linkedBlockingQueue = new LinkedBlockingQueue<>();
    /**
     * 延时消息集合 后需要用物理储存
     */
    private static Map<String, LinkedList<String>> delayMap = new HashMap<>();

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                Map<String, Channel> m = linkedBlockingQueue.poll();
                if (m != null) {
                    channelpool.putAll(m);
                } else {
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
    };

    /**
     * 获得Channel
     *
     * @param key
     * @return
     */
    public static Channel getChannel(String key) {
        return channelpool.get(key);
    }

    /**
     * 设置队列交给linkedBlockingQueue 管理
     *
     * @param key
     * @param channel
     */
    public static void setChannel(String key, Channel channel) {
        Map<String, Channel> map = new HashMap<>();
        map.put(key, channel);
        linkedBlockingQueue.offer(map);
    }

    /**
     * 删除Channel
     *
     * @param key
     * @return
     */
    public static void deleteChannel(String key) {
        delayMap.remove(key);

    }

    public static void delayMessage(String key, String message) {
        LinkedList<String> linkedList = delayMap.get(key);
        if (linkedList == null) {
            linkedList = new LinkedList<String>();
        }
        linkedList.add(message);
        delayMap.put(key, linkedList);
    }

    public static LinkedList getDelayLinkedList(String key) {
        return delayMap.get(key);
    }

}
