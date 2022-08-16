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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.U;

public class SnippetsContainer {

  
  // end Class SnippetsContainer() =======================
  
  // =======================================================
   /**
    * getSnippets returns a list of Snippets
    * 
    * @return List<SnippetContainer>
    *
    */
   // ======================================================	
  public List<SnippetContainer> getSnippets() {
     return this.snippets;
  } // end Method getSnippets() ============================




  // =======================================================
   /**
    * parse parses a vtt container into a set of snippets
    * 
    * @param pVttContainer
    * @return SnippetsContainer
   * @throws Exception 
    *
    */
   // ======================================================
  public static final SnippetsContainer parse(VTTContainer pVttContainer) throws Exception {
  
     SnippetsContainer snippetsContainer = null;
     try {
    	
       snippetsContainer = parseIntoSnippets( pVttContainer.getText());
       
       // match snippets up with annotations 
       snippetsContainer.updateSnippetsAnnotations( pVttContainer.getMarkups());
       snippetsContainer.setVttContainer( pVttContainer);
      
       
      } catch ( Exception e ) {
        e.printStackTrace();
        System.err.println("Issue within method parse " + e.getMessage() );
        throw e;
      }
   
     return snippetsContainer;
    } // End Method parse ==================================

  
  
  
  // =======================================================
   /**
    * setVttContainer 
    * 
    * @param pVttContainer 
    *
    */
   // ======================================================	
  public final void setVttContainer(VTTContainer pVttContainer) {
    this.vttContainer = pVttContainer;
    } // End Method setVttContainer =======================
    
  
  
  // =======================================================
   /**
    * getVttContainer returns the vttContainer
    * 
    * @return VTTContainer 
    *
    */
   // ======================================================  
  public final VTTContainer getVttContainer() {
    return this.vttContainer ;
    } // End Method getVttContainer() ======================
    
  


  // =======================================================
   /**
    * write writes out the snippets to a vtt file
    * 
    * @param pFileName 
   * @throws Exception 
    *
    */
   // ======================================================	
  public final void write(String pFileName) throws Exception {
    
    try {
      
      createVttTextAndMarkupsSectionFromSnippets();
      
      this.vttContainer.write( pFileName);
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with write " + e.getMessage();
      System.err.println( msg );
      throw e;
    }
   
    } // End Method write() ===============================

  // =======================================================
   /**
    * getMetaData retrieves the vtt container's metadata
    * 
    * @return String
    *
    */
   // ======================================================	
  public final String getMetaData() {
    return this.vttContainer.getMetaData();
  } // End Method getMetaData ============


  // =======================================================
   /**
    * getLabels retrieves the vtt container's labels
    * 
    * @return String
    *
    */
   // ======================================================  
  public final String getLabels() {
    return this.vttContainer.getLabels();
  } // End Method getLabels ================================




  // =======================================================
   /**
    * createVttTextAndMarkupsSectionFromSnippets creates text section
    * from the snippets
   * @throws Exception 
    *
    */
   // ======================================================	
  private final void createVttTextAndMarkupsSectionFromSnippets() throws Exception {
    
    StringBuffer textPart = null;
    StringBuffer markupPart = null;
    try {
       textPart = new StringBuffer();
       markupPart = new StringBuffer();
       
       for ( int i = 0; i < this.snippets.size(); i++ ) {
         SnippetContainer aSnippet = this.snippets.get(i);
         textPart.append( aSnippet.toString());
         
         SnippetMarkup snippetMarkup = aSnippet.getMarkup() ;
         markupPart.append( snippetMarkup.toString());
         
         
         
         
       } // end loop thru snippets
    
      } catch ( Exception e2 ) {
        e2.printStackTrace();
        System.err.println("Issue within method updateVttTextSectionFromSnippets " + e2.getMessage() );
        throw e2;
      }
    } // End Method createVttTextAndMarkupsSectionFromSnippets ============



