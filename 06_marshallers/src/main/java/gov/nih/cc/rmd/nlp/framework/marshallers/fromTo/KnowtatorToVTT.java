// =================================================
/**
 * KnowtatorToVTT converts a knowtator document to
 * a vtt document 
 *
 * @author  Guy Divita 
 * @created Oct 14, 2014
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.fromTo;

import gov.nih.nlm.nls.vtt.model.ConfigObj;
import gov.nih.nlm.nls.vtt.model.Markup;
import gov.nih.nlm.nls.vtt.model.Markups;
import gov.nih.nlm.nls.vtt.model.Tag;
import gov.nih.nlm.nls.vtt.model.Tags;
import gov.nih.nlm.nls.vtt.model.VttDocument;
import gov.nih.nlm.nls.vtt.model.VttFormat;
import gov.nih.nlm.nls.vtt.model.VttObj;
import gov.va.vinci.knowtator.ClassMention;
import gov.va.vinci.knowtator.ComplexSlotMention;
import gov.va.vinci.knowtator.HasSlotMention;
import gov.va.vinci.knowtator.MentionSlot;
import gov.va.vinci.knowtator.StringSlotMention;
import gov.va.vinci.knowtator.StringSlotMentionValue;
import gov.va.vinci.knowtator.service.KnowtatorReader;
import gov.nih.cc.rmd.nlp.framework.utils.U;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;




public class KnowtatorToVTT  {

  // =======================================================
  /**
   * Constructor KnowtatorToVTT 
   *
   *  @param pOutputDir (where do you want to write this to)
   *  @param labels     (what filtered labels you want to write)
   *  @param pAnnotator (who is doing the writing)
   *  @param projectDir  (where the saved and corpus files are)
   *  
   *  @exception Exception 
   */
  // =======================================================
  public KnowtatorToVTT( ) throws Exception {
  
   initializeColors();
  
  } // end Constructor() ----------------------------------

  // =======================================================
  /**
   * main 
   * 
   * @param pArgs
   */
  // =======================================================
  public static void main(String[] pArgs) {

    
    String[] args = setArgs( pArgs);
    // ----------------------
    // get arguments
    String projectDir = U.getOption(args, "--projectDir=", "./");
    String     vttDir = U.getOption(args, "--vttDir=",     projectDir + "/vtt");
    boolean   recurse = Boolean.valueOf( U.getOption(args, "--recurseIntoSubDirs=", "true")); 
    String     labelz = U.getOption(args, "--labels=",    ""); 
    boolean      help = Boolean.valueOf(  U.getOption(args, "--help=", "false"));
    String   labels[] = null;
    
    if ( labelz != null ) labels = U.split(labelz);
    
    
    // ----------------------
    // Print out usage
    if ( help ) {
      usage();
      return;
    }
    
    try {
      KnowtatorToVTT knowtatorToVTT = new KnowtatorToVTT();
      
      knowtatorToVTT.processFilesFromProjectDir( projectDir, vttDir,  recurse,  labels);
  
    } catch ( Exception e ) {
      e.printStackTrace();
      System.err.println("Something went wrong " + e.toString());
    }

  } // End Method main() ======================
  

  // =======================================================
  /**
   * usage 
   * 
   */
  // =======================================================
  public static void usage() {
   String buff = 
        "  KnowtatorToVTT  converts ehost/knowtator files into vtt files \n" +
        "  \n" +
        "  Usage: \n\n" +
        "  KnowtatorToVTT --projectDir=someDir [--vttDir=someDir] [--recurseIntoSubDirs] [--labels=labelList] | [--help] \n" +
        "\n" +
        "   Where \n" +
        "      --projectDir=someDir   someDir has to be a directory that has a saved and a corpus subdirectory in it \n" +
        "                             and the saved and corpus dirs need to have fileNames that match \n" +
        "                             and the files in the saved dir have to end with .knowtator.xml \n" +
        "      --vttDir=someDir       When obmitted, this value defaults to the projectDir/vtt \n" +
        "      --recurseIntoSubDirs   When set will recurse thru subdirectories to find the files \n" +
        "      --labels=labelList     Where the labelList is a list of those labels to convert, ignoring all others \n" +
        "                             Where the label list is a pipe delimited set of labels \n";
   
   System.out.print( buff);
           
  }  // End Method usage() ======================
  

  // =======================================================
  /**
   * setArgs sets default command line arguments 
   * 
   * @param pArgs
   * @return
   */
  // =======================================================
  private static String[] setArgs(String[] pArgs) {
  
    String projectDir = U.getOption(pArgs, "--projectDir=", "./");
    String     vttDir = U.getOption(pArgs, "--vttDir=",     projectDir + "/vtt");
    String    recurse = U.getOption(pArgs, "--recurseIntoSubDirs=", "true"); 
    String     labelz = U.getOption(pArgs, "--labels=",    ""); 
    String       help = U.getOption(pArgs,  "--help=", "false");
   
    String args[] = { 
        "--projectDir=" + projectDir,
        "--vttDir="     + vttDir,
        "--recurseIntoSubDirs=" + recurse +
        "--labels=" + labelz +
        "--help="   + help
    };
    return args;
      
  
        
  } // End Method setArgs() ======================
  

  // =======================================================
  /**
   * processFilesFromProjectDir
   * 
   * @param pProjectDir
   * @param pOutputDir
   * @param labels
   */
  // =======================================================
  public void processFilesFromProjectDir(String pProjectDir, String pOutputDir, boolean recurseIntoSubDirs, String[] labels) {
    
    // ----------------------------
    // Iterate thru the corpus dir
    HashMap<String,File[]> hashOfFilesToProcess = getFilesToProcess( pProjectDir, recurseIntoSubDirs);
 
    processFiles( pOutputDir, hashOfFilesToProcess, labels);
    
    
  } // End Method processFilesFrom() ======================

  // =======================================================
  /**
   * processFiles processes  
   * 
   * @param pOutputDir
   * @param listOfFilesToProcess
   * @param labels
   */
  // =======================================================
  public List<File> processFiles(String pOutputDir,  HashMap<String,File[]> hashOfSavedFilesToProcess, String[] labels) {
  
    ArrayList<File> vttFiles = new ArrayList<File>( hashOfSavedFilesToProcess.size());
    Set<String> listOfFileNames = hashOfSavedFilesToProcess.keySet();
    
    for ( String fileName : listOfFileNames ) {
      File files[] =  hashOfSavedFilesToProcess.get( fileName);
      
      try {
        File vttFile = processFile( pOutputDir, fileName, files[0], files[1], labels);
        if ( vttFile != null ) 
          vttFiles.add( vttFile);
      } catch (Exception e) {
        e.printStackTrace();
        String msg = "Issue processing the file " + fileName + " " + e.toString();
        System.err.println(msg);
      }
    } // end loop thru files

   
      
    return vttFiles;
    
  
  } // End Method processFiles() ======================
  

  
