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
 * GateUtils includes utilities useful to convert
 * to and from gate/uima
 *
 *
 * @author  Guy Divita 
 * @created Oct 17, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.gate;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gov.va.chir.model.VAnnotation;
import gov.va.vinci.model.Concept;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.FeatureValuePair;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;


public class GateUtils {

  
//-----------------------------------------
  /**
   * createGATEDocument returns a GATE Document from the
   * uima cas.
   * 
   * @param pJCas
   * @return gate.Document
   */
  // -----------------------------------------
  public static gate.Document createGATEDocument(JCas pJCas ) {
    gate.Document doc = null;
    
    try {
      String documentContent = pJCas.getDocumentText();
      doc = Factory.newDocument(documentContent );
      addGateAnnotations( pJCas, doc, DONT_USE_NAMESPACE);
      
    } catch (Exception e) {
      
    }
    return doc;
  } // end Method createGATEDocument()
  
  // -----------------------------------------
  /**
   * createGATEDocument returns a GATE Document from the
   * uima cas.
   * 
   * @param pJCas
   * @param pLabelOptions  USE_NAMESPACE if you want to have gov.va.chir.model. kind of namespace be part of the gate label)
   *                       HITEX_CHIR_MAPPINGS  maps the CHIR Labels to hitex mappings
   *                       DONT_USE_NAMESPACE uses only the label without a name space
   *                       The default is to not use the name space.
   * @return gate.Document
   */
  // -----------------------------------------
  public static gate.Document createGATEDocument(JCas pJCas, int pLabelOptions) {
    gate.Document doc = null;
    try {
      String documentContent = pJCas.getDocumentText();
      doc = Factory.newDocument(documentContent );
      addGateAnnotations( pJCas, doc, pLabelOptions);
      
    } catch (Exception e) {
      
    }
    return doc;
  } // end Method createGATEDocument()
  
  // -----------------------------------------
  /**
   * createGATEDocument returns a GATE Document from the
   * uima cas.
   * 
   * @param pJCas
   * @param pLabelOptions  USE_NAMESPACE if you want to have gov.va.chir.model. kind of namespace be part of the gate label)
   *                       HITEX_CHIR_MAPPINGS  maps the CHIR Labels to hitex mappings
   *                       DONT_USE_NAMESPACE uses only the label without a name space
   *                       The default is to not use the name space.
   * @return gate.Document
   */
  // -----------------------------------------
  public static gate.Document createGATEDocument(String pDocumentContent, List<Annotation>pUIMAAnnotations, int pLabelOptions) {
    gate.Document doc = null;
    try {
      String documentContent = pDocumentContent;
      doc = Factory.newDocument(documentContent );
      addGateAnnotations( pUIMAAnnotations, doc, pLabelOptions);
      
    } catch (Exception e) {
      
    }
    return doc;
  } // end Method createGATEDocument()
  
  
  // -------------------------------------------------------
  /**
   * createGateAnnotations creates gate annotations from
   * a UIMA CAS set of annotations, and returns these annotations
   * within a gate.Document container;
   *
   * @param pJCas
   * @param pLabelOptions  USE_NAMESPACE if you want to have gov.va.chir.model. kind of namespace be part of the gate label)
   *                       HITEX_CHIR_MAPPINGS  maps the CHIR Labels to hitex mappings
   *                       DONT_USE_NAMESPACE uses only the label without a name space
   *                       The default is to not use the name space.
   * @return gate.Document
   */
  // -------------------------------------------------------
  public static gate.Document createGateAnnotations(JCas pJCas, int pLabelOptions )  throws Exception{
    
  
    String documentContent = pJCas.getDocumentText();
    gate.Document doc = Factory.newDocument(documentContent );
    addGateAnnotations( pJCas, doc, pLabelOptions );
    
    return doc ;
    
  } // end Method addGateAnnotations() ---------------------
  
//-------------------------------------------------------
  /**
   * createGateAnnotations creates gate annotations from
   * a UIMA CAS set of annotations, and returns these annotations
   * within a gate.Document container;
   *
   * @param pJCas
   * @return gate.Document
   */
  // -------------------------------------------------------
  public static gate.Document createGateAnnotations(JCas pJCas  )  throws Exception{
    
   gate.Document doc = createGateAnnotations( pJCas, DONT_USE_NAMESPACE);
    
    return doc ;
    
  } // end Method addGateAnnotations() ---------------------
  
 
  // -----------------------------------------
  /**
   * addGateAnnotations 
   * 
   * @param pJCas
   * @param doc
   * @param pLabelOptions  USE_NAMESPACE if you want to have gov.va.chir.model. kind of namespace be part of the gate label)
   *                       HITEX_CHIR_MAPPINGS  maps the CHIR Labels to hitex mappings
   *                       DONT_USE_NAMESPACE uses only the label without a name space
   *                       The default is to not use the name space.
   * 
   * @throws Exception 
   */
  // -----------------------------------------
  public static void addGateAnnotations(JCas pJCas, Document doc, int pLabelOptions ) throws Exception {
    
   AnnotationIndex<Annotation> annotationIndex = pJCas.getAnnotationIndex();
   List<Annotation> annotations = UIMAUtil.annotationIndex2List(annotationIndex) ;
    
   addGateAnnotations( annotations, doc, pLabelOptions);
  } // end Method addGateAnnotations() ---------------------

