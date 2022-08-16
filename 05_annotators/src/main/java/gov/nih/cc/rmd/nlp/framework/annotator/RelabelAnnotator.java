// =================================================
/**
 * RelabelAnnotator re-names a given label to another given label.  
 * This is a common task, say when taking in annotations from human annotated
 * files.
 *   i.e., knowtator->uima-cas->relabeled to conform with the chir labels.
 * 
 * 
 * 
 * 
 * @author Guy Divita
 * @created Feb 8, 2012
 * 
 *  
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.VAnnotation;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.FeatureValuePair;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class RelabelAnnotator extends JCasAnnotator_ImplBase {
  
  @Override
  // -----------------------------------------
  /**
   * process takes in a cas that's filled with annotations
   * of one label, and transforms them to a cas with 
   * other labels.
   * 
   * 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public final void process(JCas pJCas) throws AnalysisEngineProcessException {
    
    // ---------------------------------------------
    // Iterate through each of the (original) labels
  

    for (int i = 0; i < this.origLabels.length; i++) {

      try {
        // ------------------------------------------
        // Find the orig label class

        org.apache.uima.cas.Type    labelType = UIMAUtil.getAnnotationType(pJCas, origLabels[i]);
        
       
        if ( labelType == null ) 
          return;
        List<Annotation> origLabelAnnotations = UIMAUtil.getAnnotations(pJCas, labelType, false);
        if ( origLabelAnnotations != null ) {
        	GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", 
        	    "The number of annotations picked up is " + origLabelAnnotations.size());
        	//System.err.println("for " + origLabels[i]);

        	// -----------------
        	// find the relabel for this annotation
        	String relabel = this.labelPairsHash.get(this.origLabels[i]);
        	//relabel = U.getNameWithoutNameSpace(relabel);
      
        	// -----------------
        	// Find the relabel's class
        	@SuppressWarnings("unchecked")
          Class<Annotation> relabelClass = (Class<Annotation>) UIMAUtil.mapLabelToUIMAClass(relabel);
        	

    
          markAnnotations( origLabelAnnotations);
          ArrayList<Annotation> annotations = new ArrayList<Annotation>();
          HashMap<String,Annotation> aHash = new HashMap<String, Annotation> ();
          for (Annotation anAnnotation : origLabelAnnotations) {
        	 String key = anAnnotation.getBegin() + "|" + anAnnotation.getEnd();
        	 Annotation vAnnotation = aHash.get(key);
        	 if ( vAnnotation != null ) continue;
        	 else aHash.put(key, anAnnotation);
        	 {
        		// System.err.println("Looking at |" + anAnnotation.getBegin() + "|" + anAnnotation.getEnd() + "|" + anAnnotation.getCoveredText() + "|"  + anAnnotation.getType().getName() + "|" );
        		// -----------------------
        		// Create a new annotation
        		annotations.add( createAnnotation(pJCas, anAnnotation, relabelClass));
        		
        		// -----------------------
        		// remove the orig annotation (not really needed here)
        		if (this.remove)
        			anAnnotation.removeFromIndexes(pJCas);
        	} // end if not marked 
          } // end loop through the annotations of this type
          
         
        } // end if there are any annotations
      } catch (Exception e) {
        e.printStackTrace();
        String msg = "Issue with relabel " + e.toString();
        System.err.println(msg);
        
        throw new RuntimeException(msg);
      }
    }
    
  } // end Method process() --------------------------
  
private final void markAnnotations(List<Annotation> origLabelAnnotations) {
	if ( origLabelAnnotations != null && !origLabelAnnotations.isEmpty() )
		for ( Annotation annotation : origLabelAnnotations)
			try {
				((VAnnotation) annotation).setMarked(false);
			}catch (Exception e) {
			}
}
/*
private boolean isMarked(Annotation anAnnotation) {
	boolean returnVal = false;
	try {
		returnVal =((VAnnotation) anAnnotation).getMarked();
	}catch (Exception e) {
		
	}
	return false;
}

private void mark(Annotation anAnnotation) {
	try {
		((VAnnotation) anAnnotation).setMarked(true);
	}catch (Exception e) {
		
	}
	
}
*/
// -----------------------------------------
  /**
   * createAnnotation creates an annotation given an original
   * annotation, and the type of annotation to create
   * 
   * @param pJCas
   * @param anAnnotation
   * @param relabel
   */
  // -----------------------------------------
  private final Annotation createAnnotation(JCas pJCas, Annotation anAnnotation, Class<?> relabelClass) {

    // ----------------------------------------------
    // create a UIMA annotation from this label
    // ----------------------------------------------
	  Object uimaAnnotation = null;
    try {
              
     
      Constructor<?> c = relabelClass.getConstructor(new Class[]{ JCas.class  });
      uimaAnnotation = c.newInstance(pJCas);
       
      // --------------------------------------------------
      // Add span and slot attributes to the uimaAnnotation
      // --------------------------------------------------
  
    
      ((Annotation) uimaAnnotation).setBegin(anAnnotation.getBegin());
      ((Annotation) uimaAnnotation).setEnd(anAnnotation.getEnd());
     
      // ------------------------------------------------
      // Copy over any attributes of the original to the the new annotation
      // ------------------------------------------------
      List<FeatureValuePair> featureValuePairs = UIMAUtil.getFeatureValuePairs( anAnnotation);
      if ( featureValuePairs != null && featureValuePairs.size() > 0) {
    	 
    	  for ( FeatureValuePair fvPair : featureValuePairs ) {
    		  String  featureName = fvPair.getFeatureName();
    		  String featureValue = fvPair.getFeatureValue();
    		  
    		  if ( featureName.contains("Negation") || featureName.contains("Assertion")) {
    			 // System.err.println("Found assertion/negation status, setting it");
    			  try {
    			  ((gov.va.vinci.model.Concept) uimaAnnotation).setAssertionStatus(featureValue);
    			  } catch ( Exception e) {
    				  try {
    				  ((VAnnotation) uimaAnnotation).setNegation_Status(featureValue); 
    				  } catch ( Exception e2) {}
    			  }
    			  
    		  } else {
    			  try {
    		  
    			  // --------------------------------------------------------------
                  // Is there a generic way to find out if this feature is a
                  // class?
                  // --------------------------------------------------------------
                  Method uimaSetFeatureMethod = UIMAUtil.mapFeatureToUIMAFeature(uimaAnnotation.getClass(), featureName);
                  if (uimaSetFeatureMethod != null) try {
                    if (featureValue != null) uimaSetFeatureMethod.invoke(featureValue, 0);
                  } catch (Exception e) {}
    			  
    		  
    		  } catch (Exception e) {
    		    e.printStackTrace();
    		  }
    		  } // end else
    	  } // end loop through the feature value pairs
    	  
      }
      
      // ----------------------------------------
      // Completely not generalizable!
      // ----------------------------------------
      // String annotatonType = anAnnotation.getType().getName();
      // if ( anAnnotation.getType().getName().equals("gov.va.chir.model.CodedEntry") ) {
      // 
      //  ((CodedEntry) uimaAnnotation).setCodeCode( ((CodedEntry) anAnnotation).getCodeCode()); 
      //  ((CodedEntry) uimaAnnotation).setCodeSystem( ((CodedEntry) anAnnotation).getCodeSystem()); 
      //  ((CodedEntry) uimaAnnotation).setDisplayName( ((CodedEntry) anAnnotation).getDisplayName()); 
      //  ((CodedEntry) uimaAnnotation).setCodeSystemName( ((CodedEntry) anAnnotation).getCodeSystemName()); 
      //  ((CodedEntry) uimaAnnotation).setSemanticType( ((CodedEntry) anAnnotation).getSemanticType()); 
      //  ((CodedEntry) uimaAnnotation).setSemanticGroup( ((CodedEntry) anAnnotation).getSemanticGroup()); 
      //  ((CodedEntry) uimaAnnotation).setParentPhrase( ((CodedEntry) anAnnotation).getParentPhrase()); 
      //  ((CodedEntry) uimaAnnotation).setParent( ((CodedEntry) anAnnotation).getParent());
      //  ((CodedEntry) uimaAnnotation).setNegation_Status(  ((CodedEntry) anAnnotation).getNegation_Status());
      //}
        
      
      ((org.apache.uima.jcas.tcas.Annotation) uimaAnnotation).addToIndexes(pJCas);
    } catch (Exception e) {
      e.getStackTrace();
      String msg = "\n" + e.toString()  ;
      System.err.println(msg);
      throw new RuntimeException(msg );
    }
      
    return ( (Annotation) uimaAnnotation);
    
  } // end Method createAnnotation

