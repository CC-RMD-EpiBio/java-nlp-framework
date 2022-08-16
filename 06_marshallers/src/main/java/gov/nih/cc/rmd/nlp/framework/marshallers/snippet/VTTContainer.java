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
package gov.nih.cc.rmd.nlp.framework.marshallers.snippet;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.U;

public class VTTContainer {

	


  // =======================================================
   /**
    * Constructor creates a new VTT Container from strings filled with metaData, text, labels, and markups
    * 
    * @param pMetaDatas
    * @param pTexts
    * @param pLabels
    * @param pMarkups
    */
   // ======================================================
  public VTTContainer(String pMetaData, String pText, String pLabels, String pMarkups) {
  
    this.metaData = pMetaData;
    this.text     = pText;
    this.labels   = pLabels;
    this.markups  = pMarkups;
  
  }  // End Constructor VTTContainer =============
    
  
  
  // -----------------------------------------
  /**
   * readVTTSnippetFile reads in a vtt snippets file (v3NLP Framework
   * 
   * @param pRows
   * @return VTTContainer
   * @throws Exception
   *
   */
  // -----------------------------------------
  public static final VTTContainer parseVTTFile(String pBuff ) throws Exception {
    
    VTTContainer returnVal = null;
    
    if ( pBuff != null && !pBuff.isEmpty()) {
      String rows[] = U.split(pBuff, "\n");
      returnVal = parseVTTFile( rows );
    }
    
    return returnVal;
  } // End Method parseVTTFile() ------------
  
  
  // -----------------------------------------
	/**
	 * readVTTSnippetFile reads in a vtt snippets file (v3NLP Framework
	 * 
	 * @param pRows
	 * @return VTTContainer
	 * @throws Exception
	 *
	 */
	// -----------------------------------------
	public static final VTTContainer parseVTTFile(String[] pRows) throws Exception {
		
		VTTContainer aVttContainer = null;
		
		int i = 0;
		List<String> metaDatas = null;
		List<String> texts = null;
		List<String> labels = null;
		List<String> markups = null;
		int sectionType = METADATA_SECTION;
		metaDatas = new ArrayList<String>();
	
		while ( i < pRows.length ) { 
		
			String row = pRows[i] + "\n";
			
			if      ( row.startsWith(      MetadataHeaderPattern)) { i = i+1;  sectionType= METADATA_SECTION; continue; }
			else if ( row.startsWith(          TextHeaderPattern)) { i = i+2;     texts = new ArrayList<String>(); sectionType =    TEXT_SECTION; continue; }
			else if ( row.startsWith(        LabelsHeaderPattern)) { i = i+5;    labels = new ArrayList<String>(); sectionType =  LABELS_SECTION; continue; }
			else if ( row.startsWith(MarkupsSectionHeaderPattern)) { i = i+3;   markups = new ArrayList<String>(); sectionType = MARKUPS_SECTION; continue; }
			
			switch ( sectionType) {
			case METADATA_SECTION: metaDatas.add( row ); break;
			case TEXT_SECTION:         texts.add( row ); break;
			case LABELS_SECTION:      labels.add( row ); break;
			case MARKUPS_SECTION:    markups.add( row ); break;
			default : ;
			}
			i++;	
			
		} // end loop thru file
		
		// trim off trailing rows that don't belong
		metaDatas.remove( metaDatas.size() - 2);  // take off the #<------ that's the start of the text section heading
		texts.remove(         texts.size() - 1);  // take off the #<------ that's the start of the tags section
		labels.remove(       labels.size() - 1);  // take off the #<------ that's the start of the markups section
		
		StringBuffer metaData = new StringBuffer();	 for (String row: metaDatas)  metaData.append( row);
		StringBuffer textData = new StringBuffer();  for (String row: texts )      textData.append( row);
		StringBuffer labelsData = new StringBuffer();for (String row: labels)      labelsData.append(row);
		StringBuffer  markupsData = new StringBuffer(); for (String row: markups)   markupsData.append(row);
		
		aVttContainer = new VTTContainer( metaData.toString(), textData.toString(), labelsData.toString(), markupsData.toString());
		
		
		return aVttContainer;
	} // End Method parseVttContainer() ======================

		
	
  // =======================================================
  /**
   * getMarkups 
   * 
   * @return the markups
   *
   */
  // ======================================================
  
  public final String getMarkups() {
    return markups;
  } // End Method getMarkups} ============


  // =======================================================
  /**
   * setMarkups 
   * 
   * @param markups the markups to set
   */
  // ======================================================
  public final void setMarkups(String markups) {
    this.markups = markups;
  } // End Method setMarkups} ============


