package com.exam.lpl.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DelayRabbitConfig {

    //TTL延时队列名称
    private static final String ORDER_DELAY_QUEUE = "user.order.delay.queue";
    /**
     * DLX，dead letter发送到的 exchange
     * 延时消息就是发送到该交换机的
     */
    public static final String ORDER_DELAY_EXCHANGE = "user.order.delay.exchange";

    /**
     * routing key 名称 路由键
     * 具体消息发送在该 routingKey 的
     */
    public static final String ORDER_DELAY_ROUTING_KEY = "order_delay";

    public static final String ORDER_QUEUE_NAME = "user.order.queue";
    public static final String ORDER_EXCHANGE_NAME = "user.order.exchange";
    public static final String ORDER_ROUTING_KEY = "order";

    /**
     * 死信接收队列
     */
    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE_NAME, true);
    }

    /**
     * 死信交换机
     * 将路由键和某模式进行匹配。此时队列需要绑定要一个模式上。
     * 符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词。因此“audit.#”能够匹配到“audit.irs.corporate”，但是“audit.*” 只会匹配到“audit.irs”。
     **/
    @Bean
    public TopicExchange orderTopicExchange() {
        return new TopicExchange(ORDER_EXCHANGE_NAME);
    }

    /**
     * 死信接收队列绑定交换机
     */
    @Bean
    public Binding orderBinding(@Qualifier("orderTopicExchange") TopicExchange orderTopicExchange,
                                @Qualifier("orderQueue") Queue orderQueue) {
        // TODO 如果要让延迟队列之间有关联,这里的 routingKey 和 绑定的交换机很关键
        return BindingBuilder.bind(orderQueue).to(orderTopicExchange).with(ORDER_ROUTING_KEY);
    }






    @Bean
    public Queue delayOrderQueue() {
        Map<String, Object> params = new HashMap<>();
        // x-dead-letter-exchange 声明了队列里的死信转发到的DLX名称，
        params.put("x-dead-letter-exchange", ORDER_EXCHANGE_NAME);
        // x-dead-letter-routing-key 声明了这些死信在转发时携带的 routing-key 名称。
        params.put("x-dead-letter-routing-key", ORDER_ROUTING_KEY);
        return new Queue(ORDER_DELAY_QUEUE, true, false, false, params);
    }

    /**
     * 需要将一个队列绑定到交换机上，要求该消息与一个特定的路由键完全匹配。
     * 这是一个完整的匹配。如果一个队列绑定到该交换机上要求路由键 “dog”，则只有被标记为“dog”的消息才被转发，
     * 不会转发dog.puppy，也不会转发dog.guard，只会转发dog。
     * @return DirectExchange
     */
    @Bean
    public DirectExchange orderDelayExchange() {
        return new DirectExchange(ORDER_DELAY_EXCHANGE);
    }

    /**
     * 延迟队列绑定交换机
     */
    @Bean
    public Binding dlxBinding(@Qualifier("orderDelayExchange") DirectExchange orderDelayExchange,
                              @Qualifier("delayOrderQueue") Queue delayOrderQueue) {
        return BindingBuilder.bind(delayOrderQueue).to(orderDelayExchange).with(ORDER_DELAY_ROUTING_KEY);
    }

}
