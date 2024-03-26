### Docker commands to build, tag and push to docker hub

    ./gradlew clean bootJar && docker build -t data-csv . && docker tag data-csv 10austin/data-csv && docker push 10austin/data-csv
    ./gradlew clean bootJar && docker build -t data-consumer . && docker tag data-consumer 10austin/data-consumer && docker push 10austin/data-consumer
    ./gradlew clean bootJar && docker build -t data-via-kafka . && docker tag data-via-kafka 10austin/data-via-kafka && docker push 10austin/data-via-kafka
    ./gradlew clean bootJar && docker build -t data-ai . && docker tag data-ai 10austin/data-ai && docker push 10austin/data-ai
    