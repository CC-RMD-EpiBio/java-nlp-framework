# Framework Legacy: A Framework to Build cNLP Applications from Common Components

Last Edited: 2022-07-29 </p>
Author: Guy Divita

# Introduction
Framework Legacy is a suite of components made available to build Java-based clinical Natural Language Processing (cNLP) applications and functionalities. Applications built using these tools include Named Entity Recognition (NER) tools for Symptom Extraction within Clinical Records, Military Sexual Trauma Mention extraction within Clinical Records, Finding Evidence of Homelessness in Clinical Records, and Body Function Extraction within Clinical Records.  

This framework works specifically with the text part of clinical records.  Clinical records are more challenging to process than other text such as newspaper articles, twitter feeds, and bio-medical literature abstracts.  Clinical records are more highly visually structured, yet, not uniformly or consistent in adhering to a formatting structure within the same document.  Clinical records often include formatting features that embed semantics by structure like slot: value, list and table structures.  These formatting conveniences require recognition and specific ways to process them beyond simple sentence and phrase identification.   

The niche that Framework Legacy addresses is attention paid to Document Decomposition into finer grained entities that then can be processed using traditional means.  Document Decomposition includes identifying structures within documents like page headers and footers, section names, section zones along with paragraphs.  Additional structures are also identified including sentences, phrases, terms,  and tokens.

# The Framework
The *Framework* is the notion of stringing along or piping methods or functions that work on a document, where the output of one method becomes the input to the next.  In traditional parlance, these methods are called (machine) annotators, and the sequence of annotators is a pipeline.

This notion of an NLP pipeline is not new, and the Framework is not unique, as Framework Legacy, is built upon   components of the Apache project Unstructured Information Management applications (UIMA).  As there is/was a large learning curve to using UIMA and UIMA includes many, many moving parts, Framework-Legacy was developed initially to simplify building NLP pipelines. 

Another notion defining this framework is that of each document to be processed is transformed into computer science-y container entity, which includes holding the original text as one piece, and holding annotations that reference spans from the original document (character based offset spans) in another place.  Traditionally this is known as stand-off annotations, where the original text is never altered, say by embedding html code within it. Each annotation includes meta information or attributes, like the beginning and ending spans, a classification or characterization or label for the span.  The pipeline passes this document container from annotator to annotator, where annotations are added, altered, or deleted. UIMA calls this container a Common Analysis System or CAS.

Another notion that defines this framework is that an application is built from adding a reader at the front end of the pipeline to read in documents, convert them into the CAS's, and adding one or more writers at the tail end of the pipeline to transform those annotations about the text into something useful, like a spreadsheet of found mentions, or the document formatted with highlights of found mentions.  

Framework Legacy includes some useful readers and writers beyond what is Distributed with UIMA.

One last notion about this framework: Labels. A label, or Type Descriptor in UIMA, is a definition of an annotation entity that is *typed* or categorized.  These are the Named Entities that a system will find, be it *Sentence* or *Symptom* or *Token* or *Protein*.  Within Framework-Legacy and UIMA, Label definitions are described within one or more type descriptor xml files.  Knowing about where and what type descriptors are specified and used is an important framework component.

### Framework Summary: ###
![Summary Picture of Framework Components [1]](https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/10_documentation/00_pictures/FrameworkLegacySummary.PNG)
	
See figure 1. Framework components that make up an application.

![Example of an NER NLP Pipeline maded up of Annotators](https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/10_documentation/00_pictures/MentalFunctioningPipelineExample.PNG)
See figure 2. An example Framework Pipeline, made up of Annotators. Reader is shown in this picture. 

# Beyond UIMA
Developers define pipelines within (xml) configuration files.  Developers define the parameters to each annotator with (xml) configuration files.  Developers define the labels to be used and referenced as configuration files. The development environment becomes a mass of confusing configuration files.  This is UIMA's Achilles heel. 
There have been various flavors of simplifying software to encapsulate and generate the UIMA needed configuration files since UIMA started.  Framework-Legacy uses UIMA-Fit to on-the-fly generate and pass along the configuration files UIMA uses.  
While UIMA-Fit also on-the-fly generates the configuration files for the labels, or type-descriptors, Framework-legacy, on purpose, keeps to the necessity of pre-defining the labels. This ensures that labels are a-priori defined for a given pipeline, not hidden, and possibly misinterpreted by an annotator which thinks it is being passed one label which is spelled the same, but includes different attributes.  

