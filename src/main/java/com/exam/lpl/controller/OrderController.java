package com.exam.lpl.controller;

import com.exam.lpl.config.DelayRabbitConfig;
import com.exam.lpl.entity.Order;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController  {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/send/{orderId}/{orderName}")
    public String sendOrder(@PathVariable("orderId") String orderId,
                            @PathVariable("orderName") String orderName){
        Order order=new Order();
        order.setOrderId(orderId);
        order.setOrderName(orderName);
        order.setOrderStatus(0);
        rabbitTemplate.convertAndSend(DelayRabbitConfig.ORDER_DELAY_EXCHANGE,DelayRabbitConfig.ORDER_DELAY_ROUTING_KEY,order,message -> {
            message.getMessageProperties().setExpiration(10000+"");
            return message;
        });
        return "下单成功!";
    }
}
