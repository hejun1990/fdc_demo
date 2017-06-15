package com.hejun.demo.service.inter.rocketmq;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by hejun-FDC on 2017/5/18.
 */
@Service("rmqProducer")
public class RMQProducer {
    private static final Logger logger = LoggerFactory.getLogger(RMQProducer.class);

    @Value("${mq.ip}")
    private String ip;

    @Value("${mq.maxbodysize}")
    private Integer maxBodySize;

    @Value("${mq.topic}")
    private String topic;

    @Value("${mq.tags}")
    private String tags;

    private DefaultMQProducer producer;

    @PostConstruct
    private void producerStart() throws Exception {
        this.producer = new DefaultMQProducer("MQProducer");
        this.producer.setNamesrvAddr(this.ip);
        this.producer.setMaxMessageSize(this.maxBodySize.intValue());
        this.producer.start();
    }

    public boolean sendMsg(String topic, String tags, String key, String msgContent) {
        try {
            Message message = new Message(topic, tags, key, msgContent.getBytes("UTF-8"));
            SendResult send = this.producer.send(message);
            logger.info(" status:{}, id:{} " + send.getSendStatus(), send.getMsgId());
            if ("send_ok".equalsIgnoreCase(send.getSendStatus().toString())) {
                return true;
            }
        } catch (Exception e) {
            logger.error("sendMsg", e);
        }
        return false;
    }

    public boolean sendMsg(String tags, String key, String msgContent) {
        try {
            Message message = new Message(this.topic, tags, key, msgContent.getBytes("UTF-8"));
            SendResult send = this.producer.send(message);
            logger.info(" status:{}, id:{} ", send.getSendStatus().toString(), send.getMsgId());
            if ("send_ok".equalsIgnoreCase(send.getSendStatus().toString())) {
                return true;
            }
        } catch (Exception e) {
            logger.error("sendMsg", e);
        }
        return false;
    }

    public boolean sendMsg(String key, String msgContent) {
        try {
            Message message = new Message(this.topic, this.tags, key, msgContent.getBytes("UTF-8"));
            SendResult send = this.producer.send(message);
            logger.info(" status:{}, id:{} " + send.getSendStatus(), send.getMsgId());
            if ("send_ok".equalsIgnoreCase(send.getSendStatus().toString())) {
                return true;
            }
        } catch (Exception e) {
            logger.error("sendMsg", e);
            return false;
        }
        return false;
    }
}
