package com.exam.lpl.config;

import com.exam.lpl.entity.Order;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RabbitListener(queues = DelayRabbitConfig.ORDER_QUEUE_NAME)
public class DelayPeceiver {

    @RabbitHandler
    public void orderDelayQueue(Order order, Message message, Channel channel){
        System.out.println("###########################################");
        System.out.println("【orderDelayQueue 监听的消息】 - 【消费时间】 - ["
                +new Date()+"]- 【订单内容】 - ["+order.toString()+"]");

        if(order.getOrderStatus() == 0) {
            order.setOrderStatus(2);
            System.out.println("【该订单未支付，取消订单】" + order.toString());
        } else if(order.getOrderStatus() == 1) {
            System.out.println("【该订单已完成支付】");
        } else if(order.getOrderStatus() == 2) {
            System.out.println("【该订单已取消】");
        }
        System.out.println("###########################################");
    }
}
