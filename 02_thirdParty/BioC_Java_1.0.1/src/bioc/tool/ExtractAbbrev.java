package bioc.tool;



	import java.util.*;
import java.io.*;
import bioc.tool.AbbrInfo;

	/**
	 * The ExtractAbbrev class implements a simple algorithm for
	 * extraction of abbreviations and their definitions from biomedical text.
	 * Abbreviations (short forms) are extracted from the input file, and those abbreviations
	 * for which a definition (long form) is found are printed out, along with that definition,
	 * one per line.
	 *
	 * A file consisting of short-form/long-form pairs (tab separated) can be specified
	 * in tandem with the -testlist option for the purposes of evaluating the algorithm.
	 *
	 * A Simple Algorithm for Identifying Abbreviation Definitions in Biomedical Text
	 * A.S. Schwartz, M.A. Hearst; Pacific Symposium on Biocomputing 8:451-462(2003) 
	 * for a detailed description of the algorithm.  
	 *
	 * @see <a href="http://biotext.berkeley.edu/papers/psb03.pdf">A Simple Algorithm for Identifying Abbreviation Definitions in Biomedical Text</a>
	 *
	 * @author Ariel Schwartz
	 * @version 03/12/03
	 * 
	 * Modified for use with BioC
	 * 6/6/2013
	 */
	public class ExtractAbbrev {

	    HashMap<String,Vector<String>> mTestDefinitions = new HashMap<String,Vector<String> >();
//	    HashMap mStats = new HashMap();
	    int truePositives = 0, falsePositives = 0, falseNegatives = 0, trueNegatives = 0;
	    char delimiter = '\t';
	    boolean testMode = false;

	    private boolean isValidShortForm(String str) {
		return (hasLetter(str) && (Character.isLetterOrDigit(str.charAt(0)) || (str.charAt(0) == '(')));
	    }

	    private boolean hasLetter(String str) {
		for (int i=0; i < str.length() ; i++)
		    if (Character.isLetter(str.charAt(i)))
			return true;
		return false;
	    }

	    private boolean hasCapital(String str) {
		for (int i=0; i < str.length() ; i++)
		    if (Character.isUpperCase(str.charAt(i)))
			return true;
		return false;
	    }

	    private void loadTrueDefinitions(String inFile) {
	      String abbrString, defnString, str = "";
	      Vector<String> entry;
	      HashMap<String, Vector<String>> definitions = mTestDefinitions;

	      try {
	        BufferedReader fin = new BufferedReader(new FileReader (inFile));
	        while ((str = fin.readLine()) != null) {
	          int j = str.indexOf(delimiter);
	          abbrString = str.substring(0,j).trim();
	          defnString = str.substring(j,str.length()).trim();		
	          entry = definitions.get(abbrString);
	          if (entry == null)
	            entry = new Vector<String>();
	          entry.add(defnString);
	          definitions.put(abbrString, entry);
	        }
	        fin.close();
	      } catch (Exception e) {
	        e.printStackTrace();
	        System.out.println(str);
	      }
	    }
	    
	    private boolean isTrueDefinition(String shortForm, String longForm) {
		Vector<String> entry;
		Iterator<String> itr;

		entry = mTestDefinitions.get(shortForm);
		if (entry == null)
		    return false;
		itr = entry.iterator();
		while(itr.hasNext()){
		    if (itr.next().toString().equalsIgnoreCase(longForm))
			return true;
		}
		return false;
	    }

	    public ArrayList <AbbrInfo> extractAbbrPairsString(String orgStr){
	    	
	    	String str = new String(orgStr);
 //           System.out.println("orgStr\t"+orgStr);
	    	String tmpStr, longForm = "", shortForm = "";
			String currSentence = "", sentence="";
			int openParenIndex, closeParenIndex = -1, sentenceEnd, newCloseParenIndex, tmpIndex = -1;
			int longFormIndex = 0, shortFormIndex = 0;
			//boolean newParagraph = true;
			StringTokenizer shortTokenizer;
			ArrayList <AbbrInfo> candidates = new ArrayList <AbbrInfo>();
			
		    	if (str.length() == 0 ){ // || newParagraph && ! Character.isUpperCase(str.charAt(0))){
			       //currSentence = "";
			       //newParagraph = true;
			       return candidates;
			    }
		    	
		    	//newParagraph = false;
			    str += " ";
			    currSentence += str;
			    sentence = str;
			    openParenIndex =  currSentence.indexOf(" (");
//			    System.out.println("openParenIndex\t"+openParenIndex);
			    do {
			       if (openParenIndex > -1)  openParenIndex++;
			       sentenceEnd = Math.max(currSentence.lastIndexOf(". "), currSentence.lastIndexOf(", "));
			       if ((openParenIndex == -1) && (sentenceEnd == -1)) {//Do nothing
			       }
			       else if (openParenIndex == -1) {
				       currSentence = currSentence.substring(sentenceEnd + 2);				
			       } else if ((closeParenIndex = currSentence.indexOf(')',openParenIndex)) > -1){
				         sentenceEnd = Math.max(currSentence.lastIndexOf(". ", openParenIndex), currSentence.lastIndexOf(", ", openParenIndex));
				         if (sentenceEnd == -1) sentenceEnd = -2;
				         longForm = currSentence.substring(sentenceEnd + 2, openParenIndex);
				         shortForm = currSentence.substring(openParenIndex + 1, closeParenIndex);
				         shortFormIndex = sentence.indexOf(currSentence) + openParenIndex + 1;
			         }
			       if (shortForm.length() > 0 || longForm.length() > 0) {
				      if (shortForm.length() > 1 && longForm.length() > 1) {
				         if ((shortForm.indexOf('(') > -1) && ((newCloseParenIndex = currSentence.indexOf(')', closeParenIndex + 1)) > -1)){
					        shortForm = currSentence.substring(openParenIndex + 1, newCloseParenIndex);
					        shortFormIndex = sentence.indexOf(currSentence) + openParenIndex + 1;
					        closeParenIndex = newCloseParenIndex;
				         }
				         shortFormIndex = sentence.indexOf(currSentence) + openParenIndex + 1;
				         if ((tmpIndex = shortForm.indexOf(", ")) > -1)
					        shortForm = shortForm.substring(0, tmpIndex);			    
				         if ((tmpIndex = shortForm.indexOf("; ")) > -1)
					        shortForm = shortForm.substring(0, tmpIndex);
				         shortTokenizer = new StringTokenizer(shortForm);
				         if (shortTokenizer.countTokens() > 2 || shortForm.length() > longForm.length()) {
					           // Long form in ( )
					           tmpIndex = currSentence.lastIndexOf(" ", openParenIndex - 2);
					           tmpStr = currSentence.substring(tmpIndex + 1, openParenIndex - 1);
					           longForm = shortForm;
					           shortForm = tmpStr;
					           if (! hasCapital(shortForm))
					               shortForm = "";
				         }
				         if (isValidShortForm(shortForm)){
					            
				            	tmpStr = extractAbbrPair(shortForm.trim(), longForm.trim());
//					            System.out.println("tmpStr\t"+tmpStr);
						        if (tmpStr != null) {
						        	longFormIndex = sentence.lastIndexOf(tmpStr, shortFormIndex);
						        	AbbrInfo info = new AbbrInfo(shortForm, shortFormIndex, tmpStr, longFormIndex);
						            candidates.add(info);	
						        }
//					            System.out.println("LongFormIndex\\t"+longFormIndex);
						        //System.out.println("currSentence: " + currSentence);
					            // System.out.println("LongForm|ShortForm\t"+tmpStr+"|"+shortForm);
//					            System.out.println("shortForm|index\t"+shortForm+"|"+shortFormIndex);
//					            System.out.println("longForm|index\t"+tmpStr+"|"+longFormIndex);
//					            System.out.println();
					            			            
				            }
				      }
				      currSentence = currSentence.substring(closeParenIndex + 1);
				      //System.out.println("currSentence: " + currSentence);
			       } else if (openParenIndex > -1) {
				          if ((currSentence.length() - openParenIndex) > 200)
				                  // Matching close paren was not found
				             currSentence = currSentence.substring(openParenIndex + 1);
				          return candidates; // Read next line
			              }
			       shortForm = "";
			       longForm = "";
			       } while ((openParenIndex =  currSentence.indexOf(" (")) > -1);
		    
	    
	    return candidates; 
	}
	    private ArrayList <AbbrInfo> extractAbbrPairs(String inFile) {

		String str;
		ArrayList <AbbrInfo> candidates = new ArrayList <AbbrInfo>();
		  

		try {
		    BufferedReader fin = new BufferedReader(new FileReader (inFile));
		    while ((str = fin.readLine()) != null) {
		    	//candidates.addAll(extractAbbrPairsString(str)); 	
		    	System.out.println("Sentence: " + str);
	            ArrayList <AbbrInfo> abbrInfos = extractAbbrPairsString(str); 
	            
	            for (AbbrInfo info : abbrInfos){  
		    	   System.out.println("LongForm|ShortForm\t"+info.longForm+"|"+info.shortForm);
	               System.out.println("shortForm|index\t"+info.shortForm+"|"+info.shortFormIndex);
	               System.out.println("longForm|index\t"+info.longForm+"|"+info.longFormIndex);
	               System.out.println();
	            }
	            candidates.addAll(abbrInfos);
	            
		    }
		    fin.close();
		    } catch (Exception ioe) {
		            ioe.printStackTrace();
			      }
		      return candidates;
	    }	       
	    

	    private String findBestLongForm(String shortForm, String longForm) {
		int sIndex;
		int lIndex;
		char currChar;

		sIndex = shortForm.length() - 1;
		lIndex = longForm.length() - 1;
		for ( ; sIndex >= 0; sIndex--) {
		    currChar = Character.toLowerCase(shortForm.charAt(sIndex));
		    if (!Character.isLetterOrDigit(currChar))
			continue;
		    while (((lIndex >= 0) && (Character.toLowerCase(longForm.charAt(lIndex)) != currChar)) ||
			   ((sIndex == 0) && (lIndex > 0) && (Character.isLetterOrDigit(longForm.charAt(lIndex - 1)))))
			lIndex--;
		    if (lIndex < 0)
			return null;
		    lIndex--;
		}
		lIndex = longForm.lastIndexOf(" ", lIndex) + 1;
		return longForm.substring(lIndex);
	    }

	    private String extractAbbrPair(String shortForm, String longForm) {
		   String bestLongForm;
		   StringTokenizer tokenizer;
		   int longFormSize, shortFormSize;

		   if (shortForm.length() == 1) return null;
		   bestLongForm = findBestLongForm(shortForm, longForm);
		   if (bestLongForm == null) return null;
		   tokenizer = new StringTokenizer(bestLongForm, " \t\n\r\f-");
		   longFormSize = tokenizer.countTokens();
		   shortFormSize = shortForm.length();
		   for (int i=shortFormSize - 1; i >= 0; i--)
		      if (!Character.isLetterOrDigit(shortForm.charAt(i)))
			      shortFormSize--;
		         if (bestLongForm.length() < shortForm.length() || 
		             bestLongForm.indexOf(shortForm + " ") > -1 ||
		             bestLongForm.endsWith(shortForm) ||
		             longFormSize > shortFormSize * 2 ||
		             longFormSize > shortFormSize + 5 ||
		             shortFormSize > 10)
		         return null;

		      if (testMode) {
		         if (isTrueDefinition(shortForm, bestLongForm)) {
			System.out.println(shortForm + delimiter + bestLongForm + delimiter + "TP");
			truePositives++;
			return bestLongForm;
		    }
		    else {
			falsePositives++;
			System.out.println(shortForm + delimiter + bestLongForm + delimiter + "FP");
		    return bestLongForm;
		    }	
		}
		else {
		    //System.out.println("this is the extract pair funtion: " +shortForm + delimiter + bestLongForm);
		    return bestLongForm;
		}
	    }
	    
	    public static void main(String[] args) {
		
	      String PATH = "C:\\Users\\islamaj\\Documents\\2012\\BioC\\code\\";
	        	        
	      ExtractAbbrev extractAbbrev = new ExtractAbbrev();
	      String filename =  null;
	      String testList = null;
  
	      int testListFlag = 0; 
		
	      //parse arguments
	      if (testListFlag == 1) {
	        testList = PATH+"data.txt";
	        extractAbbrev.testMode = true;
	      } 
	    
	      filename = PATH+"test.txt";
	    
	      extractAbbrev.extractAbbrPairs(filename);

	      if (extractAbbrev.testMode){
	        extractAbbrev.loadTrueDefinitions(testList);
	        System.out.println("TP: " + extractAbbrev.truePositives + " FP: " + extractAbbrev.falsePositives + 
	            " FN: " + extractAbbrev.falseNegatives + " TN: " + extractAbbrev.trueNegatives);
	      }
	    }

	}

