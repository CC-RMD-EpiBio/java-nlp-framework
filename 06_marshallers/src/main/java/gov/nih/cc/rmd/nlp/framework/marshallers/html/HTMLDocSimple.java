/*******************************************************************************
 * ---------------------------------------------------------------------------
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                        Epidemiology and Biostatistics Branch 
 *                                         2019
 *    
 *  This work is licensed under the Creative Commons Attribution 4.0 International License. 
 *  
 *  This license allows you to use, share and  adapt for any purpose, provided:
 *     Provide attribution to the creators of this work within your work.
 *     Indicate if changes were made to this work.
 *     No claim to merchantability, implied warranty, or liability can be made.
 *     
 *   When attributing this code, please make reference to
 *    [citation/url here] .  
 *    
 *     In the absence of a specific paper or url listed above, reference http://clinicalcenter.nih.gov/rmd/eb/nlp
 *  
 *  To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 ******************************************************************************/
/**
 * 
 */
package gov.nih.cc.rmd.nlp.framework.marshallers.html;

import java.io.PrintWriter;
import java.util.List;

import gov.va.chir.model.DocumentHeader;

/**
 * @author Guy
 *
 */
public class HTMLDocSimple {

  private String outputDir;
  private String fileName;
  private String documentId;
  private String documentTitle;
  private String documentType;
  private String patientId;
  private String metaDataFieldNames;
  private String metaData;
  private boolean positiveOrControl;
  private String body;
  private String header;
  private String head;
  private String nav;

  // ==========================================
  /**
   * HTMLDoc 
   *
   */
  // ==========================================
  public HTMLDocSimple() {
    
  } // end Constructor ========================
  

  // ==========================================
  /**
   * addHead
   *
   * @param pHeadAndCSSTypes
   */
  // ==========================================
  public void addHead(String pHeadAndCSSTypes) {
    this.head = pHeadAndCSSTypes;
    
  } // end Method addCSSTypes() ===============
  

  // ==========================================
  /**
   * setOutputDir
   *
   * @param pOutputDir
   */
  // ==========================================
  public void setOutputDir(String pOutputDir) {
    this.outputDir = pOutputDir;
    // end Method setOutputDir() ==============
  }

  // ==========================================
  /**
   * setFileName
   *
   * @param pDocumentId
   */
  // ==========================================
  public void setFileName(String pDocumentId) {
    this.fileName = pDocumentId + ".html";
    // end Method setFileName() ===============
  }

  // ==========================================
  /**
   * setMetaData
   *
   * @param pDocumentHeader
   */
  // ==========================================
  public void setMetaData(DocumentHeader pDocumentHeader) {
    
    this.documentId = pDocumentHeader.getDocumentId();
    this.documentTitle = pDocumentHeader.getDocumentTitle();
    this.documentType = pDocumentHeader.getDocumentType();
    this.patientId = pDocumentHeader.getPatientID();
    this.metaDataFieldNames = pDocumentHeader.getOtherMetaDataFieldNames();
    this.metaData = pDocumentHeader.getOtherMetaData();
    this.positiveOrControl = pDocumentHeader.getPositiveOrControl();
    
    this.header = "<h1> DocumentTitle:     " + this.documentTitle    + "</h1>\n" + 
                  "<table style=\"width: 100%\" align=\"right\" border=\"1px black\">\n" +
                  "  <tr><td align=\"right\">DocumentId       </td> <td> " + this.documentId        + "</td></tr>\n" +
                  "  <tr><td align=\"right\">Document Type    </td> <td style=\"white-space: nowrap\"> " + this.documentType      + "</td></tr>\n" +
                  "  <tr><td align=\"right\">PatientId        </td> <td> " + this.patientId         + "</td></tr>\n" +
                  "  <tr><td align=\"right\">PositiveOrControl</td> <td> " + this.positiveOrControl + "</td></tr>\n" + 
                  "  <tr><td style=\"white-space: nowrap\" align=\"right\">" + this.metaDataFieldNames                            + "</td></tr>\n" +
                  "  <tr><td style=\"white-space: nowrap\" align=\"right\">" + this.metaData                                      + "</td></tr>\n" +
                  "</table>\n";
                  
    
  } // end Method setMetaData() ===============
  

  // ==========================================
  /**
   * write assembles and writes the file out
   * 
   * @return String = the name and path of the html file
   *
   */
  // ==========================================
  public String write() {
    
    String html = renderToHTML();
    String outputFile = null;
    PrintWriter out = null;
    try {
      outputFile = this.outputDir + "/" + this.fileName;
      out = new PrintWriter( outputFile );
      
      out.print(html);
      
      out.close();
      
    } catch ( Exception e) {
      
    }
   
    return outputFile;

  } // end Method write() =====================
  

  // ==========================================
  /**
   * renderToHTML assembles the html into
   * all the right pieces
   *
   * @return String
   */
  // ==========================================
  protected String renderToHTML() {
 StringBuffer buff = new StringBuffer();
    
    buff.append("<!DOCTYPE html>\n");
    buff.append("<html>\n");
    buff.append("<head>\n");
    buff.append(this.head + "\n");
    buff.append("</head>\n");
    buff.append("  <body>\n");
    
    buff.append(this.body);
    
    buff.append("  </body>\n");
    buff.append("</html>\n");
    return buff.toString();
  }  // end Method renderToHTML() =============
  


  // ==========================================
  /**
   * setBody sets the body - this is expected to have the <body> tags already in them.
   *
   * @param pDocText
   */
  // ==========================================
  public void setBody(String pBody) {
  
    StringBuffer buff = new StringBuffer();
   
    buff.append("<pre>\n");
    buff.append( pBody );
    buff.append("</pre>\n");
    
    this.body = buff.toString();
    
  } // end Method setBody() ==================


  

}
