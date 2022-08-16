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
// =======================================================
/**
 * GramGraph is a container to hold a directed graph of bi-grams to be rendered in the
 * graphViz notation.
 *
 * @author  guy
 * @created Sep 17, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.utils.nGrams;

import java.util.ArrayList;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * @author guy
 *
 */
public class GramGraph {

  // =======================================================
  /**
   * setGraphId sets the graph id
   * 
   * @param pVal
   */
  // =======================================================
  public void setGraphId(String pVal) {
    this.graphID = pVal;
  } // End Method setGraphFontName() ======================
  
  // =======================================================
  /**
   * setGraphFontName sets the graph title font name
   * 
   * @param pFontName
   */
  // =======================================================
  public void setGraphFontName(String pFontName) {
    this.graphFontName = pFontName;
  } // End Method setGraphFontName() ======================
  

  // =======================================================
  /**
   * setGraphFontSize sets the graph title's font size
   * 
   * @param pFontSize
   */
  // =======================================================
  public void setGraphFontSize(int pFontSize) {
    this.graphFontSize = String.valueOf(pFontSize);
  } // End Method setGraphFontSize() ======================
  

  // =======================================================
  /**
   * setGraphSize sets the window size of the graph
   *    in the form of "x,y" in inches
   * @param pVal
   */
  // =======================================================
  public void setGraphSize(String pVal) {
    this.graphSize = pVal;
  }  // End Method setGraphSize() ======================
  

  // =======================================================
  /**
   * setGraphLabel sets the title of the graph
   * 
   * @param pTitle
   */
  // =======================================================
  public void setGraphLabel(String pTitle) {
    this.graphLabel = pTitle;
  } // End Method setGraphLabel() ======================
  

  // =======================================================
  /**
   * setGraphNodesep sets the default length between nodes;
   * 
   * @param pVal
   */
  // =======================================================
  public void setGraphNodesep(String pVal) {
    this.graphNodesep = pVal;
    // End Method setGraphNodesep() ======================
  }

  // =======================================================
  /**
   * setGraphRatio [Summary here]
   * 
   * @param pVal 
   */
  // =======================================================
  public void setGraphRatio(String pVal) {
    this.graphRatio = pVal;
    // End Method setGraphRatio() ======================
  }

  // =======================================================
  /**
   * setPageSize 
   * 
   * @param string
   */
  // =======================================================
  public void setPageSize(String pVal) {
    this.graphPageSize = pVal;
    // End Method setPageSize() ======================
  }

  // =======================================================
  /**
   * setDefaultNodeStyle
   * 
   * @param pVal
   */
  // =======================================================
  public void setDefaultNodeStyle(String pVal) {
    this.defaultNodeStyle = pVal;
    // End Method setNodeStyle() ======================
  }

  // =======================================================
  /**
   * setDefaultNodeFontSize [Summary here]
   * 
   * @param pVal
   */
  // =======================================================
  public void setDefaultNodeFontSize(int pVal) {
    this.defaultNodeFontSize = String.valueOf(pVal);
  } // End Method setNodeFontSize() ======================
  

  // =======================================================
  /**
   * setDefaultNodeFontName sets the default node font name
   * 
   * @param pVal
   */
  // =======================================================
  public void setDefaultNodeFontName(String pVal) {
    this.defaultNodeFontName = pVal;
  } // End Method setdefaultNodeFontName() ======================
  

  // =======================================================
  /**
   * addFocusNode
   * 
   * @param pKey
   */
  // =======================================================
  public void addFocusNode(String pKey) {
   this.focusNode = pKey;
  }// End Method addFocusNode() ======================
  

  // =======================================================
  /**
   * addNode 
   * 
   * @param pKey
   */
  // =======================================================
  public void addNode(String pKey) {
   if ( this.nodes == null )
     this.nodes = new ArrayList<String>();
   
   this.nodes.add( pKey);
  }// End Method addNode() ======================
  

  // =======================================================
  /**
   * addArc adds an arc object 
   *    Even though we could make the direction one way or
   *    the other.  In this method, we always will calculate
   *    the direction to be node1 -> node2
   * 
   * @param pNode1
   * @param pNode2
   * @param pDirection
   * @param pFrequency
   */
  // =======================================================
  public void addArc(String pNode1, String pNode2, String pDirection, int pFrequency) {
    if ( this.arcs == null )
      this.arcs = new ArrayList<GraphArc>();
    
    GraphArc arc = null;
    if ( pDirection.contains("<") )
      arc = new GraphArc( pNode2, pNode1, "->", pFrequency);
    else
      arc = new GraphArc( pNode1, pNode2, "->", pFrequency);
    
    this.arcs.add( arc);
    
    // End Method addArc() ======================
  }

