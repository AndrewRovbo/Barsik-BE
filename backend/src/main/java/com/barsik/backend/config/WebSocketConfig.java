package com.barsik.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.barsik.backend.security.CustomHandshakeHandler;
import com.barsik.backend.security.JwtHandshakeInterceptor;



//1Tut
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Autowired JwtHandshakeInterceptor jwtHandshakeInterceptor;
    @Autowired CustomHandshakeHandler customHandshakeHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue", "/topic"); //Каналы для подписок
        config.setApplicationDestinationPrefixes("/app");//Префикс для отправки сообщений
        config.setUserDestinationPrefix("/user"); //для личных сообщений
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .setAllowedOrigins("*")
                .addInterceptors(jwtHandshakeInterceptor)
                .setHandshakeHandler(customHandshakeHandler)
                .withSockJS();
    }

    //нахера?
    /* 
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }*/

}