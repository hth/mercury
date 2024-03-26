package com.github.hth.dataai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.hth.dataai.dto.LlamaResponse;
import reactor.core.publisher.Flux;

@Service
public class LlamaService {
    private final Logger log = LoggerFactory.getLogger(LlamaService.class);
    private final OllamaChatClient chatClient;

    @Autowired
    public LlamaService(OllamaChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public LlamaResponse generateJoke(String topic) {
        final String llamaMessage = chatClient.call(String.format("Tell me a joke about %s", topic));
        log.info("Joke topic={} Response={}", topic, llamaMessage);
        return new LlamaResponse().setMessage(llamaMessage);
    }

    public Flux<String> generateJokeStream(String topic) {
        return chatClient.stream(String.format("Tell me a joke about %s", topic));
    }

    public LlamaResponse generatePrompt(String promptMessage) {
        Prompt prompt = new Prompt(new UserMessage(promptMessage));
        final ChatResponse chatResponse = chatClient.call(prompt);
        log.info("{} {}", promptMessage, chatResponse.getResult());
        return new LlamaResponse().setMessage(chatResponse.getResult().getOutput().getContent());
    }

    public Flux<String> generatePromptStream(String promptMessage) {
        Prompt prompt = new Prompt(new UserMessage(promptMessage));
        return chatClient.stream(prompt).map(chatResponse -> chatResponse.getResult().getOutput().getContent());

    }
}
