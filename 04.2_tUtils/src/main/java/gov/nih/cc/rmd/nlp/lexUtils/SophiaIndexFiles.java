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
// =================================================
/**
 * SophiaIndexFiles contains the list of files to 
 * load for running term lookup and sophia.
 * 
 * This has the definitions of the 2017AA and 2020AA files in it.
 * The 2017AA definitions were hiked from the 305-nlp-sophia-annotators/ SophiaAnnotator.java 
 *
 * @author     Guy Divita
 * @created    Jun 5, 2020
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.lexUtils;

/**
 * @author divitag2
 *
 */
public class SophiaIndexFiles {
  
  public static final String SophiaLRAGRFiles2020AA_TESTING  =   
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_PDQ_00.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MSH_01.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_RXNORM_01.lragr"      + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_RXNORM_00.lragr" + ":" +
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_00.lragr" + ":" +
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_04.lragr" ;
  
 //     "resources/vinciNLPFramework/sophia/2020AA/smallMRCONSOSTY.lragr"          ;
  
  
  public static final String SophiaLRAGRFiles2020AA_SNOMED_ICF  =   
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_ICF.lragr"            + ":" +
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_09.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_08.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_07.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_06.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_05.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_04.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_03.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_200AA_SNOMEDCT_US_02.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_01.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_00.lragr" ;
     
    
  
  public static final String SophiaLRAGRFiles2020AA_SNOMED_MSH_ICD10_ICF  =   
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_09.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_08.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_07.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_06.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_05.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_04.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_03.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_02.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_01.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_00.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MTH_01.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MTH_00.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MSH_06.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MSH_05.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MSH_04.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MSH_03.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MSH_02.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MSH_01.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MSH_00.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_ICD10CM_01.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_ICD10CM_00.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_ICF.lragr" ;
     
      
      
  public static final String SophiaSNOMEDLRAGRFiles2020AA =  
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_09.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_08.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_07.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_06.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_05.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_04.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_03.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_02.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_01.lragr" + ":" + 
      "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_00.lragr" ;
  
  public static final String SophiaLRAGRFiles2020AA = 
      
        //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_VANDF_00.lragr" + ":" + 
        //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_UWDA_00.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_09.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_08.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_07.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_06.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_05.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_04.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_03.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_02.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_01.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SNOMEDCT_US_00.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_RXNORM_01.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_RXNORM_00.lragr" + ":" + 
          /*
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_PDQ_00.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_01.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_00.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCBI_07.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCBI_06.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCBI_05.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCBI_04.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCBI_03.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCBI_02.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCBI_01.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCBI_00.lragr" + ":" + 
          
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MTHSPL_02.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MTHSPL_01.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MTHSPL_00.lragr" + ":" + 
          */
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MTH_01.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MTH_00.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MSH_06.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MSH_05.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MSH_04.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MSH_03.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MSH_02.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MSH_01.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MSH_00.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_LNC_02.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_LNC_01.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_LNC_00.lragr" + ":" + 
          
        //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_CVX.lragr" + ":" + 
        //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_ICD9CM_00.lragr" + ":" + 
          /*
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_ICD10PCS_03.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_ICD10PCS_02.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_ICD10PCS_01.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_ICD10PCS_00.lragr" + ":" +
          */ 
        //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_ICD10CM_01.lragr" + ":" + 
         // "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_ICD10CM_00.lragr" + ":" + 
        //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_HGNC_00.lragr" + ":" + 
          /*
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_GO_00.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_ICH.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_INC.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_NCI-HL7.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_NCPDP.lragr" + ":" + 
          */
       
