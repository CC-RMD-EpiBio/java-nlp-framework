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
// =================================================
/**
 * FrameworkTestUtilities include methods to compare annotations
 * 
 * for instance, an annotation that includes a particular string or regex
 * An annotation that includes a specific feature or attribute.
 *
 * @author     Guy Divita
 * @created    Mar 4, 2019
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.utils.uima;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;

/**
 * @author divitag2
 *
 */
public class FrameworkTestUtilities {

  // =================================================
  /**
   * findAnnotationAt 
   * 
   * @param pJCas
   * @param pAnnotationType
   * @param pBeginOffset
   * @param pEndOffset
   * @param pSpanString  // the match will be exact, trimmed
   * @param pAttribute1
   * @param pValue1
   * @return boolean
  */
  // =================================================
  public static boolean findAnnotationAt(JCas   pJCas, 
                                         String pAnnotationType, 
                                            int pBeginOffset,
                                            int pEndOffset,
                                         String pSpanString, 
                                         String pAttribute1, 
                                         String pValue1
                                      ) {
 
    boolean returnVal = false;
      
    List<Annotation> allAnnotations = null;
    if ( pJCas != null ) {
      List<Annotation> candidates = findAnnotationsAt( pJCas, pAnnotationType, pBeginOffset, pEndOffset, pSpanString);
  
      if ( candidates != null && !candidates.isEmpty() ) {
        for ( Annotation candidate : candidates ) {
           if ( hasAttribute( candidate, pAttribute1, pValue1))  {
             returnVal = true;
             break;
           }
        }
          
      }
    
    }
      
    
    return returnVal;
  } // end Method findAnnotationsAt() -------------------
  
  // =================================================
  /**
   * hasAttribute
   * 
   * @param pAnnotation 
   * @param pFeatureName
   * @param pValue
   * @return boolean
  */
  // =================================================
 public static boolean hasAttribute(Annotation pAnnotation, String pFeatureName, String pValue) {
   boolean returnVal = false;
   
   if ( pValue == null ) return true;

   String featureValue = UIMAUtil.getFeatureValueByName(pAnnotation, pFeatureName);
   
   if ( featureValue != null && pValue.trim().toLowerCase().equals( featureValue.toLowerCase().trim() ))
     returnVal = true;
   
   
    return returnVal;
  } // end Method hasAttribute() ---------------------

  // =================================================
  /**
   * findAnnotationsAt 
   * 
   * @param pJCas
   * @param pAnnotationType
   * @param pBeginOffset
   * @param pEndOffset
   * @return boolean
  */
  // =================================================
  public static List<Annotation> findAnnotationsAt(JCas pJCas, 
                                                String pAnnotationType, 
                                                   int pBeginOffset,
                                                   int pEndOffset
                                                 ) {
                                         
 
    List<Annotation> returnVal = null;
    
    if ( pJCas != null )
      try {
      Type annotationType = UIMAUtil.getLabelType(pJCas, pAnnotationType);
      List<Annotation> someAnnotations = UIMAUtil.getAnnotationsBySpan(pJCas, annotationType, pBeginOffset, pEndOffset,true);
      
      if ( someAnnotations != null && !someAnnotations.isEmpty()) 
         returnVal = someAnnotations;
      } catch (Exception e) {
        
      }
      
    
    
    return returnVal;
  } // end Method findAnnotationsAt() -------------------

// =================================================
/**
 * findAnnotationAt 
 * 
 * @param pJCas
 * @param pAnnotationType
 * @param pBeginOffset
 * @param pEndOffset
 * @param pSpanString  // the match will be exact, trimmed
 * @return list<Annotation>
*/
// =================================================
public static List<Annotation> findAnnotationsAt(JCas pJCas, 
                                              String pAnnotationType, 
                                                 int pBeginOffset,
                                                 int pEndOffset,
                                                 String pSpanString
                                               ) {
                                       

      ArrayList<Annotation> returnVal = null;
    
      List<Annotation> someAnnotations = findAnnotationsAt( pJCas, pAnnotationType, pBeginOffset, pEndOffset);
    
      if ( someAnnotations != null && !someAnnotations.isEmpty()) {
        
        returnVal = new ArrayList<Annotation>( someAnnotations.size());
      
        for ( Annotation anAnnotation : someAnnotations) {
          String candidate = anAnnotation.getCoveredText();
          if ( candidate != null && candidate.trim().contentEquals(pSpanString )) 
            returnVal.add( anAnnotation);
        }
      }
    
  
  return returnVal;
} // end Method findAnnotationsAt() -------------------

//=================================================
/**
* findAnnotationAt 
* 
* @param pJCas
* @param pAnnotationType
* @param pBeginOffset
* @param pEndOffset
* @param pSpanString  // the match will be exact, trimmed
* @return boolean
*/
//=================================================
public static boolean findAnnotationAt(JCas pJCas, 
                                           String pAnnotationType, 
                                              int pBeginOffset,
                                              int pEndOffset,
                                              String pSpanString
                                            ) {
                                    
  boolean returnVal = false;
 
   List<Annotation> someAnnotations = findAnnotationsAt( pJCas, pAnnotationType, pBeginOffset, pEndOffset, pSpanString);
   
   if ( someAnnotations != null && !someAnnotations.isEmpty())
     returnVal = true;
 

return returnVal;
} // end Method findAnnotationsAt() -------------------

// =================================================
/**
 * findAnnotation returns true if the annotation is found 
 * 
 * @param pJCas
 * @param pAnnotationType
 * @param pSpanString

 * @return boolean
*/
// =================================================
public static boolean findAnnotation(JCas pJCas, 
                                     String pAnnotationType, 
                                     String pSpanString
                                    ) {
 
  boolean returnVal = false;
  
  try {
    List<Annotation> someAnnotations = UIMAUtil.getAnnotations(pJCas, pAnnotationType);
    
    if ( someAnnotations != null && !someAnnotations.isEmpty()) {
      for ( Annotation anAnnotation : someAnnotations ) {
        String coveredText = anAnnotation.getCoveredText();
        if ( pSpanString != null && coveredText != null && 
            pSpanString.trim().toLowerCase().equals(coveredText.trim().toLowerCase())) {
          returnVal = true;
          break;
        }
      }
    }
    
  
  } catch (Exception e) {
    e.printStackTrace();
    GLog.println( GLog.ERROR_LEVEL, FrameworkTestUtilities.class, "findAnnotation", "Issue with findAnnotation " + e.toString());
  }
  
  
  
  return returnVal;
} // end Method findAnnotation() --------------------



} // end FrameworkTestUtilities() -------------
