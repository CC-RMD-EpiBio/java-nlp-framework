Java implementation of BioC
Version 1.0
Data structures and code to read / write BioC XML.

---------------------------------------------------------------------------
BIOC

BioC XML format can be used to exchange prepared biomedical corpora and
any accompanying annotations between different research groups and
software platforms.

GOALS are to facilitate: 
  - data exchange 
  - data and tool reuse

---------------------------------------------------------------------------
|*|  PUBLIC DOMAIN NOTICE                                                        
|*| 
|*| This work is a "United States Government Work" under the terms of the
|*| United States Copyright Act. It was written as part of the authors'
|*| official duties as a United States Government employee and thus cannot
|*| be copyrighted within the United States. The data is freely available
|*| to the public for use. The National Library of Medicine and the U.S.
|*| Government have not placed any restriction on its use or reproduction
|*|                                                                             
|*| Although all reasonable efforts have been taken to ensure the accuracy
|*| and reliability of the data and its source code, the NLM and the
|*| U.S. Government do not and cannot warrant the performance or results
|*| that may be obtained by using it. The NLM and the U.S. Government
|*| disclaim all warranties, express or implied, including warranties of
|*| performance, merchantability or fitness for any particular purpose.
|*|                                                                              
|*| Please cite the authors in any work or product based on this material:
|*| 
|*| BioC: A Minimalist Approach to Interoperability for Biomedical Text
|*| Processing
|*| Donald C. Comeau, Rezarta Islamaj Dogan, Paolo Ciccarese, Kevin
|*| Bretonnel Cohen, Martin Krallinger, Florian Leitner, Zhiyong Lu, Yifan
|*| Peng, Fabio Rinaldi, Manabu Torii, Alfonso Valencia, Karin Verspoor,
|*| Thomas C. Wiegers, Cathy H. Wu, and W. John Wilbur, submitted,
|*| DATABASE, 2013.
---------------------------------------------------------------------------


This distribution includes following files and directories:

BioC.dtd
  the BioC DTD
LICENSE.txt
  The "United States Government Work" PUBLIC DOMAIN NOTICE
README.txt
  this file
bin/
  Compiled Java class files.
lib/
  bioc.jar. The compiled code.
  Several other libraries needed to use BioC code. Details below.
scripts/
  Unix test and demo scripts. They assume they are being run in the
  unpacked BioC directory.
src/
  BioC Java source files. In addition to the core files, includes test
  programs and a couple useful BioC-compatible tools.
xml/
  BioC XML files used by the test and tool demo programs.


ide
===

Several of the developers use Eclipse. There should be no difficulty
using other development tools.


libraries
=========

A jar file with the BioC class files appears in the lib directory.

A number of libraries were used in the development and testing of
BioC.  These jar files also appear in the lib subdirectory. They are
not part of the BioC project, and have their own home pages and
licenses. They are merely provided for your convenience.

------------------------------------------------------------

stax-utils.jar
https://java.net/projects/stax-utils/pages/Home
The BSD 3-Clause License

------------------------------------------------------------

Woodstox StAX XML parser
stax2-api-3.1.1.jar
woodstox-core-asl-4.1.4.jar
http://woodstox.codehaus.org/
Apache License 2.0

------------------------------------------------------------

JUnit 4
junit-4.11.jar           
hamcrest-core-1.3.jar
http://junit.org/
Eclipse Public License - v 1.0

hamcrest-core-1.3.jar
https://code.google.com/p/hamcrest/
The BSD 3-Clause License

------------------------------------------------------------

xmlunit-1.4.jar
http://xmlunit.sourceforge.net/
The BSD 3-Clause License

------------------------------------------------------------

biolemmatizer-core-1.1-jar-with-dependencies.jar
http://biolemmatizer.sourceforge.net/
The BSD 3-Clause License


parsers
=======

BioC works with both the standard Java StAX parser and Woodstox, a high
performance StAX parser. StAX allows for low memory use, unlike a DOM
parser. It also provides traditional control flow in contrast to the
callbacks or handler objects of SAX parser.


samples / tests
===============

There are sample programs to test the installation and to demonstrate
the flavor of using BioC for reading and writing data.

programs:

Appear in bioc.test and bioc.tool
All can be run directly from Eclipse
Some have Unix scripts that provide an example of possible arguments

While several of them are BioC XML copy programs, they do demonstrate
the skeleton of a BioC program without additional complicated
application details.


jUnit:

bioc.test.junit
  usually run directly from Eclipse

scripts:

While many developers run programs directly from their IDE, Unix shell
scripts have been provided. They assume they are being run in the
unpacked BioC directory.

testCopyXML-nolib.sh
  uses BioC code to read in and then write back out a BioC XML file
  uses only standard Java libraries and BioC code  

CopyXML.sh
  uses BioC code to read in and then write back out a BioC XML file

   usage: $0 [-s] [-w] in_file out_file

   -s or -w specify the STANDARD or WOODSTOX parser

testAll.sh
  runs the other test scripts

testCopyCollectionXML.sh
testCopyConverterXML.sh

testSentenceSplit.sh
  implements the trivial period-space ". " sentence segmenting algorithm.


tools
=====

BioLemmatizer
-------------

The BioLemmatizer is a domain-specific lemmatization tool for the
morphological analysis of biomedical literature. 

Haibin Liu, Tom Christiansen, William A Baumgartner Jr, and Karin
Verspoor BioLemmatizer: a lemmatization tool for morphological
processing of biomedical text Journal of Biomedical Semantics, 2012,
3:3. 

http://biolemmatizer.sourceforge.net/

test script:

  scripts/testBioLemmatizer.sh


abbr
----

The ExtractAbbrev class implements a simple algorithm for
extraction of abbreviations and their definitions from biomedical text.

 @see <a href="http://biotext.berkeley.edu/papers/psb03.pdf">A Simple Algorithm 
 for Identifying Abbreviation Definitions in Biomedical Text</a> 
 A.S. Schwartz, M.A. Hearst; Pacific Symposium on Biocomputing 8:451-462(2003) 
 
 for a detailed description of the algorithm.  

The Schwartz and Hearst code (@author Ariel Schwartz, @version 03/12/03)
has been updated to work with the BioC classes: 
        - given a BioC passage or BioC sentence, the FindAbbr.java class calls 
        a slightly modified ExtractAbbrev method to find all abbreviation definitions
        in the given text and their corresponding offsets. 
        
        the output is written back in BioC XML format, where the original BioC passage 
        or BioC sentence is enriched with new annotations and relations that 
        express the abbreviations:  
        annotations: long form and short form, 
        relation: the link between long form and corresponding short form
                                                         
see key file for more. 


------------------------
DEVELOPERS
------------------------

Don Comeau
Rezarta Islamaj Dogan
Haibin Liu
Yifan Peng
Thomas C. Wiegers
John Wilbur


------------------------
CONTACT
------------------------

Don Comeau:             comeau@ncbi.nlm.nih.gov
Rezarta Islamaj Dogan   Rezarta.Islamaj@nih.gov
John Wilbur:            wilbur@ncbi.nlm.nih.gov



----------------------------------------------------------
WEBPAGE
-----------------------------------------------------------

A BioC webpage is available with all up-to-date instructions, code, and
corpora in the BioC format, and other research on, based on and related to
BioC. 

http://www.ncbi.nlm.nih.gov/CBBresearch/Dogan/BioC/
http://bioc.sourceforge.net/

