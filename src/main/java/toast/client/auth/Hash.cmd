#!/bin/bash
#read.file.line.by.line.sh

while read line
do
echo -n $line | openssl dgst -sha256 | awk '{print $1}'
done