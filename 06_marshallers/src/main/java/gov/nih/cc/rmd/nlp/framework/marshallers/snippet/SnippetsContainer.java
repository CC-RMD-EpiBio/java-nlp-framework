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
