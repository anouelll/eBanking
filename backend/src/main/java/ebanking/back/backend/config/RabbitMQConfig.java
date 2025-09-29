package ebanking.back.backend.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name}")
    private String transactionQueue;

    @Value("${rabbitmq.exchange.name}")
    private String transactionExchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Bean
    public Queue transactionQueue(){
        return new Queue(transactionQueue,true);
    }

    @Bean
    public TopicExchange transactionExchange(){
        return new TopicExchange(transactionExchange);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Binding binding(Queue transactionQueue, TopicExchange transactionExchange){
        return BindingBuilder.bind(transactionQueue).to(transactionExchange).with(routingKey);
    }
}


