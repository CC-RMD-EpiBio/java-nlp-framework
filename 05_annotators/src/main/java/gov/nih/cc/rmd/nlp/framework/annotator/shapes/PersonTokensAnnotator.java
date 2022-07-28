// =================================================
/**
 * PersonTokensAnnotator creates a word token from the
 * tokens that make up a Person annotation. 
 * The underlying tokens are removed.
 * 
 * This is to make a de-identified name look like a single token for machine learning work.
 * 
 * Example [ First Name = 2018 ]     <--  all of that would be within one WordToken
 *
 *
 * @author  Guy Divita 
 * @created November 7 2019
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.shapes;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.model.Person;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.Token;
import gov.va.chir.model.WordToken;
import gov.va.chir.model.VAnnotation;



public class PersonTokensAnnotator extends JCasAnnotator_ImplBase {
 
  
  // -----------------------------------------
  /**
   * process  creates a word token from the
   * tokens that make up a Person annotation. 
   * The underlying tokens are removed.
   * 
   * This is to make a de-identified name look like a single token for machine learning work.
   * 
   * Example [ First Name = 2018 ]     <--  all of that would be within one WordToken
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
    this.performanceMeter.startCounter();

    List<Annotation> persons = UIMAUtil.getAnnotations(pJCas, Person.typeIndexID, true);
    
    if ( persons != null && !persons.isEmpty() ) {
      for ( Annotation person: persons )
        convertPersonTokens( pJCas, person);
    }
    
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   


// =================================================
  /**
   * convertPersonTokens retrieves all the tokens withi the Person span,
   * and creates a single wordToken of them, removing the individual tokens.
   * 
   * @param pJCas
   * @param person
  */
  // =================================================
 private final void convertPersonTokens(JCas pJCas, Annotation pPerson) {
   
  String nameType = "Name";
  String txt = pPerson.getCoveredText();
  if ( txt.toLowerCase().contains("first"))
    nameType = "fname";
  else if ( txt.toLowerCase().contains("last"))
    nameType = "lname";
  else if ( txt.toLowerCase().contains("nickname"))
    nameType = "nname";
  
  List<Annotation> personTokens = UIMAUtil.getAnnotationsBySpan(pJCas, Token.typeIndexID, pPerson.getBegin(),  pPerson.getEnd(), true);
  Annotation firstToken = null;
  if ( personTokens != null && !personTokens.isEmpty() ) {
    firstToken = personTokens.get(0);
    int k = 0;
    while ( firstToken.getClass().getName().contains("White") )
      firstToken = personTokens.get(k++);
    try {
    createWordToken( pJCas, pPerson.getBegin(), pPerson.getEnd(), (WordToken) firstToken, nameType );  
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    for ( int i = 1; i < personTokens.size(); i++ ) {
      if ( personTokens.get(i) != null )
       personTokens.get(i).removeFromIndexes();
    }
  }
    
  } // end Method convertPersonTokens() --------------



  // =================================================
/**
 * createWordToken creates a word token
 * 
 * @param pJCas
 * @param pBegin
 * @param pEnd
*/
// =================================================
private final void createWordToken(JCas pJCas, int pBegin, int pEnd, WordToken pToken, String pNameType) {
 
  
  
  WordToken statement = pToken;
  statement.setEnd( pEnd);
  statement.setReplaceWithClass( true);
  statement.setDisplayString( pNameType );
  
  // statement.setId( "PersonTokenAnnotator_" + annotationCtr++);

  
  /*
  statement.setContainsPunctuation(true);
  statement.setAllCapitalization(false);
  statement.setContainsSymbol(true);
  statement.setInitialCapitalization(false);
  statement.setInProse(true);
  statement.setListDelimiter(false);
  statement.setPunctuationOnly(false);
  statement.setSentenceBreak(false);
  statement.setWhiteSpaceFollows(true);
  
*/
  
  
} // end Method createWordToken() -------------





//----------------------------------
/**
 * destroy
* 
 **/
// ----------------------------------
public void destroy() {
  this.performanceMeter.writeProfile( this.getClass().getSimpleName());
}

  
  //----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
       
     
      String[] args = null;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  

        initialize(args);
        
      } catch (Exception e ) {
        String msg = "Issue in initializing class " + this.getClass().getName() + " " + e.toString() ;
        GLog.println(GLog.ERROR_LEVEL, msg);     // <------ use your own logging here
        throw new ResourceInitializationException();
      }
      
   
      
  } // end Method initialize() -------
  
  //----------------------------------
  /**
   * initialize initializes the class.  Parameters are passed in via a String
   *                array  with each row containing a --key=value format.
   *                
   *                It is important to adhere to the posix style "--" prefix and
   *                include a "=someValue" to fill in the value to the key. 
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    
      
  } // end Method initialize() -------
 
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  
  
  
} // end Class LineAnnotator() ---------------

