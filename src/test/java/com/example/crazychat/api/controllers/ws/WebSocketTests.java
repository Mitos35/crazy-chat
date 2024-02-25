package com.example.crazychat.api.controllers.ws;

import com.example.crazychat.api.RandomIdGenerator;
import com.example.crazychat.api.controllers.rest.ChatRestController;
import com.example.crazychat.api.dto.ChatDto;
import com.example.crazychat.config.WebSocketConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log4j2
@ActiveProfiles("test-vlad")
@RunWith(JUnitPlatform.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
class WebSocketTests {

    @Value("${local.server.port}")
    private int port;

    private static WebClient client;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @BeforeAll
    public void setup() throws Exception {

        RunStopFrameHandler runStopFrameHandler = new RunStopFrameHandler(new CompletableFuture<>());

        String wsUrl = "ws://127.0.0.1:" + port + WebSocketConfig.REGISTRY;

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));

        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient
                .connect(wsUrl, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        client = WebClient.builder()
                .stompClient(stompClient)
                .stompSession(stompSession)
                .handler(runStopFrameHandler)
                .build();
    }

    @AfterAll
    public void tearDown() {

        if (client.getStompSession().isConnected()) {
            client.getStompSession().disconnect();
            client.getStompClient().stop();
        }
    }

    @SneakyThrows
    @Test
    void should_PassSuccessfully_When_Fetch_Chats() {
        StompSession stompSession = client.getStompSession();

        RunStopFrameHandler handler = client.getHandler();

        String chatName = "Crazy chat";

        stompSession.send(
                ChatWsController.CREATE_CHAT,
                chatName
        );

        String contentAsString = mockMvc
                .perform(MockMvcRequestBuilders.get(ChatRestController.FETCH_CHATS))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<LinkedHashMap<String, Object>> params =
                (List<LinkedHashMap<String, Object>>) mapper.readValue(contentAsString, List.class);

        Assertions.assertFalse(params.isEmpty());

        String chatId = (String) params.get(0).get("id");

        String destination = ChatWsController.getFetchPersonalMessagesDestination(chatId, RandomIdGenerator.generate());

        final RunStopFrameHandler runStopFrameHandler = new RunStopFrameHandler(new CompletableFuture<>());
        stompSession.subscribe(
                destination,
                runStopFrameHandler
        );
    }

    @SneakyThrows
    @Test
    void should_PassSuccessfully_When_CreateChat() {
        var stompSession = client.getStompSession();
        var handler = client.getHandler();

        StompSession.Subscription subscription = stompSession.subscribe(
                ChatWsController.FETCH_CREATE_CHAT_EVENT,
                handler
        );

        String chatName = "Crazy chat";

        stompSession.send(
                ChatWsController.CREATE_CHAT,
                chatName
        );

        byte[] payload = (byte[]) handler.getFuture().get();

        var chat = mapper.readValue(payload, ChatDto.class);


        Assertions.assertEquals(chat.getName(), chatName);

        subscription.unsubscribe();
    }


    private List<Transport> createTransportClient() {

        List<Transport> transports = new ArrayList<>(1);

        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        return transports;
    }

    @Data
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private class RunStopFrameHandler implements StompFrameHandler {

        CompletableFuture<Object> future;

        @Override
        public @NonNull Type getPayloadType(StompHeaders stompHeaders) {

            log.info(stompHeaders.toString());

            return byte[].class;
        }

        @Override
        public void handleFrame(@NonNull StompHeaders stompHeaders, Object o) {

            log.info(o);

            future.complete(o);

            future = new CompletableFuture<>();
        }
    }

    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class WebClient {

        WebSocketStompClient stompClient;

        StompSession stompSession;

        String sessionToken;

        RunStopFrameHandler handler;
    }
}
