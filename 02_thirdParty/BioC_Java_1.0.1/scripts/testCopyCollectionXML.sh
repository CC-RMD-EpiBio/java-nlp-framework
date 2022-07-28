#!/bin/sh
CLASSPATH=lib/bioc.jar:lib/woodstox-core-asl-4.1.4.jar:lib/stax2-api-3.1.1.jar
java -cp $CLASSPATH bioc.test.CopyCollectionXML xml/everything-sentence.xml output/out.xml
diff -q xml/everything-sentence.xml output/out.xml
