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
