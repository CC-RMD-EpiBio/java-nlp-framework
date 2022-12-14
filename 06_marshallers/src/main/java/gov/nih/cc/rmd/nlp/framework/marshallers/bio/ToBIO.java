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
//=================================================
/**
 * ToBIO is a CAS Consumer that transforms
 * the vinciNLPFramework encoded CAS into a bio format.  This version is home grown. The 
 * original version was  wrapper around the python tokenizer and bio writer.
 * This version uses the framework tokenizer.
 * 
 *
 * @author  Guy Divita 
 * @created Sept 21, 2020
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.bio;


import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.WordToken;



public class ToBIO extends AbstractWriter {





  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   * 
  **/
  // =================================================
  public ToBIO(String[] pArgs) throws ResourceInitializationException {
    initialize( pArgs);
  } // end Constructor() ----------------------





    // -----------------------------------------
    /** 
     * process iterates through all annotations, filters out
     * those that should be filtered out, then pushes them
     * into a database store.
     * 
     * Each document should include a DocumentAnnotation annotation and a documentHeader annotation
     * for re-animation purposes.
     *
     * @param pAJCas
     * @throws AnalysisEngineProcessException
     */
    // -----------------------------------------
    @Override
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
      
      this.performanceMeter.startCounter();
      List<String> bioRows = new ArrayList<String>();
      try {
     
        String outputFileName = VUIMAUtil.getOutputFileName( pJCas, this.outputDir, "BIO");
     
    
        List<Annotation> tokens = UIMAUtil.getAnnotations(pJCas, WordToken.typeIndexID);
        
        tokens = UIMAUtil.uniqueAnnotations( tokens );
        
        List<Annotation>[] focusAnnotations = null;
        List<Annotation>[] evalAnnotations = null;
            
          
        
        if ( tokens != null && !tokens.isEmpty()) {
        
          
        focusAnnotations = getFocusAnnotations(pJCas, this.outputTypes );
        evalAnnotations = getFocusAnnotations(pJCas, this.evalTypes );
          
        
          for ( int i = 0; i < tokens.size(); i++ ) {
            Annotation token = tokens.get(i);
         
            
            StringBuffer buff =  new StringBuffer();
            String normalizedToken = normalizeToken( token.getCoveredText());
            buff.append( normalizedToken );
            
            
            if ( this.evalTypes != null && this.evalTypes.length > 0 ) {
              String bioEvalLabel = retrieveBioLabel( pJCas, token, this.evalTypes, evalAnnotations);
              buff.append(TAB + bioEvalLabel);
            }
            
            String bioLabel = retrieveBioLabel( pJCas, token, this.outputTypes, focusAnnotations);
            buff.append(TAB + bioLabel);  
        
            
            bioRows.add (buff.toString() ) ;
          }
          
          writeBIOFile( outputFileName, bioRows);
        }
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue with BIO " + e.toString());
      }
      this.performanceMeter.stopCounter();
    } // end Method process



 // =================================================
    /**
     * getFocusAnnotations 
     * returns an array of List<Annotation>, where
     * each List<Annotation> is the set of annotations for the 
     * outputType[i]
     * 
     * This is a method to make the algorithm more efficient
     * by retrieving the list of each focus annotations once,
     * that step doens't have to be done multiple times
     * each time one is looking for a focus type from a
     * given span.
     * 
     * @param pJCas
     * @param pOutputTypes
     * @return List<Annotation>[]
    */
    // =================================================
   @SuppressWarnings("unchecked")
  private List<Annotation>[] getFocusAnnotations(JCas pJCas, String[] pOutputTypes) {
   
     
     List<Annotation>[] returnVal = null;
     if ( pOutputTypes != null && pOutputTypes.length > 0 ) {
     
       returnVal = (List<Annotation>[]) new List<?>[ pOutputTypes.length];
     
     for ( int i = 0; i < pOutputTypes.length; i++ )
         returnVal[i] = getFocusAnnotations( pJCas, pOutputTypes[i]);
     
     }
     return returnVal;
    } // end Method getFocusAnnotations() -------------





// =================================================
    /**
     * getFocusAnnotations returns a list of annotations 
     * for this type 
     * 
     * @param pJCas
     * @param pAnnotationType
     * @return List<Annotation>
    */
    // =================================================
   private final List<Annotation> getFocusAnnotations(JCas pJCas, String pAnnotationType) {
     
     List<Annotation > returnVal = null;
     try {
       returnVal = UIMAUtil.getAnnotations(pJCas, pAnnotationType );
       UIMAUtil.sortByOffset(returnVal);
       UIMAUtil.uniqueAnnotationList( returnVal);
       
     } catch ( Exception e) {
       e.printStackTrace();
       GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getFocusAnnotations", "Issue getting the list of annotations " + e.toString() + " for : " + pAnnotationType);
     }
     
     return returnVal;
     
    } // end Method getFocusAnnotations() -------------





    // =================================================
    /**
     * normalizeToken is a placeholder for transforming 
     * non-printable and newline tokens so they print on
     * one line  newlines turn into spaces, tabs turn into ~
     * 
     * 
     * @param coveredText
     * @return String
    */
    // =================================================
   private final String normalizeToken(String coveredText) {
     String buff = coveredText;
     
     buff.replace('\n', ' ' ); 
     buff.replace('\t', '~' );
     
     return buff;
    } // end Method normalizeToken() -------------------





