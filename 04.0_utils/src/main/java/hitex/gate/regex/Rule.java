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
package hitex.gate.regex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hitex.gate.regex.util.RuleValidationException;

/**
 * The Class Rule.
 */
public class Rule implements java.io.Serializable {

  // ================
  // = Data Members =
  // ================

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -75252620960870111L;

  /** The capt group num. */
  private Integer captGroupNum;

  /** The pattern. */
  private Pattern pattern;

  /** The type. */
  private String type;

  /** The name. */
  private String name;

  /** The features. */
  private Hashtable<String, Object> features;

  // ================
  // = Constructors =
  // ================

  /**
   * Instantiates a {@link Rule} from the specified parameters.
   *
   * @param pattern the pattern
   * @param captGroupNum the capt group num
   * @param type the type
   * @param name the name
   */
  public Rule(String pattern, Integer captGroupNum, String type, String name) {
    features = new Hashtable<String, Object>();
    setPattern(pattern);
    setCaptGroupNum(captGroupNum);
    setType(type);
    setName(name);
  }

  /**
   * Instantiates a {@link Rule} from the specified parameters.
   *
   * @param pattern the pattern
   * @param captGroupNum the capt group num
   * @param type the type
   */
  public Rule(String pattern, Integer captGroupNum, String type) {
    this(pattern, captGroupNum, type, null);
  }

  /**
   * Instantiates a {@link Rule} from the specified parameters.
   *
   * @param pattern the pattern
   * @param captGroupNum the capt group num
   */
  public Rule(String pattern, Integer captGroupNum) {
    this(pattern, captGroupNum, null, null);
  }

  /**
   * Instantiates a {@link Rule} from the specified parameters.
   *
   * @param pattern the pattern
   */
  public Rule(String pattern) {
    this(pattern, 0, null, null);
  }

  /**
   * Instantiates an empty {@link Rule}.
   */
  public Rule() {
    this(null, 0, null, null);
  }

  // ===========
  // = Methods =
  // ===========

  /**
   * Sets the pattern.
   *
   * @param p the pattern
   */
  public synchronized void setPattern(String p) {
    if (p != null)
      pattern = Pattern.compile(p);
    else
      pattern = null;
  }

  /**
   * Returns the pattern.
   *
   * @return the pattern
   */
  public synchronized String getPattern() {
    if (pattern != null)
      return pattern.pattern();
    else
      return null;
  }

  /**
   * Sets the compiled pattern.
   *
   * @param p the compiled pattern
   */
  public synchronized void setCompiledPattern(Pattern p) {
    pattern = p;
  }

  /**
   * Returns the compiled pattern.
   *
   * @return the compiled pattern
   */
  public synchronized Pattern getCompiledPattern() {
    return pattern;
  }

  /**
   * Sets the capt group num.
   *
   * @param num the capt group num
   */
  public synchronized void setCaptGroupNum(Integer num) {
    if (num == null || num < 0)
      captGroupNum = 0;
    else
      captGroupNum = num;
  }

  /**
   * Returns the capt group num.
   *
   * @return the capt group num
   */
  public synchronized Integer getCaptGroupNum() {
    return captGroupNum;
  }

  /**
   * Sets the type.
   *
   * @param t the type
   */
  public synchronized void setType(String t) {
    type = t;
  }

  /**
   * Returns the type.
   *
   * @return the type
   */
  public synchronized String getType() {
    return type;
  }

  /**
   * Sets the name.
   *
   * @param n the name
   */
  public synchronized void setName(String n) {
    name = n;
  }

  /**
   * Returns the name.
   *
   * @return the name
   */
  public synchronized String getName() {
    return name;
  }

  /**
   * Returns the feature names.
   *
   * @return the feature names
   */
  public Collection<String> getFeatureNames() {
    return features.keySet();
  }

  /**
   * Returns the feature value.
   *
   * @param key the key
   * @return the feature value
   */
  public Object getFeatureValue(String key) {
    if (key != null)
      return features.get(key);
    else
      return null;
  }

  /**
   * Reset features.
   */
  public synchronized void resetFeatures() {
    if (features != null) {
      features.clear();
    }
  }

  /**
   * Adds the feature.
   *
   * @param key the key
   * @param value the value
   */
  public synchronized void addFeature(String key, Object value) {
    if (key != null && value != null) {
      features.put(key, value);
    }
  }

  /**
   * Validate.
   *
   * @throws RuleValidationException the rule validation exception
   */
  public synchronized void validate() throws RuleValidationException {
    StringBuilder errors = new StringBuilder();
    if (pattern == null) {
      errors.append("Pattern '" + pattern + "' is not valid. ");
    }
    if (captGroupNum == null || captGroupNum < 0) {
      errors.append("Capturing group number '" + captGroupNum + "' is not valid. ");
    }
    if (pattern != null && captGroupNum != null && captGroupNum >= 0) {
      Matcher m = pattern.matcher(""); // test sequence
      if (m.groupCount() < captGroupNum) {
        errors.append("Capturing group number '" + captGroupNum
            + "' is too big, the maximum # for this rule's pattern is '" + m.groupCount() + "'. ");
      }
    }
    if (type == null || type.trim().length() == 0) {
      errors.append("Concept type '" + type + "' is not valid. ");
    }
    if (name == null || name.trim().length() == 0) {
      errors.append("Concept name '" + name + "' is not valid. ");
    }
    if (features == null) {
      errors.append("Features hash table is null. ");
    }
    if (errors.length() != 0) {
      throw new RuleValidationException(errors.toString());
    }
  }

  /**
   * Indicates whether or not valid is the case.
   *
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  public synchronized boolean isValid() {
    try {
      validate();
    } catch (RuleValidationException e) {
      return false;
    }
    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    StringBuilder output = new StringBuilder();
    String newline = System.getProperty("line.separator");
    if (pattern == null) {
      output.append("Pattern: " + pattern + newline);
    } else {
      output.append("Pattern: " + pattern.pattern() + newline);
    }

    output.append("Capturing group number: " + captGroupNum + newline);
    output.append("Concept type: " + type + newline);
    output.append("Concept name: " + name + newline);

    if (features != null && !features.isEmpty()) {
      output.append("Features: " + newline);
      List<String> keys = new ArrayList<String>(features.keySet());
      Collections.sort(keys);
      for (String key : keys) {
        output.append("\t" + key + "=" + features.get(key) + newline);
      }
    }

    try {
      validate();
      output.append("The rule is valid. " + newline);
    } catch (RuleValidationException e) {
      output.append("The rule is NOT valid: " + e.getMessage() + newline);
    }

    return output.toString();
  }

  /**
   * Application entry point.
   *
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    System.out.println("Testing Rule class...");

    Rule rule1 = new Rule();
    System.out.println(rule1);

    Rule rule2 = new Rule("[a-zA-Z0-9]+", 0, "RegEx", "test concept");
    rule2.addFeature("feature1", "value1");
    rule2.addFeature("feature2", "value2");
    rule2.addFeature("feature3", 123);
    System.out.println(rule2);

    System.out.println("Done testing!");
  }

}

