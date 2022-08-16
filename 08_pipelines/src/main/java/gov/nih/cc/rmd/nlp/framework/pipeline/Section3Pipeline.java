// =================================================
/**
 * Section3Pipeline defines the section pipeline for va, ssa, ccda documents

 *     
 *     
 * @author  Guy Divita 
 * @created July 17, 2019
 *
 * 
 */
package gov.nih.cc.rmd.nlp.framework.pipeline;

import gov.nih.cc.rmd.nlp.framework.annotator.AssertionEvidenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.CheckBoxAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.FilterWriter;
import gov.nih.cc.rmd.nlp.framework.annotator.LineAnnotatorWithBlankLines;
import gov.nih.cc.rmd.nlp.framework.annotator.QuestionAnswerAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SectionFilter;
import gov.nih.cc.rmd.nlp.framework.annotator.SentenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueRepairAnnotator2;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueRepairAnnotator3;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueWithinListAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.TermAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.TokenAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.CCDAPanelSectionHeaderAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.CCDASectionHeaderAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.CCDASectionsAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.SentenceSectionRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.SentenceTabRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.TableSectionZoneAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.shapes.RegexShapeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.shapes.TermShapeAnnotator;
import gov.nih.cc.rmd.nlp.framework.utils.U;


  public class Section3Pipeline extends AbstractPipeline  {

   
	// =======================================================
	    /**
	     * Constructor SectionPipeline 
	     *
	     * @param pArgs
	     */
	    // =======================================================
	    public Section3Pipeline() {
	      super();
	      
	    }

	  
   
    // =======================================================
    /**
     * Constructor SectionPipeline 
     *
     * @param pArgs
     */
    // =======================================================
    public Section3Pipeline(String[] pArgs) {
      super(pArgs);
      
    }


    // =======================================================
    /**
     * createPipeline defines the pipeline
     * 
     * @param pArgs
     * @return FrameworkPipeline
     */
    // =======================================================
    public FrameworkPipeline createPipeline(String[] pArgs) {

          String typeDescriptorClassPath = U.getOption(pArgs,"--typeDescriptor=", "com.ciitizen.framework.Model");
      
          FrameworkPipeline pipeline = new FrameworkPipeline(pArgs);
          UimaContextParameter argsParameter = new UimaContextParameter("args",  pArgs, "String",  true, true);
          pipeline.setTypeDescriptorClassPath(typeDescriptorClassPath);  //<--- in the type.descriptor/desc/com/ciitizen/framework folder
          
           pipeline.add(   LineAnnotatorWithBlankLines.class.getCanonicalName(), argsParameter ) ;
           pipeline.add(          RegexShapeAnnotator.class.getCanonicalName(), argsParameter );
           pipeline.add(                TokenAnnotator.class.getCanonicalName(), argsParameter );
           
           
           pipeline.add(             CheckBoxAnnotator.class.getCanonicalName(), argsParameter ) ;
           pipeline.add(            SlotValueAnnotator.class.getCanonicalName(), argsParameter );
           
           pipeline.add(             SentenceAnnotator.class.getCanonicalName(), argsParameter );
           
         
       
           pipeline.add(                 TermAnnotator.class.getCanonicalName(), argsParameter );
           pipeline.add(             TermShapeAnnotator.class.getCanonicalName(), argsParameter);
           pipeline.add(    AssertionEvidenceAnnotator.class.getCanonicalName(), argsParameter);
       //    pipeline.add(       QuestionAnswerAnnotator.class.getCanonicalName(), argsParameter );  causing trouble
         
            
           pipeline.add(  SlotValueWithinListAnnotator.class.getCanonicalName(), argsParameter);
           pipeline.add(      SlotValueRepairAnnotator.class.getCanonicalName(), argsParameter );
           pipeline.add(     SlotValueRepairAnnotator2.class.getCanonicalName(), argsParameter );
           pipeline.add(     SlotValueRepairAnnotator3.class.getCanonicalName(), argsParameter );  
           
           
               
           pipeline.add(     CCDASectionHeaderAnnotator.class.getCanonicalName(), argsParameter);
           pipeline.add(CCDAPanelSectionHeaderAnnotator.class.getCanonicalName(), argsParameter);
           pipeline.add(          CCDASectionsAnnotator.class.getCanonicalName(), argsParameter);
           pipeline.add(                  SectionFilter.class.getCanonicalName(), argsParameter);
           pipeline.add( SentenceSectionRepairAnnotator.class.getCanonicalName(), argsParameter);
           pipeline.add(     SentenceTabRepairAnnotator.class.getCanonicalName(), argsParameter);    
           pipeline.add(      TableSectionZoneAnnotator.class.getCanonicalName(), argsParameter);   
       
           pipeline.add(                   FilterWriter.class.getCanonicalName(), argsParameter); 
      
      
      return pipeline;

    } // End Method createPipeline() ======================



   
   
} // end Class SophiaNegexServer 
