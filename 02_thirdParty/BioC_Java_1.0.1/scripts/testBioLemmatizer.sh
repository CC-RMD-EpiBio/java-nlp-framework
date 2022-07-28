#!/bin/sh
CLASSPATH=lib/bioc.jar:lib/woodstox-core-asl-4.1.4.jar:lib/stax2-api-3.1.1.jar:lib/biolemmatizer-core-1.1-jar-with-dependencies.jar
java -cp $CLASSPATH bioc.tool.AddLemma xml/BioLemmatizer/pos.xml output/lemma.xml
diff -q xml/BioLemmatizer/lemma.xml output/lemma.xml