  // -------------------------------------------------------
  /**
   * addGateAnnotations
   *
   * @param pUIMAAnnotations
   * @param doc
   * @param pLabelOptions  USE_NAMESPACE if you want to have gov.va.chir.model. kind of namespace be part of the gate label)
   *                       HITEX_CHIR_MAPPINGS  maps the CHIR Labels to hitex mappings
   *                       DONT_USE_NAMESPACE uses only the label without a name space
   *                       The default is to not use the name space.
   */
  // -------------------------------------------------------
  public static void addGateAnnotations(List<Annotation> pUIMAAnnotations, gate.Document doc, int pLabelOptions)  throws Exception{
    
   
    AnnotationSet aset = doc.getAnnotations();
  
    // ---------------------------------------
    // extract  all UIMA annotations
    // ---------------------------------------
    
    for ( Annotation  annotation: pUIMAAnnotations ) {
          
        long begin = annotation.getBegin();
        long end = annotation.getEnd();
        String originalAnnotationType = annotation.getType().getName();
        String annotationType = originalAnnotationType;
        switch ( pLabelOptions ) {
        // case USENAMESPACE: break;
        // case HITEX_CHIR_MAPPING: annotationType = HITExCHIR.chir2HitexLabel( originalAnnotationType); break;
        case DONT_USE_NAMESPACE:
        default:
          annotationType = annotationType.substring( annotationType.lastIndexOf('.') + 1);
    } // end switch
        
        List<FeatureValuePair> featureValuePairs = UIMAUtil.getFeatureValuePairs( annotation);
        
        gate.FeatureMap featureMap = gate.Factory.newFeatureMap();
        
        if ( featureValuePairs != null )
          for ( FeatureValuePair fvp : featureValuePairs ) {
          
            // ----------------------------------------------------
            // Need to map feature names from one side to the other
            String gateFeatureName = fvp.getFeatureName();
           
            if ( pLabelOptions == HITEX_CHIR_MAPPING ) 
              gateFeatureName = HITExCHIR.chir2HitexFeatureName( originalAnnotationType, fvp.getFeatureName());
       
            // ------------------------------
            // If there is no gate equivalant, no sense making it a feature
            if ( gateFeatureName != null )
              featureMap.put(gateFeatureName, fvp.getFeatureValue());
          }
        aset.add(begin, end, annotationType, featureMap);
    } // end loop through the annotations

    
  } // end Method addGateAnnotations() ----------      