//----------------------------------
  /**
   * initialize pulls in the pairs of label|relabel to work with
   *
   * @param aContext
   * 
   **/
  // ----------------------------------
  public final void initialize(UimaContext aContext) throws ResourceInitializationException {
    
    String labelPairz = null;
    String labelPairs[] = null;
   
    String args[] = null;
    try {
      args = (String[]) aContext.getConfigParameterValue("args");
      
      if ( args != null && args.length > 0 )
        initialize( args );
      else {
  
        // ==========================================================
        // Old way of doing things - here for backward compatibility )
        this.remove = (boolean) aContext.getConfigParameterValue("remove");
       
         try {
           labelPairs = (String[]) aContext.getConfigParameterValue("labelPairs");
         } catch (Exception e) {
           labelPairz=  (String) aContext.getConfigParameterValue("labelPairs");
       
           if ( labelPairz != null ) {
             labelPairs = U.split(labelPairz);
           }
         }
         if ( labelPairs != null ) {
           origLabels = new String[labelPairs.length];
           this.labelPairsHash = new HashMap<String,String>( labelPairs.length);
        
           for ( int i = 0; i < labelPairs.length; i++ ) {
             String label_relabel[] = U.split(labelPairs[i], "|");
          
             origLabels[i] = label_relabel[0];
             this.labelPairsHash.put( label_relabel[0], label_relabel[1]);  // orig_label as the key, new label as the value
           }  // end loop through the labels and re-labels
        
       }
      }
      
     
    } catch (Exception e) {
     
      e.printStackTrace();
      String msg = "Issue getting a parameter " + e.toString();
      System.err.println(msg);
      throw new ResourceInitializationException();
    }
  
  } // end Method initialize() --------------

  
  // ==========================================
  /**
   * initialize extracts two parameters from 
   * the string[] args:   --remove=true|false
   *                      --labelPairs=aaa:bbb|ccc:ddd| ...
   *                      
   *                      pairs with elements delimited by colon, and pairs delimited with |
   *
   * @param pArgs
   */
  // ==========================================
  private final void initialize(String[] pArgs) {
    
    this.remove = Boolean.valueOf(U.getOption(pArgs, "--remove=", "false"));
    
    String labelPairs = U.getOption(pArgs,  "--labelPairs=","Concept:Copper|Symptom:Gold");
    GLog.println(GLog.INFO_LEVEL, this.getClass(), "initialize", "Relabeling " + labelPairs );
    this.labelPairsHash = createLabelPairHash( labelPairs);
    this.origLabels = createOrigionalLabels( labelPairs);
    
  } // end Method initialize() ================
  


  // ==========================================
  /**
   * createOrigionalLabels 
   *
   * @param labelPairs 
   * @return String[] 
   */
  // ==========================================
  private final String[] createOrigionalLabels(String labelPairs) {
    String[] labelPairz = U.split(labelPairs);
    String[] _originalLabels = new String[labelPairz.length ];
    int i = 0;
    for ( String row : labelPairz ) {
      String col[] = U.split(row, ":");
      _originalLabels[i] = (col[0]);
      i++;
    }
      
    return _originalLabels;
  } // end Method createOrigionalLabels() =====
  

  // ==========================================
  /**
   * createLabelPairHash creates a hash from a string
   * of pairs, where each element of the pair is delimited
   * with a colon, and each of the pairs delimited with a pipe.
   * For example, "Concept:Copper|Symptom:Gold"
   *
   * @param labelPairs  
   * @return HashMap<String,String>
   */
  // ==========================================
  private final HashMap<String,String> createLabelPairHash(String labelPairs) {
    HashMap<String,String> _labelPairsHash = null;
    if ( labelPairs != null ) {
      _labelPairsHash = new HashMap<String,String>( );
      
      String[] labelPairz = U.split(labelPairs);
      for ( String row : labelPairz ) {
        String col[] = U.split(row, ":");
        try {
        _labelPairsHash.put(col[0], col[1]);
        } catch (Exception e) {
          GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createLabelPairHash", "malformed pair " + row );
        }
      }
    }
    return _labelPairsHash;
  } // end Method createLabelPairHash() =======
  



  // ---------------------------------------------
  // Class Variables
  // ---------------------------------------------
  private String[]                    origLabels = null;
  private HashMap<String,String>  labelPairsHash = null;
  private boolean                         remove = false;
  public static final String annotatorName = RelabelAnnotator.class.getSimpleName();
  
} // end Class RelabelAnnotator --------------------------
