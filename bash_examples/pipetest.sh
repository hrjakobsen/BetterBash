#!/bin/bash

pipe=/tmp/testpipe

COUNTER=0

reader() {
	while true
	do
	    if read line <$pipe; then
	        echo -n $line " "
	        let COUNTER+=1
	        echo $COUNTER
	    fi
	done
}

writer() {
	while true
	do
		date >$pipe
		sleep 1
	done
}

trap "rm -f $pipe" EXIT

if [[ ! -p $pipe ]]; then
    mkfifo $pipe
fi

reader &
writer &

echo setup done

wait