#!/bin/bash

pipe=/tmp/testpipe

reader() {
	while true
	do
	    if read line <$pipe; then
	        echo $line " "
	    fi
	done
}

writer() {
	./chat.sh >$pipe
}

trap "rm -f $pipe" EXIT

if [[ ! -p $pipe ]]; then
    mkfifo $pipe
fi

reader &
writer &

echo "setup done"

wait