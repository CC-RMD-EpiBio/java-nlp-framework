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
 * 
 * @author divita
 * @created Sept 16, 2014
 * 
 *          *
 *          *
 *          *
 *          *
 *          *
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.utils.nGrams;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.U;

/*
import att.grappa.Graph;
import att.grappa.Grappa;
import att.grappa.GrappaAdapter;
import att.grappa.GrappaPanel;
import att.grappa.GrappaSupport;
import att.grappa.Parser;
*/

public class Nora1 {

  // =======================================================
  /**
   * main
   * 
   * 
   */
  // =======================================================
  public static void main(String[] args) {

    

    Nora1 nora = new Nora1();
    
    System.err.println("--------------------------");
    // --------------------
    // Loop 
    //  +- Input a seed term
    //  |   generate a graph
    //  +---  display the graph
    // -------------------
    BufferedReader in = null;
    try {
 
      in =new BufferedReader(new InputStreamReader(System.in));
      String seedTerm = null;
      System.err.print( "Input a seed token : ");
      while ( (seedTerm = in.readLine() ) != null && seedTerm.trim().length() > 0 ) {
        String graph = nora.getGraph( seedTerm );
        nora.displayGraph( graph );
        
        System.err.print( "Input a seed token : ");  
      }
    
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue retriving the node " + e.getMessage();
      System.err.println(msg);
    }
 
    
    
    
    
 
  } // End Method main() ======================

  
  // =======================================================
  /**
   * Constructor
   *    reads in the bi-gram table
   */
  // =======================================================
  public Nora1() {
  
    String pFileName = "/data/vhaslcdivitg/data/ORD_Gundlapali_201302027/ngrams/output/2014-04-11_14_54_44/cumulative/gramFile_11_gramType_2.txt";

    String[] gramArray = null;
    try {
      gramArray = U.readClassPathResourceIntoStringArray("resources/com/ciitizen/framework/biGrams/bigrams15.txt");
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println( "Issue with reading in the gram file " + e.getMessage() );
     
    }
    BufferedReader in = null;
    
    String line = null;
    this.gramIndex = new HashMap<String,GramThing>();
    try {
     
      if ( gramArray == null ) { System.err.println("Empty gram array "); return ; }
      for (  int i = 0; i < gramArray.length; i++ ) {
        line = gramArray[i];
        if ( !line.startsWith( "#") && !line.trim().startsWith("Totals")) {
          if ( line.trim().length() > 0 ) {
            
            BiGram biGram = new BiGram( line);
           
            GramThing gramThing = this.gramIndex.get( biGram.getLeftGram() );
          
            // --------------------------------
            // Index the leftGram -> rightGram 
            //           --------
            //            key
            if ( gramThing == null ) {
              gramThing = new GramThing( biGram.getLeftGram());
              gramThing.tuples = new ArrayList<GraphTuple>();
              
              GraphTuple tuple= new GraphTuple( biGram.getRightGram(),biGram.getFrequency(), ">");
              
              gramThing.tuples.add( tuple );
              System.err.println( biGram.getLeftGram() + "|" + tuple.toString());
              this.gramIndex.put(biGram.getLeftGram(), gramThing);
            } else {
              // ----------------------------------------------------------
              // Check to see if this tuple is already on the set of tuples
              GraphTuple tuple = new GraphTuple( biGram.getRightGram(),  biGram.getFrequency(), ">");
             
              System.err.println( biGram.getLeftGram() + "|" + tuple);
              gramThing.addUniq(tuple);
            }
            
            

            // --------------------------------
            // Index the leftGram -> rightGram           ==   rightGram <- leftGram  
            //                        --------                ----------
            //                           key                    key
            gramThing = this.gramIndex.get(biGram.getRightGram());
            if ( gramThing == null ) {
              gramThing = new GramThing(biGram.getRightGram());
              gramThing.tuples = new ArrayList<GraphTuple>();
              GraphTuple tuple= new GraphTuple( biGram.getLeftGram(),  biGram.getFrequency().trim() ,"<");
              gramThing.addUniq( tuple);
              System.err.println( biGram.getRightGram() + "|" + tuple.toString());
              this.gramIndex.put(biGram.getRightGram(), gramThing);
            } else {
              GraphTuple tuple = new GraphTuple( biGram.getLeftGram(), biGram.getFrequency(),  "<");
              gramThing.addUniq( tuple);
              System.err.println( biGram.getRightGram() + "|" + tuple.toString() );
             
            }

          }
        }
      }
     
      System.err.println("Finished load ");
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(line + "|" );
      System.err.println(" issue " + e.getMessage());
      throw new RuntimeException();
    }
  
  
  } // end Constructor() ----------------------------------


  // =======================================================
  /**
   * getGraph
   * 
   * @param line
   */
  // =======================================================
  private String getGraph(String pSeedTerm) {
   
    String dotGraph = null;
    
   GramThing thing = this.gramIndex.get( pSeedTerm);
   
   if ( thing != null ) {
     
     List<GramThing> things = new ArrayList<GramThing>();
     things.addAll( addOneArcToLeft( thing ) );
     
    // things.add( addOneArcToRight( thing ) );
     
    // pruneGraph( things );
     
     
     dotGraph = makeDotGraph( things , pSeedTerm );
     
   }
    
   return dotGraph;
  } // End Method getGraph() ======================
  