  // =======================================================
   /**
    * updateSnippetsAnnotations adds annotations to each snippet from the 
    * markups
    * 
    * @param pMarkups
   * @throws Exception 
    *
    */
   // =======================================================
  private final void updateSnippetsAnnotations(String pMarkups) throws Exception {
   
     try {
    
       String[] markups = U.split(pMarkups, "\n");
       
       for ( String markup : markups ) {
         if ( markup != null && !markup.isEmpty()  && !markup.startsWith("#")) {
         SnippetMarkup aMarkup = SnippetMarkup.parse( markup );
        
         SnippetContainer aSnippet = getSnippet( aMarkup);
         
         if ( aSnippet != null ) {
         
             if ( aMarkup.getLabel() != SnippetMarkup.SNIPPET_LABEL ) {
               aSnippet.setDecision(  aMarkup.getLabel());
             }
         } else {
           System.err.println("Issue with this markup.  It goes to no snippet " + markup  );
         }
       }
       } // end if there is a filled out line
      } catch ( Exception e ) {
        e.printStackTrace();
        System.err.println("Issue within method updateSnippetsAnnotations " + e.getMessage() );
        throw e;
      }
    } // End Method updateSnippetsAnnotations() ===========
    
  

  // =======================================================
   /**
    * getSnippet iterates thru the snippets in this container
    * to find the snippet that corresponds to the markup passed
    * in.
    * 
    * @param aMarkup
    * @return SnippetContainer
    *
    */
   // ======================================================	
  private SnippetContainer getSnippet(SnippetMarkup aMarkup) {
    
    SnippetContainer returnVal = null;
 
    try {
    	String  t = aMarkup.getSnippetId();
        int key = Integer.parseInt(aMarkup.getSnippetId());
      
        returnVal = this.snippetContainerHash.get( key);
    
      } catch ( Exception e ) {
        e.printStackTrace();
        System.err.println("Issue within method getSnippet " + e.getMessage() );
        throw e;
      }
    
      return returnVal;
    } // End Method getSnippet ============
    

  // =======================================================
   /**
    * parseIntoSnippets walks through the v3nlpframework snippets
    * to create a set snippets within the snippet container
    * 
    * @param pText
    * @return SnippetsContainer
   * @throws Exception 
    *
    */
   // =======================================================
  private static SnippetsContainer parseIntoSnippets(String pText) throws Exception {
  
      SnippetsContainer snippetsContainer = new SnippetsContainer();
     try {
       
       String[] rows = U.split(pText, "\n");
       List<String> snippetRows = new ArrayList<String>(10);
       
       for (int i = 0; i < rows.length; i++ )  {
         String row = rows[i];
         if      ( row.equals(  Context.CONTEXT_END_DELIMITER )    ) { 
           snippetRows.add( row ); 
           SnippetContainer aSnippet =  SnippetContainer.parse( snippetRows );  
           snippetsContainer.addSnippet( aSnippet ); 
           snippetRows = null;
           
         } else if ( row.equals(Context.CONTEXT_BEGIN_DELIMITER )) { 
           snippetRows = new ArrayList<String>(); 
           snippetRows.add( row );
         } else {      
           if ( snippetRows != null )
             snippetRows.add( row );
          
         }
         
       } // end loop thru snippets
       if ( snippetRows != null  && !snippetRows.isEmpty() ) {
         SnippetContainer aSnippet =  SnippetContainer.parse( snippetRows );  
         snippetsContainer.addSnippet( aSnippet ); 
       }
      
       
      } catch ( Exception e ) {
        e.printStackTrace();
        System.err.println("Issue within method parseIntoSnippets " + e.getMessage() );
        throw new Exception(e);
      }
     
     return snippetsContainer;
    } // End Method parseIntoSnippets ============



  // =======================================================
   /**
    * addSnippet adds a snippet to the snippets container.  
    * An index based on the begin and end offset is updated.
    * 
    * @param pSnippet
    *
    */
   // ======================================================	
  private void addSnippet(SnippetContainer pSnippet) {
   
    
     try {
       if ( this.snippets == null )  this.snippets = new ArrayList<SnippetContainer >();
       
       this.snippets.add( pSnippet );
       
       // --------------------------
       // Index this snippet by begin and end offset
       int key = pSnippet.getSnippetId();
       
       if ( this.snippetContainerHash == null )  this.snippetContainerHash = new HashMap<Integer, SnippetContainer>();
       this.snippetContainerHash.put( key, pSnippet);
       
      } catch ( Exception e ) {
        e.printStackTrace();
        System.err.println("Issue within method addSnippet " + e.getMessage() );
        throw e;
      }
    } // End Method addSnippet ============
    
 
  // -----------------------------
  // Class Variables
  // -----------------------------
  private List<SnippetContainer>                        snippets = null;
  private VTTContainer                              vttContainer = null;
  private HashMap<Integer,SnippetContainer> snippetContainerHash = null;
 
    




} // End Method getSnippets ============
