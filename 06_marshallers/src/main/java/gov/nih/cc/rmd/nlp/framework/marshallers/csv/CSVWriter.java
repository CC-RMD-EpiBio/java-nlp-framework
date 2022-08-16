/**
 * CSVWriter writes out cas's into csv formatted files
 *
 * @author  Guy Divita 
 * @created May 3, 2018
 *
 *  
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.csv;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gate.util.Out;
import gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.FeatureValuePair;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.NewlineToken;
import gov.va.chir.model.WordToken;

/**
 * The Class CSVWriter.
 */
public class CSVWriter implements Writer {

  // =======================================================
  /**
   * Constructor ToCSV creates a CSV writer.
   * 
   *
   * @param pArgs the args
   * @throws ResourceInitializationException the resource initialization
   *           exception
   */
  // =======================================================
  public CSVWriter(String[] pArgs) throws ResourceInitializationException {

    this.initialize(pArgs);

  } // end Constructor() ---------------------

  // -----------------------------------------
  /**
   * 
   * process iterates through all annotations, filters out those that should be
   * filtered out, then pushes them into a database store.
   * 
   * Each document should include a DocumentAnnotation annotation and a
   * documentHeader annotation for re-animation purposes.
   *
   * @param pJCas the j cas
   * @throws AnalysisEngineProcessException the analysis engine process
   *           exception
   */
  // -----------------------------------------
  @Override
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
      this.performanceMeter.startCounter();

      
        String fileName = VUIMAUtil.getDocumentId(pJCas);
         if ( fileName != null ) {
           if ( fileName.contains("/") || fileName.contains("\\") )
             fileName = U.getFileNameOnly(fileName);
           fileName = fileName.replace(" ", "_");
         }
         writeOutText( fileName, pJCas);
       
         
         PrintWriter out = new PrintWriter( this.outputDir + "/csv/" + fileName + ".csv");
         
      switch( this.formatType ) {
        case formatType_Tokenized :          
          processWithTokenized ( pJCas, out); break;
        case formatType_With_Attributes :    
        default:                              processWithAttributes( pJCas, out); break;
      }
      
      out.close();
      

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,this.getClass(), "process", "Issue with cvs process " + e.toString());
   
    }
    this.performanceMeter.stopCounter();

  } // end Method process() --------------------------
  
  // =================================================
  /**
   * processWithTokenized    begin|length|label|token1|token2|...|tokenN
   * 
   * @param pJCas
   * @param pOut
  */
  // =================================================
 private final void processWithTokenized(JCas pJCas, PrintWriter pOut) {
   

   try {
 
    // Itterate though the annotations that are of interest
    List<Annotation> annotations = UIMAUtil.getAnnotations(pJCas);
    
    if (annotations != null) {
      for (Annotation annotation : annotations) {
        String label =  annotation.getClass().getName();
        if (isAnOutputLabel( label )) {
         String row = createCSVRowTokenized( pJCas, annotation );
         pOut.print(row);
      }

        }
      }
   
   } catch (Exception e) {
     e.printStackTrace();
     
   }
    
  } // end Method processWithTokenized() -------------
 
//=================================================
/**
 * createCSVRow 
 * 
 * @param pJCas
 * @param pLabel
 * @param annotation
 * @return String   (with a \n)
*/
// =================================================
private String createCSVRowTokenized( JCas pJCas, Annotation pAnnotation) {
  StringBuffer row = new StringBuffer();


    row.append(pAnnotation.getBegin());
    row.append("|");
    try {
      row.append(pAnnotation.getCoveredText().length());
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("something went wrong here: " + e.toString());
      row.append( "1");
    }
    
    row.append("|");
  
  String label =  U.getNameWithoutNameSpace(pAnnotation.getClass().getName());
  
  // if ( label.contains(" "))
  //  label = Boolean.toString( pAnnotation).getX;
  row.append( label );
  row.append("|");
  
  row.append( tokenizeAnnotationSpan( pJCas, pAnnotation ));
 
  row.append("\n");

 return row.toString();
 
}  // end Method createCSVRow() -------------------
  

  // =================================================
