#!/c/opt/cygwin/bin/bash
#*******************************************************************************
#                                   NIH Clinical Center 
#                             Department of Rehabilitation 
#                       Epidemiology and Biostatistics Branch 
#                                            2019 - 2022
#   ---------------------------------------------------------------------------
#   Copyright Notice:
#   This software was developed and funded by the National Institutes of Health
#   Clinical Center (NIHCC), part of the National Institutes of Health (NIH),
#   and agency of the United States Department of Health and Human Services,
#   which is making the software available to the public for any commercial
#   or non-commercial purpose under the following open-source BSD license.
#  
#   Government Usage Rights Notice:
#   The U.S. Government retains unlimited, royalty-free usage rights to this 
#   software, but not ownership, as provided by Federal law. Redistribution 
#   and use in source and binary forms, with or without modification, 
#   are permitted provided that the following conditions are met:
#      1. Redistributions of source code must retain the above copyright
#         and government usage rights notice, this list of conditions and the 
#         following disclaimer.
#  
#      2. Redistributions in binary form must reproduce the above copyright
#         notice, this list of conditions and the following disclaimer in the
#         documentation and/or other materials provided with the distribution.
#        
#      3. Neither the names of the National Institutes of Health Clinical
#         Center, the National Institutes of Health, the U.S. Department of
#         Health and Human Services, nor the names of any of the software
#         developers may be used to endorse or promote products derived from
#         this software without specific prior written permission.
#   
#      4. The U.S. Government retains an unlimited, royalty-free right to
#         use, distribute or modify the software.
#   
#      5. Please acknowledge NIH CC as the source of this software by including
#         the phrase: "Courtesy of the U.S. National Institutes of Health Clinical Center"
#          or 
#                     "Source: U.S. National Institutes of Health Clinical Center."
#  
#     THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS
#     IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
#     TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
#     PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
#     OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
#     EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
#     PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
#     PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
#     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
#     NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
#     SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#  
#     When attributing this code, please make reference to:
#        Divita G, Carter ME, Tran LT, Redd D, Zeng QT, Duvall S, Samore MH, Gundlapalli AV. 
#        v3NLP Framework: tools to build applications for extracting concepts from clinical text. 
#        eGEMs. 2016;4(3). 
#      
#     In the absence of a specific paper or url listed above, reference https://github.com/CC-RMD-EpiBio/java-nlp-framework
#   
#     To view a copy of this license, visit https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/LICENSE.MD
#*******************************************************************************
#@rem  --- Copyright Notice: --------------------------------------------------
#@rem 
#@rem  Copyright 2012 United States Department of Veterans Affairs, 
#@rem                 Health Services Research & Development Service
#@rem 
#@rem   Licensed under the Apache License, Version 2.0 (the "License");
#@rem   you may not use this file except in compliance with the License.
#@rem   You may obtain a copy of the License at
#@rem 
#@rem       http://www.apache.org/licenses/LICENSE-2.0
#@rem 
#@rem   Unless required by applicable law or agreed to in writing, software
#@rem   distributed under the License is distributed on an "AS IS" BASIS,
#@rem   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#@rem   See the License for the specific language governing permissions and
#@rem   limitations under the License. 
#@rem  
#@rem  --- End Copyright Notice: ----------------------------------------------
# 
#@rem -------------------------------------
#@rem - This script filters mrconso to the fields
#@rem - that are needed, and makes those rows sorted 
#@rem - and unique
#@rem - 
#@rem - MRCONSO fields
#@rem -   CUI|LAT|TS|LUI|STT|SUI|ISPREF|AUI|SAUI|SCUI|SDUI|SAB|TTY|CODE|STR|SRL|SUPPRESS|CVF|18|5879223|72
#@rem -    1   2   3  4   5   6    7     8    9   10   11   12  13   14  15  16   17      18  
#@rem - 
#@rem - Wanted MRCONSO fields: 
#@rem -  cui|TS|STT|SUI|SAB|TTY|CODE|STR|SRL|CVF
#@rem -   1  3   5   6   12  13  14   15  16  18
#@rem - 
#@rem -   fields to filter on, but not pass along
#@rem -     LAT=ENG  SUPRESS=N or blank
#@rem -          Don't use suppress as a filter - 
#@rem -          Looks like the CHV misspellings are suppressed 
#@rem -        
#@rem -   fields to filter on, and pass along
#@rem -     STR= a single letter (not sure how to do that at the moment
#@rem - 
#@rem -  MRSTY.RRF fields
#@rem -    CUI|TUI|STN|STY|ATUI|CVF|6|2541863|147255202|
#@rem -     1    2   3  4    5   6   
#@rem - 
#@rem -   join -t "|" -1 1 -2 1 -o 1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,1.10,2.2,2.6 MRCONSOT1.SRT MRSTY.SRT >MRCONSOSTY
#@rem - 
#@rem -   The resulting join has the following columns
#@rem -     CUI|SUI|TS|STT|SAB|TTY|CODE|STR|SRL|CVF|TUI|CVF
#@rem -      1   2   3   4  5   6   7    8   9   10  11  12 
#@rem -     STR|CUI|SUI|TUI|SAB|CODE|TS|STT|TTY|SRL|CVF|CVF
#@rem - 
#@rem - 
#@rem -    This should be transformed into 4 grams
#@rem - 
#@rem -     gram1 gram2 gram3 gram4|NoGrams|CUI|SUI|TUI|SAB|CODE|TS|STT|TTY|SRL|CVF|CVF
#@rem - 
#@rem -      
#@rem - 
#@rem - 
#@rem - 
#@rem -------------------------------------
#@rem -------------------------------------
SANDBOX=/c/utah/framework/framework.resources/src/main/resources/com/ciitizen/framework/UMLS/2011/META
MRCONSO=$SANDBOX/MRCONSO.RRF
#cat $MRCONSO|grep "|ENG|" | flds.prl 1,6,3,5,12,13,14,15,16,18,19 |sort -u -t "|" +0 -1 +1 -2 >MRCONSOT1.SRT
#head MRCONSOT1.SRT
#head $SANDBOX/MRSTY.RRF 
#sort -u -t "|" +0 -1 +1 -2 $SANDBOX/MRSTY.RRF >MRSTY.SRT
#head MRSTY.SRT
#join -t "|" -1 1 -2 1 -o 1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,1.10,2.2,2.6 MRCONSOT1.SRT MRSTY.SRT >MRCONSOSTY
head MRCONSOSTY
#flds.prl 8,1,2,11,5,7,3,4,9,11,12 MRCONSOSTY >MRCONSOSTY.INT
head MRCONSOSTY.INT
sort -t "|" +0 -1 +1 -2 +2 -3 +3 -4 +4 -5 MRCONSOSTY.INT >MRCONSOSTY.SRT
head MRCONSOSTY.SRT
