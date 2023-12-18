package fluffysnow.idearly.config;

import fluffysnow.idearly.textchat.interceptor.ChannelInboundInterceptor;
import fluffysnow.idearly.textchat.interceptor.WebsocketHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class TextChatWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ChannelInboundInterceptor channelInboundInterceptor;
    private final WebsocketHandshakeInterceptor websocketHandshakeInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/topic");

        registry.setApplicationDestinationPrefixes("/pub", "/topic");
    }

    /**
     * ws 핸드셰이크할 경로를 결정
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws/chat")
                .addInterceptors(websocketHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(channelInboundInterceptor);
    }

}