// =======================================================
  /**
   * processFile transforms a knowtator saved and corpus file into a vtt file
   * 
   * @param pOutputDir
   * @param fileName
   * @param savedFile
   * @param corpusFile
   * @param labels
   * @return List<File>  of vtt files
   * @throws Exception 
   */
  // =======================================================
  public File processFile(String pOutputDir, String fileName, File savedFile, File corpusFile, String[] labels) throws Exception {
    
    File vttFile = null;
    String outputFileName = pOutputDir + "/" + fileName + ".vtt";

    VttDocument vttDoc = new gov.nih.nlm.nls.vtt.model.VttDocument();
    
    gov.va.vinci.knowtator.Document kDoc = KnowtatorReader.read(corpusFile, savedFile);
    
    String text = U.readFile(corpusFile);

    int newLineOffsetList[] = U.getNewlineOffsets(kDoc.getTextSource());
    int oldNewLineOffsetList[] = fixNewLines(newLineOffsetList);

    // -----------------------------------------------------
    // Iterate through the knowtator annotations
    // -----------------------------------------------------
    List<gov.va.vinci.knowtator.Annotation> kAnnotations = kDoc.getAnnotations();
    List<gov.va.vinci.knowtator.ClassMention> kClassMentions = kDoc.getClassMentions();
    List<gov.va.vinci.knowtator.StringSlotMention> kStringSlotMentions = kDoc.getStringSlotMentions();
    List<gov.va.vinci.knowtator.ComplexSlotMention> kComplexSlotMentions = kDoc.getComplexSlotMentions();

    // --------------------------------------------
    // Create a knowtator annotation hash, key'd on the mention id
    // --------------------------------------------
    Hashtable<String, gov.va.vinci.knowtator.Annotation> kAnnotationHash = createKnowtatorAnnotationHashFromList(kAnnotations);
    Hashtable<String, gov.va.vinci.knowtator.StringSlotMention> kSlotMentionHash = createKnowtatorSlotMentionHashFromList(kStringSlotMentions);
    Hashtable<String, gov.va.vinci.knowtator.ComplexSlotMention> kComplexSlotMentionHash = createKnowtatorComplexSlotMentionHashFromList(kComplexSlotMentions);

    // --------------------------------------------
    // Index classMentions (UIMA Annotation labels), and tie them back to the
    // spans
    // --------------------------------------------
    gov.va.vinci.knowtator.Annotation kAnnotation = null;
    Markups markups = new Markups();
    Tags tags = new Tags();
    
    HashSet<String> tagHash = new HashSet<String>();
    for (ClassMention cm : kClassMentions) {
      String annotationId = cm.getId();
      String label = cm.getMentionClass().getId();
      kAnnotation = kAnnotationHash.get(annotationId);
      tagHash.add(label);
     
      if ( kAnnotation == null ) {
        System.err.println("issue with can't find the kannotation for annotation id " + annotationId);
      } else {
      

      // ----------------------------------------------
      // create a VTT markup (annotation) from this label
      // ----------------------------------------------
      Markup   markup = new Markup();
      try {

      
      
        StringBuffer attributes = new StringBuffer();

        // --------------------------------------------------
        // Add span and slot attributes to the markup
        // --------------------------------------------------
       
        if ( kAnnotation.getSpan() == null ) {
          System.err.println("No span for id :" + annotationId + "|" + kAnnotation.getSpannedText());
          continue;
        }
        int beginOffset = kAnnotation.getSpan().getStart();
        try {
         beginOffset = fixOffset(kAnnotation.getSpan().getStart(), oldNewLineOffsetList);
        } catch (Exception e2 ) {
          e2.printStackTrace();
         
          System.err.println("Issue with fixing the offset " + "|" + oldNewLineOffsetList.length + "|" + kAnnotation.getSpan());
          throw new RuntimeException();
        }
       
        markup.setOffset(beginOffset);
        markup.setLength( kAnnotation.getSpannedText().length());
      
        // ----------------------------------------------------
        // find the features(slots/slotMentions) associated with this
        // annotation(classMention)
        // -----------------------------------------------------
        ArrayList<HasSlotMention> slotMentions = cm.getHasSlotMentions();

        for (HasSlotMention slotMention : slotMentions) {
          StringSlotMention mention = kSlotMentionHash.get(slotMention.getId());
          if (mention == null) {

            // --------------------------------------------------
            // see if there are any complex slot mentions - these are
            // relationships to other concepts - Convert these to attributes 
            //  i.e. negation or assertion ConceptConvert any relationships to attributes
            // --------------------------------------------------
            ComplexSlotMention mentionX = kComplexSlotMentionHash.get( slotMention.getId());
            MentionSlot mentionSlot = mentionX.getMentionSlot();
            String      featureName = mentionSlot.getId();
            
            // ----------------------------------- not needed 
            //  val = mentionX.getComplexSlotValue();
            // if (val != null) {
            //  String featureValue = val.getValue();
            // } ----------------------------------
            
            // ----------------------------
            // Right now, we've only got one relationship mapping - I cannot
            // think of a generic way of mapping it other than hard coding it
            // featureName = "Negation_Yes" <==> uima attribute = NegationStatus with a value of "Negated"
            if ( featureName.equals("Negation_Yes")) {
            
                attributes.append( "Negation_Status=Negated<::>");
            }
            // System.err.println("Missing a mention for " + slotMention.getId());
          } else {
            // String mentionId = mention.getId();
            MentionSlot mentionSlot = mention.getMentionSlot();
            String featureName = mentionSlot.getId();
            StringSlotMentionValue val = mention.getStringSlotMentionValue();
            String featureValue = "true";
            if (val != null) { 
               featureValue = val.getValue();
               
              attributes.append(featureName + "=" + featureValue + "<::>");
            }
          }

        } // end loop through the attributes/slots/features of this
          // annotation/classMention
        markup.setAnnotation(attributes.toString());
        markup.setTagName(label);
        markup.setTagCategory("|"); //  like a semanitc type or cui
        
      } catch (Exception e) {
        e.printStackTrace();
        String msg = "Issue with fromKnowtator " + e.toString() ;
        throw new Exception(msg);
      }
      markups.addMarkup(markup);
      }
      } // end loop thru knowtator annotations
     
      // --------------------------
      // Assemble the vtt doc
      vttDoc.setMarkups(markups);
      vttDoc.setText(text);
      tags = initializeTags( tagHash);
      vttDoc.setTags(tags);
      
      vttFile = toVTTFile( vttDoc, outputFileName);
      
      
    return vttFile;
    
       
  }  // End Method fromKnowtator() ======================

  // =======================================================
  /**
   * toVTTFile prints out the vttDoc to a file
   * 
   * @param vttDoc
   * @return File
   */
  // =======================================================
  public File toVTTFile(VttDocument vttDoc, String pOutputFileName) {
    VttObj vttObj = new VttObj( (File) null, null,0,0 ,false);
    
    ConfigObj configObj = vttObj.getConfigObj();
    configObj.setDocDir(new File( this.outputDir));
    configObj.setUserName(this.annotator);
    int vttFormat = VttFormat.READABLE_FORMAT;
   
    configObj.setVttDir(this.outputDir);
    configObj.setVttFormat(vttFormat);
    

    File outputFile = new File ( pOutputFileName);
    VttDocument.saveFile(outputFile, vttDoc, vttFormat, vttObj);
    
    return outputFile;
    
  } // End Method toVTTFile() ======================
  

  private int[] fixNewLines(int[] newLineOffsetList) {

    int oldNewLines[] = new int[newLineOffsetList.length];

    for (int i = 0; i < newLineOffsetList.length; i++)
      oldNewLines[i] = newLineOffsetList[i] - i;
    return oldNewLines;
  }

  // -----------------------------------------
  /**
   * fixOffset will add X to the offset to fix an issue
   * where eHOST strips \r's off the text, when computing the offsets.
   * 
   * @param pOffset
   * @param newLineOffsetList
   * @return
   */
  // -----------------------------------------
  private int fixOffset(int pOffset, int[] newLineOffsetList) {
    int newOffset = pOffset;

    if (this.fixOffsets == true) {
      int lineNumber = getLineNumber(pOffset, newLineOffsetList);
      newOffset += (lineNumber);
    }

    return newOffset;
  } // end Method fixOffset() -------------------------


  
  // -----------------------------------------
  /**
   * getLineNumber returns the line number given an offset,
   * and a list of newline offsets. First line is 1.
   * 
   * @param pOffset
   * @param newLineOffsetList
   * @return int
   */
  // -----------------------------------------
  private int getLineNumber(int pOffset, int[] newLineOffsetList) {
    int lineNumber = 0;
    for (int i = 0; i < newLineOffsetList.length; i++)
      if (newLineOffsetList[i] > pOffset) {
        lineNumber = i;
        break;
      }
    // int lineNumber2 = Math.abs(Arrays.binarySearch(newLineOffsetList, pOffset));

    return lineNumber;
  } // end Method getLineNumber() ----------------

  // ----------------------------------------------
  /**
   * createKnowtatorAnnotationHashFromList creates a hash of annotations
   * key'd on mention ids
   * 
   * @param kAnnotations
   * @return Hashtable
   */
  // ----------------------------------------------
  private Hashtable<String, gov.va.vinci.knowtator.Annotation> createKnowtatorAnnotationHashFromList(
      List<gov.va.vinci.knowtator.Annotation> kAnnotations) {

    Hashtable<String, gov.va.vinci.knowtator.Annotation> hash = new Hashtable<String, gov.va.vinci.knowtator.Annotation>(
        kAnnotations.size() * 2);

    for (gov.va.vinci.knowtator.Annotation annotation : kAnnotations)
      hash.put(annotation.getMention().getMentionId(), annotation);

    return hash;
  } // end Method createKnowtatorAnnotationHashFromList() ---

  // ----------------------------------------------
  /**
   * createKnowtatorSlotMentionsHashFromList creates a hash of annotations
   * key'd on mention ids
   * 
   * @param kAnnotations
   * @return Hashtable
   */
  // ----------------------------------------------
  private Hashtable<String, gov.va.vinci.knowtator.StringSlotMention> createKnowtatorSlotMentionHashFromList(
      List<gov.va.vinci.knowtator.StringSlotMention> kStringSlotMentions) {

    Hashtable<String, gov.va.vinci.knowtator.StringSlotMention> hash = new Hashtable<String, gov.va.vinci.knowtator.StringSlotMention>(
        kStringSlotMentions.size() * 2);

    for (gov.va.vinci.knowtator.StringSlotMention stringSlotMention : kStringSlotMentions)
      hash.put(stringSlotMention.getId(), stringSlotMention);

    return hash;
  } // end Method createKnowtatorAnnotationHashFromList() ---

  // ------------------------------------------
  /**
   * createKnowtatorComplexSlotMentionHashFromList
   * 
   * 
   * @param kComplexSlotMentions
   * @return
   */
  // ------------------------------------------
  private Hashtable<String, ComplexSlotMention> createKnowtatorComplexSlotMentionHashFromList( List<ComplexSlotMention> kComplexSlotMentions) {
   
    Hashtable<String, gov.va.vinci.knowtator.ComplexSlotMention> hash = new Hashtable<String, gov.va.vinci.knowtator.ComplexSlotMention>(
        kComplexSlotMentions.size() * 2);

    for (gov.va.vinci.knowtator.ComplexSlotMention complexSlotMention : kComplexSlotMentions)
      hash.put(complexSlotMention.getId(), complexSlotMention);

    return hash;
  } // End Method createKnowtatorComplexSlotMentionHashFromList() -----------------------

 


