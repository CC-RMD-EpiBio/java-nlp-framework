package gov.nih.cc.rmd.nlp.framework.marshallers.formVariability;
//=================================================
/**
 * FormatVariabilityWriter is a CAS Consumer that transforms
 * features to figure out format variability into a csv file
 * to be put into a pca analysis.
 * 
 *
 * @author  Guy Divita 
 * @created June 26, 2012
 *
 * 
 */



import java.io.PrintWriter;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.NestedSection;
import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.framework.TableRow;
import gov.nih.cc.rmd.framework.TilingScores;
import gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.ContentHeading;
// import gov.va.chir.model.List;
import gov.va.chir.model.Delimiter;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.Line;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.Token;
import gov.va.chir.model.WordToken;



public class FormatVariabilityWriter extends JCasAnnotator_ImplBase implements Writer {


  // -----------------------------------------
  /** 
   * Constructor 
   * @throws ResourceInitializationException 
  */
    // -----------------------------------------
    public  FormatVariabilityWriter() throws AnalysisEngineProcessException, ResourceInitializationException {
     
    } // end Constructor 


    // -----------------------------------------
    /** 
     * Constructor 
     * 
     * @param pArgs
     * @throws ResourceInitializationException 
    */
      // -----------------------------------------
      public  FormatVariabilityWriter(String[] pArgs) throws AnalysisEngineProcessException, ResourceInitializationException {
        initialize( pArgs);
      } // end Constructor 



    // -----------------------------------------
    /** 
     * process counts up the features that we can tract for
     * formatting variability
     *
     * @param pAJCas
     * @throws AnalysisEngineProcessException
     */
    // -----------------------------------------
    @Override
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
      
     int chars = 0;
     int words = 0; 
     int lines = 0;
     int punctuations = 0;
     int sentences = 0;
     int delimiters = 0;
     int sectionNames = 0;
     int sectionZones = 0;
     int nestedSections = 0;
     int slotValues = 0;
     int slotNames = 0;
     int checkBoxes = 0;
     int slotValueValues = 0;
     int tables = 0;
     int questions = 0;
     int lists = 0;
     int listElements = 0;
     String tilingScores1 = "";
     String tilingScores2 = "";
     String sectionVector = "";
     
     String documentId = VUIMAUtil.getDocumentId(pJCas);
     
     
     
     Annotation tilingScores = UIMAUtil.getAnnotation(pJCas, TilingScores.typeIndexID );
    
     if ( tilingScores != null ) {
       tilingScores1 = ((TilingScores) tilingScores).getTilingScore1();
       tilingScores2 = ((TilingScores) tilingScores).getTilingScore2();
     
     }
     
     
     String fileName = VUIMAUtil.getDocumentId(pJCas);
     DocumentHeader docHeader = VUIMAUtil.getDocumentHeader( pJCas);
     if ( docHeader != null) {
       sectionVector = docHeader.getOtherMetaData();
     } 
     chars = pJCas.getDocumentText().length();
     
	   try {
     
	     List<Annotation> tokenAnnotations = UIMAUtil.getAnnotations(pJCas, WordToken.typeIndexID, false );
	    
	     if ( tokenAnnotations != null && !tokenAnnotations.isEmpty()) {
         for ( Annotation token : tokenAnnotations) 
           if (  U.isOnlyPunctuation( token.getCoveredText().trim() ))
             punctuations++;
           else
             words++;
	     }
	   
	     List<Annotation> lineAnnotations = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID, false );
       if ( lineAnnotations != null && !lineAnnotations.isEmpty()) lines = lineAnnotations.size();
     
	    
       List<Annotation> sentenceAnnotations = UIMAUtil.getAnnotations(pJCas, Sentence.typeIndexID, false );
       if ( sentenceAnnotations != null && !sentenceAnnotations.isEmpty()) sentences = sentenceAnnotations.size();
     
       
       List<Annotation> delimiterAnnotations = UIMAUtil.getAnnotations(pJCas, Delimiter.typeIndexID, true );
       if ( delimiterAnnotations != null && !delimiterAnnotations.isEmpty()) delimiters = delimiterAnnotations.size();
      
       List<Annotation> sectionZoneAnnotations = UIMAUtil.getAnnotations(pJCas, SectionZone.typeIndexID, false );
       if ( sectionZoneAnnotations != null && !sectionZoneAnnotations.isEmpty()) sectionZones = sectionZoneAnnotations.size();
       
       List<Annotation> nestedSectionAnnotations = UIMAUtil.getAnnotations(pJCas, NestedSection.typeIndexID, false );
       if ( nestedSectionAnnotations != null && !nestedSectionAnnotations.isEmpty()) nestedSections = nestedSectionAnnotations.size();
       
       
       List<Annotation> tableRowAnnotations = UIMAUtil.getAnnotations(pJCas, TableRow.typeIndexID, false );
       if ( tableRowAnnotations != null && !tableRowAnnotations.isEmpty()) tables = tableRowAnnotations.size();
       
       
       List<Annotation> slotValueAnnotations = UIMAUtil.getAnnotations(pJCas, SlotValue.typeIndexID, false );
       if ( slotValueAnnotations != null && !slotValueAnnotations.isEmpty()) {
         
         slotValues = slotValueAnnotations.size();
      
       
       for ( Annotation slot : slotValueAnnotations) {
         String slotType = ((SlotValue) slot ).getId();
       
         if ( slotType.endsWith("CheckBox3")) { 
           checkBoxes++;
           
         }
       }
       } 
     
      
       List<Annotation> contentHeadings_ = UIMAUtil.getAnnotations(pJCas, ContentHeading.typeIndexID, false );
       if ( contentHeadings_ != null && !contentHeadings_.isEmpty()) {
         
         for ( Annotation contentHeading : contentHeadings_) {
           String contentHeadingType = ((ContentHeading) contentHeading ).getId();
           
           if      ( contentHeadingType.startsWith("SlotValue"))    slotNames++;
           else if ( contentHeadingType.contains( "Question") )   questions++;
           else if ( contentHeadingType.contains( "Section") )    sectionNames++;
       
           
         }
       }
      
