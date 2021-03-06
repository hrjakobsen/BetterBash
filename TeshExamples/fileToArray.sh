#!/bin/bash

#Declare an array
declare -a LINES

#Declare a counter
NUMLINES=0

#Declare filename of file to read from
FILE="test.txt"

#Check to see if file exists
if [ -e $FILE ]; then
	#While there are lines in the file, 
	#read them and add them to the array
	while read LINE; do
	    ((NUMLINES++))
	    LINES+=($LINE)
	done < $FILE

	#Output the number of elements in the file/array
	echo Number of elements: ${#LINES[@]}

	#Output the content of the array
	echo ${LINES[@]}
else 
	#Write to stderr
	>&2 echo "The file" $FILE "does not exist!"
fi 