// =======================================================
  /**
   * getFilesToProcess retrieves files to process from the projectDir
   * This projectDir should include both a saved and corpus directory
   * with corresponding files in both.  This method assumes that
   * the files in the saved directory end in .knowtator.xml
   * 
   * @param pProjectDir
   * @param recurseIntoSubDirs
   * @return HashMap<String, File[]>  where 
   *                                      the key is the fileName devoid of a path from the corpus dir
   *                                      the file[0] is the file in the saved dir
   *                                      the file[1] is the file in the corpus dir
   */
  // =======================================================
  private HashMap<String,File[]> getFilesToProcess(String pProjectDir, boolean recurseIntoSubDirs) {
    
    HashMap<String, File[]> hashOfFilesToProcess = null;
    List<File >listOfFilesToProcess = new ArrayList<File>();
  
    File inputSavedDir = null;
    if ( pProjectDir != null && pProjectDir.length() > 0 ) {
      
      String savedDir = pProjectDir + "/saved";
      
      inputSavedDir = new File ( savedDir );
      
      if ( inputSavedDir.isDirectory() && inputSavedDir.canRead() ) {
        
        File[] filesAndSubDirs = inputSavedDir.listFiles();
          
        if ( recurseIntoSubDirs ) {
           getFiles( listOfFilesToProcess, filesAndSubDirs );
        } else {
           listOfFilesToProcess = filterFiles( filesAndSubDirs) ;
        }
      } // end if the inputSavedDir can be read                
  
      
    
      // ------------------------------
      // find the corpus files that match the files in the saved dir
      hashOfFilesToProcess = new HashMap<String, File[]>(listOfFilesToProcess.size());
      String inputCorpusDir = pProjectDir + "/corpus";
      for ( File aSavedFile : listOfFilesToProcess ) {
        
        String fileName_with_suffix    = aSavedFile.getName();
        String fileName_without_suffix = U.getFileNamePrefix(fileName_with_suffix);
        
        File aCorpusFile = getFileFromDir( inputCorpusDir, fileName_without_suffix);
        if ( aCorpusFile != null ) {
          File[] files = new File[2];
          files[0] = aSavedFile;
          files[1] = aCorpusFile;
          hashOfFilesToProcess.put( fileName_without_suffix,  files);
        } // if a corpus file as found
        else {
          System.err.println("Could not find the corpus file for the saved file " + fileName_with_suffix);
        }
      } // end loop thru the saved files
      
    
    } // end if the inputSavedDir is not null
    
    
    int numberOfFiles = listOfFilesToProcess.size();
    System.err.println("The number of files to process = " + numberOfFiles);
    
    return hashOfFilesToProcess;
    // End Method getFilesToProcess() ======================
  }

  // =======================================================
  /**
   * getFileFromDir finds a file from a dir that contains the fileName
   * pattern
   * 
   * @param inputCorpusDir
   * @param fileName_without_suffix
   * @return File
   */
  // =======================================================
  private File getFileFromDir(String inputCorpusDir, String fileName_without_suffix) {
  
    File  returnVal = null;
    File aDir = new File (inputCorpusDir);
    
    File[] listOfFiles = aDir.listFiles();
    for ( File aFile : listOfFiles ) {
      if ( aFile.getName().contains( fileName_without_suffix)) {
        returnVal = aFile;
        break;
      }
      
    }
    
    return returnVal;
  } // End Method getFileFromDir() ======================

