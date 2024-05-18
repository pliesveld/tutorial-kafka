#!/usr/bin/python3

import time
from daemonize import Daemonize
import argparse

def main():
    import startup
    args = parser.parse_args()
    startup.main(args)

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--pid', type=str, default='/tmp/startup.pid')
    parser.add_argument('--foreground', default=False, action='store_true')
    parser.add_argument('--kafka-broker', type=str, default='powerspec:29092')
    args = parser.parse_args()

    daemon = Daemonize(app='app', pid=args.pid, action=main, foreground=args.foreground)
    daemon.start()

