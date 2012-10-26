#!/bin/sh

LOCALPATH=`pwd`
ANDROOIDPATH=$LOCALPATH/../..

echo "Patching into $ANDROOIDPATH"
cp -rf kernel/* $ANDROOIDPATH/kernel
cp -rf device/* $ANDROOIDPATH/device