//=======================================================
/**
* filterFiles returns a list of text files
* 
* @param pFiles
* @return List<File>
*/
// =======================================================
private List<File> filterFiles(File[] pFiles) {

 ArrayList<File> filteredFiles = new ArrayList<File>(pFiles.length);
 for ( File file: pFiles )
   if ( filterInKnowtatorFiles( file))
     filteredFiles.add( file);
   
 return filteredFiles;
}  // End Method filterFiles() ======================


// =======================================================
/**
 * filterInKnowtatorFiles returns true if the file is a knowtator file
 * 
 * @param pFile
 * @return boolean
 */
// =======================================================
private boolean filterInKnowtatorFiles(File pFile) {
 
  boolean returnValue = false;
  if ( pFile.getName().endsWith(".knowtator.xml") ) 
      returnValue = true;
    
  return returnValue;
  
}  // End Method filterInKnowtatorFiles() ======================


// =======================================================
/**
* getFiles retrieves the list of files from the directory
* that match the filtering criteria
* 
* @param pListOfFiles
* @param pFilesAndSubDirs
* 
*/
// =======================================================
private void  getFiles(List<File> pListOfFiles, File[] pFilesAndSubDirs) {
 
 for ( File file: pFilesAndSubDirs )
   if ( file.isDirectory()) {
     getFiles( pListOfFiles, file.listFiles());
   } else if ( filterInKnowtatorFiles ( file )){
    // System.err.println("adding file " + file.getAbsolutePath());
     pListOfFiles.add( file);
   }
 
}   // End Method getFiles() ======================
	
	


