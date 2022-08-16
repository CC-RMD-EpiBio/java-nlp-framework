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
// =================================================
/**
 * An annotator that reads in one cvs file of metadata, and inserts
 * the meta data into each cas.
 *
 *
 * @author  Guy Divita 
 * @created Feb 25, 2015
 *
 **   *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.HashMap;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.Line;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;



public class FromCSVAnnotator extends JCasAnnotator_ImplBase {
 
  public static final String annotatorName = FromCSVAnnotator.class.getSimpleName();
  private String metaDataFormat = null;
  private HashMap<String,String[]> csvMetaDataHash = null;
private String metaDataColumnTypes;
  
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * This version labels lines of the document.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
    
    // -----------------------
    // retrieve the documentId - tiu note id from the documentId
    
   DocumentHeader documentHeader = VUIMAUtil.getDocumentHeader(pJCas);
   
   if ( documentHeader != null ) {
     String documentId = documentHeader.getDocumentId();
     String oldDocumentId = documentId;
     documentId = cleanDocumentId( documentId);
     
     String[] metaDataColumns = this.csvMetaDataHash.get(documentId);
     if ( metaDataColumns == null  || metaDataColumns.length < 1) {
       
       System.err.println("Why is there no data for documentId |" + documentId + "|");
       
       //Look one more time at looking for doc ids of the form "EOFMUS_XXXXX"
       int underbar = documentId.indexOf("_");
       if (underbar > 0 ) {
         documentId = documentId.substring(underbar + 1);
         metaDataColumns = this.csvMetaDataHash.get(documentId);
     }
              
     } 
       
     if ( metaDataColumns == null ) {
       
       System.err.println( "Still missing metadata for " + oldDocumentId);
     } else {
     
     StringBuffer metaDataBuff = new StringBuffer();
     for (String field:  metaDataColumns   ) metaDataBuff.append( field + "|");
       System.err.println("-->"  + metaDataBuff);
     
       documentHeader.setDocumentName( metaDataColumns[1].trim() );
       documentHeader.setDocumentTitle(metaDataColumns[2]);
       documentHeader.setDocumentType( metaDataColumns[2]);
       documentHeader.setOtherMetaData( metaDataBuff.toString());
       documentHeader.setOtherMetaDataFieldNames( this.metaDataFormat );
       documentHeader.setOtherMetaDataFieldTypes( this.metaDataColumnTypes);
     //documentHeader.setPatientID(v);
     //documentHeader.setReferenceDate(v);
     }
     
     
   }
    
  } // end Method process() ----------------
   
  
  
// =======================================================
  /**
   * cleanDocumentId retrieves a documentId sans fileName or suffix stuff
   * 
   * @param documentId
   * @return String
   */
  // =======================================================
  private String cleanDocumentId(String documentId) {
 // ----------------
    // Strip off any suffixes that might be on this - assume this matches the key in the csv file
    int period = 0;
    int fileSeparator = 0;
    if ( (period = documentId.indexOf(".")) > 1 ) {
      documentId = documentId.substring(0, period);
    if ( (fileSeparator = documentId.lastIndexOf('/')) > 0)
      documentId = documentId.substring(fileSeparator+1 );
    }
    return documentId;
  }  // End Method cleanDocumentId() ======================
  