# Framework Features
Framework-Legacy includes term look-up (or dictionary look-up) functionality.  That is, put terms in an index that are to be used as patterns to be matched within the processed text, and process. Further, each dictionary term can have one or more categories to it, and carry along other meta-information like UMLS information. 

Framework-legacy includes a dictionary composed of terms from the SPECIALIST Lexicon, a resource that covers English terminology including both common English and Medical English terminology.  

### Sidebar: What is a Term ###
	Term equates to an entry we would see in Websters Dictionary.
	A term could include both single and multi word entries.
	Sometimes we talk about terms as words that can be words with spaces in them.

Many of the framework annotators and applications rely on term lookup using dictionaries crafted to include a specific topic or terminology.  For instance, list of cities, or common symptoms, or all the words we associate with body strength. 

### Sidebar: UMLS Content Disclaimer ###
	Some of the dictionaries within applications built using Framework-Legacy
	contain terminology derived or inspired by entries within the 
	Unified Medical Language System (UMLS) Metathesaurus. 
	These dictionaries include UMLS concept Unique Identifiers (CUI's) as part of the attributes 
	associated with the term entries.  Note that the UMLS Metathesaurus derived dictionaries are not 
	part of the Framework-legacy distributions, but would be part of those distributions 
	for specific applications like the Body Function Distribution.  
	Attribution and restrictions to that content are handled at the applications part of the framework.

	This is being mentioned here because the term lookup functionality is part of the framework.
	The dictionaries themselves are found in and are handled at each specific application built from
	the framework.

# Built Using Maven #
Framework-Legacy is built using Maven.  Maven is a common way to define and control how Java programs are built and distributed.  Maven *pom.xml* files are equivalent to *MakeFiles* for building *C* programs.  

### The Reactor Pom ###
There is a Top level reactor pom file that calls all subsequent framework-legacy pom files to build all the pieces.  

### Parent Pom ###
Framework-Legacy includes a parent pom which, among other things, defines the Java compiler version used, the structure of how the man pages should be, and the various dependency plug-ins needed.  Most importantly, the parent pom holds the <version> </version> for each dependency which could be shared across components.  Note that framework-legacy has it's maven configuration set up to have these <version></version> defined in the parent pom and not in the subsequent children pom files.  This aids in making sure there are not competing versions used across an application that uses several components. 

#### Chicken and the Egg ####
The parent pom has to be installed first before the reactor pom can run, because the reactor pom references the parent pom.  As such, there is a necessary first and separate step to install the parent pom, even though it is also called from reactor pom.  


# Components


## Resources

The Resources Project includes lists, regular expressions and dictionaries for the annotators that are part of the framework-legacy package.  These include dictionaries for addresses, anatomical parts, diarrhea, gene terms, homelessness evidence, institution names, location terms, terms found within medication sections (that are not themselves medications), military sexual trauma terminology, number words, person words, CCDA section names, the SPECIALIST Lexicon, UCUM terms, and vitals.  

The resources project also includes part of speech and phrasal boundary models. (These are re-distributions of the cTAKES models). 

The resources project also includes regular expressions and patterns needed for finding dates and times, and for finding ConTEXT assertions.  

All of the above are bundled into one jar file so that it is available within the classpath of any application that does term lookup. I.e., this one dependency covers a lot. 

## Third Party Software
The contents of this directory includes dependencies relied upon that are open source, but not available through a maven nexus distribution.  Among these are two NLM projects,  BioC, the Java version of a common annotation format from NCBI, and the VTT annotation editing tool from NCBC.

This project also include a few jars that the jar was available in some other way than a nexus server, but the source was either no-longer available or was never directly available, These include two jars the cTAKES components have as dependencies (maxent, and opennlp-tools which got altered in one way to enable it to compile to work in this environment).  Also emory mathcs backport, a dependency that the GATE dependency relied upon. 
 
This project includes the statistical machine learned sectionizer obSecAn from Thuy Tran Lee.  This sectionizer was part of the V3NLP tools developed for finding symptoms within VA clinical notes. A paper about this sectionizer was  published and the sectionizer became part of v3NLP (the predisessor to this framework) but she never posted the source code anywhere that could be accessed.  The sectionizer still provides some useful sectionizing evidence it is still employed, but not as the only mechanism to find sections.   
 

## Type Descriptors ##
This project includes UIMA type descriptors for a number of projects and general type descriptors that are used everywhere.  UIMA type descriptors can import other type descriptors.  As such there is a hierarchy of types that the framework-legacy uses. 

The sources to the type descriptors are within xml files in the *src/main/resources* directory.  These are straight-up UIMA Type descriptors.  The UIMA tool *JCasGen* is used to convert the type descriptor definitions into Java Classes (Mojos) which get placed in the src/main/java directory.

Note: While the src/main/java classes are critical to the framework-legacy functionality, these sources and classes are not saved within the git repos.  They are generated via the *jcasgen* tool/plugin as part of the *mvn install* step when building from sources.  This insures the classes reflect the definitions in the type descriptor xml files.  

 The most basic are the VA CHIR type descriptors.  (CHIR was the name of the funding project at the VA that paid for the initial V3NLP development.) These include the general document decomposition types that included Sentence, Phrase, Token and such.  There was an effort within the VA within CHIR participating projects to standardize the nomenclature via an interoperability initiative.  The CHIR type descriptors were the standardized nomenclature for those entities. The VA VINCI type descriptors was the next layer, which include types that were not uniform across the VA systems, but necessary for our work.  These included the notion of a clinical concept, with Symptoms and diarrhea being more children types of such.  The assertion annotation types are found within the VINCI type descriptors.  (VINCI was the name of the entity that did NLP work for the Salt Lake City VA). Most of the Document Decomposition labels come from these two sets of type descriptors.

 Additional generalized types and some project specific types have been added within the NIH/CC/RMD name-space. These include labels to describe Section Zones, person, and shapes. Mobility type descriptors are included as are labels that map between cTAKES type descriptors and the CHIR/VINCI type descriptors.  (Some of the older code still uses the Framework-cTAKES type descriptor because it was pretty encompassing).

Note: Project specific type descriptors found in the other project specific git repos use the overlapping NIH/CC/RMD name-space to build upon the legacy functionality, to insure the type descriptors are found within the classpath without having to add additional classpaths. 


## General Utilities ##
The are a bunch of general (java) utilities the framework relies upon, including parsing through and splitting pipe delimited lines into columns; command line option parsing utilities; logging aids, to output formatting aids. These general utilities rely on no other dependencies, but most of the other projects do rely on this project.  This gets built first.


## Type Descriptor Specific Utilities ##
There are a bunch of utilities that work on or with UIMA annotations.  These include many UIMA shortcuts to finding annotations within the sets/indexes of annotations within UIMA CAS structures. Beyond those utilities, are utilities that work on (type descriptor) types that are specific to this framework. That is, they have reference attributes that are defined in types that come from the CHIR or VINCI inherited types.  For instance, there are functions that retrieve meta-document information (the path to the document, the file name and the like) which rely on calling these functions on types that have (CHIR) document header types as a component.  
(The V in the VUtils stood for the VINCI specific functions)  


## Term, Dictionary, Token Utilities
The tUtilities keeps those functions that have to do with term look-up.  Included are acronym expansion and acronym look-up, functionality that handles lexical variations with wrappers around LVG. Also included are functions around the UMLS Metatheaurus MR tables.  These are functions that read in the Metathesaurus tables like MRCONSO, MREL, MRHIER, index the fields and have lookup functions on them. The functions rely on having the locations of the tables as input parameters.  The tables themselves are not distributed. 


## Annotators ##
Annotators are the atomic units of function within the framework.  This project includes annotators for document decomposition (token, phrase, sentence, section, slot value), along with annotators to recognize date and time, event dates, family members, gene observations, labs, medications, procedures, vitals just to name a few.  At last count there were 150 annotators in this collection. 


## Marshallers (Readers and Writers) ##
Marshallers are functions that *marshall* from one format to another.  This is the project where the functions that transform from input formats like text, GATE, UIMA and CSV are transformed into framework's internal format (CAS's), and where CAS's are transformed into output formats like UIMA's XMI, GATE's XML and Serial Datastore, and CSV formats rest. Beyond GATE, UIMA and CSV, there are also marshallers for Knowtator and eHOST annotation types. 