// =======================================================
/**
 * initializeTags creates tags for all the labels seen.
 *    Internal debate here - should this file contain all the labels
 *    from type.descriptor (which are huge and not useful for a reviewer)
 *    or those that are are just seen in this document which could
 *    omit ones that a reviewer might want to add later on
 *    
 *    OK - Debate over - will only add those tags that are passed in
 *    UNLESS
 *    No tags are passed in, in which case, all the tags seen
 *    are added.
 *    
 *    Also, the vtt labels will be devoid of name spaces.
 * 
 * @param pJCas
 * @return
 */
// =======================================================
private Tags initializeTags( HashSet<String> pLabels) {

 
  Tags pTags = new Tags();
  Tag tag = null;
  
  // --------------------------------
  // Add default Tags
  try {
   tag = new Tag("highlight","" , true, BOLD, ITALIC, UNDERLINE, Color.black, Color.lightGray, "Monospaced", "+0" );  pTags.addTag(tag);
   tag = new Tag("True",     "" , true, BOLD, ITALIC, UNDERLINE, Color.black, Color.GREEN,     "Monospaced", "+0" );  pTags.addTag(tag);
   tag = new Tag("False",    "" , true, BOLD, ITALIC, UNDERLINE, Color.black, Color.RED,       "Monospaced", "+0" );  pTags.addTag(tag);
   tag = new Tag("Uncertain","" , true, BOLD, ITALIC, UNDERLINE, Color.black, Color.PINK,      "Monospaced", "+0" );  pTags.addTag(tag);
  } catch (Exception e) {
    String msg = "Something went wrong here : " + e.toString() + "\n" + U.getStackTrace(e);
    System.err.println(msg);
  }
  
  // --------------------------------
  // Add those that were passed in via configuration
  
  if ( pLabels != null )
    for ( String label: pLabels ) {
      
      label = U.getNameWithoutNameSpace( label);
 
      // ------------------------------
      // break labels to label:category
      String labelAndCategory[] = getLabelAndCategory( label);
      
      
      int colorPattern = generateAnnotationColors();
      
      tag = new Tag(labelAndCategory[LABEL], labelAndCategory[CATEGORY], true, BOLD, ITALIC, UNDERLINE, colors[colorPattern][FORGROUND], colors[colorPattern][BACKGROUND], "Monospaced", "+0" );
      pTags.addTag(tag);                  
      
      
    } // end loop through the labels
  if ( pTags == null || pTags.getNameList().size() == 0 ) {
    System.err.println("No tags created");
    pTags = null;
  }
  
  return pTags;
  
  
}  // End Method createTags() ======================


