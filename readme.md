# Framework Legacy

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

## General Utilities ##

## Readers And Writers ##
marshallers

## Type Descriptors ##
type-descriptors

## Pipeline Utilities ##
pUtils

## Type Descriptor Specific Utilities ##
vUtils

## Term, Dictionary, Token Utilities
tUtil 


# Dependencies

# Compiled Jars

# Building

# Version

# Acknowledgments 

# License

# Contact

# History

# Related Projects