  // -----------------------------------------
  /**
   * convertGateAnnotationsToCAS adds gate annotations to the JCAS
   * 
   * @param gatDoc
   * @param pJCas
   * @param pUimaToGateTypeHash
   */
  // -----------------------------------------
  public static void convertGateAnnotationsToCAS(gate.Document gateDoc,  JCas pJCas,  HashMap<String, String> pUimaToGateTypeHash) {
    
    // Iterate through the gate annotations
    AnnotationSet gateAnnotationSet = gateDoc.getAnnotations();
    
    Set<String> annotationSetNames = gateDoc.getAnnotationSetNames();
    
    if ( annotationSetNames == null || annotationSetNames.isEmpty()) {
      gateAnnotationSet = gateDoc.getAnnotations();
      convertGateNamedAnnotatonSetAnnotatonsToCAS( pJCas, "none", gateAnnotationSet , pUimaToGateTypeHash );
    } else 
   
    
      for ( String annotationSetName : annotationSetNames ) {
        gateAnnotationSet = gateDoc.getAnnotations(annotationSetName);
    
        if ( gateAnnotationSet == null || gateAnnotationSet.isEmpty() ) {
          gateAnnotationSet = gateDoc.getAnnotations();
          convertGateNamedAnnotatonSetAnnotatonsToCAS( pJCas, "none", gateAnnotationSet , pUimaToGateTypeHash );
        } else 
          convertGateNamedAnnotatonSetAnnotatonsToCAS( pJCas, annotationSetName, gateAnnotationSet , pUimaToGateTypeHash );
      } 
  
    
  
    
  } // end Method convertGateAnnotationsToCAS() ---
  
  // =================================================
  /**
   * convertGateNamedAnnotatonSetAnnotatonsToCAS [TBD] summary
   * 
   * @param pJCas
   * @param annotationSetName
   * @param gateAnnotationSet
   * @param pUimaToGateTypeHash 
  */
  // =================================================
  private static final void convertGateNamedAnnotatonSetAnnotatonsToCAS(JCas pJCas, String annotationSetName, AnnotationSet gateAnnotationSet, HashMap<String, String> pUimaToGateTypeHash) {
    
    if ( gateAnnotationSet != null && !gateAnnotationSet.isEmpty()) {
    
       for ( gate.Annotation gateAnnotation : gateAnnotationSet ) {
         long begin = gateAnnotation.getStartNode().getOffset();
         long   end = gateAnnotation.getEndNode().getOffset(); 
         String annotationName = gateAnnotation.getType();
         int labelOptions = GateUtils.DONT_USE_NAMESPACE;
         Annotation uimaAnnotation = createUIMAAnnotation(pJCas, annotationName, begin, end , labelOptions, pUimaToGateTypeHash );
      
      
         if ( uimaAnnotation != null ) {
         FeatureMap gateFeatureMap = gateAnnotation.getFeatures();
     
         if ( (gateFeatureMap != null ) && (gateFeatureMap.size() > 0 )) {
           Set<Object> featureNames = gateFeatureMap.keySet();
           if (( featureNames != null ) && (featureNames.size() > 0) ) {
        
             for ( Object featureName : featureNames ) {
               Object featureValue = gateFeatureMap.get( featureName);
               addFeature( pJCas, uimaAnnotation, (String) featureName, (String) featureValue);
           
             }
           } // end if there are any keys
         } // end if there are any features 
         } // end if there is a mapping
       } // end loop through gate annotations
       
      
    } // end if there are any annotations 
    
  } // end Method convertGateNamedAnnotatonSetAnnotatonsToCAS() ------

