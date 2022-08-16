// =================================================
/**
 * FamilyMemersApplication 
 *
 *  Converts concepts with the semantic type t099 (FamilyMember) to the annotations 
 *  that is FamilyMember.  
 * @author  Guy Divita 
 * @created April 28, 2013
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.familyMembers;

import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.CodedEntry;
import gov.va.vinci.model.FamilyMember;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class FamilyMembersAnnotator extends JCasAnnotator_ImplBase {
 
  
  // -----------------------------------------
  /**
   * process 
   * 
   * @param pJCas
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
   
    HashSet<String> snippetSet = new HashSet<String>();
    // retrieve coded entries if they exist
    List<Annotation> codedEntries = UIMAUtil.getAnnotations(pJCas, gov.va.chir.model.CodedEntry.typeIndexID );
    
    if ( codedEntries != null && codedEntries.size() > 0) {
      
      // ------------------------------
      // Retrieve just those codedEntries that are T099 (Family Member)
      
      for ( Annotation codedEntry: codedEntries ) {
        StringArray semanticTypez = ((CodedEntry) codedEntry).getSemanticType();
        if ( semanticTypez != null) {
          String semanticTypes = UIMAUtil.stringArrayToString(semanticTypez);
          
          if ( semanticTypes.contains("T099")|| 
              semanticTypes.contains("famg") || 
              semanticTypes.contains("Family Group")) {
              
            String key = codedEntry.getBegin() + "|" + codedEntry.getEnd();
            
            if ( !snippetSet.contains(key) ) {
              snippetSet.add( key);
              
              createFamilyMember(pJCas, codedEntry);
              
            }
        
            
            
          } // found a family member
          
        } // end if there are semantic types
      }
      
      
    } // we've got codedEntries
   
  } // end Method process 


 
// ------------------------------------------
  /**
   * createFamilyMember
   *
   *
   * @param pJCas
   * @param pAnnotation
   */
  // ------------------------------------------
  private void createFamilyMember(JCas pJCas, Annotation pAnnotation) {
    
    FamilyMember fm = new FamilyMember( pJCas);
    
    fm.setBegin( pAnnotation.getBegin());
    fm.setEnd(   pAnnotation.getEnd());
    fm.addToIndexes();
    
    
  }  // End Method createFamilyMember() -----------------------
  



//----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
       
   
      
  } // end Method initialize() -------
  
  
  
} // end Class MetaMapClient() ---------------
