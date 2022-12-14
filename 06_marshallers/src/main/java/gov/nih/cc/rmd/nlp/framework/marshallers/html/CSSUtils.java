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
/**
 * 
 */
package gov.nih.cc.rmd.nlp.framework.marshallers.html;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Guy
 *
 */
public class CSSUtils {

  
  // See https://www.vogatek.com/html-tutorials/cssref/css_colornames.asp.html
  private static int colorRotation = 0;
  public static final String[] colorScheme = { 
      
      "{background-color: AliceBlue;    color: black; font-family: verana;  font-size:100%;} \n " ,
   //   "{background-color: AntiqueWhite; color: black; font-family: verana;  font-size:100%;} \n",
      "{background-color: BurlyWood;   color: black; font-family: verana;  font-size:100%;} \n",
      "{background-color: Aqua;         color: black; font-family: verana;  font-size:100%;} \n" ,
      "{background-color: Chocolate;   color: black; font-family: verana;  font-size:100%;} \n" ,
      "{background-color: Aquamarine;   color: black; font-family: verana;  font-size:100%;} \n",
   // "{background-color: Azure;        color: black; font-family: verana;  font-size:100%;} \n" ,
      "{background-color: Beige;        color: black; font-family: verana;  font-size:100%;} \n" ,
   // "{background-color: Bisque;       color: black; font-family: verana;  font-size:100%;} \n",
      "{background-color: Black;        color: white; font-family: verana;  font-size:100%;} \n" ,
   //   "{background-color: BlanchedAlmond; color: black; font-family: verana;  font-size:100%;} \n",
      "{background-color: Blue;        color: yellow; font-family: verana;  font-size:100%;} \n" ,
      "{background-color: Coral;       color: black; font-family: verana;  font-size:100%;} \n",
      "{background-color: Cornsilk;    color: black; font-family: verana;  font-size:100%;} \n",
         "{background-color: BlueViolet;  color: white; font-family: verana;  font-size:100%;} \n",
      "{background-color: Brown;       color: black; font-family: verana;  font-size:100%;} \n" ,
    
      "{background-color: CadetBlue;   color: black; font-family: verana;  font-size:100%;} \n" ,
      "{background-color: Chartreuse;  color: black; font-family: verana;  font-size:100%;} \n",
      "{background-color: Chocolate;   color: black; font-family: verana;  font-size:100%;} \n" ,
      "{background-color: CornflowerBlue; color: black; font-family: verana;  font-size:100%;} \n" 
     };
  
public static final String[] colorScheme2 = { 
      
      "background-color: AliceBlue;   color: black;" ,
      "background-color: BurlyWood;   color: black;",
      "background-color: Aqua;         color: black;" ,
      "background-color: Chocolate;   color: black;" ,
      "background-color: Aquamarine;   color: black;",
      "background-color: Beige;        color: black;" ,
      "background-color: Black;        color: white;" ,
      "background-color: Blue;        color: yellow;" ,
      "background-color: Cornsilk;    color: black; ",
      "background-color: BlueViolet;  color: white;",
      "background-color: Brown;       color: black;" ,
      "background-color: CadetBlue;   color: black;" ,
      "background-color: Chartreuse;  color: black; ",
      "background-color: Chocolate;   color: black;" ,
      "background-color: CornflowerBlue; color: black;",
      "background-color: AliceBlue;   color: black;" ,
      "background-color: BurlyWood;   color: black;",
      "background-color: Aqua;         color: black;" ,
      "background-color: Chocolate;   color: black;" ,
      "background-color: Aquamarine;   color: black;",
      "background-color: Beige;        color: black;" ,
      "background-color: Black;        color: white;" ,
      "background-color: Blue;        color: yellow;" ,
      "background-color: Cornsilk;    color: black; ",
      "background-color: BlueViolet;  color: white;",
      "background-color: Brown;       color: black;" ,
      "background-color: CadetBlue;   color: black;" ,
      "background-color: Chartreuse;  color: black; ",
      "background-color: Chocolate;   color: black;" ,
      "background-color: CornflowerBlue; color: black;" 
     };
  
  
  
