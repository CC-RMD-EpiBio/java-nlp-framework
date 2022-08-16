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
 *
 *
 */
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.net.URL;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ClinicalStatement;
import gov.va.chir.model.CodedEntry;
import gov.va.vinci.model.Concept;

import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.RegExMatcher;
import gov.nih.cc.rmd.nlp.framework.utils.RuleMatch;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import hitex.gate.regex.Rule;

/**
 * Evaluates a set of regular expression rules against documents and adds
 * annotations defined in those rules.
 * 
 * @author vhaislreddd
 */
public class RegExAnnotator extends JCasAnnotator_ImplBase {

	private List<Rule> ruleList;
	public static final String ExampleRegExRule = "<concepts>   \n" +
	                                              "  <concept>  \n" +
	                                              "    <def><![CDATA[(?i)(?:(?:ejection\\s+fraction)|(?:ej))\\s+(?:of\\s+)?(\\d+%?)]]></def> \n" +
	                                              "    <capt_group_num>0</capt_group_num> \n" +
	                                              "    <type>RegEx</type> \n" +
	                                              "    <name>CardioVascular</name> \n" +
	                                              "    <features> \n" +
	                                              "       <feature> \n" +
	                                              "          <name>code</name> \n" +
	                                              "          <value><![CDATA[ventric]]></value> \n" +
	                                              "       </feature> \n" +
	                                              "    </features> \n" +
	                                              "   </concept> \n" +
	                                              "</concepts> \n" ;
  



	@Override
	public void process(JCas pJCas) throws AnalysisEngineProcessException {

	  this.performanceMeter.startCounter();

		String docText = pJCas.getDocumentText();
		if (docText != null) {
			List<RuleMatch> ruleMatches = RegExMatcher.evaluate(docText,	this.ruleList);
			for (RuleMatch ruleMatch : ruleMatches) {
			  ruleMatch.getRule();
			  int captureGroupNum = 0;
			
			//	List<Concept> concepts = new ArrayList<Concept>();
			  List<String> captureGroupValues = ruleMatch.getCaptureGroupValues();
			  if ( captureGroupValues != null && captureGroupValues.size() > 0  ) {
			    for (String captureGroupValue : ruleMatch.getCaptureGroupValues()) 
				  
			      createConcept(pJCas,	(int) ruleMatch.getStart(),	(int) ruleMatch.getEnd(), ++captureGroupNum, captureGroupValue);
			    
				} else {
				   createConcept(pJCas, (int) ruleMatch.getStart(), (int) ruleMatch.getEnd(), ++captureGroupNum, null );	         
				}
			
			}
		}
		this.performanceMeter.stopCounter();
	}

	/**
	 * createClinicalStatement creates a clinicalStatement from a regular
	 * expression rule match. The clinicalStatement is set as the parent of each
	 * codedEtnry
	 * 
	 * @param pJCas
	 * @param ruleMatch
	 * @param codedEntries
	 * 
	 * @return ClinicalStatement
	 */
	private ClinicalStatement createClinicalStatement(JCas pJCas,
			RuleMatch ruleMatch, List<CodedEntry> codedEntries) {
		int begin = (int) ruleMatch.getStart();
		int end = (int) ruleMatch.getEnd();

		ClinicalStatement statement = new ClinicalStatement(pJCas);
		VUIMAUtil.setProvenance(pJCas, statement, this.getClass().getName());
		statement.setBegin(begin);
		statement.setEnd(end);
		statement.setDisplayString(ruleMatch.getText());
		StringArray stringArray = new StringArray(pJCas, 5 + ruleMatch
				.getRule().getFeatureNames().size());
		int i = 0;
		stringArray.set(i++, "text|" + ruleMatch.getText());
		stringArray.set(i++, "rule.name|" + ruleMatch.getRule().getName());
		stringArray
				.set(i++, "rule.pattern|" + ruleMatch.getRule().getPattern());
		stringArray.set(i++, "rule.type|" + ruleMatch.getRule().getType());
		stringArray.set(i++, "rule.captGroupNum|"
				+ ruleMatch.getRule().getCaptGroupNum());
		for (String featureName : ruleMatch.getRule().getFeatureNames()) {
			stringArray.set(i++, "rule." + featureName + "|"
					+ ruleMatch.getRule().getFeatureValue(featureName));
		}
		stringArray.addToIndexes(pJCas);
		statement.setOtherFeatures(stringArray);
		FSArray codedEntriez = UIMAUtil.list2FsArray(pJCas, codedEntries);
		statement.setCodedEntries(codedEntriez);
		statement.addToIndexes(pJCas);

		for (CodedEntry codedEntry : codedEntries) {
			codedEntry.setParent(statement);
		}

		return statement;
	}

