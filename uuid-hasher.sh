#!/bin/bash

if [ `which jq` ]
  then
    echo "Reading usernames and generating UUID hashes..."
    HASHES=$""
    while read line
    do
    CURRENTNAME=`echo -n "$line"`
    CURRENTUUID=`curl -s "https://api.mojang.com/users/profiles/minecraft/"$CURRENTNAME | jq -r '.id'`
    echo "Hashing $line: $CURRENTUUID..."
    HASH=`echo -n "$CURRENTUUID" | openssl sha256`"\n"
    HASHES+=`echo -n "${HASH/(stdin)= /}"`
    done
    echo -e "$HASHES" > "$1"
    echo "Done."
else
  echo "Please install jq to use this script"
fi