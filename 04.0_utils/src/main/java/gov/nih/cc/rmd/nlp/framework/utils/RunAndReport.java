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
 * RunAndReport runs a scale-out pipeline application 
 * with a specific configuration, and report back the 
 * average time per record it took
 *
 * @author  Divita
 * @created Mar 27, 2015
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.utils;

/**
 * @author Divita
 *
 */
public class RunAndReport {

  // =======================================================
  /**
   * run kicks off an external process with 
   * 
   *   This method assumes that the main from the pJarFile will include data on standard out
   *   and that data will end with a pipe surrounded string with |avgMSPerRecord|   
   *   
   *   
   * @param pMemory
   * @param pNumberOfPipelines
   * @param pJarFile
   * @param pInputOptions
   * @return double   The average milliseconds (ms) it takes to process a record 
   */
  // =======================================================
  public double run(double pMemory, 
                       int pNumberOfPipelines, 
                       String pJarFile, 
                       String pInputOptions, 
                       int pContextNumberOfThreads, 
                       int pNumberToProcess) {
    
    double averageMsPerRecord = 0.0;
    
    // convert gb to mb and only keep the integer portion
    int mbMemory = (int) pMemory * 1000 ;
    System.err.println(U.run("which java"));
    String command = "java -Xms" + mbMemory + "m " + 
                          "-Xmx16g -jar " + 
                           pJarFile + " " + 
                           pInputOptions + " " + 
                           "--numberOfApplications="   + pNumberOfPipelines + " " + 
                           "--contextNumberOfThreads=" + pContextNumberOfThreads + " " +
                           "--numberToProcess="        + pNumberToProcess;
    
    System.err.println("About to kick off the command \n" + command + '\n');
    String returnOutput =  U.run(command);
    System.err.println("Got back " + returnOutput);
    
    String cols[] = U.split(returnOutput);
    if ( cols != null && cols.length > 0 )
      averageMsPerRecord = Double.parseDouble(cols[ cols.length - 2]);
    
  
    return averageMsPerRecord;
  } // End Method run() ======================
  

  
}