## Pipeline Utilities ##
The pipeline utilities project includes wrappers around UIMA-FIT, and the structures needed to stitch together a reader, a type descriptor, a pipeline definition, and writers. 


## Common Pipelines ##
Pipelines included in this project have been referenced elsewhere either as examples in other documentation such as the noOp (no operation) pipeline and the Line Pipeline or are called by other pipelines (yes you can do that), such as the Sentence and Syntactic pipelines.  

## Example Applications ##
Pipeline Applications included in this project have been referenced by either as examples in other documentation such as the noOp (no operation) application and the Line application or are fundamental applications such as term lookup, or Sophia applications. 
[Note to self - take out applications that should not be distributed]  


## General Dependencies ##

1. Java 1.8
2. Maven
3. UIMA
4. UIMA-FIT
5. 
This framework is all java code.  As such, it has to be built using Java 1.8 or later compilers. Note, until I update UIMA-FIT, The code compliance set within Maven or eclipse has to be Java 1.8's defintions.  There is an issues with the version of UIMA-FIT that is employed that prevents compilation beyond 1.8, with a conflict between what is referenced and what got migrated into Java 9 (and beyond) core functionality.  Other than the UIMA-FIT conflict, the rest of the code base can be compiled from 1.8 thru Java 18.

This framework relies on Maven 3.x to build.  

