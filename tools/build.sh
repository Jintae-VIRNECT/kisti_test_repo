#!/bin/bash

cmake -DCMAKE_INSTALL_SYSCONFDIR=/etc -DCMAKE_INSTALL_PREFIX=/usr .
make