//-----------------------------------------
 /**
  * initialize loads in the resources.
  *   Put pipeline parameters in the args parameter
  *   to be retrieved here.
  *
  * @param aContext
  *
  */
 // -----------------------------------------
  public void initialize(UimaContext aContext)  throws ResourceInitializationException {
   
    String args[] = null;
    
    try {
      args                  = (String[]) aContext.getConfigParameterValue("args");
      
      System.err.println(" version : feb26: 9:01");
      // ------------------------------------------------------------
      // Example parameter passed from the pipeline to this annotator.
      //    The args string array contains --key=value elements. The 
      //    U.getOption() method traverses thru the args to find the
      //    --key=  and parse the element to retrieve the value for 
      //    this key.
      // -------------------------------------------------------------
      String        metaDataFile = U.getOption(args,  "--csvMetaDataFile=", "./someFile.csv");
      String metaDataColumnNames = U.getOption(args, "--csvColumnNames=", "documentId|documentName|documentTitle|...");
      this.metaDataColumnTypes = U.getOption(args,  "--csvColumnTypes=", "varchar(20)|varchar(20)|varchar(40)");
      int     csvDocumentIdField = Integer.parseInt(U.getOption(args, "--csvDocumentIdField=", "0"));
      char        csvDelimiter =  U.getOption(args,  "--csvDelimiter=", ",").charAt(0);
      
      initializeMetaData(metaDataFile, metaDataColumnNames, csvDelimiter, csvDocumentIdField);
      
      
     
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with getting the passed in arguments " + e.toString());
      throw new ResourceInitializationException();
    
    }
 
  } // end Method initialize() -----------
  
// =======================================================
/**
 * initializeMetaData Opens and retrieves the data in the 
 * csv file, and creates a hash key'd by documentId 
 * 
 * Assume the first row is the column headings, and the that the first
 * field is the documentID
 * 
 * N.B.  The contents of this file are read into memory - if this
 * is too big a file (~1gig size, don't use this mechanism)
 * 
 * @param pMetaDataFile
 * @param pMetaDataColumnNames
 * @param pCSVDelimiter   (by default a ",")
 * @param pDocumentIdField     (by default the first field in the row) THIS IS ZERO based index)
 * @throws Exception 
 */
// =======================================================
private void initializeMetaData(String pMetaDataFile, 
                                String pMetaDataColumnNames, 
                                char pCSVDelimiter, 
                                int pDocumentIdField) throws Exception {
 
  String rows[] = null;
  
  
  try {
    rows = U.readFileIntoStringArray(pMetaDataFile);
    
    if ( rows != null && rows.length > 0 ) {
  
      this.csvMetaDataHash = new HashMap<String,String[]>();
      this.metaDataFormat = rows[0];
      this.metaDataFormat = this.metaDataFormat.replace(',', '|');   // downstream parsers parse on pipe
      this.metaDataFormat = this.metaDataFormat.replace('#', '_' );  // first row starts with #
      
      for ( int i = 1; i < rows.length; i++ ) {
        if ( rows[i].trim().length() > 0 ) {
          try {
            String row = rows[i].replace(',', '|');
            String cols[] = U.split( row );
            String documentId = cols[pDocumentIdField];
            
            System.err.println("The document ID =|" + documentId + "|");
            this.csvMetaDataHash.put( documentId, cols);
            
          } catch (Exception e ) {
            e.printStackTrace();
            System.err.println("on row " + i);
            System.err.println("using col =" + pDocumentIdField);
            System.err.println("Something went wrong with this line:\n" + rows[i] + "\n" + e.toString());
          }
        } // end non-empty rows
        
      } // end loop thru rows
      
    } // end if there are any rows
  
    
    
    
  } catch (Exception e) {
    e.printStackTrace();
    System.err.println("Issue reading in the metadata file " + pMetaDataFile + " " + e.toString());
    throw e;
  }
  
} // End Method initializeMetaData() ======================




//-----------------------------------------
 /**
  * createExampleAnnotation creates lineAnnotatons
  * 
  * See the type.decriptor project gov.va.chir.Model.xml for the 
  * the definition of what a Line Type is.
  * 
  * @param pJCas
  * @param beginSpan
  * @param endSpan
  * 
  */
 // -----------------------------------------
 private void createExampleAnnotation( JCas pJCas,  int beginSpan, int endSpan )  {
  
  Line statement = new Line( pJCas);
   
   
   VUIMAUtil.setProvenance( pJCas, statement, this.getClass().getName() );
   statement.setBegin(                    beginSpan);
   statement.setEnd(                        endSpan);
   
   statement.addToIndexes(pJCas);   // <------------- very important to do this

 } // end Method createEvidence() ---
  
  


  
  
} // end Class ExampleAnnotator() ---------------
