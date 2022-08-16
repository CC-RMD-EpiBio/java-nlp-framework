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
