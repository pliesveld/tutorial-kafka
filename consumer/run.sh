#!/bin/bash

dir=$(cd -P -- "$(dirname -- "$0")" && pwd -P)
cd $dir

if [[ ! -d venv ]]; then
	# pip3 install virtualenv
	# python3 -m virtualenv venv
    python3 -m venv venv
	source venv/bin/activate
	pip3 install -r requirements.txt
else
	source venv/bin/activate
fi


set -x 
python3 main.py "$@"


