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
package gov.nih.cc.rmd.nlp.framework.pipeline;




import gov.nih.cc.rmd.nlp.framework.annotator.AssertionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.AssertionEvidenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.CheckBoxAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.FilterWriter;
import gov.nih.cc.rmd.nlp.framework.annotator.LineAnnotatorWithBlankLines;
import gov.nih.cc.rmd.nlp.framework.annotator.QuotedUtteranceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SentenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SentenceListRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueRepairAnnotator2;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueRepairAnnotator3;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueWithinListAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.TermAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.TokenAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.TokenSlashRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.CCDAPanelSectionHeaderAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.CCDASectionHeaderAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.CCDASectionsAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.SectionNameInTermAttributeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.SentenceSectionRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.SentenceTabRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.TableSectionZoneAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.DateAndTimeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.DateAndTimeByTokenAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.DateByLookupAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.shapes.BTRIS_RedactionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.shapes.PersonTokensAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.shapes.PunctuationTermsAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.shapes.RegexShapeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.shapes.TermShapeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.shapes.UnitOfMeasureAnnotator;
import gov.nih.cc.rmd.nlp.framework.utils.U;



// =================================================
/**
 * FrameworkRMDSentencePipeline  chunks documents into utterances via Frameworks annotators
 * This version uses the RMD python sentence chunker
 * 
 * optionally
 
 *        
 * @author  Guy Divita 
 * @created Sept 03, 2020
 *
 */


  public class FrameworkRMDSentenceNoFilterPipeline extends AbstractPipeline  {


    // =======================================================
    /**
     * Constructor  
     *
     * @param pArgs
     */
    // =======================================================
    public FrameworkRMDSentenceNoFilterPipeline(String[] pArgs) {
      super(pArgs);
    
    }


    // -----------------------------------------
    /**
     * Constructor 
     * 
     * @throws Exception
     */
    // -----------------------------------------
    public FrameworkRMDSentenceNoFilterPipeline()  throws Exception {
     super( );
      
    } // End Constructor() -----------------------------
    
    
	// =======================================================
  /**
	 * createPipeline defines the pipeline - this is usually defined in a uima configuration
	 * file.  It's being done programmatically here because I detest programming by configuration files and java source files.
	 * 
	 * @param pArgs
	 * @return FrameworkPipeline
	 */
	// =======================================================
	@Override
  public FrameworkPipeline createPipeline(String[] pArgs) {

	    FrameworkPipeline pipeline = new FrameworkPipeline(pArgs);
	    pipeline.setTypeDescriptorClassPath("gov.nih.cc.rmd.framework.Model");   // <----- this might be ignored if the config file is used
	    
      setPipeline( pipeline, pArgs);
	   
      return pipeline;
    
    }  // End Method createPipeline() ======================


  // =================================================
  /**
   * setPipeline sets the pipeline 
   * 
   * @param pipeline
   * @param pArgs
  */
  // =================================================
   public synchronized static void setPipeline(FrameworkPipeline pipeline, String[] pArgs) {
    
     
     // pipeline.setTypeDescriptorClassPath("gov.nih.cc.rmd.FrameworkAndCtakes");   // <----- this might be ignored if the config file is used
       
    
	   
     String typeDescriptor = U.getOption( pArgs, "--typeDescriptor=", "gov.nih.cc.rmd.framework.Model"); //  "gov.nih.cc.rmd.FrameworkAndCtakes");
     pipeline.setTypeDescriptorClassPath( typeDescriptor );  //<--- in the 73_03-type.descriptor/src/main/resources ... folder
   
     
     UimaContextParameter argsParameter = new UimaContextParameter("args",  pArgs, "String",  true, true);
   
    
     pipeline.add(   LineAnnotatorWithBlankLines.class.getCanonicalName(), argsParameter ) ;
     pipeline.add(          RegexShapeAnnotator.class.getCanonicalName(), argsParameter  );
     pipeline.add(           DateAndTimeAnnotator.class.getCanonicalName(), argsParameter );
    //  pipeline.add(          AbsoluteTimeAnnotator.class.getCanonicalName(), argsParameter );
     
     pipeline.add(                TokenAnnotator.class.getCanonicalName(), argsParameter );
     pipeline.add(      BTRIS_RedactionAnnotator.class.getCanonicalName(), argsParameter );
     pipeline.add(     TokenSlashRepairAnnotator.class.getCanonicalName(), argsParameter);
     pipeline.add(         DateByLookupAnnotator.class.getCanonicalName(), argsParameter );
     pipeline.add(   DateAndTimeByTokenAnnotator.class.getCanonicalName(), argsParameter );
     
     pipeline.add(             CheckBoxAnnotator.class.getCanonicalName(), argsParameter ) ;
     pipeline.add(            SlotValueAnnotator.class.getCanonicalName(), argsParameter );
    
     pipeline.add(             SentenceAnnotator.class.getCanonicalName(), argsParameter );
     // pipeline.add(       SentenceModelWrapperAnnotator.class.getCanonicalName(), argsParameter);

       
     
     
     pipeline.add(                 TermAnnotator.class.getCanonicalName(), argsParameter );
     pipeline.add(    AssertionEvidenceAnnotator.class.getCanonicalName(), argsParameter);
     
     pipeline.add(        UnitOfMeasureAnnotator.class.getCanonicalName(), argsParameter);
     pipeline.add(            TermShapeAnnotator.class.getCanonicalName(), argsParameter);
     pipeline.add(      PunctuationTermsAnnotator.class.getCanonicalName(), argsParameter);
     
     pipeline.add(         PersonTokensAnnotator.class.getCanonicalName(), argsParameter); //<--- new tokens are created in term - need to do it again
   
     pipeline.add(  SlotValueWithinListAnnotator.class.getCanonicalName(), argsParameter);
     pipeline.add(      SlotValueRepairAnnotator.class.getCanonicalName(), argsParameter );
     pipeline.add(     SlotValueRepairAnnotator2.class.getCanonicalName(), argsParameter );
     pipeline.add(     SlotValueRepairAnnotator3.class.getCanonicalName(), argsParameter );  
   
    
     pipeline.add(     CCDASectionHeaderAnnotator.class.getCanonicalName(), argsParameter);
     pipeline.add(CCDAPanelSectionHeaderAnnotator.class.getCanonicalName(), argsParameter);
     pipeline.add(          CCDASectionsAnnotator.class.getCanonicalName(), argsParameter);
     pipeline.add( SentenceSectionRepairAnnotator.class.getCanonicalName(), argsParameter);
     pipeline.add(   QuotedUtteranceAnnotator.class.getCanonicalName(), argsParameter ) ;
     
     pipeline.add(    SentenceListRepairAnnotator.class.getCanonicalName(), argsParameter);
    
     pipeline.add(     SentenceTabRepairAnnotator.class.getCanonicalName(), argsParameter);    
     pipeline.add(      TableSectionZoneAnnotator.class.getCanonicalName(), argsParameter); 
    
     pipeline.add(            AssertionAnnotator.class.getCanonicalName(), argsParameter);  
     pipeline.add(SectionNameInTermAttributeAnnotator.class.getCanonicalName(), argsParameter);  
     
    
   
  } // end Method setPipeline() -----------------------------*/
    
    

} // end Class DateAndTimePipeline
