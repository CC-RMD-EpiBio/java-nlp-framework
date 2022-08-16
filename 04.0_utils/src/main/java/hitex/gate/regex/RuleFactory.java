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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import hitex.gate.regex.util.RuleLoadingException;
import hitex.gate.regex.util.RuleValidationException;

/**
 * A factory for creating Rule objects.
 */
public class RuleFactory {

  // ================
  // = Data Members =
  // ================

  /** The Constant VALUE. */
  private static final String VALUE = "value";

  /** The Constant FEATURE. */
  private static final String FEATURE = "feature";

  /** The Constant FEATURES. */
  private static final String FEATURES = "features";

  /** The Constant NAME. */
  private static final String NAME = "name";

  /** The Constant TYPE. */
  private static final String TYPE = "type";

  /** The Constant CAPT_GROUP_NUM. */
  private static final String CAPT_GROUP_NUM = "capt_group_num";

  /** The Constant DEF. */
  private static final String DEF = "def";

  /** The Constant CONCEPT. */
  private static final String CONCEPT = "concept";

  /** The Constant THIS_CLASS_NAME. */
  private static final String THIS_CLASS_NAME = RuleFactory.class.getName();

  // ===========
  // = Methods =
  // ===========

  /**
   * Read.
   *
   * @param xml the xml
   * @return the list
   * @throws RuleLoadingException the rule loading exception
   * @throws RuleValidationException the rule validation exception
   */
  public static synchronized List<Rule> read(String xml)
    throws RuleLoadingException, RuleValidationException {

    if (xml == null) {
      throw new RuleLoadingException("The XML string representing the rules is null. ");
    }

    // convert XML string into list of rules
    Document doc = null;
    SAXBuilder builder = new SAXBuilder();
    try (final Reader reader = new StringReader(xml);) {
      doc = builder.build(reader);
    } catch (JDOMException e) {
      throw new RuleLoadingException(e.getMessage(), e);
    } catch (IOException e) {
      throw new RuleLoadingException(e.getMessage(), e);
    }

    return readXMLRules(doc);
  }

  /**
   * Read.
   *
   * @param url the url
   * @return the list
   * @throws RuleLoadingException the rule loading exception
   * @throws RuleValidationException the rule validation exception
   */
  public static synchronized List<Rule> read(URL url)
    throws RuleLoadingException, RuleValidationException {

    if (url == null) {
      throw new RuleLoadingException("The URL of XML rules file is invalid: '" + url + "'");
    }

    // convert rules into the JDOM document
    Document doc = null;
    SAXBuilder builder = new SAXBuilder();
    try (final InputStream is = new FileInputStream(url.getFile())) {
      doc = builder.build(is);

    } catch (FileNotFoundException e) {
      throw new RuleLoadingException(e.getMessage(), e);
    } catch (JDOMException e) {
      throw new RuleLoadingException(e.getMessage(), e);
    } catch (IOException e) {
      throw new RuleLoadingException(e.getMessage(), e);
    }

    return

    readXMLRules(doc);
  }

  /**
   * Read XML rules.
   *
   * @param doc the doc
   * @return the list
   * @throws RuleLoadingException the rule loading exception
   * @throws RuleValidationException the rule validation exception
   */
  @SuppressWarnings("unchecked")
  public static synchronized List<Rule> readXMLRules(Document doc)
    throws RuleLoadingException, RuleValidationException {

    // extract rules from the JDOM document
    if (doc == null) {
      throw new RuleLoadingException("Rules XML DOM is null.");
    }

    Element root = doc.getRootElement();

    if (root == null) {
      throw new RuleLoadingException("Cannot find rules XML JDOM root.");
    }

    // Create a list to hold the rules
    List<Rule> rules = new ArrayList<Rule>();

    // Get the list of nodes containing rules.
    List<Element> conceptNodes = root.getChildren(CONCEPT);

    if (conceptNodes == null || conceptNodes.size() == 0) {
      Logger.getLogger(THIS_CLASS_NAME).warning("No rules were discovered in the XML document.");
      return rules;
    }

    for (Element conceptNode : conceptNodes) {
      Rule rule = getRule(conceptNode);
      rule.validate();
      rules.add(rule);
    }

    return rules;
  }

