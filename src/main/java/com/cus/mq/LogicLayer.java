package com.cus.mq;

import com.alibaba.rocketmq.client.consumer.*;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

/**
 * 逻辑层，从MQ中获取连接层的消息，并进行处理
 *
 * @author beyond
 * @date 2016/12/12
 */
public class LogicLayer {
    private static final Logger m_logger = LoggerFactory.getLogger(LogicLayer.class);
    private static int CONSUMER_NUM = 0;

    private DefaultMQPushConsumer pushConsumer;
    private DefaultMQPullConsumer pullConsumer;
    private MQPullConsumerScheduleService pullConsumerService;


    public LogicLayer initPushConsumer() throws MQClientException {
        pushConsumer = new DefaultMQPushConsumer(Constants.COMSUMER_GROUP_LOGIC);
        pushConsumer.setNamesrvAddr(Constants.NAME_SRV);
//        pushConsumer.setMessageModel(MessageModel.BROADCASTING);
        pushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        try {
            pushConsumer.subscribe(Constants.TOPIC_IM, Constants.BUSINESS_ALL);
        } catch (MQClientException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        pushConsumer.registerMessageListener(new MessageListenerConcurrently() {

            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                ++CONSUMER_NUM;
                for (MessageExt messageExt : msgs) {
                    try {
                        m_logger.info("[{}:{}] {}", CONSUMER_NUM, messageExt.getMsgId(),
                                new String(messageExt.getBody(), RemotingHelper.DEFAULT_CHARSET));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        pushConsumer.start();
        m_logger.info("PUSHCONSUMER START SUCCESS!!!");
        return this;
    }

    public void initPullConsumer() {
        pullConsumer = new DefaultMQPullConsumer(Constants.COMSUMER_GROUP_LOGIC);
        pullConsumer.setNamesrvAddr(Constants.NAME_SRV);

        pullConsumer.registerMessageQueueListener(Constants.TOPIC_IM, new MessageQueueListener() {
            public void messageQueueChanged(String topic, Set<MessageQueue> mqAll, Set<MessageQueue> mqDivided) {

            }
        });
    }

    public LogicLayer initPullConsumerService() throws MQClientException {
        pullConsumerService = new MQPullConsumerScheduleService(Constants.COMSUMER_GROUP_LOGIC);
        pullConsumerService.setMessageModel(MessageModel.BROADCASTING);
        pullConsumerService.getDefaultMQPullConsumer().setNamesrvAddr(Constants.NAME_SRV);

        pullConsumerService.registerPullTaskCallback(Constants.TOPIC_IM, new PullTaskCallback() {

            public void doPullTask(MessageQueue mq, PullTaskContext context) {
                MQPullConsumer consumer = context.getPullConsumer();
                try {
                    long offset = consumer.fetchConsumeOffset(mq, false);
                    offset = offset < 0 ? 0 : offset;
                    PullResult pullResult = consumer.pull(mq,Constants.PRODUCER_GROUP_CONN,offset,32);
//                    System.out.println(offset + "\t" + mq + "\t" + pullResult);
                    switch (pullResult.getPullStatus()) {
                        case FOUND:
                            ++CONSUMER_NUM;
                            for(MessageExt messageExt:pullResult.getMsgFoundList()){
                                try {
                                    m_logger.info("[{}:{}] {}", CONSUMER_NUM, messageExt.getMsgId(),
                                            new String(messageExt.getBody(), RemotingHelper.DEFAULT_CHARSET));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case NO_MATCHED_MSG:
                            break;
                        case NO_NEW_MSG:
                        case OFFSET_ILLEGAL:
                            break;
                        default:
                            break;
                    }
                    //更新消费偏移的位置
                    consumer.updateConsumeOffset(mq, pullResult.getNextBeginOffset());
                    //设置下次拉取的时间
                    context.setPullNextDelayTimeMillis(100);

                } catch (MQClientException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RemotingException e) {
                    e.printStackTrace();
                } catch (MQBrokerException e) {
                    e.printStackTrace();
                }
            }
        });
        pullConsumerService.start();
        m_logger.info("PULLCONSUMERSERVICE START SUCCESS!!!");
        return this;
    }

    public static void main(String[] agrs) throws MQClientException {
        LogicLayer logicLayer = new LogicLayer();
        logicLayer.initPushConsumer();
        logicLayer.initPullConsumerService();
    }

}
