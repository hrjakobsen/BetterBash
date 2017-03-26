#!/bin/bash
 
#Declare variables to store the user's height and weight
height=0
weight=0

#Print to stdout
echo "Please choose which unit you would like to use"
    
choices=("Metric" "Imperial" "Quit")
select choice in "${choices[@]}"
do
    case $choice in
        "Metric")
            echo "Please input your height in cm"

            read height

            until [ -n "$height" ] && [ -z "${height##[0-9]}" ]; do
                echo "Please input a number"

                #Read input from the user
                read height 
            done
            echo "You input " $height
            ;;
        "Imperial")
            echo "Chose 2"
            ;;
        "Quit")
            break
            ;;
        *) 
            echo "Please choose a valid option"
            ;;
    esac
done


else
    :;
fi