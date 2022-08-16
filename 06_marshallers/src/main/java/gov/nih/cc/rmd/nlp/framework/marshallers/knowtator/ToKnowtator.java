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
package gov.nih.cc.rmd.nlp.framework.marshallers.knowtator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.marshallers.knowtator.util.KnowtatorUtils;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;

// import com.sun.org.apache.xml.internal.serializer.utils.Utils;

//=================================================
/**
 * ToKnowtator is a CAS Consumer that transforms
 * the vinciNLPFramework encoded CAS into a knowtator
 * encoded set of objects.  
 *
 * @author  Guy Divita 
 * @created June 3, 2011
 *
 * *  
 * 

 */
// ================================================
 public class ToKnowtator extends JCasAnnotator_ImplBase implements gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer {
  
    
    
	private HashSet<String> mustRemoveTypes;
  // =======================================================
    /**
     * Constructor ToKnowtator 
     *
     * @param pEHostWorkSpace
     * @param pEHostProject
     * @param pOutputTypes
     * @throws ResourceInitializationException 
     */
    // =======================================================
    public ToKnowtator(String pEHostWorkSpace, String pEHostProject, String[] pOutputTypes) throws ResourceInitializationException {
      
      initialize( pEHostWorkSpace, pEHostProject, pOutputTypes);
      
    } // end Constructor ----------------------

 // =======================================================
    /**
     * Constructor ToKnowtator 
     *
     * @param pArgs
     * @throws ResourceInitializationException 
     */
    // =======================================================
    public ToKnowtator(String pArgs[]) throws ResourceInitializationException {
      
      initialize( pArgs);
      
    } // end Constructor ----------------------

    
    
    // -----------------------------------------
    /** 
     * process iterates through all annotations and converts them
     * to knowtator annotations then puts them out to a knowtator formatted
     * persistant store.
     *
     * @param pAJCas
     * @throws AnalysisEngineProcessException
     */
//-----------------------------------------
  @Override
    public void process(JCas pJCas) throws AnalysisEngineProcessException  {
 
	  
	  try {
		  
		  this.performanceMeter.startCounter();

   // VinciNLPFramework_UIMA_Knowtator knowtator = new VinciNLPFramework_UIMA_Knowtator();
     KnowtatorUtils knowtator = new KnowtatorUtils();
     
      // -------------------------------------------
      // The class definition file and attributes file needs to be
      // written out for each corpus.  Since I don't have a handle
      // on where to write it out until a file with the path to
      // the output, it's written out once here.
      // -------------------------------------------
      if ( !this.classDefinitionsWritten ) {
       
        try {
        
          if ( this.outputTypeStrings == null || this.outputTypeStrings.length == 0 )
            this.outputTypeStrings = getAllLabels(pJCas);
         
            
            this.outputTypeHash = knowtator.createOutputTypeHash(this.outputTypeStrings );
         
          
          knowtator.writeClassDefinitions( outputProjectDirectoryConfig, this.outputTypeStrings, pJCas) ;
          this.classDefinitionsWritten = true;
        } catch ( Exception e) {
          e.printStackTrace();
          throw new AnalysisEngineProcessException( e);
        }
      } // end if the class file hasn't been written
      
          
      String outputFileName = getOutputFileName( pJCas);
      System.err.println("The out put fileName = " + outputFileName);
     
      // --------------------------------------------------
      // Write the source file out to the corpus directory
      // --------------------------------------------------
      try {
        String sourceFileName = this.getSourceName( pJCas);
        PrintWriter out = new PrintWriter( this.outputProjectDirectoryCorpus + "/" + sourceFileName );
        out.println( pJCas.getDocumentText());
        out.close();
        
      } catch (FileNotFoundException e1) {
       
        e1.printStackTrace();
        throw new RuntimeException ( "Something went wrong in ToKnowtator writing out the source file : " +  e1.toString() );
      }
      
      try {
        gov.va.vinci.knowtator.Document knowtatorDocument = knowtator.convert(pJCas, this.outputTypeHash, annotator );
      
 
        knowtator.write( outputFileName, knowtatorDocument );
        
      } catch ( Exception e ) {
        e.printStackTrace();
        throw new AnalysisEngineProcessException( e);
        
      }
      
      this.performanceMeter.stopCounter();

	  }catch ( Exception e2 ) {
		  e2.printStackTrace();
		  System.err.println("Issue in knowtator :" + e2.toString());
		  this.performanceMeter.stopCounter();

	  }
    } // end Method process
    
    
    // ------------------------------------------
  /**
   * getAllLabels retrieves all the label names
   * from the annotations in the cas (sans the uima 
   * labels)
   *
   *
   * @param pJCas
   * @return String[]
   */
  // ------------------------------------------
  private String[] getAllLabels(JCas pJCas) {
    
    String[] returnVal = null;
    TypeSystem typeSystem = pJCas.getTypeSystem();
    
    Iterator<Type> itr = typeSystem.getTypeIterator();
    ArrayList<String> labelNames = new ArrayList<String>();
    while ( itr.hasNext()) {
      Type aType = itr.next();
      String name = aType.getName();
      // System.err.println("The name of the type = " + name);
      
      if ( !name.contains("uima") && !this.mustRemoveTypes.contains(name) )
        labelNames.add( name);
      
    }
    if ( labelNames != null)
      returnVal = labelNames.toArray( new String[labelNames.size()]);
    
    return returnVal;
    
    
  }  // End Method getAllLabels() -----------------------
  
  
    // -----------------------------------------
    /**
     * getOuputFileName extracts the name of the cas, 
     * and returns the path defined by 
     * $EHOSTWorkSpace/saved/xxxx.knowtator.xml
     * 
     * 
     * @param pJCas
     * @return String
     */
    // -----------------------------------------
    private String getOutputFileName(JCas pJCas) {

      String returnValue = null;
      
      String inputFileName = getSourceName( pJCas);
     
      returnValue = this.outputProjectDirectorySaved + "/" + inputFileName + ".knowtator.xml";
              
      return returnValue;
    } // end Method getOutputFileName() --------
    
 // -----------------------------------------
    /**
     * getSourceName extracts the name of the cas, 
     * devoid of a class path
     * 
     * 
     * @param pJCas
     * @return String
     */
    // -----------------------------------------
    private String getSourceName(JCas pJCas) {
          
      String sourceURI = VUIMAUtil.getDocumentId( pJCas);
      String name = null;
      if ( sourceURI != null ) {
        File aFile = new File( sourceURI);
        name = aFile.getName();
        
      } else {
        name = "knowtator_" + String.valueOf( this.inputFileCounter++ );
      }
    
      
      return name;
    } // end Method getOutputFileName() --------

    

    // ----------------------------------
    /**
     * destroy
     * 
     **/
    // ----------------------------------
    public void destroy() {
      this.performanceMeter.writeProfile(this.getClass().getSimpleName());
    }

    //----------------------------------
    /**
     * initialize initializes the knowtator writer - This version relies on
     * the configuration variable eHostWorkspace to know where the ./saved and ./corpus 
     * directories should go.  It no longer tries to build a path from
     * outputDir/workspaceName/ProjectName to figure it out. 
     *
     * @param aContext
     * @throws ResourceInitializationException 
     * 
     **/
    // ----------------------------------
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
      
     try {
   
        String args[] = (String[]) aContext.getConfigParameterValue("args");
        initialize( args);
     
      } catch ( Exception e ) {
        e.printStackTrace();
        String msg = "Issue with ToKnowtator: " + e.getMessage();
        System.err.println(msg);
        this.log.error( msg);
        throw new ResourceInitializationException();
      }
      
    
    } // end Method initialize() --------------
    
    
    //----------------------------------
    /**
     * initialize initializes the knowtator writer - This version relies on
     * the configuration variable eHostWorkspace to know where the ./saved and ./corpus 
     * directories should go.  It no longer tries to build a path from
     * outputDir/workspaceName/ProjectName to figure it out. 
     *
     * @param pArgs
     * @throws ResourceInitializationException 
     * 
     **/
    // ----------------------------------
    public void initialize(String pArgs[]) throws ResourceInitializationException {
    
    	
    	try {
    		  this.performanceMeter = new ProfilePerformanceMeter(pArgs, this.getClass().getSimpleName());
    		     
    	
      String _outputDir   =  U.getOption(pArgs, "--outputDir", "/someOutputDir");
      String _annotator   =  U.getOption(pArgs, "--annotator", "me");
      String __outputTypes =  U.getOption(pArgs, "--outputTypes", "someLabel:someLabel");
      String _outputTypes[] = null;
      
      if ( !__outputTypes.equals("someLabel:someLabel") ) {
        if ( __outputTypes.contains(":"))
          _outputTypes = U.split(__outputTypes, ":");
        else 
          _outputTypes = U.split(__outputTypes);
      }
      
      this.initialize( _outputDir, _annotator, _outputTypes );
    
    	} catch (Exception e) {
    		e.printStackTrace();
    		System.err.println("Issue with ToKnotator " + e.toString());
    		throw new ResourceInitializationException();
    	}
    
    } // End Method initialize() --------
    
    // =======================================================
    /**
     * initialize initializes the knowtator reader
     * 
     * @param pEHostWorkSpace
     * @param pEHostProject
     * @throws ResourceInitializationException 
     */
    // =======================================================
    public void initialize(String pEHostWorkSpace, String pAnnotator, String[] pOutputTypes)  throws ResourceInitializationException  {
    
      
      try {
        
        this.annotator   = pAnnotator;
        
        this.eHostWorkSpaceName = pEHostWorkSpace;        
        this.outputTypeStrings = pOutputTypes;
        this.mustRemoveTypes = new HashSet<String>();
       // this.mustRemoveTypes.add("DocumentHeader");
        this.mustRemoveTypes.add("CSI");
        this.mustRemoveTypes.add("SourceDocumentInformation");
        this.mustRemoveTypes.add("Colonoscopy_Related_Pathology_Report");
       
        this.outputProjectDirectory        = this.eHostWorkSpaceName;
        this.outputProjectDirectoryConfig  = this.outputProjectDirectory + "/config";
        this.outputProjectDirectorySaved   = this.outputProjectDirectory + "/saved";
        this.outputProjectDirectoryCorpus  = this.outputProjectDirectory + "/corpus";
        
        // ------------------------------------------------
        // Create the project directories and populate them
        // ------------------------------------------------
       
        U.mkDir( this.outputProjectDirectoryConfig );
        U.mkDir( this.outputProjectDirectorySaved  );
        U.mkDir( this.outputProjectDirectoryCorpus );
    
        this.classDefinitionsWritten = false;
        
        System.err.println("Attached Knowtator writer.  Output going to " + this.outputProjectDirectorySaved );
      } catch (Exception e) {
        e.printStackTrace();
        String msg = "Issue with ToKnowtator: " + e.getMessage();
        System.err.println(msg);
        //this.log.error( msg);
        throw new ResourceInitializationException();
      }
       
    }  // End Method initialize() ======================
    

    private Logger log;
    // ----------------------------------------
    // Global variables
    // ----------------------------------------
    // private String outputDir = null;
   // private String projectName = null;
    private String outputProjectDirectory       = null;
    private String outputProjectDirectoryConfig = null;
    private String outputProjectDirectorySaved  = null;
    private String outputProjectDirectoryCorpus = null;
    private String annotator = null;

    private int inputFileCounter = 0;
    private boolean classDefinitionsWritten = false;
    private String eHostWorkSpaceName = null;
    
    private String[] outputTypeStrings = null;
    private HashSet<String> outputTypeHash = null;
  
    private ProfilePerformanceMeter performanceMeter = null;
    
   
  


} // end Class toKnowtator