  // =======================================================
  /**
   * getLabels 
   * 
   * @return the labels
   *
   */
  // ======================================================
  
  public final String getLabels() {
    return labels;
  } // End Method getLabels} ============


  // =======================================================
  /**
   * setLabels 
   * 
   * @param labels the labels to set
   */
  // ======================================================
  public final void setLabels(String labels) {
    this.labels = labels;
  } // End Method setLabels} ============


  // =======================================================
  /**
   * getText 
   * 
   * @return the text
   *
   */
  // ======================================================
  
  public final String getText() {
    return text;
  } // End Method getText} ============


  // =======================================================
  /**
   * setText 
   * 
   * @param text the text to set
   */
  // ======================================================
  public final void setText(String text) {
    this.text = text;
  } // End Method setText} ============


  // =======================================================
  /**
   * getMetaData 
   * 
   * @return String
   *
   */
  // ======================================================
  
  public final String getMetaData() {
    return metaData;
  } // End Method getMetaData} ============


  // =======================================================
  /**
   * setMetaData 
   * 
   * @param metaData the metaData to set
   */
  // ======================================================
  public final void setMetaData(String metaData) {
    this.metaData = metaData;
  } // End Method setMetaData} ============


  

  // =======================================================
  /**
   * getFileName
   * 
   * @return String
   *
   */
  // ======================================================
  
  public final String getFileName() {
    return this.fileName;
  } // End Method getFileName ============


  // =======================================================
  /**
   * setFileName
   * 
   * @param pFileName
   */
  // ======================================================
  public final void setFileName(String pFileName) {
    this.fileName = pFileName;
  } // End Method setFileName() ============

//=======================================================
 /**
  * write writes out this container 
  * 
  * @param pFileName
 * @throws Exception 
  */
 // ======================================================
 public final void write(String pFileName) throws Exception {
   
   PrintWriter out = null;
   
   try {
  
     out = new PrintWriter( pFileName);
     
     // ----------------------
     // Write out the metadata
     // ----------------------
     out.print(MetadataHeader);
     out.print(this.metaData);
     
     //-----------------------
     // write out the text
     // ----------------------
     out.print(TextHeader);
     out.print(this.text);
     
     // ---------------------
     // write out the labels
     // ---------------------
     out.print(LabelsHeader);
     out.print(this.labels);
     
     // ---------------------
     // write out the markups
     // ---------------------
     out.print(MarkupsHeader);
     out.print(this.markups);
     
     out.close();
     
     
    } catch ( Exception e ) {
      try { out.close(); } catch (Exception e2) {}
      e.printStackTrace();
      System.err.println("Issue within method write " + e.getMessage() );
      throw e;
    }
   
 } // End Method write  ==================================


  
  
  // -------------------------
  // Class Variables
  // -------------------------
  
  private  String  markups = null;
  private  String   labels = null;
  private  String     text = null;
  private  String metaData = null;
  private  String fileName = null;
	
    
  
  
  
  
		private static final int METADATA_SECTION = 0;
		private static final int     TEXT_SECTION = 1;
		private static final int   LABELS_SECTION = 2;
		private static final int  MARKUPS_SECTION = 3;
		
		public static final String       MetadataHeaderPattern = "#<Meta Data>";
		public static final String           TextHeaderPattern = "#<Text Content>";
		public static final String         LabelsHeaderPattern = "#<Tags Configuration>";
		public static final String MarkupsSectionHeaderPattern = "#<MarkUps Information>";
		
	  public static final String       MetadataHeader = 
	  "#<---------------------------------------------------------------------->\n" +
	  "#<Meta Data>\n" +
	  "#<---------------------------------------------------------------------->\n" ;
	  public static final String           TextHeader = 
    "#<---------------------------------------------------------------------->\n" + 
    "#<Text Content>\n" +
    "#<---------------------------------------------------------------------->\n";
    public static final String         LabelsHeader = 
    "#<---------------------------------------------------------------------->\n" +
    "#<Tags Configuration>\n" +
    "#<Name|Category|Bold|Italic|Underline|Display|FR|FG|FB|BR|BG|BB|FontFamily|FontSize>\n" +
    "#<---------------------------------------------------------------------->\n" +
    "highlight||false|false|false|true|0|0|0|192|192|192|Monospaced|+0\n" +
    "#<---------------------------------------------------------------------->\n";
    public static final String MarkupsHeader = 
    "#<---------------------------------------------------------------------->\n" +
    "#<MarkUps Information>\n" +
    "#<Offset|Length|TagName|TagCategory|Annotation|TagText>\n" +
    "#<---------------------------------------------------------------------->\n" ;
	
		
		
} // end Class VTT Container