  // -----------------------------------------
  /**
   * convertGateAnnotationsToCAS adds gate annotations to the JCAS
   * 
   * @param gatDoc
   * @param pJCas
   * @deprecated
   */
  // -----------------------------------------
  public static void convertGateAnnotationsToCASObs(gate.Document gateDoc,  JCas pJCas ) {
    
    // Iterate through the gate annotations
     Map<String, AnnotationSet> gateAnnotationMapz = gateDoc.getNamedAnnotationSets();
    
     
     Set<String> keys = gateAnnotationMapz.keySet();
     
     for ( String key: keys) {
       AnnotationSet gateAnnotationSet = gateAnnotationMapz.get(key);
     
    
       for ( gate.Annotation gateAnnotation : gateAnnotationSet ) {
         long begin = gateAnnotation.getStartNode().getOffset();
         long   end = gateAnnotation.getEndNode().getOffset(); 
         String annotationName = gateAnnotation.getType();
         int labelOptions = GateUtils.DONT_USE_NAMESPACE;
         Annotation uimaAnnotation = createUIMAAnnotation(pJCas, annotationName, begin, end , labelOptions, (HashMap<String, String> )null );
      
      
         if ( uimaAnnotation != null ) {
         FeatureMap gateFeatureMap = gateAnnotation.getFeatures();
     
         if ( (gateFeatureMap != null ) && (gateFeatureMap.size() > 0 )) {
           Set<Object> featureNames = gateFeatureMap.keySet();
           if (( featureNames != null ) && (featureNames.size() > 0) ) {
        
             for ( Object featureName : featureNames ) {
               Object featureValue = gateFeatureMap.get( featureName);
               addFeature( pJCas, uimaAnnotation, (String) featureName, (String) featureValue);
           
             }
           } // end if there are any keys
         } // end if there are any features 
         } // end if there is a mapping
       } // end loop through gate annotations
       
      
    } // end loop through gate annotation sets;
    
  
    
  } // end Method convertGateAnnotationsToCAS() ---
  
  
  // -----------------------------------------
  /**
   * convertGateAnnotationsToCAS adds back to the CAS annotations from
   * the gate document, merging and adding features for overlapping annotations.
   * 
   * For example, the JCas includes gov.va.chir.model.Phrase, and these are 
   * passed into and become the gate document as "Phrase" annotations.
   * 
   * The gate process adds a Negation_Status to each Phrase.  These phrases
   * are handed back.
   * 
   * This method will recognize gov.va.chir.model.Phrase(s) and gate Phrase(s) 
   * with the same span are the same - and add the gate Phrase features to
   * the gov.va.chir.model.Phrase features. 
   * 
   * @param gateOutputDoc
   * @param pJCas
   * @param newFeatures  (List of <String> of the form label:featureName
   * @param labelOptions 
   */
  // -----------------------------------------
  public static void convertGateAnnotationsToCAS(Document    gateOutputDoc, 
                                                JCas         pJCas, 
                                                List<String> newFeatures, 
                                                int labelOptions,
                                                HashMap<String, String> pUimaToGateTypeHash
                                                ) {
    
    if ( newFeatures == null || newFeatures.size() == 0) {
      convertGateAnnotationsToCAS(gateOutputDoc, pJCas, pUimaToGateTypeHash);
    } else {

      // Iterate through the gate annotations
      AnnotationSet gateAnnotationz = gateOutputDoc.getAnnotations();

      for (gate.Annotation gateAnnotation : gateAnnotationz) {
        long begin = gateAnnotation.getStartNode().getOffset();
        long end = gateAnnotation.getEndNode().getOffset();
        String annotationName = gateAnnotation.getType();

        // ---------------------------------
        // Find this annotation in the pJCas
        Annotation uimaAnnotation = findUIMAAnnotation(pJCas, annotationName, begin, end, labelOptions,  pUimaToGateTypeHash );

        if (uimaAnnotation != null) {

          // ------------------------------------------
          // Add/reset the features to this annotation
          FeatureMap gateFeatureMap = gateAnnotation.getFeatures();

          if ((gateFeatureMap != null) && (gateFeatureMap.size() > 0)) {
            Set<Object> featureNames = gateFeatureMap.keySet();
            if ((featureNames != null) && (featureNames.size() > 0)) {

              for (Object featureName : featureNames) {
                if ( !featureName.equals("null")) {
                  Object featureValue = gateFeatureMap.get(featureName);
                 
                  if (onNewFeatureList( annotationName, featureName.toString(), newFeatures)) {
                  
                    if ( featureName != null && featureValue != null && annotationName != null) {
                                      
                      addFeature(pJCas, uimaAnnotation, (String) featureName,(String) featureValue);
                    }
                  } // en dif the featureName is not "null"
                } // end if on the new FeatureList
              } // end loop through feature names
            } // end if there are any keys
          } // end if there are any features

        } // end loop through gate annotations;
      } // end if there were any gate annotations
    } // end if merging or not merging annotations
  }   // end Method findUIMAAnnotation --------------- 
  
  
  // =================================================
  /**
   * convertGateAnnotationsToCAS 
   * 
   * @param gateDocument
   * @param pJCas
   * @param uimaToGateTypeHash
  */
  // =================================================
   public static void convertGateAnnotationsToCASAux(Document gateDocument, JCas pJCas, HashMap<String, String> uimaToGateTypeHash) {
    
     convertGateAnnotationsToCAS( gateDocument, pJCas, uimaToGateTypeHash  );
    
   } // end Method convertGateAnnotationsToCAS()-----

