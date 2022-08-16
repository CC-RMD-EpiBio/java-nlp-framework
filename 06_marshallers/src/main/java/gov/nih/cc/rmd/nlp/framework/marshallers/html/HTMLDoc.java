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
public class HTMLDoc {

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
  public HTMLDoc() {
    
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
  private String renderToHTML() {
 StringBuffer buff = new StringBuffer();
    
    buff.append("<!DOCTYPE html>\n");
    buff.append("<html>\n");
    
    buff.append(" <head>\n");
    buff.append("  <style>\n");
    buff.append(this.head);
    buff.append("  </style>\n");
    buff.append(" </head>\n\n");
    
    buff.append(this.body);
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
   
    buff.append("<body>\n");
    buff.append("<div class=\"flex-container\">\n");
    
    buff.append("<header>\n");
    buff.append("   " + this.header + "\n");
    buff.append("</header>\n");
    
  
    buff.append(this.nav);
   
    buff.append("<article class=\"article\">\n");
    buff.append( pBody );
    
    buff.append("</article>\n");
    buff.append("</div>\n");
    buff.append("</body>\n");
    
    this.body = buff.toString();
    
  } // end Method setBody() ==================


  // ==========================================
  /**
   * setNavigation
   *
   * @param annotationAnchors
   */
  // ==========================================
  public void setNavigation(List<String> annotationAnchors) {

    StringBuffer buff = new StringBuffer();
    
    buff.append("  <nav class=\"nav\">\n");
    buff.append("     <ul>\n");
    
    for ( String annotationAnchor: annotationAnchors ) {
      buff.append("      ");
      buff.append(annotationAnchor);
      buff.append("\n");
    }
    buff.append("    </ul>\n");
    buff.append("  </nav>\n");

    this.nav = buff.toString();
  } // end Method setNavigation() =============
  
  

}
