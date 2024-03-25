package com.github.hth.dataai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.stereotype.Service;

@Service
public class AIJokeService {
    private static final Logger log = LoggerFactory.getLogger(AIJokeService.class);
    private final OpenAiChatClient openAiChatClient;

    public AIJokeService(OpenAiChatClient openAiChatClient) {
        this.openAiChatClient = openAiChatClient;
    }

    public String getJoke(String topic) {
        PromptTemplate promptTemplate = new PromptTemplate("""
                 Please act as a funny person and  create a joke on the given {topic}?
                 Please be mindful and sensitive about the content though.
                """);
        promptTemplate.add("topic", topic);

        ChatResponse chatResponse = this.openAiChatClient.call(promptTemplate.create());
        log.info("{}/{} {}",
                chatResponse.getMetadata().getRateLimit().getRequestsRemaining(),
                chatResponse.getMetadata().getRateLimit().getRequestsLimit(),
                chatResponse.getResult().getOutput().getContent().substring(0, 50));
        return chatResponse.getResult().getOutput().getContent();
    }
}