  // -----------------------------------------
  /**
   * onNewFeatureList returns true if for a given annotationName,
   * this annotationName:featureName is on the list of new features. 
   * 
   * @param pAnnotationName
   * @param pFeatureName
   * @param pFeatureList
   * @return boolean
   */
  // -----------------------------------------
  private static boolean onNewFeatureList(String pAnnotationName, String pFeatureName,
      List<String> pFeatureList ) {
    
    boolean returnValue = false;
    
    for ( String  annotationAndFeature: pFeatureList) {
      if (annotationAndFeature.equalsIgnoreCase(pAnnotationName + ":" + pFeatureName)) {
        returnValue = true;
        break;
      }
    }
    return returnValue;
  } // end Method onNewFeatureList() ---------

 

  // -----------------------------------------
  /**
   * createUIMAAnnotation returns a new uima annotation for this label
   * 
   * @param pJCas
   * @param pGateLabel
   * @param pBegin
   * @param pEnd
   * @param labelOptions 
   * @param pUimaToGateTypeHash 
   * @return Annotation
   */
  // -----------------------------------------
  public static Annotation createUIMAAnnotation(JCas pJCas, 
                                                String pGateLabel, 
                                                long pBegin, 
                                                long pEnd, 
                                                int labelOptions,
                                                HashMap<String, String> pGateToUIMATypeHash) {
    
    Object uimaAnnotation = null;
   
    
    //----------------------------------------------
    // Map this label to a uima class
    // ----------------------------------------------
    Class<?> uimaLabelClass = mapLabelToUIMAClassFromTable( pGateLabel, pGateToUIMATypeHash);
   
   
      
    // ----------------------------------------------
    // create a UIMA annotation from this label
    // ----------------------------------------------
    try {
       if ( uimaLabelClass == null ) {
         GLog.println(GLog.INFO_LEVEL, GateUtils.class, "createUIMAAnnotation", "Issue here - couldnt find a suitable match for " + pGateLabel );
         return null;
       }
      Constructor<?> c = uimaLabelClass.getConstructor(new Class[]{ JCas.class  });
      uimaAnnotation = c.newInstance(pJCas);
     
      // --------------------------------------------------
      // Add span and slot attributes to the uimaAnnotation
      // --------------------------------------------------

      // VUIMAUtil.setProvenance(pJCas, (VAnnotation) uimaAnnotation , Class.forName("GateUtils")  .getName()); 
    
      ((Annotation) uimaAnnotation).setBegin((int) pBegin);
      ((Annotation) uimaAnnotation).setEnd((int) pEnd);
     // ((Annotation) uimaAnnotation).setId(hitexLabel + "_" + annotationId++ );
      ((Annotation) uimaAnnotation).addToIndexes(pJCas);
    
   
    } catch (Exception e ) {
      e.printStackTrace();
      GLog.println(GLog.INFO_LEVEL, "GateUtils:createUIMAAnnotation:Not able to find a compatable annotation for this gate annotation " + e.toString() );
    
    }
    
    return (Annotation) uimaAnnotation;
    
  } // end Method convertGateAnnotationsToCAS() -----
  
