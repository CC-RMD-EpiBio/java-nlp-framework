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
 * Merge merges annotations from two corpus sources.
 * This annotator will create a new corpus, with
 * SystemA annotations and SystemB annotations.
 * 
 * It presumes that the names of the files are the same
 * and that it's looking for SystemA and SystemB annotations.
 *       
 * @author  Guy Divita 
 * @created March 22, 2012
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.FsIndexDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.TypePriorities_impl;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.TypeSystemUtil;

import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class MergeAnnotator extends JCasAnnotator_ImplBase {
 
  
  // -----------------------------------------
  /**
   * process 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    // ------------------------------------
    // Gather System B Labels
    // ------------------------------------
    String fileName =  VUIMAUtil.getDocumentId(pJCas);
    
    if ( fileName == null )
      return;
    
    //String fileName = f.getName();
    
    String systemBFileName = this.systemBPath + "/" + fileName; //
    
    if ( !systemBFileName.contains(".xmi" ))
      systemBFileName = systemBFileName + ".xmi";
    
    File aFile = new File( systemBFileName);
    
    if ( !aFile.exists() &&  systemBFileName.contains(".txt")) {
    // -----------------------------------------------------
    // Try again, without the .txt extension if it exists
   
      systemBFileName = systemBFileName.replace(".txt", "");
      aFile = new File( systemBFileName);
      
      if ( !aFile.exists()) {
        System.err.println("Could not find file "+ systemBFileName );
        
        return;
      }
    }
    
    
    FileInputStream inputStream;
    try {
      inputStream = new FileInputStream(systemBFileName);
    } catch (FileNotFoundException e1) {
      
      e1.printStackTrace();
      System.err.println("Issue opening up the file "+ systemBFileName + " " + e1.toString());
      return;
      //throw new RuntimeException( e1.toString());
    }
   
    CAS  aCas = null;
    JCas aJCas = null;
    TypeSystem currentTypeSystem = pJCas.getTypeSystem();
    TypeSystemDescription currentTypeSystemDescription = TypeSystemUtil.typeSystem2TypeSystemDescription(currentTypeSystem);
    FsIndexDescription[] indexes = null;
    try {
      
      aCas = CasCreationUtils.createCas( currentTypeSystemDescription, new TypePriorities_impl(), indexes);
      XmiCasDeserializer.deserialize(inputStream, aCas, false);
      aJCas = aCas.getJCas();
        
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException( "Something went wrong with reading the system b annotations " + e.toString());
      
    }
    //List<Annotation>  systemB_Annotations = UIMAUtil.getAnnotations(aJCas, gov.va.chir.model.SystemB.type);
    List<Annotation>  systemB_Annotations = UIMAUtil.getAnnotations(aJCas);
    if ( systemB_Annotations != null ) {
    for ( Annotation annotation : systemB_Annotations) {
      
    try {
      createAnnotation( pJCas, annotation);
    } catch ( Exception e) {
      e.getStackTrace();
      System.err.println("Something went wrong with annotation " + annotation.getCoveredText() + " " + e.toString());
    }
    }
    }
    
  } // end Method process() ----------------
  
 

  // -----------------------------------------
  /**
   * createAnnotation adds this annotation to the other cas
   * 
   * @param pJCas
   * @param pAnnotation
   * @param string
   * @throws Exception 
   */
  // -----------------------------------------
  private void createAnnotation(JCas pJCas, Annotation pAnnotation ) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
    
    Class<?> uimaLabelClass = null;
    uimaLabelClass = pAnnotation.getClass();
    
    // ----------------------------------------------
    // create a UIMA annotation from this label
    // ----------------------------------------------


      Constructor<?> c = uimaLabelClass.getConstructor(new Class[] { JCas.class });
      Object newAnnotation = c.newInstance(pJCas);
      // --------------------------------------------------
      // Add span and slot attributes to the uimaAnnotation
      // --------------------------------------------------
   
     // VUIMAUtil.setProvenance(pJCas, (VAnnotation) newAnnotation, this.getClass().getName());
          
      Type type = pAnnotation.getType();
      List<Feature> features = type.getFeatures();
      for ( Feature feature : features ) {
       
        Type featureType = feature.getRange();
        
        if ( featureType.isPrimitive()) {
          try { ((Annotation) newAnnotation ).setBooleanValue( feature,  pAnnotation.getBooleanValue(feature));  } catch (Exception e) {}
          try { ((Annotation) newAnnotation ).setIntValue(     feature,  pAnnotation.getIntValue(feature));  } catch (Exception e) {}
          try { ((Annotation) newAnnotation ).setStringValue(  feature,  pAnnotation.getStringValue(feature));  } catch (Exception e) {}
               
        } else {
          
       
          try {
          //  ((VAnnotation) newAnnotation).setFeatureValue( feature, featureValue);
          } catch ( Exception e) {
            e.printStackTrace();
            System.err.println("Something went wrong here " + e.toString());
          }
        }
      }
      ((Annotation)newAnnotation).addToIndexes( pJCas); 
     
  
  } // end createAnnotation() ----------------

  //----------------------------------
  /**
   * initialize loads in the resources needed for phrase chunking. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
  
   

  this.systemBPath = (String)  aContext.getConfigParameterValue("systemBDir"); 

  
  
  } // end Method initialize() -------



  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  private String systemBPath = null;
 
  
} // end Class MetaMapClient() ---------------