  // ==========================================
  /**
   * createCssTypes 
   *
   * @param annotationTypes
   * @return List<String>
   */
  // ==========================================
  public static String createCssTypes(List<String> annotationTypes) {
    
    StringBuffer types = new StringBuffer();
    
   
   
    types.append( labelDefinitions(annotationTypes) );
    types.append( commonTags());
    types.append( layoutAttributes());
    
     
    
    
    int colorSchemePtr = 0;
    for ( String type : annotationTypes ) {
      if ( !types.toString().contains(type + " {")) {
        String cssType = "    " + type + " " + colorScheme[ colorSchemePtr ];
        types.append(cssType);
      
        if ( colorSchemePtr < colorScheme.length -1) colorSchemePtr++; else colorSchemePtr = 0;
      }
    }
    
    // Create the default labels
   
  
    
    return types.toString();
  } // end Method createCssTypes() ============

  // ==========================================
  /**
   * toolTips creates the css tags for the hover over
   *          and annotation attribute data
   *
   * @return String
   */
  // ==========================================
  private static String labelDefinitions( List<String> pLabels) {
    
    StringBuffer types = new StringBuffer();
   
    for ( String aLabel: pLabels ) {
      String aLabelDefinition = labelDefinition( aLabel );
      types.append( aLabelDefinition);
    }
   
    return types.toString();
  } // end Method toolTips() ==================
  

  // ==========================================
  /**
   * labelDefinition creates a good and a bad
   * label definition for a given label.  
   *
   * @param aLabel
   * @return
   */
  // ==========================================
  private static String labelDefinition(String aLabel) {
   
    StringBuffer buff = new StringBuffer();
    
    buff.append(labelDefinition( aLabel, "Good"));
    buff.append(labelDefinition( aLabel, "Bad"));
    
    return buff.toString();
  } // end Method labelDefinition() ===========
  

  // ==========================================
  /**
   * labelDefinition creates a css definition
   *  with the polarity attribute attached.
   *    i.e. if the label is "highlight", and polarity
   *    is "Good" 
   *    this method will create the following
   *    css definitions
   *      highlightGodd
   *      highlightGood hover
   *      highlightGood-top[data-hightlightGood]:hover:after
   *      
   *
   * @param pLabel
   * @param pPolarity
   * @return String
   */
  // ==========================================
  private static String labelDefinition(String pLabel, String pPolarity) {
    
    StringBuffer buff = new StringBuffer();
    String spaces = "     ";
    String  _colorScheme_ = "color: #ff4d4d; font-size:80%;";  // red;
    if (!pPolarity.equals("Bad") ) { 
        _colorScheme_ = colorScheme2[colorRotation] + " font-size:120%;";
        colorRotation++;
        if ( colorRotation > colorScheme.length -1)
          colorRotation = 0;
    
    }
        
    String label = "." + pLabel + pPolarity;
    
    buff.append(spaces +  label + "       { text-decoration: none; " + _colorScheme_ + "\n"); 
    buff.append(spaces + "                       font-size:100%; border: 1px solid black }\n");
    buff.append(spaces + label + ":hover                         { color: black; position: relative; }\n");
    buff.append(spaces + label + "-top[data-" + pLabel + pPolarity + "]:hover:after { content: attr(data-" + pLabel + pPolarity + "); padding: 4px 8px;\n");
    buff.append(spaces + "                                                     position: absolute; left: 0; bottom: 120%;\n");
    buff.append(spaces + "                                                     white-space: wrap; z-index: 10px; background-color: #e6f2ff; font-size:70%; color: black;\n");
    buff.append(spaces + "                                                     font-family: \"Lucida Console\", Monaco, monospace; border: 1px solid black }\n");
  
   
    
    return buff.toString();
  
  }  // end Method labelDefinition() ==========
  

