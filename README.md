# tutorial-kafka

Small playground for experimenting with kafka. 

## Setup

Start Kafka, Zookeeper, and kafdrop

    docker-compose up -d


## Kafdrop

Navigate to http://localhost:9000/ to view the topics, partitions, and payload

## Tutorial Shell

Interactive shell for staging test data and invoking the various examples. 

launch with:

    mvn spring-boot:run


At the prompt, run `help` for a list of available commands.

## Deployment

Ansible playbooks are used to stage and deploy the various examples. See [./ansible/](./ansible/) for more details.   

