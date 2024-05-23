# Tutorial Consumers

Kafka consumers are a collection of raspberry pi computers with the Google Voice Kit.

![image](https://aiyprojects.withgoogle.com/static/images/voice-v2/product_voice@2x.png)

# Setup
## librdkafka

Install the librdkafka from source with the command below:

```
sudo apt-get install -y --no-install-recommends gcc git libssl-dev g++ make && \
  cd /tmp && git clone https://github.com/edenhill/librdkafka && \
  cd librdkafka && git checkout tags/v2.4.0 && \
  ./configure && make && make install && ldconfig
```

## pulseaudio as daemon

Following steps are used to configure pulseaudio system-wide so that sound can be used from a systemd service unit:

```
sudo systemctl --global disable pulseaudio.service pulseaudio.socket
sudo adduser root pulse-access
```

## Sound hardware

The AIY project is no longer maintained.  To utilize the speaker hardware on newer Raspberry Pi OS releases, the following configuration steps are needed:

Edit the file `/boot/firmware/config.txt` and the contents to be the following: 
```
# dtparam=audio=on
dtoverlay=googlevoicehat-soundcard
```
 

## Ansible

Ansible playbooks are used for deployment. SystemD service units are used to start (and restart) after deployment. 

All tutorial examples are compiled into a single jar. Ansible playbooks are used to configure the SystemD service to launch the different examples.

To run the ansible playbook, execute the following:

```
    ansible-playbook example1.yml
```

