## Ollama
[Refer Ollama FAQ](https://github.com/ollama/ollama/blob/main/docs/faq.md#how-can-i-allow-additional-web-origins-to-access-ollama)

### How do I keep a model loaded in memory or make it unload immediately?

By default, models are kept in memory for 5 minutes before being unloaded. This allows for quicker response times if you are making numerous requests to the LLM. You may, however, want to free up the memory before the 5 minutes have elapsed or keep the model loaded indefinitely. Use the keep_alive parameter with either the /api/generate and /api/chat API endpoints to control how long the model is left in memory.

The keep_alive parameter can be set to:

- a duration string (such as "10m" or "24h")
- a number in seconds (such as 3600)
- any negative number which will keep the model loaded in memory (e.g. -1 or "-1m")
- '0' which will unload the model immediately after generating a response

For example, to preload a model and leave it in memory use:

    curl http://localhost:11434/api/generate -d '{"model": "llama2", "keep_alive": -1}'

To unload the model and free up memory use:

    curl http://localhost:11434/api/generate -d '{"model": "llama2", "keep_alive": 0}'