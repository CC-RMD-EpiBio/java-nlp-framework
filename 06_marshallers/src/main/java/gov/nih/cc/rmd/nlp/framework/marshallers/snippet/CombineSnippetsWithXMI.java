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
 * CombineSnippetsWithXMI takes vtt files that are snippets of annotations in lots of files
 * and puts the decisions that were made in the snippets file back as new (Relevance) annotations of those
 * annotations in the xmi files.
 * 
 * In the initialize, read in ALL the Snippit files into a hash key'd by filename, of snippet (contexts)
 * 
 * 
 * @author  Guy Divita 
 * @created Nov 8, 2019
 *
 *   
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.snippet;


import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.marshallers.xmi.FromXMI;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.VAnnotation;
import gov.va.vinci.model.Concept;
import gov.va.vinci.model.Snippet;


public class CombineSnippetsWithXMI extends FromXMI {

  
 
  private FromVTTSnippets fromVTTSnippets;
  private static int annotationCounter = 0;



  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws ResourceInitializationException
   * 
  **/
  // =================================================
  public CombineSnippetsWithXMI(String[] pArgs) throws ResourceInitializationException {
    super( pArgs);
    
    initialize ( pArgs);
  }



  // =================================================
  /**
   * getNext  
   * 
   * @param pCas
  */
  // =================================================
  @Override
  public final void getNext(CAS pCas) {
    
    super.getNext( pCas );
    
    try {
      marryAnnotationsToSnippets(pCas.getJCas());
    } catch (Exception e) {
      System.err.println("Issue trying to marry annotatoins to snippets " + e.toString());
    }
    
  } // end Method getNext() ---------------------------
  
  

  // =================================================
  /**
   * marryAnnotationsToSnippets 
   * 
   * @param pCas
  */
  // =================================================
  private final void marryAnnotationsToSnippets(JCas pJCas) {
   
    
   String documentId = VUIMAUtil.getDocumentId(pJCas);
    
    
   try {
   HashMap<String,Annotation> casAnnotations = getCasAnnotations( pJCas );
   
   
   List<Context> snippetContexts = this.fromVTTSnippets.fileBySnippetHash.get( documentId);
   
   if ( snippetContexts != null && !snippetContexts.isEmpty() ) 
     for (  Context snippet: snippetContexts ) 
       marryAnnotationToSnippet( pJCas, snippet, casAnnotations );
   
   } catch (Exception e) {
     e.printStackTrace();
     System.err.println("Issue with marryAnnotatoinsToSnippets " + e.toString());
   }
   
  } // end Method marrayAnnotationsToSnippets() ------



