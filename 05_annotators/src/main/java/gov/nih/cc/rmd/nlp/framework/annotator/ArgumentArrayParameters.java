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
package gov.nih.cc.rmd.nlp.framework.annotator;
// =======================================================
/**
 * ArgumentArrayParameters is a container to hold argument parameters
 *
 * @author  guy
 * @created Jan 19, 2014
 *
   
 */
// =======================================================

/**
 * @author guy
 *
 */
public class ArgumentArrayParameters {

  private Object argument1 = null;
  private Object argument2 = null;
  private Object argument3 = null;
  private Object argument4 = null;
  

  // =======================================================
  /**
   * Constructor ArgumentArrayParameters 
   *
   * @param conceptText
   * @param sentenceText
   * @param aConcept
   */
  // =======================================================
  public ArgumentArrayParameters(Object pArg1, Object pArg2, Object pArg3) {
    this.argument1 = pArg1;
    this.argument2 = pArg2;
    this.argument3 = pArg3;
  } // end Constructor

  /**
   * @return the argument1
   */
  public Object getArgument1() {
    return argument1;
  }

  /**
   * @param argument1 the argument1 to set
   */
  public void setArgument1(Object argument1) {
    this.argument1 = argument1;
  }

  /**
   * @return the argument2
   */
  public Object getArgument2() {
    return argument2;
  }

  /**
   * @param argument2 the argument2 to set
   */
  public void setArgument2(Object argument2) {
    this.argument2 = argument2;
  }

  /**
   * @return the argument3
   */
  public Object getArgument3() {
    return argument3;
  }

  /**
   * @param argument3 the argument3 to set
   */
  public void setArgument3(Object argument3) {
    this.argument3 = argument3;
  }

  /**
   * @return the argument4
   */
  public Object getArgument4() {
    return this.argument4;
    
    // End Method getArgument4() ======================
  }
  
  /**
   * @param argument4 the argument4 to set
   */
  public void setArgument4(Object argument4) {
    this.argument4 = argument4;
  }
  
} // end Class ArgumentArrayParameters
