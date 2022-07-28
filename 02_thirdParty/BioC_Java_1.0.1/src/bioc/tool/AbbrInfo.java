package bioc.tool;

public class AbbrInfo {
   public String shortForm;
   public String longForm;
   public int shortFormIndex;
   public int longFormIndex;

   public AbbrInfo(String inShortForm, int inShortFormIndex, String inLongForm, int inLongFormIndex){
	   shortForm = inShortForm;
	   shortFormIndex = inShortFormIndex;
       longForm = inLongForm;
       longFormIndex = inLongFormIndex;
   }
}


