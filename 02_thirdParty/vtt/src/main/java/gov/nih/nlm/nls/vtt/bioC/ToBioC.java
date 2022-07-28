//=================================================
/**
 * ToBioC is an api that transforms a vtt instance into a bioC instance 
 * 
 * @author  Guy Divita 
 * @created Feb 10, 2015
 *
 *   *
 *   * --- Copyright Notice: --------------------------------------------------
 *   *
 *   * Copyright 2015 United States Department of Veterans Affairs, 
 *   *                Health Services Research & Development Service
 *   *
 *   *  Licensed under the Apache License, Version 2.0 (the "License");
 *   *  you may not use this file except in compliance with the License.
 *   *  You may obtain a copy of the License at
 *   *
 *   *      http://www.apache.org/licenses/LICENSE-2.0
 *   *
 *   *  Unless required by applicable law or agreed to in writing, software
 *   *  distributed under the License is distributed on an "AS IS" BASIS,
 *   *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   *  See the License for the specific language governing permissions and
 *   *  limitations under the License. 
 *   * 
 *   * --- End Copyright Notice: ----------------------------------------------
 *   *
 * 
 */
// ================================================
package gov.nih.nlm.nls.vtt.bioC;

import gov.nih.nlm.nls.vtt.model.Markup;
import gov.nih.nlm.nls.vtt.model.Markups;
import gov.nih.nlm.nls.vtt.model.Tag;
import gov.nih.nlm.nls.vtt.model.Tags;
import gov.nih.nlm.nls.vtt.model.VttDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.stream.XMLStreamException;