  /**
   * Write.
   *
   * @param rules the rules
   * @param file the file
   * @throws ParserConfigurationException the parser configuration exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void write(Collection<Rule> rules, File file)
    throws ParserConfigurationException, IOException {
    Element concepts = new Element("concepts");
    Document doc = new Document(concepts);
    doc.setRootElement(concepts);

    for (Rule rule : rules) {
      Element concept = new Element(CONCEPT);
      concept.addContent(new Element(DEF).setText(rule.getPattern()));
      concept.addContent(new Element(CAPT_GROUP_NUM).setText("" + rule.getCaptGroupNum()));
      concept.addContent(new Element(TYPE).setText(rule.getType()));
      concept.addContent(new Element(NAME).setText(rule.getName()));

      Element features = new Element(FEATURES);
      for (String featureName : rule.getFeatureNames()) {
        Element feature = new Element(FEATURE);
        feature.addContent(new Element(NAME).setText(featureName));
        feature.addContent(new Element(VALUE).setText((String) rule.getFeatureValue(featureName)));
        features.addContent(feature);
      }
      concept.addContent(features);
      concepts.addContent(concept);
    }
    XMLOutputter xmlOut = new XMLOutputter();
    xmlOut.setFormat(Format.getPrettyFormat());
    try (final FileWriter fw = new FileWriter(file)) {
      xmlOut.output(doc, fw);
    }
  }

  /**
   * Returns the rule.
   *
   * @param concept the concept
   * @return the rule
   * @throws RuleLoadingException the rule loading exception
   * @throws RuleValidationException the rule validation exception
   */
  @SuppressWarnings("unchecked")
  private static Rule getRule(Element concept)
    throws RuleLoadingException, RuleValidationException {

    Rule rule = new Rule();

    if (concept == null) {
      throw new RuleLoadingException("Concept node is null.");
    }

    Element def = concept.getChild(DEF);
    Element capt_group_num = concept.getChild(CAPT_GROUP_NUM);
    Element type = concept.getChild(TYPE);
    Element name = concept.getChild(NAME);

    StringBuilder errors = new StringBuilder();
    if (def == null) {
      errors.append("Concept definition element (def) is missing in the XML rules file. ");
    }
    if (capt_group_num == null) {
      errors.append(
          "Concept's capturing group number (capt_group_num) element is missing in the XML rules file. ");
    }
    if (type == null) {
      errors.append("Concept's type element (type) is missing in the XML rules file. ");
    }
    if (name == null) {
      errors.append("Concept's name element (name) is missing in the XML rules file. ");
    }

    String n = (capt_group_num == null ? null : capt_group_num.getText().trim());
    Integer num = null;
    try {
      num = Integer.parseInt(n);
    } catch (NumberFormatException e) {
      errors.append("Concept's capturing group number '" + n + "' is not an integer. ");
    }

    if (errors.length() != 0) {
      throw new RuleLoadingException("Unable to extract rule from XML file: " + errors.toString());
    }

    if (def != null)
      rule.setPattern(def.getText().trim());
    rule.setCaptGroupNum(num);
    if (type != null)
      rule.setType(type.getText().trim());
    if (name != null)
      rule.setName(name.getText().trim());

    // features is optional
    Element _features = concept.getChild(FEATURES);
    if (_features != null) {
      List<Element> features = _features.getChildren(FEATURE);
      for (Element feature : features) {
        Element featureName = feature.getChild(NAME);
        Element featureValue = feature.getChild(VALUE);
        if (featureName != null && featureValue != null) {
          rule.addFeature(featureName.getText().trim(), featureValue.getText().trim());
        }
      }
    }

    return rule;
  }

}

