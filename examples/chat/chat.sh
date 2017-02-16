#!/bin/bash

declare -a people=("Person 1" "Person 2")

while true 
do
	MESSAGE=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | head -n 1)
	PERSON=$(cat /dev/urandom | tr -dc '0-1' | fold -w 256 | head -n 1 | head --bytes 1)

	echo "${people[$PERSON]}: $MESSAGE"
	sleep 3
done
