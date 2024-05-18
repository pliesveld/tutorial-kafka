# Tutorial Consumers

Kafka consumers are a collection of raspberry pi computers with the Google Voice Kit.

![image](https://aiyprojects.withgoogle.com/static/images/voice-v2/product_voice@2x.png)

# Setup

## librdkafka

```
 apt-get install -y --no-install-recommends gcc git libssl-dev g++ make && \
  cd /tmp && git clone https://github.com/edenhill/librdkafka && \
  cd librdkafka && git checkout tags/v2.4.0 && \
  ./configure && make && make install && ldconfig
```

## pulseaudio as daemon

sudo systemctl --global disable pulseaudio.service pulseaudio.socket

sudo adduser root pulse-access


## Ansible

Ansible playbooks are used for provisioning and deployment

##
