//=================================================
/**
 * ToDatabase is a CAS Consumer that transforms
 * the vinciNLPFramework encoded CAS into a database through
 * jdbc calls to pre-existing database tables that conform to
 * the database schema common to framework and chartReader.
 * 
 * Make sure you have the path to your jdbc driver in the classpath.
 * 
 *   The following parameters are found in the toDatabase.xml configuration file:
 *   
 *      jdbcDriver        |
 *      databaseUserName  |  
 *      databaseName      |
 *      databasePassword  |
 *      jdbcConnectString | Note: this is devoid of the databaseName, userName,databasePassword, which are taken from
 *                                the other parameters
 *
 *
 *   Can filter to/out annotation labels via
 *      includeLabels      parameter set in the toDatabase.xml 
 *                         a list of full namespace labels (one per line)
 *      excludedLabels     a list of full namespace labels (one per line)
 *    
 *    This should be an either/or not both
 *
 * @author  Guy Divita 
 * @created June 26, 2012
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.frameworkObject;



import java.io.IOException;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;



import gov.nih.cc.rmd.ClinicalStatusEvidence;
import gov.nih.cc.rmd.DoseForm;
import gov.nih.cc.rmd.Medication;
import gov.nih.cc.rmd.framework.Condition;
import gov.nih.cc.rmd.framework.Diagnosis;
import gov.nih.cc.rmd.framework.NestedSection;
import gov.nih.cc.rmd.framework.Observation;
import gov.nih.cc.rmd.framework.PageFooter;
import gov.nih.cc.rmd.framework.PageFooterEvidence;
import gov.nih.cc.rmd.framework.PageHeader;
import gov.nih.cc.rmd.framework.PageHeaderEvidence;
import gov.nih.cc.rmd.framework.Procedure;
import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.framework.TableRow;
import gov.nih.cc.rmd.framework.model.Address;
import gov.nih.cc.rmd.framework.model.City;
import gov.nih.cc.rmd.framework.model.Country;
import gov.nih.cc.rmd.framework.model.Date;
import gov.nih.cc.rmd.framework.model.DateTime;
import gov.nih.cc.rmd.framework.model.Email;
import gov.nih.cc.rmd.framework.model.Fax;
import gov.nih.cc.rmd.framework.model.Identifier;
import gov.nih.cc.rmd.framework.model.Location;
import gov.nih.cc.rmd.framework.model.Number;
import gov.nih.cc.rmd.framework.model.NumberRange;
import gov.nih.cc.rmd.framework.model.Person;
import gov.nih.cc.rmd.framework.model.PersonNamePrefix;
import gov.nih.cc.rmd.framework.model.PersonNameSuffix;
import gov.nih.cc.rmd.framework.model.PersonNameSuffix_Type;
import gov.nih.cc.rmd.framework.model.PhoneNumber;
import gov.nih.cc.rmd.framework.model.ReferenceDate;
import gov.nih.cc.rmd.framework.model.SSN;
import gov.nih.cc.rmd.framework.model.Shape;
import gov.nih.cc.rmd.framework.model.SocialMediaHandle;
import gov.nih.cc.rmd.framework.model.State;
import gov.nih.cc.rmd.framework.model.Street;
import gov.nih.cc.rmd.framework.model.Time;
import gov.nih.cc.rmd.framework.model.URL;
import gov.nih.cc.rmd.framework.model.UnitOfMeasure;
import gov.nih.cc.rmd.framework.model.Zipcode;


import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.ClinicalStatement;
import gov.va.chir.model.CodedEntry;
import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Delimiter;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.DocumentClassification;
import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Line;
import gov.va.chir.model.ListElement;
import gov.va.chir.model.PartOfSpeech;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.Utterance;
import gov.va.chir.model.VAnnotation;
import gov.va.chir.model.WhitespaceToken;
import gov.va.chir.model.WordToken;
import gov.va.vinci.model.Activity;
import gov.va.vinci.model.AnatomicalPart;
import gov.va.vinci.model.AssertedEvidence;
import gov.va.vinci.model.Concept;
import gov.va.vinci.model.ConditionalEvidence;
import gov.va.vinci.model.Copper;
import gov.va.vinci.model.False;
import gov.va.vinci.model.FamilyHistoryEvidence;
import gov.va.vinci.model.Gold;
import gov.va.vinci.model.HistoricalEvidence;
import gov.va.vinci.model.MinimalPhrase;
import gov.va.vinci.model.Modifier;
import gov.va.vinci.model.NegationEvidence;
import gov.va.vinci.model.NoEvidence;
import gov.va.vinci.model.Phrase;
import gov.va.vinci.model.Problem;
import gov.va.vinci.model.ScopeBreakEvidence;
import gov.va.vinci.model.Severity;
import gov.va.vinci.model.SubjectIsOtherEvidence;
import gov.va.vinci.model.SubjectIsPatientEvidence;
import gov.va.vinci.model.Symptom;
import gov.va.vinci.model.True;
import gov.va.vinci.model.Uncertain;
import gov.va.vinci.model.temporal.AbsoluteDate;
import gov.va.vinci.model.temporal.AbsoluteTime;
import gov.va.vinci.model.temporal.CollectionDate;
import gov.va.vinci.model.temporal.Duration;
import gov.va.vinci.model.temporal.Event;
import gov.va.vinci.model.temporal.EventDate;
import gov.va.vinci.model.temporal.OtherTemporalEntity;
import gov.va.vinci.model.temporal.RelativeDate;
import gov.va.vinci.model.temporal.RelativeTime;
import gov.va.vinci.model.temporal.Set;
import gov.va.vinci.model.temporal.Signal;



public class FrameworkObjectWriter extends gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter {

  private static final String ALL_OUTPUT_TYPES =
  "AssertedEvidence:NegatedEvidence:HistoricalEvidence:ConditionalEvidence:HypotheticalEvidence:SubjectIsPatientEvidence:SubjectIsOtherEvidence:FamilyHistoryEvidence:NoEvidence:ScopeBreakEvidence:" +   
  "DocumentHeader:Delimiter:Line:List:ListElement:" +
  "SectionZone:NestedSection:SlotValue:ContentHeading:DependentContent:TableRow" +
  "PageHeader:PageFooter:PageHeaderEvidence:PageFooterEvidence:" + 
  
  "Utterance:Phrase:MinimalPhrase:POS:Sentence:" +         
  "LexicalElement:WordToken:" + 
  "Concept:ClinicalStatement:CodedEntry:" +
  "Medication:DoseForm:ClinicalStatusEvidence:" + 
  "AnatomicalPart:Diagnosis:Symptom:Problem:Condition:Procedure:Observation" + 
  "Activity:Modifier:Severity:Duration" +
  "Shape:UnitOfMeasure:Number:NumberRange:Email:DateTime:URL:PhoneNumber:Fax:Identifier:SSN:" +
  "Location:Address:Street:City:State:Country:Zipcode:" + 
  "Person:PersonNamePrefix:PersonNameSuffix:" + 
  "SocialMediaHandle:" +
  "Date:AbsoluteDate:RelativeDate:Time:AbsoluteTime:RelativeTime:Signal:Set:Event:OtherTemporalEntity:" +
  "ReferenceDate:CollectionDate:EventDate:" + 
  
  "DocumentClassification:" +
  "Gold:Copper:True:False:Uncertain";
  
 
  // VAnnotation, WhiteSpaceToken <---- we never want to pass these around without really good reason.   
         
  
  

  // -----------------------------------------
  /** 
   * Constructor ToString
   * @throws ResourceInitializationException 
  */
    // -----------------------------------------
    public  FrameworkObjectWriter() throws AnalysisEngineProcessException, ResourceInitializationException {
      String args[] = null;
      initialize(  args);
    } // end Constructor 


    // -----------------------------------------
    /** 
     * Constructor ToString
     * 
     * @param pArgs
     * @throws ResourceInitializationException 
    */
      // -----------------------------------------
      public  FrameworkObjectWriter(String[] pArgs) throws AnalysisEngineProcessException, ResourceInitializationException {
        initialize( pArgs);
      } // end Constructor 

    
    // -----------------------------------------
    /** 
     * process iterates through all annotations, filters out
     * those that should be filtered out, then pushes them
     * into a database store.
     * 
     * Each document should include a DocumentAnnotation annotation and a documentHeader annotation
     * for re-animation purposes.
     *
     * @param pAJCas
     * @throws AnalysisEngineProcessException
     */
    // -----------------------------------------
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
      
	 
    	// -----------------------------------
      // Set the text
      String docText = pJCas.getDocumentText();
      this.outputStringList.add( docText);

    } // end Method process



    //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize(List<String> pOutputStringList ) throws ResourceInitializationException {
      
      this.outputStringList = pOutputStringList;
 
    } // end Method initialize() --------------

  


    // -----------------------------------------
    /** 
     * destroy closes the database
     *
     */
    // -----------------------------------------
    public void destroy() {
    
      this.outputStringList = null;
      
    } // end Method destroy() 



    /* (non-Javadoc)
     * @see gov.va.vinci.nlp.framework.marshallers.writer.Writer#initialize(org.apache.uima.UimaContext)
     */
    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
    } // End Method initialize() ======================



    /* (non-Javadoc)
     * @see gov.va.vinci.nlp.framework.marshallers.writer.Writer#initialize(java.lang.String[])
     */
    @Override
    public void initialize(String[] pArgs) throws ResourceInitializationException {
      
    }// end Method initialize() ========================================



    // ----------------------------------------
    // Global variables
    // ----------------------------------------
    private List<String> outputStringList;


    
 // =================================================
    /**
     * processToFrameworkObject 
     * 
     * @param pJCas
     * 
     * @return FrameworkObject
    */
    // =================================================
   public FrameworkObject processToFrameworkObject(JCas pJCas) {
     
     
     return processToFrameworkObject( pJCas,  ALL_OUTPUT_TYPES );
     
   } // end Method processToFrameworkObject() ----------

    // =================================================
    /**
     * processToFrameworkObject 
     * 
     * @param pJCas
     * @param pOutputTypes  the same format as the --outputType= variable
     *                      a colon delimited set of labels - sans name spaces
     *                      for example Line:WordToken:
     * @return FrameworkObject
    */
    // =================================================
   public FrameworkObject processToFrameworkObject(JCas pJCas, String pOutputTypes ) {
     
     FrameworkObject returnVal = new FrameworkObject();
     
     
     String[] outputTypes = U.split(pOutputTypes, ":");
     
     if ( outputTypes != null && outputTypes.length > 0 ) {
    	 for ( String outputType: outputTypes ) {
    		 switch ( outputType ) {
    		   case "AssertedEvidence"  :       returnVal.setAssertedEvidences        ( (AssertedEvidence[])         getAnnotations( pJCas, AssertedEvidence.typeIndexID)   );       break;
    		   case "NegatedEvidence"   :       returnVal.setNegationEvidences        ( (NegationEvidence[])         getAnnotations( pJCas, NegationEvidence.typeIndexID)   );       break;
    		   case "HistoricalEvidence":       returnVal.setHistoricalEvidences      ( (HistoricalEvidence[])       getAnnotations( pJCas, HistoricalEvidence.typeIndexID) );       break;
    		   case "ConditionalEvidence":      returnVal.setConditionalEvidences     ( (ConditionalEvidence[])      getAnnotations( pJCas, ConditionalEvidence.typeIndexID) );      break;
        // case "HypotheticalEvidence:     returnVal.setHypotheticalEvidences    ( (HypotheticalEvidence[])     getAnnotations( pJCas, HypotheticalEvidence.typeIndexID) );     break;
    	     case "SubjectIsPatientEvidence": returnVal.setSubjectIsPatientEvidences( (SubjectIsPatientEvidence[]) getAnnotations( pJCas, SubjectIsPatientEvidence.typeIndexID) ); break;
    		   case "SubjectIsOtherEvidence":   returnVal.setSubjectIsOtherEvidences  ( (SubjectIsOtherEvidence[])   getAnnotations( pJCas, SubjectIsOtherEvidence.typeIndexID) );   break;
    		   case "FamilyHistoryEvidence":    returnVal.setFamilyHistoryEvidences   ( (FamilyHistoryEvidence[])    getAnnotations( pJCas, FamilyHistoryEvidence.typeIndexID) );    break;
    		   case "NoEvidence":               returnVal.setNoEvidences              ( (NoEvidence[])               getAnnotations( pJCas, NoEvidence.typeIndexID) );    break;
    		   case "ScopeBreakEvidence":       returnVal.setScopeBreakEvidences      ( (ScopeBreakEvidence[])       getAnnotations( pJCas, ScopeBreakEvidence.typeIndexID) );    break;
           
    		   
    		   
    		   case "DocumentHeader":   try {
    		     returnVal.setDocumentHeaders   ( (   getAnnotations( pJCas, DocumentHeader.typeIndexID) ));  break;
    		   } catch (Exception e ) {
    		     e.printStackTrace();
    		   }
    		     case "Delimiter":         returnVal.setDelimiters       (  getAnnotations( pJCas, Delimiter.typeIndexID) );        break;
    		   case "Line":              returnVal.setLines            ( (Line[])             getAnnotations( pJCas, Line.typeIndexID) );             break;
    		   case "List":              returnVal.setLists            ( (gov.va.chir.model.List[])  getAnnotations( pJCas, gov.va.chir.model.List.typeIndexID) );   break;
    		   case "ListElement":       returnVal.setListElements     ( (ListElement[])      getAnnotations( pJCas, ListElement.typeIndexID) );      break;
         
    		   case "SectionZone":       returnVal.setSectionZones     (    getAnnotations( pJCas, SectionZone.typeIndexID) );      break;
    		   case "NestedSection":     returnVal.setNestedSections   ( (NestedSection[])    getAnnotations( pJCas, NestedSection.typeIndexID) );      break;
           
    		   case "SlotValue":         returnVal.setSlotValues       ( (SlotValue[])        getAnnotations( pJCas, SlotValue.typeIndexID) );        break;
    		   case "ContentHeading":    returnVal.setContentHeadings  ( (ContentHeading[])   getAnnotations( pJCas, ContentHeading.typeIndexID) );   break;
    		   case "DependentContent":  returnVal.setDependentContents( (DependentContent[]) getAnnotations( pJCas, DependentContent.typeIndexID) ); break;
              
    		   
    		   case "Phrase":           returnVal.setPhrases           ( (Phrase[])           getAnnotations( pJCas, Phrase.typeIndexID) );           break;
    		   case "MinimalPhrase":    returnVal.setMinimalPhrases    ( (MinimalPhrase[])    getAnnotations( pJCas, MinimalPhrase.typeIndexID) );    break;
    		   case "POS":              returnVal.setPartsOfSpeech     ( (PartOfSpeech[])     getAnnotations( pJCas, PartOfSpeech.typeIndexID) );     break;
    		   case "Sentence":         returnVal.setSentences         ( (Sentence[])         getAnnotations( pJCas, Sentence.typeIndexID) );         break;
    		   case "LexicalElement":   returnVal.setTerms             ( (LexicalElement[])   getAnnotations( pJCas, LexicalElement.typeIndexID) );   break;
    		   case "WordToken":        returnVal.setWordTokens        ( (WordToken[])        getAnnotations( pJCas, WordToken.typeIndexID) );        break;
    		   case "WhiteSpaceToken":  returnVal.setWhiteSpaceTokens  ( (WhitespaceToken[])  getAnnotations( pJCas, WhitespaceToken.typeIndexID) );  break;
    		   
    		   case "Concept":          returnVal.setConcepts          (                      getAnnotations( pJCas, Concept.typeIndexID) );          break;
         
    		   case "Shape":            returnVal.setShapes            ( (Shape[])            getAnnotations( pJCas, Shape.typeIndexID) );            break;
    		   case "UnitOfMeasure":    returnVal.setUnitsOfMeasure    ( (UnitOfMeasure[])    getAnnotations( pJCas, UnitOfMeasure.typeIndexID) );    break;
    		   case "Email":            returnVal.setEmails            ( (Email[])            getAnnotations( pJCas, Email.typeIndexID) );            break;
    		   case "DateTime":         returnVal.setDateTimes         ( (DateTime[])         getAnnotations( pJCas, DateTime.typeIndexID) );         break;
    		   case "Date":             returnVal.setDates             ( (Date[])             getAnnotations( pJCas, Date.typeIndexID) );             break;
    		   case "Time":             returnVal.setTimes             ( (Time[])             getAnnotations( pJCas, Time.typeIndexID) );             break;
    		   case "URL":              returnVal.setUrls              ( (URL[])              getAnnotations( pJCas, URL.typeIndexID) );              break;
    		   case "PhoneNumber":      returnVal.setPhoneNumbers      ( (PhoneNumber[])      getAnnotations( pJCas, PhoneNumber.typeIndexID) );      break;
    		   case "Fax":              returnVal.setFaxes             ( (Fax[])              getAnnotations( pJCas, Fax.typeIndexID) );              break;
    		   case "Location":         returnVal.setLocations         ( (Location[])         getAnnotations( pJCas, Location.typeIndexID) );         break;
    		   case "Address":          returnVal.setAddresses         ( (Address[])          getAnnotations( pJCas, Address.typeIndexID) );          break;
    		   case "Street":           returnVal.setStreets           ( (Street[])           getAnnotations( pJCas, Street.typeIndexID) );           break;
    		   case "City":             returnVal.setCities            ( (City[])             getAnnotations( pJCas, City.typeIndexID) );             break;
    		   case "State":            returnVal.setStates            ( (State[])            getAnnotations( pJCas, State.typeIndexID) );            break;
    		   case "Country":          returnVal.setCountries         ( (Country[])          getAnnotations( pJCas, Country.typeIndexID) );          break;
    		   case "Zipcode":          returnVal.setZipcodes          ( (Zipcode[])          getAnnotations( pJCas, Zipcode.typeIndexID) );          break;
    		   case "SocialMediaHandle":returnVal.setSocialMediaHandles( (SocialMediaHandle[])getAnnotations( pJCas, SocialMediaHandle.typeIndexID) );break;
    		   case "Identifier":       returnVal.setIdentifiers       ( (Identifier[])       getAnnotations( pJCas, Identifier.typeIndexID) );       break;
    		   case "SSN":              returnVal.setSSNs              ( (SSN[])              getAnnotations( pJCas, SSN.typeIndexID) );              break;
    		   case "Number":           returnVal.setNumbers           ( (Number[])           getAnnotations( pJCas, Number.typeIndexID) );           break;
    		   case "NumberRange":      returnVal.setNumberRanges      ( (NumberRange[])      getAnnotations( pJCas, NumberRange.typeIndexID) );      break;
    	
    		   case "ClinicalStatement":returnVal.setClinicalStatements( (ClinicalStatement[])getAnnotations( pJCas, ClinicalStatement.typeIndexID) );break;
    		   case "CodedEntry":     
    		     returnVal.setCodedEntries      (        getAnnotations( pJCas, CodedEntry.typeIndexID) );  
    		     break;
    	
    		 
      	   case "DocumentClassification":          returnVal.setDocumentClassifications          ( (DocumentClassification[])          getAnnotations( pJCas, DocumentClassification.typeIndexID) );         break;
    		   case "AnatomicalPart":          returnVal.setAnatomicalParts          ( (AnatomicalPart[])          getAnnotations( pJCas, AnatomicalPart.typeIndexID) );         break;
    		   case "PageHeader":          returnVal.setPageHeaders          ( (PageHeader[])          getAnnotations( pJCas, PageHeader.typeIndexID) );         break;
    		   case "PageFooter":          returnVal.setPageFooters          ( (PageFooter[])          getAnnotations( pJCas, PageFooter.typeIndexID) );         break;
    		   case "PageHeaderEvidence":          returnVal.setPageHeaderEvidences          ( (PageHeaderEvidence[])          getAnnotations( pJCas, PageHeaderEvidence.typeIndexID) );         break;
    		   case "PageFooterEvidence":          returnVal.setPageFooterEvidences          ( (PageFooterEvidence[])          getAnnotations( pJCas, PageFooterEvidence.typeIndexID) );         break;
    		   case "Person":          returnVal.setPersons          ( (Person[])          getAnnotations( pJCas, Person.typeIndexID) );         break;
    		   case "PersonNamePrefix":          returnVal.setPersonNamePrefixes          ( (PersonNamePrefix[])          getAnnotations( pJCas, PersonNamePrefix.typeIndexID) );         break;
    		   case "PersonNameSuffix":          returnVal.setPersonNameSuffixes          ( (PersonNameSuffix[])          getAnnotations( pJCas, PersonNameSuffix.typeIndexID) );         break;
    		   case "Medication":          returnVal.setMedications          ( (Medication[])          getAnnotations( pJCas, Medication.typeIndexID) );         break;
    		   case "Activity":          returnVal.setActivities          ( (Activity[])          getAnnotations( pJCas, Activity.typeIndexID) );         break;
    		   case "Modifier":          returnVal.setModifiers          ( (Modifier[])          getAnnotations( pJCas, Modifier.typeIndexID) );         break;  
    		   case "Severity":          returnVal.setSeverities          ( (Severity[])          getAnnotations( pJCas, Severity.typeIndexID) );         break;
    		   case "Duration":          returnVal.setDurations          ( (Duration[])          getAnnotations( pJCas, Duration.typeIndexID) );         break;
           
    		   case "TableRow":          returnVal.setTableRows          ( (TableRow[])          getAnnotations( pJCas, TableRow.typeIndexID) );         break;
    		   case "Utterance":          returnVal.setUtterances          ( (Utterance[])          getAnnotations( pJCas, Utterance.typeIndexID) );         break;
    		   case "VAnnotation":          returnVal.setVAnnotations          ( (VAnnotation[])          getAnnotations( pJCas, VAnnotation.typeIndexID) );         break;
    	
    		   case "AbsoluteDate":          returnVal.setAbsoluteDates          ( (AbsoluteDate[])          getAnnotations( pJCas, AbsoluteDate.typeIndexID) );         break;
    		   case "AbsoluteTime":          returnVal.setAbsoluteTimes          ( (AbsoluteTime[])          getAnnotations( pJCas, AbsoluteTime.typeIndexID) );         break;
    		   case "RelativeDate":          returnVal.setRelativeDates          ( (RelativeDate[])          getAnnotations( pJCas, RelativeDate.typeIndexID) );         break;
    		   case "RelativeTime":          returnVal.setRelativeTimes          ( (RelativeTime[])          getAnnotations( pJCas, RelativeTime.typeIndexID) );         break;
    		   
    		   
    		   case "Signal":          returnVal.setSignals          ( (Signal[])          getAnnotations( pJCas, Signal.typeIndexID) );         break;
    		   case "Set":          returnVal.setSet          ( (Set[])          getAnnotations( pJCas, Set.typeIndexID) );         break;
    		   case "Event":          returnVal.setEvents          ( (Event[])          getAnnotations( pJCas, Event.typeIndexID) );         break;
    		   case "OtherTemporalEntity":          returnVal.setOtherTemporalEntities         ( (OtherTemporalEntity[])          getAnnotations( pJCas, OtherTemporalEntity.typeIndexID) );         break;
    		   case "ReferenceDate":          returnVal.setReferenceDates          ( (ReferenceDate[])          getAnnotations( pJCas, ReferenceDate.typeIndexID) );         break;
    		   case "CollectionDate":          returnVal.setCollectionDates          ( (CollectionDate[])          getAnnotations( pJCas, CollectionDate.typeIndexID) );         break;
    		   case "EventDate":          returnVal.setEventDates          ( (EventDate[])          getAnnotations( pJCas, EventDate.typeIndexID) );         break;
    		   case "Gold":          returnVal.setGolds          ( (Gold[])          getAnnotations( pJCas, Gold.typeIndexID) );         break;
    		   case "Copper":          returnVal.setCoppers          ( (Copper[])          getAnnotations( pJCas, Copper.typeIndexID) );         break;
    		   case "True":          returnVal.setTrues          ( (True[])          getAnnotations( pJCas, True.typeIndexID) );         break;
    		   case "False":          returnVal.setFalses          ( (False[])          getAnnotations( pJCas, False.typeIndexID) );         break;
    		   case "Uncertain":          returnVal.setUncertains          ( (Uncertain[])          getAnnotations( pJCas, Uncertain.typeIndexID) );         break;
    		
    		   case "Diagnosis":          returnVal.setDiagnoses          ( (Diagnosis[])          getAnnotations( pJCas, Diagnosis.typeIndexID) );         break;
    		   case "Symptom":          returnVal.setSymptoms          ( (Symptom[])          getAnnotations( pJCas, Symptom.typeIndexID) );         break;
    		   case "Problem":          returnVal.setProblems          ( (Problem[])          getAnnotations( pJCas, Problem.typeIndexID) );         break;
    		   case "Condition":          returnVal.setConditions          ( (Condition[])          getAnnotations( pJCas, Condition.typeIndexID) );         break;
    		   case "Procedure":          returnVal.setProcedures          ( (Procedure[])          getAnnotations( pJCas, Procedure.typeIndexID) );         break;
    		   case "Observation":          returnVal.setObservations          ( (Observation[])          getAnnotations( pJCas, Observation.typeIndexID) );         break;
      
    		 
    	    case "DoseForm":          returnVal.setDoseForms          ( (DoseForm[])          getAnnotations( pJCas, DoseForm.typeIndexID) );         break;
    	    case "ClinicalStatusEvidence":          returnVal.setClinicalStatusEvidences          ( (ClinicalStatusEvidence[])          getAnnotations( pJCas, ClinicalStatusEvidence.typeIndexID) );         break;
           
    		   // case "XX":          returnVal.setXXs          ( (XX[])          getAnnotations( pJCas, XX.typeIndexID) );         break;
         
         
    		   
    		  
    		   default : break;       
    		   
    		   
    		 }
    	 }
     }
    
    
       
   
   
  
  
  
   
  
    
    returnVal.setCoveredText( pJCas.getDocumentText()); 
     
     return returnVal;
    }


    // =================================================
    /**
     * getAnnotations [TBD] summary
     * 
     * @param pJCas
     * @param pFrameworkObject
     * @param pTypeIndexId
     * @return Annotation[]  
    */
    // =================================================
    private Annotation[] getAnnotations(JCas pJCas, int pTypeIndexId) {
    
      
      List<Annotation> annotationz =  UIMAUtil.getAnnotations(pJCas, pTypeIndexId );
    

    Annotation[] annotations = null;
    if ( annotationz != null && !annotationz.isEmpty())
        annotations = annotationz.toArray( new Annotation[ annotationz.size()]);
    
    
     
     return annotations;
     
    } // end Method getAnnotations() ----------------
    
    


} // end Class toCommonModel