       List<Annotation> slotValueValues_ = UIMAUtil.getAnnotations(pJCas, DependentContent.typeIndexID, false );
       if ( slotValueValues_ != null && !slotValueValues_.isEmpty())  slotValueValues = slotValueValues_.size();
         
       
       List<Annotation> liists = UIMAUtil.getAnnotations(pJCas, gov.va.chir.model.List.typeIndexID, false );
       if ( liists != null && !liists.isEmpty())  lists = liists.size();
      
       List<Annotation> listElements_ = UIMAUtil.getAnnotations(pJCas, gov.va.chir.model.ListElement.typeIndexID, false );
       if ( listElements_ != null && !listElements_.isEmpty())  listElements = listElements_.size();
      
       String row =
           
           fileName + "|" + 
           tilingScores1 + "|" +  //< ---- should be three fields
           tilingScores2 + "|" +  //< ---- should be three fields
           chars    + "|" + 
           words + "|" +  
           lines + "|" + 
         punctuations + "|" + 
         sentences + "|" + 
         delimiters + "|" + 
         sectionNames + "|" + 
         sectionZones + "|" + 
         nestedSections + "|" + 
         slotValues + "|" + 
         slotNames + "|" + 
         checkBoxes + "|" + 
         slotValueValues + "|" + 
         tables + "|" + 
         questions + "|" + 
         lists + "|" + 
         listElements + "|" +  
         sectionVector +   "\n";
      
       synchronizedPrint( row );
     
      
	     
      } catch ( Exception e) {
      e.getStackTrace();
      String msg = "Issue " + e.getMessage() ;
      System.err.println(msg);
    }
      
      
    // System.err.println("Finished processing file" );
  } // end Method process -------------

          
      // =================================================
      /**
       * synchronizedPrint 
       * 
       * @param row
      */
      // =================================================
      private synchronized void synchronizedPrint(String row) {
        out.print( row );
        out.flush();
      } // end Method synchronizedPrint() ---------------
     



    //----------------------------------
     /**
      * destroy
     * 
      **/
     // ----------------------------------
     public void destroy() {
       try {
         
         this.performanceMeter.writeProfile( this.getClass().getSimpleName());
         if ( out != null )
           out.close();
        } catch ( Exception e) {
         e.printStackTrace();
       }
      
     }


    //----------------------------------
    /**
     * initialize 
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
      
      String[] args = null;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  
        
        initialize( args );   
        
      } catch (Exception e ) {
        System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
        throw new ResourceInitializationException();
      }
      
 
    } // end Method initialize() ---------

    //----------------------------------
      /**
       * initialize 
       *
       * @param pArgs
       * 
       **/
      // ----------------------------------
      public void initialize(String pArgs[]) throws ResourceInitializationException {
        
        this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
        
        String outputDir = U.getOption(pArgs,  "--outputDir=", "/some/output/dir" ); 
        
        initialize( outputDir );
    
    } // end Method initialize() --------------
    

   
    

    //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize(String pOutputDir ) throws ResourceInitializationException {
      
     
      try {
        
        String outputDir = pOutputDir + "/stats";
        U.mkDir(outputDir );
        String outputFile = outputDir + "/" + "pcaFile.csv";
        
        if ( out == null ) {
         out = new PrintWriter( outputFile);
        
        out.print(
        "fileName" + "|" + 
        "tilingScore1_1" + "|" +
        "tilingScore1_2" + "|" +
        "tilingScore1_3" + "|" +
        
        "tilingScore2_1" + "|" +
        "tilingScore2_2" + "|" +
        "tilingScore2_3" + "|" +
        
        "numChars" + "|" + 
        "numWords" + "|" +  
        "lines"    + "|" + 
        "numPunctuations" + "|" + 
        "numSentences" + "|" + 
        "numDelimiters" + "|" + 
        "numSectionNames" + "|" + 
        "numSectionZones" + "|" + 
        "numNestedSections" + "|" + 
        "numSlotValues" + "|" + 
        "numSlotNames" + "|" + 
        "numCheckBoxes" + "|" + 
        "numSlotValueValues" + "|" + 
        "numTables" + "|" + 
        "numQuestions" + "|" + 
        "numLists" + "|" + 
        "numListElements" + "\n" );
        }
      } catch (Exception e) {
        e.printStackTrace();
        throw new ResourceInitializationException();
      }
    
     
 
    } // end Method initialize() --------------


    

    
  // ----------------------------------------
    // Global variables
    // ----------------------------------------
    private  static PrintWriter out = null;
    private ProfilePerformanceMeter performanceMeter = null;
  
    
  
  
  

} // end Class toCommonModel
