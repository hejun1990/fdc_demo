package com.hejun.demo.service.inter.rocketmq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by hejun-FDC on 2017/5/18.
 */
@Service("rmqComsumer")
public class RMQComsumer {
    private static final Logger logger = LoggerFactory.getLogger(RMQComsumer.class);

    @Value("${mq.ip}")
    private String ip;

    @Value("${mq.topic}")
    private String topic;

    @PostConstruct
    private void comsumerStart() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("PushConsumer");
        consumer.setNamesrvAddr(ip);
        try {
            consumer.subscribe(topic, "TestRocketMQ");
            // 程序第一次启动从消息队列头取数据
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    try {
                        Message msg = msgs.get(0);
                        String content = new String(msg.getBody(), "UTF-8");
                        if ("testContent".equals(msg.getKeys())) {
                            logger.info("测试成功, 收到消息：{}", content);
                        }
                    } catch (UnsupportedEncodingException e) {
                        logger.error("MQClientException: {}", e.getMessage());
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();
        } catch (MQClientException e) {
            logger.error("MQClientException: {}", e.getMessage());
        }
    }
}
