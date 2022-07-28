#!/c/opt/cygwin/bin/bash
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