  // =================================================
  /**
   * mapLabelToUIMAClassFromTable finds the pLabel in the uimaToGateMappings.csv file
   * grabs the uima version, and creates an instance from
   * it.
   * 
   * @param pUimaToGateTypeHash 
   * @param pGateLabel
   * @return class<?>
  */
  // =================================================
  private final static Class<?> mapLabelToUIMAClassFromTable(String pGateLabel, HashMap<String, String> pGateToUIMATypeHash) {
   
    Class<?> returnValue = null;
    try {
    
      
      
     String uimaLabel = pGateToUIMATypeHash.get( pGateLabel );
       
     if ( uimaLabel != null && uimaLabel.length() > 0 )
       returnValue = Class.forName(uimaLabel);
       
     else {
       //----------------------------------------------
       // if the class wasn't found, try again as if the gateLabel and the uimaLabel are the same
       // ----------------------------------------------
       if ( returnValue == null ) { 
         returnValue = UIMAUtil.mapLabelToUIMAClass( pGateLabel);
       
         if ( returnValue == null ) {
           //----------------------------------------------
           // if the class wasn't found, try again, this time permuting thru known namespaces
           // ----------------------------------------------
           String gateLabelNoNamespace = pGateLabel.substring( pGateLabel.lastIndexOf('.') + 1);
           returnValue = UIMAUtil.mapLabelToUIMAClass( gateLabelNoNamespace);
         }
       
       } 
     }
       
     
     
     
    } catch (Exception e) {
     
    }
    
    return returnValue;
    
    
  } // end Method mapLabelToUIMAClassFromTable() ------------

  // -----------------------------------------
  /**
   * findUIMAAnnotation will return an existing annotation that matches
   * the input hitex annotation
   * 
   * @param pJCas
   * @param hitexLabel
   * @param begin
   * @param end
   * @param labelOptions
   * @return
   */
  // -----------------------------------------
  private static Annotation findUIMAAnnotation(JCas   pJCas,
                                                String hitexLabel, 
                                                long   pStart, 
                                                long   pEnd, 
                                                int    labelOptions,
                                                HashMap<String, String> pGate2UIMATypeHash ) {
    
    Annotation foundAnnotation = null;
    
    Object uimaAnnotation = null;
    
    String chirLabel = null;
    switch ( labelOptions ) {
    case USENAMESPACE:       chirLabel = hitexLabel;                             break;
   // case HITEX_CHIR_MAPPING: chirLabel = HITExCHIR.hitex2ChirLabel( hitexLabel); break;
    case DONT_USE_NAMESPACE:
    default:
      chirLabel = hitexLabel.substring( hitexLabel.lastIndexOf('.') + 1);
    } // end switch
    //----------------------------------------------
    // Map this label to a uima class
    // ----------------------------------------------
    Class<?> uimaLabelClass = mapLabelToUIMAClassFromTable( chirLabel, pGate2UIMATypeHash );
  
    // ----------------------------------------------
    // create a UIMA annotation from this label
    // ----------------------------------------------
    try {
            
      Constructor<?> c = uimaLabelClass.getConstructor(new Class[]{ JCas.class  });
      uimaAnnotation = c.newInstance(pJCas);
      Type uimaType = ((VAnnotation) uimaAnnotation).getType();
      
      // -------------------------
      // find the annotation from the list b that has the same offsets
      List<Annotation> matchedAnnotations = UIMAUtil.getAnnotationsBySpan(pJCas, uimaType, (int) pStart, (int) pEnd);
      
      if ( matchedAnnotations != null && matchedAnnotations.size() > 0) {
        foundAnnotation = (Annotation) matchedAnnotations.get(0);
      
       
      } // end found matching annotations
      
    } catch ( Exception e) {
      // ignore if nothing was found
    }
    
    return foundAnnotation;
    
  
  }

