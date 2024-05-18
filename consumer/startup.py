import time
import json
import sys
import socket
from confluent_kafka import Consumer, KafkaError, KafkaException
import espeak

MIN_COMMIT_COUNT=1

espeak.init()
speaker = espeak.Espeak()
speaker.rate = 300

def msg_process(msg):


    payload = json.loads(msg.value())
    sys.stdout.write('%s\n' % payload['content'])
    sys.stderr.write('%s\n' % payload['content'])
    speaker.say(payload['content'])
    while speaker.playing():
        pass
    
def commit_completed(err, partitions):
    if err:
        print(str(err))
    else:
        print("Committed partition offsets: " + str(partitions))

conf = {'bootstrap.servers': 'host1:9092,host2:9092',
        'group.id': 'foo',
        'auto.offset.reset': 'smallest',
        'on_commit': commit_completed}

debug_last_message=None

def main(args):
    conf['bootstrap.servers'] = 'powerspec:9092'

    conf['group.id'] = '%s-%s' % ('tutorial-kafka', socket.gethostname())

    speaker.say('started')

    consumer = Consumer(conf)
    topic = 'demo_java'

    def consume_loop(consumer, topics):
        try:
            consumer.subscribe(topics)

            msg_count = 0
            while True:
                msg = consumer.poll(timeout=1.0)
                if msg is None: continue

                if msg.error():
                    if msg.error().code() == KafkaError._PARTITION_EOF:
                        # End of partition event
                        sys.stderr.write('%% %s [%d] reached end at offset %d\n' %
                                         (msg.topic(), msg.partition(), msg.offset()))
                    elif msg.error():
                        speaker.say('error')
                        raise KafkaException(msg.error())
                else:
                    global debug_last_message
                    debug_last_message = msg
                    msg_process(msg)
                    msg_count += 1
#                     if msg_count % MIN_COMMIT_COUNT == 0:
                    consumer.commit(asynchronous=True)
        finally:
            # Close down consumer to commit final offsets.
            consumer.close()

    consume_loop(consumer, [topic])


if __name__ == '__main__':
    main({})
    speaker.say('end')