         // "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_HPO.lragr" + ":" + 
        //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_NICHD.lragr" + ":" + 
        //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SRC.lragr" + ":" + 
           // "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MTHCMSFRF.lragr" + ":" + 
          // "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_DCP.lragr" + ":" + 
          // "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_RAM.lragr" + ":" + 
        //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MVX.lragr" + ":" + 
          // "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_CDC.lragr" + ":" + 
          // "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_FDA.lragr" + ":" + 
       //   "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MEDLINEPLUS.lragr" + ":" + 
          // "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SOP.lragr" + ":" + 
       //   "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_USP.lragr" + ":" + 
       //   "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_ICPC.lragr" + ":" + 
          /*
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_KEGG.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_JAX.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_GENC.lragr" + ":" +
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_SPN.lragr" + ":" + 
         "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MCM.lragr" + ":" + 
         "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_UCUM.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_NCI-GLOSS.lragr" + ":" + 
          */
       //   "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_HL7V3.0.lragr" + ":" + 
       //   "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MTHMST.lragr" + ":" + 
        //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_LCH.lragr" + ":" + 
         //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_ICF-CY.lragr" + ":" + 
        //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_USPMG.lragr" + ":" + 
         // "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_PID.lragr" + ":" + 
        //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_HL7V2.5.lragr" + ":" + 
       //   "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_LCH_NW.lragr" + ":" + 
        //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_HCPCS.lragr" + ":" + 
          /*
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_GAIA.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_RENI.lragr" + ":" + 
          */
        //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_QMR.lragr" + ":" + 
        //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MED-RT.lragr" + ":" + 
        //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_MTHICD9.lragr" + ":" + 
        //  "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_LNC_SPECIAL_USE.lragr" + ":" + 
          /*
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_DTP.lragr" + ":" + 
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_NCI_PI-RADS.lragr" + ":" + 
          */
          "resources/vinciNLPFramework/sophia/2020AA/mrconsosty_2020AA_ICF.lragr" ;
  
  
  
  
  
  public static final String SophiaLRAGRFiles2017AA =
      
      "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_09.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_08.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_07.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_06.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_05.lragr" + ":"
   
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_04.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_03.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_02.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_01.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_00.lragr" + ":"
  
    
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_LNC_00.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_LNC_01.lragr" + ":"

    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_RXNORM_02.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_RXNORM_01.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_RXNORM_00.lragr" + ":"
  
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_ICF_00.lragr"  + ":"
 
    
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_CHV_00.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_CHV_01.lragr" + ":"
//    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_FMA_00.lragr" + ":"
    
//    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_ICD10CM_01.lragr" + ":"
 //   + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_ICD10CM_00.lragr" + ":"
//    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_ICD9CM_00.lragr" + ":"        
    
 
//    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_ICD10CM_00.lragr" + ":"
//    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_HCPCS_00.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_HL7V2.5_00.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_HL7V3.0_00.lragr" + ":"
 //   + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_HPO_00.lragr" + ":"
//    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_ICD10AMAE_00.lragr" + ":"   <--- Australian version
//    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_ICD10AM_00.lragr" + ":"
    
 //   + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_ICD10PCS_00.lragr" + ":"
 //  + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_ICD10PCS_01.lragr" + ":"
 //   + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_ICD10PCS_02.lragr" + ":"
 //   + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_ICD10PCS_03.lragr" + ":"
 //   + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_ICD10PCS_04.lragr" + ":"
 //  + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_ICD10_00.lragr" + ":"
  

//      + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_LCH_NW_00.lragr" + ":"   //<---- lib of congress north western subset

    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_MDR_00.lragr" + ":" //< --- medra
    /*
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_MSH_00.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_MSH_01.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_MSH_02.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_MSH_03.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_MSH_04.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_MSH_05.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_MSH_06.lragr" + ":"
    
   // "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_MTHCMSFRF_00.lragr" + ":"
   // "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_MTHHH_00.lragr" + ":"
    *
    */
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_MTHICD9_00.lragr" + ":"
    //+ "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_MTHSPL_00.lragr" + ":"   <--- fda structured products
    //+ "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_MTHSPL_01.lragr" + ":"
    //+ "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_MTHSPL_02.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_MTH_00.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_MTH_01.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_NDFRT_00.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_NDFRT_01.lragr" + ":"
  
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SRC_00.lragr" + ":"
    + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_VANDF_00.lragr" ;    // va ndf file


public static final String QuickSophiaLRAGRFiles2017AA =

  "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_09.lragr" + ":"
+ "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_08.lragr" + ":"
+ "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_07.lragr" + ":"
+ "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_06.lragr" + ":"
+ "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_05.lragr" + ":"

+ "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_04.lragr" + ":"
+ "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_03.lragr" + ":"
+ "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_02.lragr" + ":"
+ "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_01.lragr" + ":"
+ "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_SNOMEDCT_US_00.lragr" ; // + ":"


// + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_LNC_00.lragr" + ":"
// + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_LNC_01.lragr" + ":"

//  + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_RXNORM_02.lragr" + ":"
// + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_RXNORM_01.lragr" + ":"
//  + "resources/vinciNLPFramework/sophia/2017AA/mrconsosty_2017AA_RXNORM_00.lragr" ;





      

}
