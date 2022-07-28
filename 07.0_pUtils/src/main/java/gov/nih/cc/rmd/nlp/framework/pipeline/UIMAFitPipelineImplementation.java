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
