/*******************************************************************************
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                       Epidemiology and Biostatistics Branch 
 *                                            2019 - 2022
 *   ---------------------------------------------------------------------------
 *   Copyright Notice:
 *   This software was developed and funded by the National Institutes of Health
 *   Clinical Center (NIHCC), part of the National Institutes of Health (NIH),
 *   and agency of the United States Department of Health and Human Services,
 *   which is making the software available to the public for any commercial
 *   or non-commercial purpose under the following open-source BSD license.
 *  
 *   Government Usage Rights Notice:
 *   The U.S. Government retains unlimited, royalty-free usage rights to this 
 *   software, but not ownership, as provided by Federal law. Redistribution 
 *   and use in source and binary forms, with or without modification, 
 *   are permitted provided that the following conditions are met:
 *      1. Redistributions of source code must retain the above copyright
 *         and government usage rights notice, this list of conditions and the 
 *         following disclaimer.
 *  
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *        
 *      3. Neither the names of the National Institutes of Health Clinical
 *         Center, the National Institutes of Health, the U.S. Department of
 *         Health and Human Services, nor the names of any of the software
 *         developers may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
 *   
 *      4. The U.S. Government retains an unlimited, royalty-free right to
 *         use, distribute or modify the software.
 *   
 *      5. Please acknowledge NIH CC as the source of this software by including
 *         the phrase: "Courtesy of the U.S. National Institutes of Health Clinical Center"
 *          or 
 *                     "Source: U.S. National Institutes of Health Clinical Center."
 *  
 *     THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS
 *     IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *     TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *     PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
 *     OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *     EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *     PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *     PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *     NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *     SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *     When attributing this code, please make reference to:
 *        Divita G, Carter ME, Tran LT, Redd D, Zeng QT, Duvall S, Samore MH, Gundlapalli AV. 
 *        v3NLP Framework: tools to build applications for extracting concepts from clinical text. 
 *        eGEMs. 2016;4(3). 
 *      
 *     In the absence of a specific paper or url listed above, reference https://github.com/CC-RMD-EpiBio/java-nlp-framework
 *   
 *     To view a copy of this license, visit https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/LICENSE.MD
 *******************************************************************************/
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
package gov.nih.cc.rmd.nlp.framework.marshallers.html;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.DocumentHeader;
import gov.va.vinci.model.Concept;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



  public class HTMLWriterNotWorking extends gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter  {

    private int counter = 0;
	private HashSet<String> outputTypesHash;
	private String[] outputTypes;
	private ArrayList<String> outputTypesL;
    // =======================================================
    /**
     * Constructor ToVTT creates a vtt writer. 
     *   
     * @param pArgs
     * @throws ResourceInitializationException 
     *
     */
    // =======================================================
    public HTMLWriterNotWorking(String[] pArgs ) throws ResourceInitializationException {
      
      
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
      
  	
      // ------------------------
      // get document meta data
      DocumentHeader documentHeader = VUIMAUtil.getDocumentHeader(pJCas);
      String documentId = documentHeader.getDocumentId();
      
      // ------------------------
      // Create a html doc 
      //    set the css definitions
      //    render the cas into it
      //    write the doc out
      
      HTMLDoc htmlDoc = new HTMLDoc();
      
     
      // ---------------------------
      // Figure out the output types (auto colored from a list of 1-n pre-configured colors)
     // List<String>annotationTypes = VUIMAUtil.getAnnotationTypes( pJCas);
      
      // --------------------------
      // Create css types for the annotationTypes
      String head = CSSUtils.createCssTypes( this.outputTypesL );
      
      // Attach the cssTypes to the htmlDoc
      htmlDoc.addHead(head);
      
      // ---------------------
      // Set the metadata for the htmlDoc
      htmlDoc.setOutputDir( this.outputDir);
      htmlDoc.setFileName( documentId );
      htmlDoc.setMetaData( documentHeader);
      
      processHTMLDoc( pJCas, htmlDoc );
      String outputFile = htmlDoc.write();
      documentHeader.setDocumentPath( outputFile);
 
    } // end Method process



    // ==========================================
    /**
     * processHTMLDoc 
     *
     * @param pJCas
     * @param htmlDoc
     * @param pAnnotationAnchors 
     */
    // ==========================================
    @SuppressWarnings("unchecked")
    private void processHTMLDoc(JCas pJCas, HTMLDoc htmlDoc ) {
      
    // what do I iterate thru?    
    // lines?  
    // then for each line, find all annotations 
    // can't because lines, tokens may have been removed
    // from the text, I can inject tags, from the bottom up?
    // 
    // \n's will need to be replaced at the end with <br>'s
     
    String documentText = pJCas.getDocumentText();
    documentText = replaceTextHeaderWithSpaces( documentText );
    StringBuffer buff = new StringBuffer(documentText);
    List<String> annotationAnchors = new ArrayList<String>();
    
    List<Annotation> allAnnotations = UIMAUtil.getAnnotations(pJCas);
    
    // sort these by offsets (descending)
    UIMAUtil.sortByOffsetDescending(allAnnotations);
    int anchorNumber = 1;
    for ( Annotation annotation: allAnnotations ) {
      
    	try { 
    		annotation.getCoveredText();
    	} catch (Exception e) {
    		// bad annotation, skip
    		continue;
    	}
      int b = annotation.getBegin();
      int e = annotation.getEnd();
      
      // -----------------------------------------------
      // Figure out the annotation labels here 
      // The assumption is that the non-name-space names
      // of the classes map to the output labels
      String annotationCategory = annotation.getClass().getSimpleName();
      
      if ( annotationCategory.contains("DocumentHeader")) continue;
      if ( this.outputTypesHash.contains ( annotationCategory) ) {
      
      String metaData = getMetaDataForAnnotation( pJCas, annotation);
      String goodOrBad = getGoodOrBad(pJCas, annotation);
      
      injectTag(buff, annotationCategory, b, e, metaData, anchorNumber);
      String tease =  U.spacePadRight(15, annotation.getCoveredText());
     
      annotationAnchors.add( "<li><a href=\"#" + anchorNumber + "\" >" + annotationCategory + ":" + tease + "</a></li>\n");
      
      anchorNumber++;
      }
    } // end loop thru annotations
    
    
    // ----------------------------
    // replace newlines with <br>'s 
   String buff2 = buff.toString();
   buff2 = buff2.replaceAll("\n", "<br>\n");
   buff2 = buff2.replaceAll("_n#&_", "\n");  //<---- tool tips can't have <br> in them
   
     // --------------------
     // Set the navigation pane 
   //    reverse the annotationAnchors - they are reversed 
     annotationAnchors = (List<String>) U.reverse( annotationAnchors );
     htmlDoc.setNavigation( annotationAnchors );
  
   
    htmlDoc.setBody(  buff2.toString() );
      
    } // end Method processHTMLDoc() ===========
    

    

    // ==========================================
    /**
     * replaceTextHeaderWithSpaces [Summary]
     *
     * @param pDocumentText
     * @return
     */
    // ==========================================
    private String replaceTextHeaderWithSpaces(String pDocumentText) {
    
      String buff = pDocumentText.replace(
          "= ==============================================MetaData=Not=Part=of=Record=",
          "                                                                            ");
      
       buff = buff.replace(
          "===============================================MetaData=Not=Part=of=Record=",
          "                                                                           ");
      return buff;
    }// end Method replaceTextHeaderWithSpaces() 
    


    // ==========================================
    /**
     * getMetaDataForAnnotation returns a string
     * with some of the annotation attributes
     * 
     *   id:
     *   AssertionStatus:
     *   Category:
     *   ConceptName:
     *   Cui:
     *   Offsets:
     *
     * @param pJCas
     * @param pAnnotation
     * @return String
     */
    // ==========================================
    private String getMetaDataForAnnotation(JCas pJCas, Annotation pAnnotation) {
      String returnVal = " ";
      
      String    annotationId = " ";
      String assertionStatus = " ";
      String      categories = " ";
      String    conceptNames = " ";
      String            cuis = " ";
      String         offsets = " ";
  
      try {
            annotationId = ((Concept)pAnnotation).getId() ;
         assertionStatus = VUIMAUtil.getAssertionStatus(pJCas, pAnnotation);
              categories = VUIMAUtil.getCategory(pAnnotation);
            conceptNames = VUIMAUtil.getConceptNames(pAnnotation);
                    cuis = VUIMAUtil.getCuis(pAnnotation);
                 offsets = pAnnotation.getBegin() + "|" + pAnnotation.getEnd();
      } catch (Exception e) {}
      
      annotationId = annotationId + " _";
      assertionStatus = assertionStatus + " _";
      categories = categories + " _";
      conceptNames = conceptNames + " _";
      cuis = cuis + " _";
      
      
      returnVal = "Id:______________" + annotationId + "_n#&_" +      // the _n#&_  will be replaced with \n's after 
                  "AssertionStatus:_" + assertionStatus + "_n#&_" +   // the \n's in the text get changed to <br>'s
                  "Category:________" + categories + "_n#&_" + 
                  "ConceptName:_____" + conceptNames + "_n#&_" + 
                  "Cui:_____________" + cuis + "_n#&_" + 
                  "Offsexts:_________" + offsets + "_n#&_";
      
      
      
      return returnVal;
      // end Method getMetaDataForAnnotation() ==
    }


    // ==========================================
    /**
     * getGoodOrBad returns "Good" if the annotation
     * is asserted, otherwise it returns "Bad"
     *
     * @param pAnnotation
     * @return String
     */
    // ==========================================
    private String getGoodOrBad(JCas pJCas, Annotation pAnnotation) {
     
      String returnVal = "Bad";
      
      String assertionStatus = VUIMAUtil.getAssertionStatus(pJCas, pAnnotation);
      
      if ( assertionStatus != null )
        if ( assertionStatus.equals("Asserted"))
          returnVal = "Good";
      
      return returnVal;
    } // end Method getGoodOrBad() =============
    


    // ==========================================
    /**
     * injectTag injects the tag into the buffer
     *
     * @param pBuff
     * @param pAnnotationCategory
     * @param pGoodOrBad
     * @param pBeginOffset
     * @param pEndOffset
     * @param pAnchorNumber 
     */
    // ==========================================
    private void injectTag(StringBuffer         pBuff, 
                           String pAnnotationCategory, 
                          
                           int           pBeginOffset, 
                           int             pEndOffset, 
                           String           pMetaData, 
                           int          pAnchorNumber) {
      
      
      String beginTag = "\n<span class= \"" + pAnnotationCategory + " " + 
                                              pAnnotationCategory + "-top\" data-" +
                                              pAnnotationCategory + "=\"" + 
                                              pMetaData + "\">\n" +
                                              "<a name=\"" + pAnchorNumber + "\">" ;
      String endTag = "</a></span>\n ";
     
      //   xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxB---------Eyyyyyyyyyy
      //                                            | </endTag>
      //                                           \|/
      //   xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxB---------E </endTag> yyyyyyyyyy
      //                                 | <beginTag>B
      //                                \|/
      //   xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx <beginTag> B---------E </endTag> yyyyyyyyyy
       
     
      pBuff.insert(pEndOffset, endTag );
      pBuff.insert(pBeginOffset, beginTag);
      
    }  // end Method injectTag() ===============
    




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
      
      
      initialize( args );
    
    
      
    } // end Method initialize() --------------




    //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param pArgs
     * 
     **/
    // ----------------------------------
    public void initialize(String[] pArgs ) throws ResourceInitializationException {
      
      this.outputDir = U.getOption(pArgs,  "--outputDir=", "./") + "/html";
     
      String outputTypez = U.getOption(pArgs, "--outputTypes=", "SectionZone:ContentHeading"); 
      
      this.outputTypes = U.split(outputTypez, ":");
      this.outputTypesHash = new HashSet<String>();
      this.outputTypesL = new ArrayList<String>( this.outputTypes.length);
      
      for ( String outputType : outputTypes ) {
    	  this.outputTypesHash.add( outputType);
    	  this.outputTypesL.add( outputType );
      }
     
      // -----------------------
      // Make this directory if it doesn't exit
      try {
        U.mkDir(this.outputDir);
      } catch (Exception e1) {
        e1.getStackTrace();
        String msg = "Issue with trying to make the html output dir " + e1.getMessage();
        System.err.println( msg );
        throw new ResourceInitializationException ();
      }
      
     
      initializeIgnoreList();
      
    } // end Method initialize() --------------




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
     * initializeIgnoreList is a list of annotations never to
     * pass along to vtt - documentHeader, CIS, sourceInformation, ...
     * 
     */
    // =======================================================
    private void initializeIgnoreList() {
    
        this.ignoreList = new HashSet<String>();
        
        this.ignoreList.add("DocumentHeader");
        this.ignoreList.add("SourceDocumentInformation");
        this.ignoreList.add("Line");
        this.ignoreList.add("DocumentAnnotation");
        this.ignoreList.add("CSI");
        this.ignoreList.add("SourceInformation");
        this.ignoreList.add("WhitespaceToken");
      
    }  // End Method initializeIgnoreList() ======================
    



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
    private HashSet<String> ignoreList = null;
    private String outputDir;
  

} // end Class toHTML