import bioc.BioCDocument;
import bioc.BioCPassage;
import bioc.io.BioCDocumentWriter;
import bioc.io.standard.BioCFactoryImpl;
import bioc.io.BioCFactory;
import bioc.BioCAnnotation;
import bioc.BioCCollection;
import bioc.BioCLocation;



  public class ToBioC  {

   
    // =======================================================
    /**
     * Constructor ToBioC creates a BioC writer. 
     *   
     * @throws Exception 
     *
     */
    // =======================================================
    public ToBioC() throws Exception {
      
    this.bioCFactory = BioCFactoryImpl.newFactory(BioCFactoryImpl.STANDARD);
      
    } // end Constructor()  ---------------------

    // =======================================================
    /**
     * Constructor ToBioC creates a BioC writer. 
     *   
     * @param pBioCDir,
     *
     * @throws Exception 
     *
     */
    // =======================================================
    public ToBioC(String pBioCDir ) throws Exception {
      
      
      this.initialize(pBioCDir );
      
      
    } // end Constructor()  ---------------------

    
    
    // -----------------------------------------
    /** 
     * process iterates through all vtt markups and puts them into the bioC document
     * 
     * Each document should include a DocumentAnnotation annotation and a documentHeader annotation
     * for re-animation purposes.
     *
     * @param pVTTDocument
     * @param pFileName   ( devoid the extension.  This method will attach .bioC.xml to the end)
     * @throws Exception
     */
    // -----------------------------------------
    public BioCDocument process(VttDocument pVTTDocument, String pFileName ) throws Exception {

      BioCDocument bioCDocument = null;
      
      try {
       bioCDocument = processDocument( pVTTDocument, pFileName );
      
       String outFileName = this.outputDir + "/" + pFileName + ".bioC.xml";
     
       try {
         BioCDocumentWriter writer = this.bioCFactory.createBioCDocumentWriter( new OutputStreamWriter( new FileOutputStream(outFileName), "UTF-8"));
  
         writer.writeDocument(bioCDocument);
         writer.close();
       } catch  (Exception e1 ) {
         e1.printStackTrace();
         String msg = "Issue writing the bioC document out " + e1.toString();
         System.err.println(msg);
             
       }
      } catch (Exception e2) {
        e2.printStackTrace();
        String msg = "Issue processing the document " + e2.toString();
        System.err.println(msg);
      }
         
      return bioCDocument;
    } // end Method process() ------------------
    
    
    // -----------------------------------------
    /** 
     * process iterates through all markups, and puts them into a bioC Document
     * 
     * Each document should include a DocumentAnnotation annotation and a documentHeader annotation
     * for re-animation purposes.
     *
     * @param pVttDocument
     * @throws Exception
     */
    // -----------------------------------------
    public BioCDocument processDocument(VttDocument pVttDocument, String pFileName) throws Exception {
      
    	// ------------------------------------
    	// Create the components of a BioC file
    	//    text
    	//    MetaData
      //    Tags
      //    Markups


      BioCDocument bioCDocument = new BioCDocument();
  
    
    	HashMap<String,String> infons = new HashMap<String,String>();
    	
    	// -----------------------------
    	// Set the annotator (the thing or source that produced the annotations
    	infons.put("Document.annotatator", this.annotator);
    	bioCDocument.setInfons(infons);
    
      
    	// -----------------------------------
      // Set the text
      String docText = pVttDocument.getText();
      
      // -----------------------------------
      // set the original document text in a "passage"
      // -----------------------------------
      BioCPassage passage = new BioCPassage();
      passage.setText(docText);
      passage.setOffset(0);
      
      // ----------------------------------
      // attach the passage to the document
  
      bioCDocument.setID(pFileName);
     
      
      
      // ----------------------------------
      // Document header will be handled as an annotation taken care of below
      
      
      try {
        // ----------------------------------
        // Convert annotations to bioCAnnotations
        List<BioCAnnotation>bioCAnnotations = convertAnnotations( pVttDocument, pFileName );
     
        for ( BioCAnnotation annotation : bioCAnnotations)
          passage.addAnnotation(annotation);
      } catch (Exception e) {
        e.printStackTrace();
        String msg = "Issue converting this annotation " + e.toString();
        System.err.println(msg);
      }
      
      bioCDocument.addPassage( passage);
      
      return bioCDocument;

    } // end Method process


 // =======================================================
    /**
     * serialize serializes a bioC Document. Technically,
     * given the tools they provide, it creates a collection
     * with one document in it.  You cannot, with their tools
     * create a document xml stream without providing
     * a collection.
     * 
     * @param bioCDocument
     * @return
     * @throws XMLStreamException 
     * @throws IOException 
     */
    // =======================================================
    public String serialize(BioCDocument bioCDocument) throws XMLStreamException, IOException {
      
      String returnVal = null;
      StringWriter out = new StringWriter();
      
      BioCCollection collection = new BioCCollection();
      collection.setDate( getDateStampSimple());
      collection.setSource("v3NLPFramework.marshallers.marshaller.bioC.ToBioC");
      
      // ------------------------------
      // gotta do this or it will crash
      Map<String, String> infons = new HashMap<String, String>();
      collection.setInfons(infons);
   
      BioCDocumentWriter writer = this.bioCFactory.createBioCDocumentWriter( out );
   
     
      writer.writeCollectionInfo(collection);
      writer.writeDocument( bioCDocument);
      writer.close();
      returnVal = out.toString();
      out.close();
    
      return returnVal;
    } // End Method serialize() ======================
    

   
     
    
    // =======================================================
       /**
        * processToXML serializes a bioC Document. Technically,
        * given the tools they provide, it creates a collection
        * with one document in it.  You cannot, with their tools
        * create a document xml stream without providing
        * a collection.
        * 
        * @param pVttDocument
        * @param pFileName
        * @return String 
        *
        * @throws Exception 
        */
       // =======================================================
       public String processToXML(VttDocument pVttDocument, String pFileName ) throws XMLStreamException, IOException, Exception {
         
         String returnVal = null;
         
         BioCDocument doc = processDocument(pVttDocument, pFileName);
         
         returnVal = serialize( doc);

         return returnVal;
         
       } // End Method serialize() ======================
       

    
    // =======================================================
    /**
     * convertAnnotations converts UIMA annotations to bioC annotations
     * 
     * @param pVttDocument
     * @param pFileName 
     * @return List<BioCAnnotation>
     * 
     * @throws Exception 
     */
    // =======================================================
    private List<BioCAnnotation> convertAnnotations(VttDocument pVttDocument, String pFileName) throws Exception {
      
    
      BioCAnnotation bioCAnnotation = null;
      String docId = pFileName;
      String docText = pVttDocument.getText();
      
      
      // ------------------------
      // retrieve the markups 
      Markups markupContainer = pVttDocument.getMarkups();
      Vector<Markup> markups = markupContainer.getMarkups();
      Tags tags = pVttDocument.getTags();
      
      
      ArrayList<BioCAnnotation> bioCAnnotations = new ArrayList<BioCAnnotation>();
     
      if ( markups != null ) {
        for (Markup markup: markups) {
        
            bioCAnnotation = convertAnnotation( docId, tags, markup, docText);
    
          if ( bioCAnnotation != null)
            bioCAnnotations.add(bioCAnnotation);
          
      
        } // end loop through the annotations
      } // end if there are any annotations
      
      return bioCAnnotations;
    } // End Method convertAnnotations() ======================
    



// =======================================================
/**
 * convertAnnotation converts a uima annotation to a bioC Annotation
 * 
 * @param pDocId
 * @param pTags
 * @param pAnnotation
 * @return BioCAnnotation
 */
// =======================================================
private BioCAnnotation convertAnnotation( String pDocId, Tags pTags, Markup pAnnotation, String pDocText) {

  BioCAnnotation bioCAnnotation = new BioCAnnotation();
  
  BioCLocation location = new BioCLocation();
  location.setOffset(pAnnotation.getOffset() );
  location.setLength( pAnnotation.getLength());
  bioCAnnotation.addLocation(location);
  bioCAnnotation.setID(pDocId + "_" + this.annotationCounter++);
  bioCAnnotation.setText( getDocText( pDocText, pAnnotation.getOffset(), pAnnotation.getLength()));
  
  // ----------------------------------
  // bioC does not have a label for an annotation - Within BioC, that would 
  // be Annotation + infon< "Type", $LABEL>
  //
  // BioC has a notion of a label per inferon.  UIMA has label per annotation
  //   
  //  We will align later, and do one bioC Annotation + one Infon type=$LABEL
  //    here.
  //      A post processing step "can" combine annotations of the same span
  //      and combine infons from those annotations.
  //         We will have to have a convention that the infon keys
  //         will be type.feature = $VALUE to keep track of what
  //         infon's go to what labels.
  //       
  //  
  String annotationLabel = pAnnotation.getClass().getSimpleName();
  HashMap<String,String> infons = new HashMap<String, String>();
  infons.put("Type", annotationLabel);
  setAttributes(  pAnnotation, pTags, infons);
  bioCAnnotation.setInfons(infons);
  
  
  return bioCAnnotation;
} // End Method convertAnnotation() ======================



  // =======================================================
  /**
   * getDocText retrieves the text from the offset to offset + length
   *   
   * @param pDocText
   * @param pOffset
   * @param pLength
   * @return String
   */
  // =======================================================
  private String getDocText(String pDocText, int pOffset, int pLength) {
    
    String returnVal = null;
    
    if ( pDocText != null && pDocText.length() > (pOffset + pLength )) {
      returnVal = pDocText.substring(pOffset, pOffset + pLength -1);
    }
    
    return returnVal;
  } // End Method getDocText() ======================
  

  //----------------------------------
  /**
   * setAttributes inserts first level attributes into infons
   *   Each attribute value pair will have the attributeName be
   *   the annotationLabel.attributeName 
   *   
   *   vtt markups have one possible attribute.
   * 
   * @param pAnnotation
   * @param pInfons 
   * 
   **/
  // ----------------------------------
  private void setAttributes( Markup pAnnotation, Tags pTags, Map<String,String> pInfons) {

    String attributeName = null;
    String attributeValue = null;
    String tagName = pAnnotation.getTagName();
    Tag        tag = pTags.getTagByNameCategory(tagName);
    
    
    attributeName = tag.getCategory(); 
    if ( attributeName != null && attributeName.length() > 0 ) {
     attributeValue = pAnnotation.getTagCategory();  // this is the value;
    
     pInfons.put(  attributeName, attributeValue);
    
      
    }
  
  } // end Method setAttributes() ------

  
  // ------------------------------------------
  /**
   * getDateStampSimple returns a simple date stamp with no
   * colons in it.  Colons mess up file names.  Use this method
   * for appending a date stamp to a file name.
   * The format is yyyy-mm-dd_hh_mm_ss
   *
   * @return String
   */
  // ------------------------------------------
  private static String getDateStampSimple() {
    String dateStamp = "0000-01-01_00:00:00";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
    dateStamp  = sdf.format(new Date());
    
    return dateStamp;
    
    // End Method getDateStampSimple() -----------------------
  }

  // ------------------------------------------
  /**
   * mkDir creates a directory and any paths to that dir
   *
   * @param pDir
   * @throws Exception 
   */
  // ------------------------------------------
  private static void mkDir(String pDir ) throws Exception {
  
    File adir = new File ( pDir);
    if ( !adir.exists() )
      adir.mkdirs();
    
  } // End Method mkDir() -----------------------
  
    //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param pOutputDir
     * @param pLabels  List of annotations to keep (if null, all annotations are converted
     * @param pAnnotator
     * 
     **/
    // ----------------------------------
    public void initialize(String pOutputDir ) throws Exception {
      
    
      this.outputDir = pOutputDir;
      
      this.bioCFactory = (BioCFactoryImpl) BioCFactoryImpl.newFactory(BioCFactoryImpl.WOODSTOX);
   // new BioCFactoryImpl();
      // -----------------------
      // Make this directory if it doesn't exit
      try {
        mkDir(this.outputDir);
      } catch (Exception e1) {
        e1.getStackTrace();
        String msg = "Issue with trying to make the BioC output dir \n" + e1.getMessage();
        System.err.println( msg );
        throw new Exception ();
      }
      
    } // end Method initialize() --------------



    // ----------------------------------------
    // Global variables
    // ----------------------------------------
   
   
  
    private String annotator = "v3NLP";
    private String outputDir;
    private BioCFactory bioCFactory = null;
    private int annotationCounter = 0;
  
} // end Class toCommonModel
