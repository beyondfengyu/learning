package com.cus.mq;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;
import com.alibaba.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 连接层，把接收到的消息通过MQ传递给逻辑层
 *
 * @author beyond
 * @date 2016/12/12
 */
public class ConnLayer {
    private DefaultMQProducer producer;

    public void init(){
        producer = new DefaultMQProducer(Constants.PRODUCER_GROUP_CONN);
        producer.setNamesrvAddr(Constants.NAME_SRV);
        try {
            producer.start();
        } catch (MQClientException e) {
            System.exit(-1);
        }
    }

    public String getMessage(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        String dateStr = sdf.format(new Date());
        return "Time:"+dateStr;
    }

    public SendResult pushMessage(String message){
        Message msg = null;
        try {
            msg = new Message(Constants.TOPIC_CONN,// topic
                    Constants.TAG_CONN,            // tag
                    message.getBytes(RemotingHelper.DEFAULT_CHARSET)// body
            );
            SendResult sendResult = producer.send(msg);
            System.out.println("["+message+"] sendResult:" + sendResult);
            return sendResult;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws InterruptedException {
        ConnLayer connLayer = new ConnLayer();
        connLayer.init();
        for(;;) {
            String message = connLayer.getMessage();
            connLayer.pushMessage(message);
            TimeUnit.SECONDS.sleep(2);
        }

    }
}
