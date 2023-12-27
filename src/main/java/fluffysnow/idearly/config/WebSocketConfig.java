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
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ChannelInboundInterceptor channelInboundInterceptor;
    private final WebsocketHandshakeInterceptor websocketHandshakeInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/topic");

        registry.setApplicationDestinationPrefixes("/pub", "/topic", "/app");
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

        registry
                .addEndpoint("/ws/signaling")// webSokcet 접속시 endpoint 설정
                .setAllowedOriginPatterns("*"); // cors 에 따른 설정 ( * 는 모두 허용 )
                //.withSockJS(); // 브라우저에서 WebSocket 을 지원하지 않는 경우에 대안으로 어플리케이션의 코드를 변경할 필요 없이 런타임에 필요할 때 대체하기 위해 설정
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(channelInboundInterceptor);
    }
}
