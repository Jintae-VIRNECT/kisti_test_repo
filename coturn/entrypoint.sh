#!/bin/bash

CONFIG_FILE=/etc/turnserver.conf
URI=$CONFIG_SERVER/turnserver/$VIRNECT_ENV/master/turnserver-$VIRNECT_ENV.conf

wget -O $CONFIG_FILE $URI
res=$?
if test "$res" != "0"; then
    echo "the wget command failed with: $res"
    exit 1
fi
if [[ ! -s $CONFIG_FILE ]]; then
    echo "config file is empty"
    exit 1
fi

/usr/local/bin/turnserver $@