	/**
	 * createConcept creates a concept given capture group details from a
	 * regular expression match.

   * @param pJCas
	 * @param pBegin
	 * @param pEnd
	 * @param pCaptureGroupNumber
	 * @pram pValue
	 
	 * @return Concept
	 */
	// -----------------------------------------
	private Concept createConcept(JCas pJCas, final int pBegin, final int pEnd, final int pCaptureGroupNumber, String pValue) {
		Concept concept = new Concept(pJCas);
		concept.setBegin(pBegin);
		concept.setEnd(pEnd);
		concept.setCuis(pValue);
		concept.setId("regEx_" + pCaptureGroupNumber );
		String snippet = pJCas.getDocumentText().substring(pBegin, pEnd);
		concept.setConceptNames( snippet );

	
		concept.addToIndexes();

		return concept;
	}


  /**
   * destroy
   * 
   **/
  public void destroy() {
    this.performanceMeter.writeProfile( this.getClass().getSimpleName());
  }

           



/**
   * initialize loads in the resources.
   * 
   * @param aContext
   **/
  public void initialize(UimaContext aContext)	throws ResourceInitializationException {
   
      String args[] = null;
      String rulesXML = null;
      String rulesURL = null;
      
     
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  

      } catch (Exception e ) {
        System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
        throw new ResourceInitializationException();
      }
      

      this.outputDir      = U.getOption(args, "--outputDir=", "./");
      this.performanceMeter = new PerformanceMeter( this.outputDir + "/logs/profile_" + this.getClass().getSimpleName() + ".log" );
      
       rulesXML = U.getOption(args, "--rulesXML=", RegExAnnotator.ExampleRegExRule);
       rulesURL = U.getOption(args,  "--rulesURL=",  "/data/regexFiles/example1.xml");
      
      
        
      
    initialize( rulesXML, rulesURL);
    
  }
  
  /**
   * initialize loads in the resources.
   * 
   * @param pRulesXML
   * @pram pRulesURL
   * 
   **/
  public void initialize( String pRulesXML, String pRulesURLString)  throws ResourceInitializationException {
  
    URL rulesURL = null;
    String rulesXML = pRulesXML;
  
  	  
  	  if ( pRulesURLString != null && pRulesURLString.trim().length() > 0 ) { 
  	    try {
  	      rulesURL = new URL ( pRulesURLString);
  	    } catch (Exception e) {
  	      
  	    
  	      try {
  	    
  	        this.ruleList = RegExMatcher.loadRegexRules(rulesXML, rulesURL);
  	        rulesXML = null;
  	      } catch (Exception e2 ) {
  	        e2.printStackTrace();
  	        String msg = "Failed to load regular expression rules "+ e2.toString();
  	        System.err.println(msg);
  	        throw new ResourceInitializationException(e2);
  	      }
  	     
  	      
  	    }
  	  } 
  	  
  	    try {
          
          this.ruleList = RegExMatcher.loadRegexRules(null, rulesURL);
          
        } catch (Exception e2 ) {
          e2.printStackTrace();
          String msg = "Failed to load regular expression rules "+ e2.toString();
          System.err.println(msg);
          throw new ResourceInitializationException(e2);
  	  }
        
       
        
      
  	  
  } // end Method initialize() ------------


// ---------------------------------------
// Global Variables
// ---------------------------------------
   PerformanceMeter              performanceMeter = null;
   private String                       outputDir = null;

	
}
