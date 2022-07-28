#!/bin/sh

## run BioC copy program with only standard libraries

CLASSPATH=lib/bioc.jar
java -cp $CLASSPATH bioc.test.CopyXML xml/everything.xml output/out.xml
diff -q output/everything-nolib.xml output/out.xml
