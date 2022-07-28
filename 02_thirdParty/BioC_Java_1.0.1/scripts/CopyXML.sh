#!/bin/sh

## run BioC copy program

if [ "-s" == "$1" ] ; then
    parser='-p STANDARD'
    shift
fi
if [ "-w" == "$1" ] ; then
    parser='-p WOODSTOX'
    shift
fi

if [ $# != 2 ] ; then
    echo usage: $0 [-s] [-w] in_file out_file
    exit -1
fi

CLASSPATH=lib/bioc.jar:lib/woodstox-core-asl-4.1.4.jar:lib/stax2-api-3.1.1.jar:lib/stax-util.jar
java -cp $CLASSPATH bioc.test.CopyXML $parser $1 $2
