# mercury

This code runs multiple microservices to absorb transactions from via `kafka` or via `csv` file from `AWS S3`.

`data-consumer` consumes transactions from `data-via-kafka` & `data-via-csv`. `data-consumer` produces its own transaction tagged as `CONSUMER`, whereas data
coming from other source are tagged as `KAFKA`, `CSV`. `Success` and `Failed` transactions are streamed live. Percentage of failed transactions is `10%`.

## Navigate code 

- data-consumer (Consumes from Self, Kafka and S3. Persist data to Mongo. Stream Success and Failed transactions)
- data-via-kafka (Data Produce and Published to Kafka)
- data-csv (Produce data in CSV format. Stores data periodically to AWS S3)

## Solution to design

![Design Problem](./images/design-problem.png)

## Running Locally

Follow steps mentioned in [Commands](Commands.md) to run this code locally

## Standalone Kafka 

#### Run kafka
Start `kafka` via by running `docker-compose.yml` under folder `data-via-kafka/kafka-setup`

OR 

    docker run --name kafka -p "9092:9092" --volume ./data:/tmp/kafka-logs -d apache/kafka:3.7.0

## OpenRewrite

Recipe added for license and static import. Refer [OpenRewrite](https://docs.openrewrite.org/)

## Spring AI

Note: Running locally via Kubernetes needs higher memory footprint. Recommended to run `data-ai` using IntelliJ   

- [Refer video on Ollama](https://youtube.com/watch?v=IJYC6zf86lU&t=36s)
- library https://github.com/ollama/ollama
  - Llama 2	7B	3.8GB
    - ollama run llama2
  - Llama 2 13B	13B	7.3GB
    - ollama run llama2:13b
- [Rest Api](https://github.com/ollama/ollama/blob/main/docs/api.md#api) 
- https://ollama.com/
- https://useanything.com/
- [Discord location Ollama](https://discord.com/channels/1128867683291627614/1150902223417655317)

[For more Refer](./data-ai/ReadMe.md)