This framework is built upon the UIMA version 2.9.  It also can be build with 2.10, but has not (yet) migrated to UIMA 3.X.  

This framework relies upon UIMA-FIT version 2.4 to simplify development. UIMA-FIT has newer versions that are based on UIMA 3.  As such, until the rest of the Framework code base is updated to UIMA 3, Framework using UIMA-FIT 3 will not work.



## The Jars Compiled, distributed and used by other CC-RMD Projects
The following jars have been compiled using the above code, and are dependencies for 

# Building
For reference sake, $FRAMEWORK_HOME equates to where this repo got cloned out to.
Install the parent pom first.  This is the pom in $FRAMEWORK_HOME/00_parent.
Once the parent pom has been installed, go back and build the set of projects.  There is a reactor pom in the $FRAMEWORK_HOME directory that refers to each project to be built.
 
### Example Build from the command line:
<pre>
> cd $FRAMEWORK_HOME/00_parent
> mvn install

> cd $FRAMEWORK_HOME
> mvn install

</pre>
When the process is complete, the output from the process should like like this:
<pre>
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for 000-top 2022.09.0:
[INFO]
[INFO] gov.nih.cc.rmd.framework: 00-nlp-parent ............ SUCCESS [  1.988 s]
[INFO] 01-nlp-resources ................................... SUCCESS [  4.355 s]
[INFO] unmavenedJars ...................................... SUCCESS [  1.786 s]
[INFO] 02-thirdParty ...................................... SUCCESS [  0.060 s]
[INFO] 03-nlp-type-descriptors ............................ SUCCESS [ 22.214 s]
[INFO] 04.0-nlp-util ...................................... SUCCESS [  2.899 s]
[INFO] 04.1-nlp-vUtil ..................................... SUCCESS [  0.772 s]
[INFO] 04.2-nlp-tUtil ..................................... SUCCESS [  2.892 s]
[INFO] 05-nlp-annotators .................................. SUCCESS [  3.247 s]
[INFO] 06-nlp-marshallers ................................. SUCCESS [  4.324 s]
[INFO] 07.0-nlp-pUtils .................................... SUCCESS [  0.904 s]
[INFO] 08-nlp-pipelines ................................... SUCCESS [  1.184 s]
[INFO] 09-00-nlp-NoOpApplication .......................... SUCCESS [ 27.119 s]
[INFO] 09-02-nlp-syntacticApplication ..................... SUCCESS [01:14 min]
[INFO] 09-nlp-applications ................................ SUCCESS [  0.057 s]
[INFO] 000-top ............................................ SUCCESS [  0.053 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  02:28 min
[INFO] Finished at: 2022-08-03T11:42:48-04:00
[INFO] ------------------------------------------------------------------------
</pre>

> mvn eclipse:eclipse
</pre>



# Latest Version
2022-09-0

# Acknowledgments 
See the [Acknowledgments](https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/acknowldgements.md)  page.

# License
See the [License](https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/LICENSE.MD) page.

# Paper(s) to Reference
  See [referenceTheseCitations.md](https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/referenceTheseCitations.md) page.

# Contact
 Guy dot Divita  at   n i h  . gov

# History
2022.09.0 - Initial release version.

# Related Projects

- [Body Function (Strength, Range of Motion, Reflex) Project](https://github.com/CC-RMD-EpiBio/bodyFunction)
- 