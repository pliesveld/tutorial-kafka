# tutorial-kafka

Small playground for experimenting with kafka. 

## Setup

Start Kafka, Zookeeper, and kafdrop

    docker-compose up -d

## Java - Sping Boot Shell

Interactive shell for reading and writing to the kafka 

launch with:

    mvn spring-boot:run


At the prompt, run `help` for a list of available commands.

## Kafdrop

    Navigate to http://localhost:9000 to view the topics, partitions, and payload


