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
/*
 *
 */
/**
 * UIMAFITUtils implements frameworkPipeline Utils, encapsulates the
 * underlying pipeline implementation
 *
 * @author  Guy Divita
 * @created Jul 17, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.pipeline;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Properties;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.Resource;
import org.apache.uima.resource.SharedResourceObject;
import org.w3c.dom.ls.DocumentLS;




import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * The Class UIMAFitPipelineImplementation.
 */
@SuppressWarnings("unused")
public class UIMAFitPipelineImplementation {

  /** The class path to type descriptors. */
  private String classPathToTypeDescriptors = "gov/va/vinci/Model.xml;gov/va/chir/Model.xml";

  // =======================================================
  /**
   * Constructor UIMAFitPipelineImplementation .
   */
  // =======================================================
  public UIMAFitPipelineImplementation() {
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "Constructor", "Using UIMA-FIT ");
  }

  // =======================================================
  /**
   * createAggregateEngine returns an aggregate engine for the pipeline passed
   * in.
   *
   * @param pPipeline the pipeline
   * @return AnalysisEngine
   * @throws Exception the exception
   */
  // =======================================================
  public AnalysisEngine createAggregateEngine(FrameworkPipeline pPipeline) throws Exception {

    AnalysisEngine ae = null;
    String typeDescriptorPath = pPipeline.getTypeDescriptorClasspath();
    GLog.println(GLog.STD___LEVEL, this.getClass(), "createAggregateEngine",
        "The type descriptor is " + typeDescriptorPath);
    setClassPathToTypeDescriptors(pPipeline.getTypeDescriptorClasspath());

    try {
      Properties props = System.getProperties();
      props.setProperty("org.apache.uima.fit.type.import_pattern",
          "classpath*:" + classPathToTypeDescriptors);

      AggregateBuilder builder = new AggregateBuilder();

      List<PipelineComponent> components = pPipeline.getPipelineComponents();

      for (PipelineComponent component : components) {
        Class<JCasAnnotator_ImplBase> componentClass = component.getAnnotatorClass();
        Object[] parameters = component.getParametersForFit();
        if (componentClass != null) {
          
          AnalysisEngineDescription annotatorDescription = null;
          String aeConfigFile = component.getAEConfigName();  // this will be without the .xml
          if ( aeConfigFile != null ) {
           //aeConfigFile = aeConfigFile.replace("/", ".");
          //  aeConfigFile = aeConfigFile.substring(19);
            URI aeConfigURI = new File( aeConfigFile).toURI();
            annotatorDescription = AnalysisEngineFactory.createEngineDescription( aeConfigURI.toString() );
          } else {
            UimaResource externalResources[] = null;
            try {
            annotatorDescription = AnalysisEngineFactory.createEngineDescription(componentClass, parameters);
          
            } catch (Exception e) {
              e.printStackTrace();
              System.err.println("");
            } // this is an exception I don't want to see
            
             externalResources = component.getUimaResources();
          
            if ( externalResources != null && externalResources.length > 0 ) 
              for ( UimaResource externalResource : externalResources ) {
                ExternalResourceFactory.createDependencyAndBind(annotatorDescription, 
                                                                externalResource.getName(), 
                                                               (Class<SharedResourceObject>)externalResource.getResourceImplClass(), 
                                                               (String) externalResource.getUrl() );
            }
          }
          builder.add(annotatorDescription);
        } 
        
      } // end loop through the components

     
      ae = AnalysisEngineFactory.createEngine(builder.createAggregateDescription());
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with creating a uima-fit analysis engine " + e.toString();
      System.err.println(msg);
      throw new Exception(msg);
    }
    return ae;
  } // End Method createAggregateEngine() ======================

  // =======================================================
  /**
   * getclassPathToTypeDescriptors .
   *
   * @return the classPathToTypeDescriptors
   */
  // =======================================================
  public final String getClassPathToTypeDescriptors() {
    return classPathToTypeDescriptors;
  }

  // =======================================================
  /**
   * setclassPathToTypeDescriptors .
   *
   * @param pClassPathToTypeDescriptors the classPathToTypeDescriptors to set if
   *          you pass in a dot path'd classpath, this routine will convert the
   *          dot path to a relative file path and add the extension .xml on to
   *          it. That's what UIMAFit needs
 * @throws Exception 
   */
  // =======================================================
  public final void setClassPathToTypeDescriptors(String pClassPathToTypeDescriptors) throws Exception {

    String classPathToTypeDescriptors = pClassPathToTypeDescriptors;
    if (pClassPathToTypeDescriptors.contains(".")) {
      classPathToTypeDescriptors = convertDotPathToFilePath(pClassPathToTypeDescriptors);
    }
    
    // -----------------------------------------
    // verify that this path is in the classpath
    String buff = null;
    try {
        buff = U.readClassPathResource(classPathToTypeDescriptors);
    } catch (Exception e) {
    	e.printStackTrace();
    	System.err.println("Could not find the file " + classPathToTypeDescriptors + e.toString() );
    	throw e;
    }
    if ( buff == null || buff.isEmpty() ) {
    	System.err.println("Could not find the file " + classPathToTypeDescriptors  );
    	throw new Exception("Could not find the file " + classPathToTypeDescriptors );
    }
    	
    	
    	
    this.classPathToTypeDescriptors = classPathToTypeDescriptors;
  }

  /**
   * Convert dot path to file path.
   *
   * @param pClasspathToTypeDescriptors the classpath to type descriptors
   * @return the string
   */
  private static String convertDotPathToFilePath(String pClasspathToTypeDescriptors) {
    String returnVal = pClasspathToTypeDescriptors;
    returnVal = returnVal.replace('.', '/');
    returnVal = returnVal + ".xml";
    return returnVal;
    
   
  } // End Method convertDotPathToFilePath() ======================
  
 
  // --------------------------
  // Global Variables
  // --------------------------
  private  org.w3c.dom.ls.DocumentLS dummy2 = null;  //<------------ this is needed to make sure uimafit binds to the correct
                                             //<------------- xercesImpl-2.4.0 version

} // end Class UIMAFITUtils ------------------------
