package com.mqInitializer.MqMessageGeneration.broker;

import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import com.ibm.mq.spring.boot.MQConnectionFactoryCustomizer;
import com.ibm.mq.spring.boot.MQConnectionFactoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.core.JmsTemplate;

import java.util.List;

@Configuration
@Slf4j
@EnableJms
public class JmsConfig implements JmsListenerConfigurer {

    private ObjectProvider<List<MQConnectionFactoryCustomizer>> factoryCustomizers;
    private DefaultJmsListenerContainerFactoryConfigurer configurer;
    private QueueProperties qmProperties;
    private ApplicationContext applicationContext;

    public JmsConfig(ObjectProvider<List<MQConnectionFactoryCustomizer>> factoryCustomizers,
                     DefaultJmsListenerContainerFactoryConfigurer configurer,
                     QueueProperties qmProperties,
                     ApplicationContext context) {
        this.factoryCustomizers = factoryCustomizers;
        this.configurer = configurer;
        this.qmProperties = qmProperties;
        this.applicationContext = context;
    }

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
        qmProperties.getConfigs().forEach(
                (name, properties) -> {
                    MQConnectionFactory connectionFactory = new MQConnectionFactoryFactory(
                            properties, factoryCustomizers.getIfAvailable()).createConnectionFactory(MQConnectionFactory.class);
                    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
                    configurer.configure(factory, connectionFactory);
                    if (!applicationContext.containsBean(name)) {
                        JmsTemplate jmsTemplate = new JmsTemplate();
                        jmsTemplate.setConnectionFactory(connectionFactory);
                        ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
                        beanFactory.registerSingleton(name, jmsTemplate);
                    }
                }
            );
    }
}
