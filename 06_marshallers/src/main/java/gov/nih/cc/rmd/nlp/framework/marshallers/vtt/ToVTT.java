//=================================================
/**
 * ToDatabase is a CAS Consumer that transforms
 * the vinciNLPFramework encoded CAS into a database through
 * jdbc calls to pre-existing database tables that conform to
 * the database schema common to framework and chartReader.
 * 
 * Make sure you have the path to your jdbc driver in the classpath.
 * 
 *   The following parameters are found in the toDatabase.xml configuration file:
 *   
 *      jdbcDriver        |
 *      databaseUserName  |  
 *      databaseName      |
 *      databasePassword  |
 *      jdbcConnectString | Note: this is devoid of the databaseName, userName,databasePassword, which are taken from
 *                                the other parameters
 *
 *
 *   Can filter to/out annotation labels via
 *      includeLabels      parameter set in the toDatabase.xml 
 *                         a list of full namespace labels (one per line)
 *      excludedLabels     a list of full namespace labels (one per line)
 *    
 *    This should be an either/or not both
 *
 * @author  Guy Divita 
 * @created June 26, 2012
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.vtt;



import java.awt.Color;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.DefaultStyledDocument;

import gov.nih.nlm.nls.vtt.model.ConfigObj;
import gov.nih.nlm.nls.vtt.model.Markup;
import gov.nih.nlm.nls.vtt.model.Markups;
import gov.nih.nlm.nls.vtt.model.MetaData;
import gov.nih.nlm.nls.vtt.model.SaveInfo;
import gov.nih.nlm.nls.vtt.model.Tag;
import gov.nih.nlm.nls.vtt.model.Tags;
import gov.nih.nlm.nls.vtt.model.VttDocument;
import gov.nih.nlm.nls.vtt.model.VttFormat;
import gov.nih.nlm.nls.vtt.model.VttObj;
import gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.FeatureValuePair;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;



  public class ToVTT extends gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter  {


    // =======================================================
    /**
     * Constructor ToVTT creates a vtt writer. 
     *   
     * @param pVttDir,
     * @param pOutputTypes
     * @throws ResourceInitializationException 
     *
     */
    // =======================================================
    public ToVTT(String pVttDir, String[] pOutputTypes) throws ResourceInitializationException {
      
      
      this.initialize(pVttDir, pOutputTypes );
      
      
    } // end Constructor()  ---------------------


 // =======================================================
    /**
     * Constructor ToVTT creates a vtt writer. 
     *   
     * @param pArgs
     * 
     * @throws ResourceInitializationException 
     *
     */
    // =======================================================
    public ToVTT(String[] pArgs ) throws ResourceInitializationException {
      
      
      this.initialize(pArgs );
      
      
    } // end Constructor()  ---------------------

    




    // -----------------------------------------
    /** 
     * process iterates through all annotations, filters out
     * those that should be filtered out, then pushes them
     * into a database store.
     * 
     * Each document should include a DocumentAnnotation annotation and a documentHeader annotation
     * for re-animation purposes.
     *
     * @param pAJCas
     * @throws AnalysisEngineProcessException
     */
    // -----------------------------------------
    @Override
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
      
  	
      VttDocument vttDoc = processToVttDoc( pJCas);
  
      // -----------------------------------
      // create a vttObj with a ConfigObj to store default paths, and the like
      // VttObj vttObj = new VttObj( (File) null, this.vttPropertiesFile.getAbsolutePath(),0,0 ,false);
      VttObj vttObj = new VttObj( (File) null, null,0,0 ,false);
     
      ConfigObj configObj = vttObj.getConfigObj();
      configObj.setDocDir(new File( this.outputDir));
      configObj.setUserName(this.annotator);
      int vttFormat = VttFormat.READABLE_FORMAT;
     
      configObj.setVttDir(this.outputDir);
      configObj.setVttFormat(vttFormat);
      
      
      // ----------------------------------
      // write out the vttDoc
      String inputFileName = VUIMAUtil.getDocumentId(pJCas);
  
      if ( inputFileName == null ) inputFileName = "inputFile" + counter++;
      inputFileName = U.getFileNamePrefix(U.getOnlyFileName(inputFileName));
      
      String outputFileName = inputFileName + ".vtt";
      File outputFile = new File ( this.outputDir + "/" + outputFileName);
      VttDocument.saveFile(outputFile, vttDoc, vttFormat, vttObj);
     
     
   
    } // end Method process


    // =======================================================
    /**
     * processToString processes the jcas to a vtt string - the whole vtt document returned as a string.
     * 
     * @param pJCas
     * @return String
     * @throws Exception 
     */
    // =======================================================
    public String processToString(JCas pJCas) throws Exception {
      String vttString = null;
      try {
        
      
        VttDocument vttDoc = processToVttDoc( pJCas );
             
        // -----------------------------------
        // create a vttObj with a ConfigObj to store default paths, and the like
       
        VttObj vttObj = new VttObj( (File) null, null,0,0,false);
     
        ConfigObj configObj = vttObj.getConfigObj();
        configObj.setDocDir(new File( this.outputDir));
        configObj.setUserName(this.annotator);
        int vttFormat = VttFormat.READABLE_FORMAT;
     
        configObj.setVttDir(this.outputDir);
        configObj.setVttFormat(vttFormat);
      
        vttString = VttDocument.toString(vttDoc, vttFormat, vttObj);
        
      } catch (Exception e ) {
        e.printStackTrace();
        String msg = "Issue converting the common model to vtt string " + e.getMessage();
        GLog.println(GLog.ERROR_LEVEL, msg); 
        throw new Exception ( msg);
      }
      return vttString ;
      
    } // End Method processToVttString() ======================
    

    // =======================================================
    /**
     * processPipedString processes the jcas to just the piped annotation rows 
     * 
     * @param pJCas
     * @return String 
     * @throws Exception 
     */
    // =======================================================
    public String processToPipedString(JCas pJCas) throws Exception {
      String vttString = null;
      try {
        
      
        VttDocument vttDoc = processToVttDoc( pJCas );
             
        // -----------------------------------
        // create a vttObj with a ConfigObj to store default paths, and the like
       /*
        VttObj vttObj = new VttObj( (File) null, null,0,0,false);
     
        ConfigObj configObj = vttObj.getConfigObj();
        configObj.setDocDir(new File( this.outputDir));
        configObj.setUserName(this.annotator);
        int vttFormat = VttFormat.READABLE_FORMAT;
     
        configObj.setVttDir(this.outputDir);
        configObj.setVttFormat(vttFormat);
      
        vttString = VttDocument.toString(vttDoc, vttFormat, vttObj);
        */
        
        Markups markups = vttDoc.getMarkups();
        vttString = Markups.toString( markups.getMarkups());
        
        // strip the comment lines from the vtt string
        vttString = stripMarkupHeading( vttString);
        
        
      } catch (Exception e ) {
        e.printStackTrace();
        String msg = "Issue converting the common model to vtt string " + e.getMessage();
        GLog.println(GLog.ERROR_LEVEL, msg); 
        throw new Exception ( msg);
      }
      return vttString ;
      
    } // End Method processToVttString() ======================
    




    // =================================================
    /**
     * stripMarkupHeading removes the Markup headings that look something like
     *        #<---------------------------------------------------------------------->
     *        #<MarkUps Information>
     *        #<Offset|Length|TagName|TagCategory|Annotation|TagText>
     *        #<---------------------------------------------------------------------->
     * 
     * @param vttString
     * @return String 
    */
    // =================================================
    private String stripMarkupHeading(String vttString) {
      
      StringBuffer buff = new StringBuffer();
      String[] rows = U.split(vttString, "\n");
      
      for ( String row: rows )
        if ( row.startsWith("#") )
          ;
        else
          buff.append(row + "\n");
      
      return buff.toString();
    } // end Method stripMarkupHeading() --------------------


    // =======================================================
    /**
     * processToVttDoc processes the jcas to a vtt document
     * 
     * @param pJCas
     * @return vttDoc
     */
    // =======================================================
    public VttDocument processToVttDoc(JCas pJCas) {
      
   // ------------------------------------
      // Create the components of a vtt file
      //    text
      //    MetaData
      //    Tags
      //    Markups

    
      VttDocument vttDoc = new gov.nih.nlm.nls.vtt.model.VttDocument();
      
      
      String[] lineAndOffsets = UIMAUtil.getLinesFromDocument( pJCas);
      
      // -----------------------------------
      // Set the text
      String docText = pJCas.getDocumentText() + '\n';
          
      boolean fixOffsets = false;
      docText = toASCII8( docText);
      
      if ( docText.contains("\r\n")) {
        fixOffsets = true;
        docText = docText.replaceAll("\r\n", "\n");
        
      }
      
      vttDoc.setText(docText);
      
      // -----------------------------------
      // Set the metaData
      
      String userName = this.annotator;
      String vttFileVersion = "Version 1";   // may revisit this
      String dateStamp = U.getDateStampSimple();
      SaveInfo saveInfo = new SaveInfo(vttFileVersion, userName, dateStamp );
      MetaData metaData = vttDoc.getMetaData();
      metaData.addSaveInfo(saveInfo);
      
      // -----------------------------------
      // Create the tags (Label section for VTT
      Tags currentTags = createTags( pJCas);
      vttDoc.setTags( currentTags);
      
      // -----------------------------------
      // Create the annotations (Markups) 
      Markups markups = vttDoc.getMarkups();
      createMarkups( pJCas, markups, lineAndOffsets, fixOffsets, docText);
      
      
      return vttDoc;
    } // End Method processToVttDoc() ======================
    




    // =======================================================
    /**
     * toASCII8 converts any characters outside the ascii 8
     * range to ascii 8 characters.  If a translation from
     * a strange character is not possible, the character
     * will be replaced with a space to keep the offsets
     * the same.
     * 
     * @param pJCas
     * @param markups
     */
    // =======================================================
    private String toASCII8(String docText) {
		String buff = null;
    	
	 	char    chars[]  = docText.toCharArray();
    	char newChars[] = new char[chars.length]; 
	 	
    	for (int i = 0 ; i < chars.length; i++ ) {
    		if ( chars[i] >= SPACE && chars[i] <= TILDA )
    			newChars[i] = chars[i];
    		else if ( chars[i] == NEWLINE )
    			newChars[i] = NEWLINE;
    		else if ( chars[i] == TAB )
    		    newChars[i] = chars[i];
    		else
    			newChars[i] = SPACE;
    	}
    	buff = new String( newChars);
    	
	 	
		return buff;
	} // end Method toASCII8 -------------------------------




	// =======================================================
    /**
     * createMarkups creates vtt markups for each annotation 
     * 
     * @param pJCas
     * @param markups
     */
    // =======================================================
    private void createMarkups(JCas pJCas, Markups markups, String[] linesAndOffsets, boolean pFixOffsets, String pDocText) {
    
      List<Annotation> annotations = UIMAUtil.getAnnotations(pJCas);
      if ( annotations != null ) {
      for ( Annotation annotation: annotations) {
        String shortName = getShortName( annotation.getType().getName());
        if ( !this.ignoreList.contains( shortName) && isAndOutputLabel( shortName) ) {
          Markup markup = createMarkup( pJCas, annotation, shortName, linesAndOffsets, pFixOffsets, pDocText);
          markups.addMarkup( markup);
          
          
        } //end if we need to ignore this annotation
      } // end loop through the annotations
      } // end if there are any annotations
      
    } // End Method createMarkups() ======================
    




    // =================================================
  /**
   * isAndOutputLabel returns true if this shortName 
   * is one of the labels to show
   * 
   * @param pShortName
   * @return boolean
  */
  // =================================================
    private boolean isAndOutputLabel(String pShortName) {
    boolean returnVal = false;
    if ( this.labels_ != null )
      for ( String label: this.labels_)
        if ( label.endsWith( pShortName )) {
          returnVal = true;
          break;
        }
   
      return returnVal;
  } // end Method isAnOutputLabel() -------------


    // =======================================================
    /**
     * createMarkup creates a markup from an annoation
     * 
     * @param annotation
     * @return
     */
    // =======================================================
    private Markup createMarkup(JCas pJCas, Annotation annotation, String shortName, String[]linesAndOffsets, boolean pFixOffsets, String pDocText) {
   
      Markup markup = new Markup();
      
      int offset = annotation.getBegin();
      
      if ( pFixOffsets ) {
        offset = fixOffsetNow(pJCas, linesAndOffsets, annotation, pDocText);
      }
      markup.setOffset( offset);
      markup.setLength( (annotation.getEnd() - annotation.getBegin() )  );
      markup.setTagName(shortName);
      markup.setTagCategory("|"); //  like a semanitc type or cui
      
      String attributes = getAttributes( pJCas, annotation);
      markup.setAnnotation(attributes);    //  like metadata to pass along
      
      
      return markup;
      
    }  // End Method createMarkup() ======================
    




	// =======================================================
    /**
     * fixOffsetNow 
     * 
     * @param pJCas
     * @param linesAndOffsets
     * @param annotation
     * @param pDocText
     * @return int
     * 
     */
    // =======================================================
    private int fixOffsetNow(JCas pJCas, String[] pLinesAndOffsets, Annotation pAnnotation, String pDocText) {
     
      int offset = 0;
      int   lineRowNumber = UIMAUtil.getLineAndOffset(pJCas, pAnnotation, pLinesAndOffsets);
     
      offset =  pAnnotation.getBegin() - lineRowNumber;
      
      // -------------------
      // Check to see if this is right
      
      offset = fixOffset( pDocText, pAnnotation.getCoveredText(), offset);
      
      return offset;
    }  // End Method fixOffsetNow() ======================
    

    // =======================================================
    /**
     * fixOffset adjusts the offset if the offset is not sitting on the
     * focus string.  I have no idea why it is sometimes off by one. I give up
     * trying to figure it out. Checking for it and fixing it is the only option at this point.
     * 
     * @param pSnippet
     * @param pFocus
     * @param pBeginOffset
     * @return int
     */
    // =======================================================
    private int fixOffset(String pSnippet, String pFocus, int pBeginOffset) {
    
      int returnVal = 0;
      
      int zBeginOffset = pBeginOffset;
      int zEndOffset   = zBeginOffset + pFocus.length();
    
      
      if ( zEndOffset > pSnippet.length() )  zEndOffset = pSnippet.length();
      if ( zEndOffset > zBeginOffset)  {
        
      int ctr = 0;
      String buff = null;
      try {
       buff = pSnippet.substring(zBeginOffset, zEndOffset).trim();
      } catch (Exception e) {
        e.printStackTrace();
        String msg = e.getMessage() + "\n" + "snippetSize = " + pSnippet.length() + "|zbeginOffset =" + zBeginOffset + "|focusLength =" + pFocus.length()  + "|Focus=|" + pFocus +"|";
        GLog.println(GLog.ERROR_LEVEL,msg);
        throw new RuntimeException(msg);
      }
       while ( 
            zBeginOffset > 0 &&
            ctr < 6 &&
          !buff.startsWith( pFocus.trim()) ) {
        zBeginOffset--;
        zEndOffset = zBeginOffset + pFocus.length() ;
        if ( zEndOffset > pSnippet.length() )  zEndOffset = pSnippet.length();
        buff = pSnippet.substring(zBeginOffset, zEndOffset ).trim();
     
        ctr++;
      }
      } else {
    	 // zEndOffset = zBeginOffset -1 ;
      }
      returnVal = zBeginOffset;
      
      return returnVal;
    }  // End Method fixOffset() ======================


    //----------------------------------
    /**
     * getAttributes creates a colon delimited string of first level attribute values
     * for this annotation
     * 
     * @param pJCas
	 * @param annotation
	 * @return String  (colon delimited fields)
     * 
     **/
    // ----------------------------------
    private String getAttributes(JCas pJCas, Annotation annotation) {
		
	StringBuffer buff = null;
    String returnVal = null;
	
	List<FeatureValuePair> featureValuePairs = UIMAUtil.getFeatureValuePairs( annotation);
	if ( featureValuePairs != null ) {
		buff = new StringBuffer();
		for ( FeatureValuePair pair : featureValuePairs ) {
		  String value = pair.getFeatureValue();
		  if ( value != null )
		    value = value.replace('|', ':' );  // <----- Pipes mess up the annotation parser 
			buff.append( U.display2(pair.getFeatureName()) + "=" + U.display(value) + "<::>");
		}
		returnVal = buff.toString();
	}
    
	return returnVal;
    } // end Method getAttributes() ------




	  //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
      
      
      String args[] = null;
      args                  = (String[]) aContext.getConfigParameterValue("args");
      initialize ( args);
     
      
    } // end Method initialize() --------------


    //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param pArgs
     * 
     **/
    // ----------------------------------
    public void initialize(String pArgs[]) throws ResourceInitializationException {
      
      String   outputDir =  U.getOption(pArgs,  "--outputDir=",   "/someDir" ) + "/vtt"; 
      String outputTypez =  U.getOption(pArgs,  "--outputTypes=", "Concept:Snippet");
      
      String vttOutputTypez =  U.getOption(pArgs,  "--vttOutputTypes=", "Concept:Snippet");
      
      if ( outputTypez == null || outputTypez.equals("Concept:Snippet"))
        outputTypez = vttOutputTypez;
      
      String[] outputTypes = U.split(outputTypez, ":");
      initialize( outputDir, outputTypes  );

    } // end Method initialize() --------------



    //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param pOutputDir
     * @param pLabels
     * 
     **/
    // ----------------------------------
    public void initialize(String pOutputDir, String[] labels) throws ResourceInitializationException {
      
     
      initializeColors();
      
      this.outputDir = pOutputDir;
      
      // -----------------------
      // Make this directory if it doesn't exit
      try {
        U.mkDir(this.outputDir);
      } catch (Exception e1) {
        e1.getStackTrace();
        String msg = "Issue with trying to make the vtt output dir \n" + e1.getMessage();
        GLog.println( GLog.ERROR_LEVEL, msg );
        throw new ResourceInitializationException ();
      }
      
      this.labels_   = labels;
      this.tags      = initializeTags( labels);
      
      String resources = null;
      try {
        resources = U.getClassPathToResources() + "/resources/com/ciitizen/framework/vtt/";
      } catch (Exception e) {
        
        String msg = "Issue with toVTT \n" + e.getMessage() + "\n" + U.getStackTrace(e);
        GLog.println( GLog.ERROR_LEVEL,  msg );
        throw new ResourceInitializationException ();
      }
      this.vttPropertiesFile = new File(resources + "vtt.properties");
      initializeIgnoreList();
      
    } // end Method initialize() --------------


  
    

    // =======================================================
    /**
     * createTags 
     * 
     * @param pJCas
     * @return
     */
    // =======================================================
    private Tags createTags(JCas pJCas) {

      // Walk through all the annotatons and get the annotation types
     
      List<Annotation> annotations = UIMAUtil.getAnnotations(pJCas);
      Tags localTags = new Tags();
     
      HashSet<String> uniqSet = new HashSet<String>(10);
      if ( annotations != null ) {
      for ( Annotation annotation: annotations) {
        String shortName = getShortName( annotation.getType().getName());
        
        if ( !this.ignoreList .contains( shortName) ) {
          if ( !uniqSet.contains(shortName)) {
                       
            uniqSet.add( shortName);
          } // end unique
        } // end filter 
      } // end loop through annotations
      
      // ---------------
      // Add in labels that are passed in
      // ---------------
      if ( this.labels_ != null )
        for (String label: this.labels_ ) 
          uniqSet.add( label);
      
      
      String labels[] = new String[ uniqSet.size()];
      int k = 0;
      if ( this.labels_ != null )
        for ( String label: this.labels_)  {
          if ( uniqSet.contains(label)) {
            labels[k] = label;
            k++;
            uniqSet.remove(label);
          }
        
      }
     String[] otherAnnotatationLabels = hashToStringArray( uniqSet);
     Arrays.sort(otherAnnotatationLabels);
     int j = 0;
     for ( ; k< labels.length; k++) {
       labels[k] = otherAnnotatationLabels[j];
       j++;
     }
      
      
      localTags = initializeTags( labels);
      } // end if there are any annotations;
      return localTags;
    }  // End Method createTags() ======================
    




    // =======================================================
    /**
     * hashToStringArray
     * 
     * @param pUniqSet
     * @return String[]
     */
    // =======================================================
	private String[] hashToStringArray(HashSet<String> pUniqSet) {
		
		String[] buff = new String[ pUniqSet.size()];
		int i = 0;
		for ( Iterator<String> itr = pUniqSet.iterator(); itr.hasNext();)
			buff[i++] = itr.next();
		
		return buff;
		
	} // end Method hashToStringArray() ----------------------




	// =======================================================
    /**
     * getShortName returns the name of the class devoid of its namespace
     * 
     * @param name
     * @return
     */
    // =======================================================
    private String getShortName(String pName) {
    
      String shortName = pName;
     
       int period = pName.lastIndexOf(".");  
       if ( period > 0 && period < pName.length() ) {
         shortName = pName.substring( period + 1);
       }
       return shortName;
    }  // End Method getShortName() ======================
    



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
     * generateAnnotationColors will create up to X combinations of colors 

     * @param pBackgroundColor
     *  
     * @return int pointer into the preconfigured colors array
     */
    // =======================================================
    private int getAnnotationColors(String pBackgroundColor ) {
    
     int currentColor = 0;
     
     for ( int i = 0; i < colorNames.length; i++ ) {
       
       if ( pBackgroundColor.equals(colorNames[i])) {
         currentColor = i;
         break;
       }
     }
     
      return currentColor;
    } // End Method generateAnnotationColors() ======================
    

    

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
      
      // End Method getLabelAndCategory() ======================
    }



    // =======================================================
    /**
     * initializeIgnoreList is a list of annotations never to
     * pass along to vtt - documentHeader, CIS, sourceInformation, ...
     * 
     */
    // =======================================================
    private void initializeIgnoreList() {
    
        this.ignoreList = new HashSet<String>();
        
        this.ignoreList.add("DocumentHeader");
        this.ignoreList.add("SourceDocumentInformation");
     //   this.ignoreList.add("Line");
        this.ignoreList.add("DocumentAnnotation");
        this.ignoreList.add("CSI");
        this.ignoreList.add("SourceInformation");
        this.ignoreList.add("WhitespaceToken");
      
    }  // End Method initializeIgnoreList() ======================
    



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
    private Tags initializeTags( String[] pLabels) {
    
     
      Tags pTags = new Tags();
      Tag tag = null;
      
      // --------------------------------
      // Add default Tags
      try {
       tag = new Tag("highlight","" , true, BOLD, ITALIC, UNDERLINE, Color.black, Color.lightGray, "Monospaced", "+0" );  pTags.addTag(tag);
       tag = new Tag("Snippet",  "" , true, BOLD, ITALIC, UNDERLINE, Color.black, Color.yellow,    "Monospaced", "+0" );  pTags.addTag(tag);
       tag = new Tag("True",     "" , true, BOLD, ITALIC, UNDERLINE, Color.black, Color.GREEN,     "Monospaced", "+0" );  pTags.addTag(tag);
       tag = new Tag("False",    "" , true, BOLD, ITALIC, UNDERLINE, Color.black, Color.RED,       "Monospaced", "+0" );  pTags.addTag(tag);
       tag = new Tag("Uncertain","" , true, BOLD, ITALIC, UNDERLINE, Color.black, Color.PINK,      "Monospaced", "+0" );  pTags.addTag(tag);
      } catch (Exception e) {
          e.printStackTrace();
          GLog.println(GLog.ERROR_LEVEL, "Something went wrong here : " + e.toString() + "\n" + U.getStackTrace(e));
       
      }
      
      // --------------------------------
      // Add those that were passed in via configuration
      
      if ( pLabels != null )
        for ( String label: pLabels ) {
        	
          if ( label == null || label.trim().length() == 0 ) break;
          String[] labelOptions = U.split(label, "+");
          
          String labelName = labelOptions[0];
         
          int colorPattern = generateAnnotationColors();
          if ( labelOptions.length == 2 ) {
      
             colorPattern = getAnnotationColors( labelOptions[1]);
                  
          }
          
          labelName = U.getNameWithoutNameSpace( labelName);
     
          // ------------------------------
          // break labels to label:category
          String labelAndCategory[] = getLabelAndCategory( labelName);
          
         
          
          tag = new Tag(labelAndCategory[LABEL], labelAndCategory[CATEGORY], true, BOLD, ITALIC, UNDERLINE, colors[colorPattern][FORGROUND], colors[colorPattern][BACKGROUND], "Monospaced", "+0" );
          pTags.addTag(tag);                  
          
          
        } // end loop through the labels
      if ( pTags == null || pTags.getNameList().size() == 0 ) {
        GLog.println(GLog.ERROR_LEVEL,"No tags created");
        pTags = null;
      }
      
      return pTags;
      
      
    }  // End Method createTags() ======================



    // =======================================================
    /**
     * initializeColors predefines 28 background/forground color combinations
     * 
     */
    // =======================================================
    private static void initializeColors() {
    
      colors = new Color[26][2]; 
      colorNames = new String[26];
      
     
      
      
     
      
      colors[0][BACKGROUND] = Color.magenta;
      colors[0][FORGROUND] = Color.white;
      colorNames[0] = "MAGENTA";
      
      
      colors[1][BACKGROUND] = Color.orange;
      colors[1][FORGROUND] = Color.black;
      colorNames[1] = "ORANGE";   
      
      
      colors[2][BACKGROUND] = Color.yellow;
      colors[2][FORGROUND] = Color.black;
      colorNames[2] = "YELLOW";
      
      colors[3][BACKGROUND] = Color.cyan;
      colors[3][FORGROUND] = Color.black;
      colorNames[3] = "CYAN";
      
      
      colors[4][BACKGROUND] = Color.blue;
      colors[4][FORGROUND] = Color.white;
      colorNames[4] = "BLUE";
      
      colors[5][BACKGROUND] = Color.black; 
      colors[5][FORGROUND] = Color.white;
      colorNames[5] = "BLACK";
      
      colors[6][BACKGROUND] = Color.lightGray;
      colors[6][FORGROUND] = Color.black;
      colorNames[6] = "LIGHTGRAY";

      
      colors[7][BACKGROUND] = Color.darkGray;
      colors[7][FORGROUND] = Color.white;
      colorNames[7] = "DARKGRAY";
      
    
      
     
      
      colors[8][BACKGROUND] = Color.red.brighter();
      colors[8][FORGROUND] = Color.black;
      colorNames[8] = "RED";

      
      
      colors[9][BACKGROUND] = Color.pink.brighter();
      colors[9][FORGROUND] = Color.black;
      colorNames[9] = "PINK";
      
      
      
      colors[10][BACKGROUND] = Color.orange.brighter();
      colors[10][FORGROUND] = Color.black;
      colorNames[10] = "ORANGE_BRIGHTER";

      
      colors[11][BACKGROUND] = Color.yellow.brighter();
      colors[11][FORGROUND] = Color.black;
      colorNames[11] = "YELLOW_BRIGHTER";

      
      colors[12][BACKGROUND] = Color.green.brighter();
      colors[12][FORGROUND] = Color.black;
      colorNames[12] = "GREEN_BRIGHTER";
      
      colors[13][BACKGROUND] = Color.magenta.brighter();
      colors[13][FORGROUND] = Color.white;
      colorNames[13] = "MAGENTA_BRIGHTER";
      
      
      colors[14][BACKGROUND] = Color.cyan.brighter();
      colors[14][FORGROUND] = Color.black;
      colorNames[14] = "CYAN_BRIGHTER";

      
      colors[15][BACKGROUND] = Color.blue.brighter();
      colors[15][FORGROUND] = Color.white;
      colorNames[15] = "BLUE_BRIGHTER";

      
      colors[16][BACKGROUND] = Color.red.brighter();
      colors[16][FORGROUND] = Color.white;
      colorNames[16] = "RED_BRIGHTER";

      
      colors[17][BACKGROUND] = Color.pink.darker();
      colors[17][FORGROUND] = Color.black;
      colorNames[17] = "PINK_DARKER";

      
      colors[18][BACKGROUND] = Color.orange.darker();
      colors[18][FORGROUND] = Color.black;
      colorNames[18] = "ORANGE_DARKER";

      
      colors[19][BACKGROUND] = Color.yellow.darker();
      colors[19][FORGROUND] = Color.black;
      colorNames[19] = "YELLOW_DARKER";

      
      colors[20][BACKGROUND] = Color.green.darker();
      colors[20][FORGROUND] = Color.black;
      colorNames[20] = "GREEN_DARKER";

      
      colors[21][BACKGROUND] = Color.magenta.darker();
      colors[21][FORGROUND] = Color.white;
      colorNames[21] = "MAGENTA_DARKER";

      
      colors[22][BACKGROUND] = Color.cyan.darker();
      colors[22][FORGROUND] = Color.black;
      colorNames[22] = "CYAN_DARKER";

      
      colors[23][BACKGROUND] = Color.blue.darker();
      colors[23][FORGROUND] = Color.white;
      colorNames[23] = "BLUE_DARKER";

      colors[24][BACKGROUND] = Color.decode("#808000");
      colors[24][FORGROUND] = Color.black;
      colorNames[24] = "OLIVE";

      colors[25][BACKGROUND] = Color.GREEN;
      colors[25][FORGROUND] = Color.BLACK;
      colorNames[25] = "GREEN";
      
      
      // End Method initializeColors() ======================
    }



    // -----------------------------------------
    /** 
     * destroy closes the database
     *
     */
    // -----------------------------------------
    @Override
    public void destroy() {
    
    } // end Method destroy() 





    // ----------------------------------------
    // Global variables
    // ----------------------------------------
    private int counter = 0;
    private static final int LABEL = 0;
    private static final int CATEGORY = 1;
    private static final boolean BOLD = false;
    private static final boolean ITALIC = false;
    private static final boolean UNDERLINE = false;
    private static final int FORGROUND = 0;
    private static final int BACKGROUND = 1;
    public static Color[][]  colors = null;
    public static String[] colorNames = null;
    private static final char NEWLINE = '\n';
    private static final char TILDA = '~';
    private static final char SPACE = ' ';
    private static final char TAB = '\t';
   
    private Tags tags = null;
    private String[] labels_ = null;
    private int nextColor = 0;
    private String annotator = "v3NLP";
    private HashSet<String> ignoreList = null;
    private String outputDir;
    private File vttPropertiesFile = null; 
    public static final String[] DEFAULT_OUTPUT_TYPES = {"true", "false", "unknown", "notSure", "focus", "Concept" };
  

} // end Class toCommonModel