 // =================================================
  /**
   * getCasAnnotations returns a hash of annotations, key'd by
   * annotationIds from Framework annotations
   * 
   * @param pJCas
   * @return HashMap<String, Annotation> 
   * @throws Exception
  */
  // =================================================
  private final HashMap<String, Annotation> getCasAnnotations(JCas pJCas) throws Exception {
   
    HashMap<String, Annotation> casAnnotations = null;
    
    try {
   
      
    List<Annotation> allAnnotations = UIMAUtil.getAnnotations(pJCas);
    
    if ( allAnnotations != null && !allAnnotations.isEmpty() ) {
      casAnnotations = new HashMap<String, Annotation>( allAnnotations.size() );
      for ( Annotation  anAnnotation: allAnnotations ) {
        String annotationId = getAnnotationId( anAnnotation );
        if ( annotationId != null && annotationId.trim().length() > 0 ) 
          casAnnotations.put( annotationId,  anAnnotation);
      }
    }
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with getting annotations from the cas " + e.toString());
      throw e;
    }
    return casAnnotations;
  } // end Method getCasAnnotations() -------------



// =================================================
/**
 * getAnnotationId returns the annotation id if it exists
 * on this annotation type
 * 
 * @param pAnnotation
 * @return String    Null if it doesn't have one
*/
// =================================================
private String getAnnotationId(Annotation pAnnotation) {
 
  String returnVal = null;
  try {
    returnVal = UIMAUtil.getFeatureValueByName(pAnnotation, "id");
  
  } catch ( Exception e) {
    
  }
  
  
  return returnVal;
} // end Method getAnnotationId() ------------------



// =================================================
  /**
   * marryAnnotationToSnippet finds the annotation that
   * belongs to this snippet
   * 
   * It creates a new annotation overlying the found
   * annotation of the class/label that was marked in
   * the snippet.  I.e., true,false, uncertain.
   * 
   * It won't make "snippet" relevance annotations.
   * 
   * @param pSnippet
   * @param pAnnotations
   * @param pJCas
  */
  // =================================================
 private final void marryAnnotationToSnippet(JCas pJCas, Context pSnippet, HashMap<String,Annotation> pAnnotations) {
   
  
   String annotationId = pSnippet.getAnnotationId();
   String label = pSnippet.getRelevance();
   String snippetId = pSnippet.getContextNumberString();
   
   
   Annotation annotation = pAnnotations.get( annotationId);
   
   if ( annotation == null ) {
     System.err.println("Issue - could not find an xmi annotation for snippet " + annotationId);
     
   } else {
     createRelevanceAnnotation( pJCas, annotation, label , snippetId);
   }
   
  } // end Method marryAnnotationToSnippet() --------



// =================================================
/**
 * createRelevanceAnnotation creates a new annotation
 * of the type named by pLabel 
 * 
 * @param pJCas
 * @param pAnnotation
 * @param pLabel
*/
// =================================================
private final void createRelevanceAnnotation(JCas pJCas, Annotation pAnnotation, String pLabel, String pSnippetId) {
  
  
  try {
    Class<?> labelClass = UIMAUtil.mapLabelToUIMAClass(pLabel.trim());
  
    if ( labelClass != null ) {
    
      try {
        Constructor<?> c = labelClass.getConstructor(new Class[] { JCas.class });
        Object statement = c.newInstance(pJCas);

        ((Annotation) statement).setBegin(pAnnotation.getBegin());
        ((Annotation) statement).setEnd(pAnnotation.getEnd());
        try {
          ((Snippet) statement).setAnnotationId("CombineSnippetsWithXMIAnnotator_" + annotationCounter++ + "_from_" + pSnippetId );
        } catch ( Exception e) {
          try {
            ((Concept) statement).setId("CombineSnippetsWithXMIAnnotator_" + annotationCounter++ + "_from_" + pSnippetId );
          } catch ( Exception e2) {
            ((VAnnotation) statement).setId("CombineSnippetsWithXMIAnnotator_" + annotationCounter++ + "_from_" + pSnippetId );
            
            
          }
        }
        ((Annotation) statement).addToIndexes(pJCas);

      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("createRelevanceAnnotation: something went wrong here " + e.toString());
      }
    
    } 
  } catch (Exception e) {
    e.printStackTrace();
    System.err.println("Issue with trying to make an annotation " + pLabel + " " + pLabel );
  }
  
  
} // end Method createRelevanceAnnotation() ---------



// =================================================
  /**
   * initialize does the initialization that the fromXMI
   * does, but also reads in the contents of all the 
   * snippet files into an array
   * 
   * @param pCas
   */
  // =================================================
  @Override
  public void initialize(String[] pArgs) throws ResourceInitializationException {
   
    super.initialize(pArgs);

    String inputDir = U.getOption(pArgs, "--inputDir=", "/some/input/dir" );
    String snippetDir_ = inputDir + "/snippets";
    
    File snippetDir = new File ( snippetDir_);
 
    this.fromVTTSnippets = new FromVTTSnippets();
    if ( snippetDir.exists() && snippetDir.canRead() )
      readSnippetFiles(  snippetDir );
    else 
      System.err.println("No snippet files ");

   
    
    
  } // end method initialize() -----------------------



// =================================================
/**
 * readSnippetFiles reads in the snippets and decisions
 * into a hash key'd by documentIds, of snippets
 * 
 * This method populates 
 * 
 * @param snippetDir
*/
// =================================================
private final void readSnippetFiles(File snippetDir) {
  
  try {
  
  File[] snippetFiles = snippetDir.listFiles();
  
  if ( snippetFiles != null && snippetFiles.length > 0 )
    for ( File snippetFile : snippetFiles )
      this.fromVTTSnippets.getSnippets(snippetFile);
  
  if ( this.fromVTTSnippets.fileBySnippetHash != null && this.fromVTTSnippets.fileBySnippetHash.isEmpty() )
    System.err.println("Picked up " + this.fromVTTSnippets.fileBySnippetHash.size() + " snippets ");
  
  } catch (Exception e) {
    e.printStackTrace();
    System.err.println("Issue with reading the snippets directory " + e.toString());
  }
  
} // end Method readSnippetFiles() -----------------

	


	

	
} // end Class CombineSnippetsWithXMI() ----
