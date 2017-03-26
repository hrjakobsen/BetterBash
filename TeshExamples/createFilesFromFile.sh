#!/bin/tesh

#Declare filename of file to read from
var fileName = "test.txt"

#Declare a file we can read from
file f = openFile(fileName, READ)

#Check to see if file exists
if f.exists() {
    #Create a file for every line in f
    for line in f.content() {
        $mkdir line
    }

    file.close()
} else {
    stderr << "The file " + fileName + " does not exist!"
}