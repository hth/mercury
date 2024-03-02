# mercury

#### Run kafka
Start `kafka` via by running `docker-compose.yml` under folder `kafka-setup`

OR 

    docker run --name kafka --port "9092:9092" --volume ./data:/tmp/kafka-logs -d apache/kafka:3.7.0

