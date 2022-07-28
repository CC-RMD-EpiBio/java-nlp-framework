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
package gov.nih.cc.rmd.nlp.framework.regex;

import java.net.URL;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.nih.cc.rmd.nlp.framework.utils.RegExMatcher;
import gov.nih.cc.rmd.nlp.framework.utils.RuleMatch;
import hitex.gate.regex.Rule;
import junit.framework.Assert;

/**
 * The Class RegExMatcherTest.
 *
 * @author vhaislreddd
 */
public class RegExMatcherTest {

  /** The Constant TEST_TEXT. */
  private static final String TEST_TEXT =
      "CardioVascular Exam Report:\nRIGHT VENTRICULAR FUNCTION: normal\nECHOCARDIOGRAM: abnormal";

  /**
   * Sets the up before class.
   *
   * @throws Exception the exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    // n/a
  }

  /**
   * Tear down after class.
   *
   * @throws Exception the exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    // n/a
  }

  /**
   * Sets the up.
   *
   * @throws Exception the exception
   */
  @Before
  public void setUp() throws Exception {
    // n/a
  }

  /**
   * Tear down.
   *
   * @throws Exception the exception
   */
  @After
  public void tearDown() throws Exception {
    // n/a
  }

  /**
   * Test.
   */
  @Test
  public void test() {
    URL rulesURL = this.getClass().getResource("/rules.xml");
    List<Rule> rules;
    try {
      rules = RegExMatcher.loadRegexRules(null, rulesURL);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
    List<RuleMatch> matches = RegExMatcher.evaluate(TEST_TEXT, rules);
    Assert.assertNotNull(matches);
    Assert.assertEquals(2, matches.size());
    boolean found1 = false;
    boolean found2 = false;
    for (RuleMatch match : matches) {
      if (!found1) {
        found1 = (0 == match.getStart() && 14 == match.getEnd()
            && "CardioVascular".equals(match.getText())
            && "CardioVascular".equals(match.getRule().getName())
            && "RegEx".equals(match.getRule().getType())
            && "vasc".equals(match.getFeatureMap().get("code"))
            && (match.getCaptureGroupValues() == null
                || match.getCaptureGroupValues().size() == 0));
      }
      if (!found2) {
        found2 = (63 == match.getStart() && 78 == match.getEnd()
            && "ECHOCARDIOGRAM:".equals(match.getText())
            && "CardioVascular".equals(match.getRule().getName())
            && "RegEx".equals(match.getRule().getType())
            && "cardio".equals(match.getFeatureMap().get("code"))
            && (match.getCaptureGroupValues() == null
                || match.getCaptureGroupValues().size() == 0));
      }
    }
    Assert.assertTrue(found1);
    Assert.assertTrue(found2);
  }

}