/**
 * tokenizeAnnotationSpan loops through the word tokens
 * of the annotation
 * @param pAnnotation
 * 
 * @return String
*/
// =================================================
private String tokenizeAnnotationSpan(JCas pJCas, Annotation pAnnotation) {

  StringBuffer buff = new StringBuffer();
  List<Annotation>   tokens = UIMAUtil.getAnnotationsBySpan(pJCas, WordToken.typeIndexID, pAnnotation.getBegin(), pAnnotation.getEnd());
  List<Annotation> newLines = UIMAUtil.getAnnotationsBySpan(pJCas, NewlineToken.typeIndexID, pAnnotation.getBegin(), pAnnotation.getEnd());
  
  
  
  if ( tokens != null && !tokens.isEmpty() ) {
    if ( newLines != null && !newLines.isEmpty())
      tokens.addAll( newLines);
    UIMAUtil.sortByOffset( tokens);
   UIMAUtil.uniqueAnnotations(tokens);
  
   for ( int i = 0; i < tokens.size(); i++  ) {
      if ( tokens.get(i) != null ) {
        
        if ( tokens.get(i).getClass().getName().contains("Newline"))
         buff.append(((NewlineToken)tokens.get(i)).getDisplayString());
        else if (((WordToken) tokens.get(i)).getReplaceWithClass() )
          buff.append (U.normalizePipesAndNewLines( ((WordToken)tokens.get(i)).getDisplayString()));
        else 
          buff.append (U.normalizePipesAndNewLines( ((WordToken)tokens.get(i)).getCoveredText()));
      }
      if ( i < tokens.size() -1 )
        buff.append("|");
      }
    }
  return buff.toString();
} // end Method tokenizeAnnotationSpan() -------------

  // =================================================
  /**
   * processWithAttributes
   * 
   * @param pJCas
   * @param out 
  */
  // =================================================
 private final  void processWithAttributes(JCas pJCas, PrintWriter out) {
   
  
   try {
 
    // Itterate though the annotations that are of interest
    List<Annotation> annotations = UIMAUtil.getAnnotations(pJCas);
    
    if (annotations != null) {
      for (Annotation annotation : annotations) {
        String label =  annotation.getClass().getName();
        if (isAnOutputLabel( label )) {
         String row = createCSVRow( annotation );
         out.print(row);
      }

        }
      }
   
   } catch (Exception e) {
     e.printStackTrace();
     
   }
  } // end Method processWithAttributes() ------------

  // =================================================
  /**
   * writeOutText writes out the text into the txt dir
   * 
   * @param fileName
   * @throws Exception
  */
  // =================================================
 private void writeOutText(String fileName, JCas pJCas) throws Exception {
    
    try { 
      PrintWriter out = new PrintWriter( this.outputDir + "/txt/" + fileName + ".txt");
      String coveredText = pJCas.getDocumentText(); 
      out.print( coveredText );
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "writeOutText", "Issue writing out the text file " + e.toString());
    }
    
  }

  // =================================================
  /**
   * createCSVRow 
   * 
   * @param pLabel
   * @param annotation
   * @return String   (with a \n)
  */
  // =================================================
  private String createCSVRow( Annotation pAnnotation) {
    StringBuffer row = new StringBuffer();
  
    String label =  U.getNameWithoutNameSpace(pAnnotation.getClass().getName());
    row.append( label );
    row.append("|");
    
    row.append(pAnnotation.getBegin());
    row.append("|");
    row.append(pAnnotation.getEnd());
    row.append("|");
    row.append(  U.normalize( pAnnotation.getCoveredText()));
    row.append("|");
   
    
  
    String attributes = null;
    switch ( label ) {
   
      default:
                             attributes = getPipeDelimitedAttributes( pAnnotation);   break;
    }
    row.append( attributes );
    row.append("\n");
  
   return row.toString();
   
  }  // end Method createCSVRow() -------------------
    
   
  // =================================================
  /**
   * getAttributes 
   * 
   * @param annotation
   * @return
  */
  // =================================================
   private String getAttributes(Annotation annotation, HashSet<String> annotationFeatures ) {
   
     List<FeatureValuePair> featureValues = UIMAUtil.getFeatureValuePairs( annotation);
     
     String returnVal = null;
     if ( featureValues != null ) {
       StringBuffer row = new StringBuffer();
       for (FeatureValuePair fvp : featureValues ) {
         String featureName = fvp.getFeatureName();
         if ( annotationFeatures.contains( featureName ) ) {
           String value = fvp.getFeatureValue();
           if ( value == null ) value = "";
           row.append(value );
           row.append("|");
         }
       }
       if ( row != null && row.length() > 1 ) 
         returnVal = row.substring(0, row.length() -1);
     }
     
    return returnVal;
  }

   // =================================================
   /**
    * writeCSVLabelAttributes writes out a file with
    * label|attributeLabel1|attributeLabel2 ... 
   * @throws Exception 
    * 
    * 
    * 
   */
   // =================================================
    private void writeCSVLabelAttributes( ) throws Exception {
    
      
      Set<String> keyz = LabelHash.keySet(); 
      String keys[] = keyz.toArray(new String[ keyz.size()]);
      Arrays.sort( keys );
      PrintWriter out = null;
      try {
      
        out = new PrintWriter( this.outputDir + "/csv/csvLabelsAndColumns.txt");
        
        if ( this.formatType.contentEquals(formatType_Tokenized ) ) {
          out.print("begin|length|DocumentAnnotation|Token1|Token2|...\n");
          out.print("begin|length|Label|Content\n");
          
        } else {
      if ( keys != null )
        for ( String key: keys ) {
          String row = getAttributeNameOrder(    LabelHash.get(key ));
          out.print( U.getNameWithoutNameSpace( key ) + "|begin|end|mention|" + row + "\n" );
        }
      
        }
      
      out.close();
      
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "writeCSVLabelAttributes", "Issue writing the csv label " + e.toString());
        throw new Exception (e );
        
      }
     
   }
  // =================================================
  /**
   * getAttributeNameOrder writes out the list of attributes
   * Note that these should be in the same order that 
   * they come out when writing a csv file out.
   * 
   * Right now it is random
   * 
   * @param pAttributes
   * @return String
  */
  // =================================================
 private final String getAttributeNameOrder(HashSet<?> pAttributes ) {
   
   String returnVal = null;
   if (pAttributes != null ) {
     StringBuffer buff = new StringBuffer();
     for ( Object attribute : pAttributes ) {
       buff.append( attribute);
       buff.append("|");
     }
   
     if ( buff != null && buff.length() > 1)
       returnVal = buff.substring(0, buff.length() -1);
   } 
   return returnVal;
   
  } // end Method writeAttributeName() ----------------

 
 
  // =================================================
  /**
   * getPipeDelimitedAttributes builds a row of the attribute values from the 
   * feature value pairs that come from the annotation.  The ordering of the feature values
   * is taken from the order that come out of the LabelHash enumeration of feature names
   * for the label.   
   * 
   * @param pAnnotation
   * @return String  (pipe delimited, in the order they were on the type descriptor)
  */
  // =================================================
   private String getPipeDelimitedAttributes(Annotation pAnnotation) {
    List<FeatureValuePair> featureValues = UIMAUtil.getFeatureValuePairs( pAnnotation);
    
    String returnVal = null;
    StringBuffer row = new StringBuffer();
    if ( featureValues != null ) {
      
    
      
      String label = pAnnotation.getClass().getName();
      
     
      
      String rowz = getAttributeNameOrder( getLabelHash( label) );
    
       if ( rowz != null && rowz.trim().length() > 0 ) {
        String featureNames[] = U.split( rowz);
        
        for ( String featureName : featureNames ) {
          
         String value = getFeatureValue( featureValues, featureName);
         row.append( value);
         row.append("|"); 
        }
        
      }
    }
    if ( row != null && row.toString().trim().length() > 0  )
        returnVal = row.toString().substring(0, row.length() -1);
      
     
    return returnVal;
   } // end Method getAttributes() -------------------

  // =================================================
  /**
   * getLabelHash returns the hash of the label attributes
   * given a label. If the label has a name space, but
   * the hash only has the simple name in it, it strips
   * the name space and tries to find it with the simple name.
   * 
   * @param pLabel
   * @return HashSet<?>
  */
  // =================================================