  // ==========================================
  /**
   * commonTags creates the 
   *
   * @return
   */
  // ==========================================
  private static String commonTags() {
 
    StringBuffer types = new StringBuffer();
    types.append("    " + "body" + " " +   "{background-color: White; color: black; font-family: verana;  font-size:100%;} \n");
    types.append("    " + "mark" + " " +   "{background-color: Khaki; color: black; font-family: verana;  font-size:100%;} \n");
    types.append("    " + "h1"   + " " +   "{background-color: Ivory; color: black; font-family: verana;  font-size:100%;} \n");
    types.append("    " + "SearchTerm"   + " " +   "{background-color: Gold; color: black; font-family: verana;  font-size:120%;} \n");
    types.append("    " + "SearchTermNegated"   + " " +   "{background-color: Red; color: black; font-family: verana;  font-size:120%;} \n");
        
    return types.toString();
  } // end Method commonTags() ================
  

  // ==========================================
  /**
   * layoutAttributes 
   *
   * @param annotationTypes
   * @return String
   */
  // ==========================================
  private static String layoutAttributes() {
   
    StringBuffer types = new StringBuffer();
   
    types.append("    " + "/* -- -- -- -- -- -- -- */\n");
    types.append("    " + "/* Layout Attributes    */\n");
    types.append("    " + "/* -- -- -- -- -- -- -- */\n");
    types.append("    " + ".flex-container {\n");
    types.append("    " + "display: -webkit-flex;\n");
    types.append("    " + "display: flex;\n");
    types.append("    " + "-webkit-flex-flow: row nowrap;\n");
    types.append("    " + "flex-flow: row wrap;\n");
    types.append("    " + "text-align: center;\n");
    types.append("    " + "}\n");

    types.append("    " + "/* -- -- -- -- -- -- -- */\n");
    types.append("    " + "/* General Attributes   */\n");
    types.append("    " + "/* -- -- -- -- -- -- -- */\n");
    types.append("    " + ".flex-container > * {\n");
    types.append("    " + "    padding: 1px;\n");
    types.append("    " + "    -webkit-flex: 1 100%;\n");
    types.append("    " + "    flex: 1 100%;\n");
    types.append("    " + "}\n");
    
    types.append("    " + "/* -- -- -- -- -- -- -- */\n");
    types.append("    " + "/* Body Attributes      */\n");
    types.append("    " + "/* -- -- -- -- -- -- -- */\n");
    types.append("    " + ".article {\n");
    types.append("    " + "text-align: left;\n");
    types.append("    " + "}\n");
    
    types.append("    " + "/* -- -- -- -- -- -- -- */\n");
    types.append("    " + "/* Header Attributes    */\n");
    types.append("    " + "/* -- -- -- -- -- -- -- */\n");
    
    types.append("    " + "/* -- -- -- -- -- -- -- */\n");
    types.append("    " + "/* Navigation Attributes*/\n");
    types.append("    " + "/* -- -- -- -- -- -- -- */\n");
    types.append("    " + ".nav {background:#fbefcc;  }\n");
    types.append("    " + ".nav { -webkit-flex: 1; flex: 1; }\n");
    types.append("    " + ".nav ul { list-style-type: none; padding: 0; }\n");
    types.append("    " + ".nav ul a { text-decoration: none; text-align: bottom;  }\n");
    
    types.append("    " + "/* -- -- -- -- -- -- -- */\n");
    types.append("    " + "/* footer Attributes    */\n");
    types.append("    " + "/* -- -- -- -- -- -- -- */\n");
    types.append("    " + "footer {background: #aaa;color:white;}\n");

    types.append("    " + "@media all and (min-width: 768px) {\n") ;
    types.append("    " + "    header   {text-align: center; background: gold; font-size: 100% }\n" );
    types.append("    " + "    .nav     {text-align:left; max-width:220px; -webkit-flex: 1 auto;flex:1 auto;-webkit-order:1;order:1; font-size:90%}\n" );
    types.append("    " + "    .article {-webkit-flex:5 0px;flex:5 0px;-webkit-order:2;order:2; webkit-flex-wrap: nowrap; flex-wrap: nowrap; }\n" );
    types.append("    " + "    footer   {-webkit-order:3;order:3;}\n" );
    types.append("    " + "}\n");
    
    return types.toString();
  }  // end Method layoutAttributes() ========================================
  
  

}