// =======================================================
/**
 * getLabelAndCategory returns the label in [0] and the category in [1].  
 * It is common that there will be no category, so [1] will be null.
 * 
 * @param label
 * @return
 */
// =======================================================
private String[] getLabelAndCategory(String pLabel) {

  String       category = null;
  String          label = null;
  String [] returnValue = new String[2];
  String labelAndCategory[] = U.split(pLabel, ":");
  label = labelAndCategory[0];
  if ( labelAndCategory.length == 2)
    category = labelAndCategory[1];
  returnValue[0] = label;
  
  if ( category == null || category.length() == 0 || category.equals("null"))
    category = "";
  returnValue[1] = category;
  
  return returnValue;
  
} // End Method getLabelAndCategory() ======================



// =======================================================
/**
 * generateAnnotationColors will create up to X combinations of colors 
 * 
 * @return int pointer into the preconfigured colors array
 */
// =======================================================
private int generateAnnotationColors() {

 int currentColor = 0;
 
 if ( this.nextColor < colors.length -1 )
   this.nextColor++;
 else
   this.nextColor = 0;
 
 currentColor = this.nextColor;
  
  return currentColor;
  // End Method generateAnnotationColors() ======================
}


// =======================================================
/**
 * initializeColors predefines 28 background/forground color combinations
 * 
 */