private HashSet<?> getLabelHash(String pLabel) {
   
  HashSet<?> returnVal = null;
  
  returnVal = LabelHash.get( pLabel );
  if ( returnVal == null )
    returnVal = LabelHash.get( U.getNameWithoutNameSpace(pLabel ));
    
  return returnVal;
  
  } // end Method getLabelHash() -----------------------

  // =================================================
  /**
   * getFeatureValue from the feature value pairs
   * 
   * @param pFeatureValues
   * @param pFeatureName
   * @return String
  */
  // =================================================
  private String getFeatureValue(List<FeatureValuePair> pFeatureValues, String pFeatureName) {

    String returnValue = null;
    for (int i = 0; i < pFeatureValues.size(); i++ ) {
      FeatureValuePair fvp = pFeatureValues.get(i);
      if ( fvp.getFeatureName().contentEquals( pFeatureName ) ) {
          returnValue = fvp.getFeatureValue();
          break;
      }
     }
    
    return returnValue;
    
  } // end Method getFeatureValue() ------------------

  // =================================================
  /**
   * getLabel returns the label sans the namespace.
   *
   * @param pLabel the annotation
   * @return String
   */
  // =================================================
  private final String getLabel(Annotation pLabel) {

    String returnVal = U.getNameWithoutNameSpace(pLabel.getClass().getName());

    return returnVal;
  } // end Method getLabel() -------------------------

  // =================================================
  /**
   * isAnOutputLabel .
   *
   * @param pLabel the label
   * @return boolean
   */
  // =================================================
  private final boolean isAnOutputLabel(String pLabel) {

    boolean returnVal = false;

    
    if ( this.outputTypez == null || this.outputTypez.isEmpty()  )
      returnVal = true;
      else 
        returnVal = this.outputTypez.contains(pLabel);
    
    if ( !returnVal ) 
      returnVal = this.outputTypez.contains( U.getNameWithoutNameSpace( pLabel ));
    
    
    if ( returnVal ) {
      if (observedLabelHash.get( pLabel ) == null )
        observedLabelHash.put( pLabel, createAttributeHashForAnnotation( pLabel) );
    }
      
    return returnVal;
  } // end Method isAnOutputLabel() ------------------

  // end Method process

  // =================================================
  /**
   * getFileName divines the file name from the documentHeader's documentId.
   * What gets returned is devoid of a namespace. If the documentId is a
   * filename, the extension is stripped off in this method.
   *
   * @param pJCas the j cas
   * @return String
   */
  // =================================================
  public static String getFileName_(JCas pJCas) {
    String fileName = null;
    fileName = VUIMAUtil.getDocumentId(pJCas);
    if (fileName.endsWith(".txt") || fileName.endsWith(".csv") || fileName.endsWith(".rpt")
        || fileName.endsWith(".doc") || fileName.endsWith(".docx") || fileName.endsWith(".xlsx")) {

      int lastPeriod = fileName.lastIndexOf('.');
      fileName = fileName.substring(0, lastPeriod);
    }

    return fileName;
  } // end Method getFilename() ------------------

  // -----------------------------------------
  /**
   * 
   * destroy.
   */
  // -----------------------------------------
  @Override
  public void destroy() {

    try {
     
      if ( LabelHash.isEmpty()) {
        LabelHash = observedLabelHash;
        writeCSVLabelAttributes();
      }
      
    } catch (Exception e) {
      // n/a
    }
    this.performanceMeter.writeProfile(this.getClass().getSimpleName());

  } // end Method process

  // ----------------------------------
  /**
   * initialize picks up the parameters needed to write a cas out to the
   * database
   * 
   * Within framework, this doesn't get called. It's here for compatability
   * sake.
   *
   * @param aContext the a context
   * @throws ResourceInitializationException the resource initialization
   *           exception
   */
  // ----------------------------------
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {

    try {
      String args[] = null;
      args = (String[]) aContext.getConfigParameterValue("args");
      initialize(args);

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with getting the uima context " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, msg);
      throw new ResourceInitializationException();
    }

  } // end Method initialize() --------------

  // ----------------------------------
  /**
   * initialize picks up the parameters needed to write a cas out to the brat
   * format brat files go in the $outputDir/brat/ directory
   * 
   * Looking for the following arguments --outputDir= --outputTypes=
   * --filterOut=.
   *
   * @param pArgs the args
   * @throws ResourceInitializationException the resource initialization
   *           exception
   */
  // ----------------------------------
  @Override
  public void initialize(String pArgs[]) throws ResourceInitializationException {

    LabelHash = new HashMap<String, HashSet<?>>( );
    observedLabelHash = new HashMap<String, HashSet<?>>( );
    try {
      
      this.formatType = U.getOption(pArgs,"--csvFormatType=", formatType_With_Attributes ); 
      this.performanceMeter = new ProfilePerformanceMeter(pArgs, this.getClass().getSimpleName());
      this.outputDir = U.getOption(pArgs, "--outputDir=", "/someDir") ;
      String _outputTypes = U.getOption(pArgs, "--outputTypes=", "Concept");
     
      if ( _outputTypes != null && _outputTypes.trim().length() > 0 ) {
        String[] outputTypes = U.split(_outputTypes, ":");
        this.outputTypez = new HashSet<String>(outputTypes.length);
        for (String label : outputTypes) {
          this.outputTypez.add(label); // < ---- might need normalization no name
                                       // space/ lowercase?
        
          LabelHash.put( label, createAttributeHashForAnnotation( label) );
        }
        
        
        // Need to create the output/csv directory
        U.mkDir( this.outputDir + "/csv");
        // Need to create the output/txt directory
        U.mkDir( this.outputDir + "/txt");
        
        // if these are specified, write out the file now,
        // if not, gather LabelHash, then do this in destroy.
        writeCSVLabelAttributes();
      }
        
      GLog.println(GLog.STD___LEVEL, "Output is going to " + outputDir);
      U.mkDir(this.outputDir + "/csv");
      U.mkDir(this.outputDir + "/txt");

    //  MobilityFeatures       = new HashSet<String>(4);  MobilityFeatures.add("typeOfMobility");  MobilityFeatures.add("subject"); MobilityFeatures.add("timelime"); MobilityFeatures.add("history");
    //  ActionFeatures         = new HashSet<String>(2);  ActionFeatures.add("subDomain_Code");     ActionFeatures.add("polarity"); 
    //  QuantificationFeatures = new HashSet<String>(1);  QuantificationFeatures.add("typeOf");  
    //  ScoreFeatures          = new HashSet<String>(1);  ;   
      
    
    //  LabelHash.put( "Mobility", MobilityFeatures);
    //  LabelHash.put( "Action", ActionFeatures);
    //  LabelHash.put( "Quantification", QuantificationFeatures);
    //  LabelHash.put( "Score", ScoreFeatures);
        
      
      
     
      
      

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue initializing the csv writer " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", msg);
      throw new ResourceInitializationException();
    }

  } // end Method initialize() --------------

  // =================================================
  /**
   * createAttributeHashForAnnotation 
   * 
   * @param pLabelName
   * @return HashSet<?>
  */
  // =================================================
   private HashSet<?> createAttributeHashForAnnotation(String pLabelName) {
     
     
    
     HashSet<String> returnVal = new HashSet<String>(); 
     List<String> featureNames = null;
     
     if ( this.formatType.contentEquals(formatType_Tokenized ) ) {
     /* featureNames = new ArrayList<String>(3);
      featureNames.add("beginOffset");
      featureNames.add("length");
      featureNames.add("Content");
      */
     } else {
        featureNames = UIMAUtil.getAnnotationFeatureNames( pLabelName );
     }
    
     if ( featureNames != null && !featureNames.isEmpty())
       for ( String featureName : featureNames )
         returnVal.add(featureName); 
    
    return returnVal;
  } // end Method createAttributeHashForAnnotation() ----

  // ----------------------------------------
  // Global Variables
  /** The output dir. */
  // ----------------------------------------
  private String outputDir = null;

  /** The performance meter. */
  private ProfilePerformanceMeter performanceMeter = null;

 

  /** The output typez. */
  private HashSet<String> outputTypez = null;
  
  
  private static  HashSet<String>       MobilityFeatures = null;
  private static  HashSet<String>         ActionFeatures = null;
  private static  HashSet<String> QuantificationFeatures = null;
  private static  HashSet<String>          ScoreFeatures = null;
  private static HashMap<String, HashSet<?>>   LabelHash = null;
  private static HashMap<String, HashSet<?>>   observedLabelHash = null;
  private String formatType = null;
  public static final String formatType_With_Attributes = "WITH_ATTRIBUTES";
  public static final String formatType_Tokenized = "TOKENIZED";
  

} // end Class toCSV() -----------------------
