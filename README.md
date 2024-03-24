# mercury

This code runs multiple microservices to absorb transactions from via `kafka` or via `csv` file from `AWS S3`.

## Running Locally

Follow steps mentioned in [Commands](Commands.md) to run this code locally

## Standalone Kafka 

#### Run kafka
Start `kafka` via by running `docker-compose.yml` under folder `data-via-kafka/kafka-setup`

OR 

    docker run --name kafka -p "9092:9092" --volume ./data:/tmp/kafka-logs -d apache/kafka:3.7.0

## OpenRewrite

Recipe added for license and static import. Refer [OpenRewrite](https://docs.openrewrite.org/)

