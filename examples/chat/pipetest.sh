#!/bin/bash

#Path to the communication pipe
pipe=/tmp/testpipe

chathandler() {
    ./chat.sh >$pipe &

    person1Match="Person 1*"

    Person1Count=0
    Person2Count=0

	while true
	do
	    if read line <$pipe; then
	        if [[ $line == $person1Match ]]
            then
                ((Person1Count++))
            else
                ((Person2Count++))
            fi
            echo -e "Person 1: $Person1Count\nPerson 2: $Person2Count\n"
	    fi
	done
}

#Remove pipe when program is terminated
trap "rm -f $pipe" EXIT

#Create the pipe where the chat will communicate to
if [[ ! -p $pipe ]]; then
    mkfifo $pipe
fi

#Start function in background
chathandler &

#Do other work
#...

#Wait for function to finish
wait