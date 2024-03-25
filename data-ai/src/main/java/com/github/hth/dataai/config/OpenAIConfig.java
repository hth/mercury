package com.github.hth.dataai.config;

import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

    @Value("${spring.ai.openai.api-key}")
    private String openApiKey;

    public OpenAiApi openAiApi() {
        return new OpenAiApi(openApiKey);
    }

    @Bean
    public OpenAiChatClient openAiChatClient() {
        return new OpenAiChatClient(openAiApi());
    }
}
