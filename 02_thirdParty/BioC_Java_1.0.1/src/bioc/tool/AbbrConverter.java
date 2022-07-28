package bioc.tool;

import java.util.ArrayList;

import bioc.BioCAnnotation;
import bioc.BioCPassage;
import bioc.BioCRelation;
import bioc.BioCSentence;
import bioc.util.CopyConverter;


/**
 * BioC wrapper
 */
public class AbbrConverter extends CopyConverter{
  
  /** load abbr */
  private static ExtractAbbrev extractAbbr = new ExtractAbbrev();
  private static int counter = 0; 
/**
    Modify an {@code BioCPassage} and BioCSentence.
    add annotation for short and long form and add a relation between 
 */
 public BioCPassage getPassage(BioCPassage in) {
		BioCPassage out = new BioCPassage();
		out.setOffset( in.getOffset() );
		//out.setText( in.getText() );
		String passageText = in.getText();
		
		if (passageText != null) {
			ArrayList <AbbrInfo> infos = extractAbbr.extractAbbrPairsString(passageText); 
			for (AbbrInfo info : infos){  	    	  
	            addAbbrInfo(info, out);             
	         }
		}
		
	
			
		out.setInfons( in.getInfons() );
		for ( BioCSentence sentence : in.getSentences() ) {
			out.addSentence( getSentence(sentence) );
		}
		for (BioCAnnotation note : in.getAnnotations() ) {
			out.addAnnotation( getAnnotation(note) );
		}
		for (BioCRelation rel : in.getRelations() ) {
			out.addRelation( rel );
		}
	
		return out;
	}
	
	/**
	* Copy a {@code BioCSentence}.
	*/
	public BioCSentence getSentence(BioCSentence in) {
		BioCSentence out = new BioCSentence();
		out.setOffset( in.getOffset() );
		//out.setText( in.getText() );
		
		String sentenceText = in.getText();
		
		ArrayList <AbbrInfo> infos = extractAbbr.extractAbbrPairsString(sentenceText); 
		
		for (AbbrInfo info : infos){  	    	  
            addAbbrInfo(info, out);             
         }
		
		out.setInfons( in.getInfons() );
	
		for (BioCAnnotation note : in.getAnnotations() ) {
			out.addAnnotation( getAnnotation(note) );
		}
		for (BioCRelation rel : in.getRelations() ) {
			out.addRelation( rel );
		}
	
		return out;
	}
 
private void addAbbrInfo(AbbrInfo info, BioCPassage currentPassage) {
   BioCAnnotation shortForm = new BioCAnnotation(); 
   BioCAnnotation longForm  = new BioCAnnotation(); 
   BioCRelation abbreviation = new BioCRelation();
   
   String sfId = "SF"+ counter;
   String lfId = "LF"+ counter; 
   String relId = "R" + counter; 
   counter++; 
   
   shortForm.setID(sfId); 
   shortForm.putInfon("type", "ABBR");
   shortForm.putInfon("ABBR", "ShortForm");
   shortForm.setLocation(info.shortFormIndex+currentPassage.getOffset(), info.shortForm.length());
   shortForm.setText(info.shortForm);
   
   longForm.setID(lfId); 
   longForm.putInfon("type", "ABBR");
   longForm.putInfon("ABBR", "LongForm");
   longForm.setLocation(info.longFormIndex+currentPassage.getOffset(), info.longForm.length());
   longForm.setText(info.longForm);
   
   abbreviation.setID(relId);
   abbreviation.putInfon("type", "ABBR");
   abbreviation.addNode(sfId, "ShortForm");
   abbreviation.addNode(lfId, "LongForm");
   
   currentPassage.addAnnotation(shortForm);
   currentPassage.addAnnotation(longForm);
   currentPassage.addRelation(abbreviation);
   
 }

private void addAbbrInfo(AbbrInfo info, BioCSentence currentSentence) {
	   BioCAnnotation shortForm = new BioCAnnotation(); 
	   BioCAnnotation longForm  = new BioCAnnotation(); 
	   BioCRelation abbreviation = new BioCRelation();
	   
	   String sfId = "SF"+ counter;
	   String lfId = "LF"+ counter; 
	   String relId = "R" + counter; 
	   counter++; 
	   
	   shortForm.setID(sfId); 
	   shortForm.putInfon("type", "ABBR");
	   shortForm.putInfon("ABBR", "ShortForm");
	   shortForm.setLocation(info.shortFormIndex+currentSentence.getOffset(), info.shortForm.length());
	   shortForm.setText(info.shortForm);
	   
	   longForm.setID(lfId); 
	   longForm.putInfon("type", "ABBR");
	   longForm.putInfon("ABBR", "LongForm");
	   longForm.setLocation(info.longFormIndex+currentSentence.getOffset(), info.longForm.length());
	   longForm.setText(info.longForm);
	   
	   abbreviation.setID(relId);
	   abbreviation.putInfon("type", "ABBR");
	   abbreviation.addNode(sfId, "ShortForm");
	   abbreviation.addNode(lfId, "LongForm");
	   
	   currentSentence.addAnnotation(shortForm);
	   currentSentence.addAnnotation(longForm);
	   currentSentence.addRelation(abbreviation);
	   
	 }
 
}