  // =======================================================
  /**
   * addOneArcToLeft finds all the grams for the left side grams
   * 
   * @param thing
   * @return GramThing
   */
  // =======================================================
  private List<GramThing> addOneArcToLeft(GramThing pThing) {
    
    ArrayList<GraphTuple> leftTuples = new ArrayList<GraphTuple>();
    ArrayList<GraphTuple> rightTuples = new ArrayList<GraphTuple>();
    ArrayList<GramThing> gramThings = new ArrayList<GramThing>();
    List<GraphTuple> allTuples = pThing.getTuples();
    for ( GraphTuple tuple : allTuples) {
      if ( tuple.getDirection().contains ("<")  )
        leftTuples.add( tuple);
      else
        rightTuples.add( tuple);
    }
    
    if ( leftTuples != null && leftTuples.size() > 0 ) {
      for ( GraphTuple tuple : leftTuples ) {
        String oneOffKey = tuple.getKey();
        GramThing aGramThing = new GramThing(oneOffKey);
        aGramThing.setTuples(new ArrayList<GraphTuple>());
        GramThing leftThing = this.gramIndex.get( tuple.getKey());
        if ( leftThing != null) {
          List<GraphTuple> leftLeftTuples = leftThing.getTuples();
          for ( GraphTuple dtuple: leftLeftTuples) {
            if ( dtuple.getKey().equals(oneOffKey) && dtuple.getDirection().contains("<") )
                aGramThing.addUniq(dtuple);
          } // end loop through leftLeftTuples
        } // end if there is a leftLeft thing
        gramThings.add(aGramThing);  
      } // end loop thru left tuples
    } // end if there is a left thing
    
    
    return gramThings;
    
  }// End Method addOneArcToLeft() ======================
  


  // =======================================================
  /**
   * makeGraph
   * 
   * @param pGramThings
   * @param pSeedTerm
   * 
   * @return String
   */
  // =======================================================
  private String makeDotGraph(List<GramThing> pGramThings, String pSeedTerm) {
    
    
    GramGraph graph = new GramGraph();
    // ---------------------------
    // Define the graph metadata
    graph.setGraphId( "Graph_1");
    graph.setGraphFontName ( "Helvetica-Oblique");
    graph.setGraphFontSize(36);
    graph.setGraphNodesep("1.25");
    graph.setGraphLabel("Seed Term = " + pSeedTerm);
    graph.setGraphSize( "11,17");
    graph.setGraphRatio("auto");
    graph.setPageSize("11,17");
   
    // --------------------------
    // Define the node attributes
    
    graph.setDefaultNodeStyle( "filled");
    graph.setDefaultNodeFontSize( 24);
    graph.setDefaultNodeFontName("Helvetica-Outline");
    
    // --------------------------
    // Define the nodes
    
    for ( GramThing thing : pGramThings ) {
    
      String key = thing.getKey();
    
      graph.addFocusNode( key );
      for ( GraphTuple tuple : thing.getTuples() ) {
      
        // -----------------------------
        // Define the node
        graph.addNode( tuple.getKey() );
        
        
        // --------------------------
        // Define the arc
        graph.addArc( key, tuple.getKey(), tuple.getDirection(), tuple.getFrequency());
        
      }
   
    }
      String dotGraph = graph.renderIntoDot();
    
      return dotGraph;
      
  } // End Method makeGraph() ======================
  


  


  // =======================================================
  /**
   * displayGraph displays a graph 
   * 
   * @param pGraph
   */
  // =======================================================
  private void displayGraph(String pGraphString ) {
 
    // -------------------
    //  covert the String into a tmp file
    // ------------------
    File tempDotFile = null;
    try {
      tempDotFile = File.createTempFile("dotGraph", ".dot");
      System.err.println("The output file = " + tempDotFile.getAbsolutePath());
      PrintWriter out = new PrintWriter( tempDotFile);
      out.println(pGraphString);
      out.close();
    } catch (Exception e) { 
      e.printStackTrace();
      System.err.println("Issue with writing out the dot file " + e.toString());
    }
    // -------------------
    // Kick off the "dotty.exe" command
    try {
      Process p = Runtime.getRuntime().exec( DottyExePath + " " + tempDotFile.getAbsolutePath());
      BufferedReader dottyError = new BufferedReader( new InputStreamReader( p.getErrorStream()) );
     
      String line = null; 
      while ( ( line = dottyError.readLine() )!= null ) {
        System.err.println(line);
      }
      dottyError.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue kicking off dotty " + e.toString());
    }
   
  
  } // End Method displayGraph2() ======================
  


  

  
  // ------------------------------------
  // Global Private Variables
  // -----------------------------------

  private HashMap<String, GramThing> gramIndex =  null;
  private static final String DottyExePath = "/opt/graphViz/bin/dotty.exe";
  
} // end Class Test1
