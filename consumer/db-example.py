#!/usr/bin/env python3
from influxdb import InfluxDBClient
import socket

client = InfluxDBClient(host='pi3', port=8086)
client.create_database('load')
client.switch_database('load')

with open('/proc/loadavg') as f:
    line = f.readline()
    measurements = line.split(' ')

    json_body = [
        {
            "measurement": "load",
            "tags": {
                "host": socket.getfqdn(),
            },
            "fields": {
                "avg1": measurements[0],
                "avg5": measurements[1],
                "avg15": measurements[2],
            }
        }
    ]
    client.write_points(json_body)