// =================================================
    /**
     * writeBIOFile writes out a bio file 
     * 
     * @param outputFileName
     * @param bioRows
    */
    // =================================================
  private final void writeBIOFile(String pOutputFileName, List<String> pBioRows) {
   
    PrintWriter out = null;
    
    try {
     
      out = new PrintWriter( pOutputFileName );
      
      for ( String row: pBioRows )
        out.print( row + '\n');
      
      out.close();
     
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println( GLog.ERROR_LEVEL, this.getClass(), "writeBIOFile", "Issue writing out the file " + pOutputFileName + " " + e.toString());
    }
      
    } // end Method writeBIOFile() ------------------



// =================================================
    /**
     * retrieveBioLabel retrieves whether this
     * label is the beginning, middle or end for 
     * this token
     * 
     * If there are multiple labels on a line, they are delimited
     * by a - 
     * @param pJCas
     * @param pToken
     * @param outputTypes
     * @return String   B-[Label]-B[Label]
     *                  I-[Label]
     *                  O
    */
    // =================================================
   private String retrieveBioLabel(JCas pJCas, Annotation pToken, String[] pOutputTypes, List<Annotation>[] focusAnnotations) {
      
     String returnVal = "O";
     StringBuffer buff = new StringBuffer();
     
     try {
     
       if ( pOutputTypes != null && pOutputTypes.length > 0 && focusAnnotations != null && focusAnnotations.length > 0)
         for ( int i = 0; i < pOutputTypes.length; i++  ) {
           String returnType = retrieveBioLabel( pJCas, pToken, focusAnnotations[i], pOutputTypes[i]  );
      
           if ( returnType != null )
             if ( buff.length() > 0 )
               buff.append("-" + returnType );
             else
               buff.append( returnType);
       
         } // end loop through outputTypes
     
         if ( buff.length() > 0 )
         returnVal = buff.toString();
     
       
     } catch (Exception e) {
       e.printStackTrace();
       GLog.println(GLog.ERROR_LEVEL, this.getClass(), "retrieveBioLabel", "no mapped label for " + outputTypes + " " + e.toString() );
     }

      return returnVal;
    } // end Method retrieveBioLabel() -------------




	// =================================================
/**
 * retrieveBioLabel
 * 
 * @param pJCas
 * @param pToken
 * @param focusLabel
 * @return String  B-[FocusLabel]  I-[FocusLabel] or null
*/
// =================================================
private final String retrieveBioLabel(JCas pJCas, Annotation pToken, List<Annotation> pLikeAnnotations, String focusLabel) {

  String returnVal = null;
  List<Annotation> annotations = null;
  try {

    
    int typeId = UIMAUtil.getLabelTypeId(focusLabel);

   
    if ( typeId != -1 )
       annotations = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, typeId, pToken.getBegin(), pToken.getEnd()-1);
    else {
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "retrieveBioLabel", "no mapped label for " + focusLabel );
    }
    
    if (annotations != null && !annotations.isEmpty())
      if (annotations.get(0).getBegin() == pToken.getBegin())
        returnVal = "B-" + focusLabel;
      else 
        returnVal = "I-" + focusLabel;

  } catch (Exception e) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "retrieveBioLabel", "no mapped label for " + focusLabel + " " + e.toString());
  }

  return returnVal;

} // end Method retrieveBioLabel() ----




  //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
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
      
   
    
      
    } // end Method initialize() --------------


  //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param pArgs
     * 
     **/
    // ----------------------------------
    public void initialize( String[] pArgs ) throws ResourceInitializationException {
      
      this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
      
      this.outputDir =  U.getOption(pArgs, "--outputDir=", "./outputDir");
     
      String  outputTypez = U.getOption(pArgs, "--outputTypes=" ,"Concept:Qualifier" );
      String evalTypez =  U.getOption(pArgs, "--evalTypes=" ,"" );
   
      if ( outputTypez != null ) {
        outputTypez = outputTypez.replaceAll(":WordToken","");
        outputTypez = outputTypez.replaceAll("WordToken:", "");
      }
      
      this.outputTypes = U.split( outputTypez, ":");
     
      if ( evalTypez != null  && evalTypez.trim().length() > 0 )
        this.evalTypes = U.split( evalTypez, ":");
      
      
      this.outputDir = this.outputDir + "/BIO" ;
    
      try {
        
        U.mkDir( this.outputDir );
        
      
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue with bio writer " + e.toString());
        throw new ResourceInitializationException();
      }
      
      
      
     
      
    
      
    } // end Method initialize() --------------


    //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize(String pOutputDir ) throws ResourceInitializationException {
      
    
      String args[] = new String[1];
      args[0] = "--ouputDir=" + pOutputDir;
      
      initialize( args);
    
      
      
    } // end Method initialize() --------------


    


  //----------------------------------
  /**
   * destroy
  * 
   **/
  // ----------------------------------
  public void destroy() {
    
  
    GLog.println("Output is going to |" + this.outputDir );
    this.performanceMeter.writeProfile( this.getClass().getSimpleName());
  }


    // ----------------------------------------
    // Global variables
    // ----------------------------------------
  
  
    protected int annotationCtr = 0;
    ProfilePerformanceMeter performanceMeter = null;
    private static final char TAB = '\t';
    private String outputDir = null;
    private String outputTypes[] = null;
    private String evalTypes[] = null;
  

} // end Class toCommonModel
