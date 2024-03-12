### Deploy 

    k create -f mongo.yml && k create -f kafka.yml && sleep 30 && k create -f data-via-kafka.yml && sleep 5 && k create -f data-consumer.yml && k create -f data-csv.yml

### Delete 

    echo 'Delete Mongo'
    k delete deployment mongo-deployment -n mercury-microservice 
    k delete service mongo-service -n mercury-microservice
    k delete persistentvolumeclaim mongo-data-pvc-mercury -n mercury-microservice  
    k delete persistentvolume mongo-data-pv-mercury 
    
    echo 'Delete Kafka'
    k delete serviceaccount kafka -n mercury-microservice
    k delete service kafka-headless -n mercury-microservice
    k delete statefulset kafka -n mercury-microservice

    echo 'Delete data-via-kafka'
    k delete service via-kafka-service -n mercury-microservice
    k delete deployment via-kafka-deployment -n mercury-microservice
    
    echo 'Delete data-consumer'
    k delete service consumer-service -n mercury-microservice
    k delete deployment consumer-deployment -n mercury-microservice

    echo 'Delete data-csv'
    k delete service csv-service -n mercury-microservice
    k delete deployment csv-deployment -n mercury-microservice

### Logs 
    
    echo "Logs"
    k logs deployment.apps/consumer-deployment -n mercury-microservice
    k logs deployment.apps/via-kafka-deployment -n mercury-microservice
    k logs deployment.apps/csv-deployment -n mercury-microservice
    k logs kafka-0 -n mercury-microservice
    
### Connect bash pod
    
    k exec -it consumer-deployment-<id> /bin/sh -n mercury-microservice
    k exec -it kafka-0 /bin/sh -n mercury-microservice
    k exec -it mongo-deployment-<id> /bin/sh -n mercury-microservice
    k exec -it -c busybox nginx-busybox -- /bin/sh

[//]: # (k logs deployment.apps/kafka-deployment -n mercury-microservice)