  // end Method createUIMAAnnotation() ---------------
  
  //-----------------------------------------
  /**
   * addFeature
   * 
   * @param pLabel
   * @param pJCas
   */
  // -----------------------------------------
  public static void addFeature(JCas pJCas, Annotation uimaAnnotation, String pFeatureName, String pFeatureValue )  {
    
    // --------------------------------------------------------------
    // Is there a generic way to find out if this feature is a class?
    // --------------------------------------------------------------
    
    // --------------------------------------------------------------
    // There are some gate features that cannot be uima features because they are
    // reserved words like "type"
    // Use the gate-uima mapping to translate from a gate feature
    // to a uima candiate feature
    // --------------------------------------------------------------
    
    
    
    Method uimaSetFeatureMethod = UIMAUtil.mapFeatureToUIMAFeature(  uimaAnnotation.getClass(), pFeatureName);
    
    String className = uimaAnnotation.getClass().getName();
    if ( uimaSetFeatureMethod != null ) {
      try {
        if ( pFeatureValue != null)
          uimaSetFeatureMethod.invoke(uimaAnnotation, pFeatureValue );
      } catch (Exception e) {
        try {
          uimaSetFeatureMethod.invoke(uimaAnnotation, Integer.valueOf(pFeatureValue) );
        } catch ( Exception e2) {
          try {
            uimaSetFeatureMethod.invoke(uimaAnnotation, Float.valueOf(pFeatureValue) );
          } catch ( Exception e3) {
          GLog.println(GLog.INFO_LEVEL, "GateUtils:addFeature:exception: couldn't find a method/feature for " + className + ":" +  pFeatureName);
        }
        }
      }
      ((Annotation) uimaAnnotation).addToIndexes(pJCas);
    } else {
     // GLog.println(GLog.INFO_LEVEL, "GateUtils:addFeature: couldn't find a method/feature for " + className + ":" +  pFeatureName);
      
    }
  } // end Method addFeature() -------------------------------
  
  
//=================================================
/**
* isGATESerialDataStoreDir returns true if the inputDir contains
* the file __GATE_SerialDataStore__ in it.
* 
* @param pInputDir
* @return boolean 
*/
//=================================================
public static final boolean isGATESerialDataStoreDir(String pInputDir) {

 boolean returnVal = false;
 
 if ( pInputDir != null ) {
   
   File inputCorpusDir = new File ( pInputDir);
   String[] dirsAndFiles;
  if ( inputCorpusDir.exists() && inputCorpusDir.canRead() ) {
     dirsAndFiles = inputCorpusDir.list();
     if ( dirsAndFiles != null && dirsAndFiles.length > 0 ) {
       for ( String dirAndFile: dirsAndFiles ) 
         if ( dirAndFile != null &&  dirAndFile.toLowerCase().contains("__gate_serialdatastore__")) {
             returnVal = true;
             break;
          }
     }
  }
 }
 
 return returnVal;

} // end Method isGATESerialStoreDir() -------------
  
  // ---------------------
  // Class Variables
  // ---------------------
  private static int annotationId  = 0;
  public static final int USENAMESPACE       = 1;
  public static final int HITEX_CHIR_MAPPING = 2;
  public static final int DONT_USE_NAMESPACE = 3;
    
  } // end Class GateUtils() ------------------------