  // =======================================================
  /**
   * renderIntoDot
   * 
   * @return String
   */
  // =======================================================
  public String renderIntoDot() {
   
    StringBuffer dotGraph = new StringBuffer();
    
    
    // ------------------------------
    // Graph properties
    dotGraph.append("digraph " +  U.quote(this.graphID) + " {\n");
    dotGraph.append("  graph  [\n");
    dotGraph.append("           fontname = " + U.quote(this.graphFontName) + ",\n");
    dotGraph.append("           fontsize = " +         this.graphFontSize  + ",\n");
    dotGraph.append("           label = " + U.quote(this.graphLabel)       + ",\n");
    dotGraph.append("           ratio = " +            this.graphRatio     + ",\n");
    dotGraph.append("           rankdir = LR ,\n");
    dotGraph.append("           page = "  +         U.quote(this.graphPageSize) + "\n");
    dotGraph.append("         ]\n");
    
    
    // ------------------------------
    // node properties
    dotGraph.append("  node   [\n");
    dotGraph.append("            style = filled,\n");
    dotGraph.append("            fontsize = " + this.defaultNodeFontSize + " ,\n");
    dotGraph.append("            fontNaname = " + U.quote(this.defaultNodeFontName) + "\n");
    dotGraph.append("         ]\n");
    
    // --------------------------
    // Nodes 
    
    // -------------------------
    //  center of focus node
    dotGraph.append("  " + U.quote(this.focusNode)  +"    [ shape=box, color=" + FocusNodeColor + "]\n"  );
    
    
    for (String node : this.nodes ) {
      if ( node == null ) continue;
      dotGraph.append("  " + U.quote(node)  + " [ color=" + calculateColor() + "]\n"  );
    }
    
    // ----------------------
    // Arcs
    
    // ----------------------
    // Calculate the relative arc size
    calculateStrength( this.arcs);
    
    for ( GraphArc arc : this.arcs )
      dotGraph.append("   " + U.quote(arc.getNode1()) +  " " + arc.getDirection() + " " + U.quote(arc.getNode2()) + "[penwidth=" + arc.getScaledStrength() + "]\n");
      
    
    dotGraph.append(" }\n");
    
    
    return dotGraph.toString();
    // End Method renderIntoDot() ======================
  }
  
  // =======================================================
  /**
   * calculateStrength calculates the relative strength of the
   * arcs between the nodes 
   * 
   * @param arcs
   */
  // =======================================================
  private void calculateStrength(List<GraphArc> pArcs) {
    
    int maxFreq = 0;
    int minFreq = 999999;
    for ( GraphArc arc : pArcs ) { 
      if ( maxFreq < arc.getStrength() )
          maxFreq = arc.getStrength();
      if ( minFreq > arc.getStrength())
        minFreq = arc.getStrength();
    }
    
    for ( GraphArc arc : pArcs ) 
      arc.setScaledStrength( scaleFreq( maxFreq, minFreq, arc.getStrength()) );
  } // End Method calculateStrength() ======================
  

  // =======================================================
  /**
   * scaleFreq scales the frequency from 1 - 10 from
   * a range of 1 to maxFreq
   * 
   * @param pMaxFreq
   * @paramm pMinFreq
   * @param pStrength
   * @return int
   */
  // =======================================================
  private int scaleFreq(int pMaxFreq, int pMinFreq, int pStrength) {
     int val = 1;
     
     val = LowRange + ( pStrength - pMinFreq ) * ( HighRange -LowRange) / ( pMaxFreq - 1);
     
    return val;
    // End Method scaleFreq() ======================
  }

  // =======================================================
  /**
   * calculateColor retrieves one of 5 colors in a round robin fashion
   * 
   * @return string
   */
  // =======================================================
  private String calculateColor() {
    currentColor++;
    return colors[currentColor % 4 ];
    // End Method calculateColor() ======================
  }

  // ====================================================
  // Global variables
  private String               graphID = null;
  private String            graphLabel = null;
  private String         graphFontSize = null;
  private String         graphFontName = null;
  private String             graphSize = null;
  private String          graphNodesep = null;
  private String            graphRatio = "auto";
  private String            graphPageSize = "8.5,11" ;
  private String      defaultNodeStyle = null;
  private String      defaultNodeLabel = null;
  private String   defaultNodeFontSize = null;
  private String   defaultNodeFontName = null;
  private List<String>           nodes = null;
  private String             focusNode = null;
  private List<GraphArc>          arcs = null;
  private static String FocusNodeColor = "lightpink2";
  private int             currentColor = 1;
  private String[]              colors = {"lavenderblush2", "salmon2", "tan2", "palegoldenrod"};
  private static int          LowRange = 1;
  private static int         HighRange = 10;
  
  

}