// =======================================================
private static void initializeColors() {

  colors = new Color[24][2]; 
 
  /*   reserved colors 
   *    highlight
  colors[9][BACKGROUND] = Color.gray;
  colors[9][FORGROUND] = Color.black;
  
  *  true
  colors[3][BACKGROUND] = Color.green;
  colors[3][FORGROUND] = Color.black;
  
  *  false
  colors[11][BACKGROUND] = Color.red;
  colors[11][FORGROUND] = Color.black;
    
  * uncertain
  colors[0][BACKGROUND] = Color.pink;
  colors[0][FORGROUND] = Color.black;
  */
  
  colors[0][BACKGROUND] = Color.magenta;
  colors[0][FORGROUND] = Color.white;
  
  colors[1][BACKGROUND] = Color.orange;
  colors[1][FORGROUND] = Color.black;
     
  
  colors[2][BACKGROUND] = Color.yellow;
  colors[2][FORGROUND] = Color.black;
  
  colors[3][BACKGROUND] = Color.cyan;
  colors[3][FORGROUND] = Color.black;
  
  colors[4][BACKGROUND] = Color.blue;
  colors[4][FORGROUND] = Color.white;
  
  
  colors[5][BACKGROUND] = Color.black; 
  colors[5][FORGROUND] = Color.white;
  
  colors[6][BACKGROUND] = Color.lightGray;
  colors[6][FORGROUND] = Color.black;
  

  
  colors[7][BACKGROUND] = Color.darkGray;
  colors[7][FORGROUND] = Color.white;
  

  
 
  
  colors[8][BACKGROUND] = Color.red.brighter();
  colors[8][FORGROUND] = Color.black;
  
  colors[9][BACKGROUND] = Color.pink.brighter();
  colors[9][FORGROUND] = Color.black;
  
  colors[10][BACKGROUND] = Color.orange.brighter();
  colors[10][FORGROUND] = Color.black;
  
  colors[11][BACKGROUND] = Color.yellow.brighter();
  colors[11][FORGROUND] = Color.black;
  
  colors[12][BACKGROUND] = Color.green.brighter();
  colors[12][FORGROUND] = Color.black;
  
  colors[13][BACKGROUND] = Color.magenta.brighter();
  colors[13][FORGROUND] = Color.white;
  
  colors[14][BACKGROUND] = Color.cyan.brighter();
  colors[14][FORGROUND] = Color.black;
  
  colors[15][BACKGROUND] = Color.blue.brighter();
  colors[15][FORGROUND] = Color.white;
  
  colors[16][BACKGROUND] = Color.red.brighter();
  colors[16][FORGROUND] = Color.black;
  
  colors[17][BACKGROUND] = Color.pink.darker();
  colors[17][FORGROUND] = Color.black;
  
  colors[18][BACKGROUND] = Color.orange.darker();
  colors[18][FORGROUND] = Color.black;
  
  colors[19][BACKGROUND] = Color.yellow.darker();
  colors[19][FORGROUND] = Color.black;
  
  colors[20][BACKGROUND] = Color.green.darker();
  colors[20][FORGROUND] = Color.black;
  
  colors[21][BACKGROUND] = Color.magenta.darker();
  colors[21][FORGROUND] = Color.white;
  
  colors[22][BACKGROUND] = Color.cyan.darker();
  colors[22][FORGROUND] = Color.black;
  
  colors[23][BACKGROUND] = Color.blue.darker();
  colors[23][FORGROUND] = Color.white;
  // End Method initializeColors() ======================
}



  // ----------------------------------------
	// Class Variables
	// ----------------------------------------
  private boolean fixOffsets = false;
  private static final int LABEL = 0;
  private static final int CATEGORY = 1;
  private static final boolean BOLD = false;
  private static final boolean ITALIC = false;
  private static final boolean UNDERLINE = false;
  private static final int FORGROUND = 0;
  private static final int BACKGROUND = 1;
  public static Color[][]  colors = null;
  

  private int nextColor = 0;
  private String annotator = "v3NLP";

  private String outputDir;



  
	

} // end Class KnowtatorToVTT() ----
