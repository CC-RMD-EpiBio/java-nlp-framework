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
//=================================================
/**
 * Container to pass annotations to python with
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.frameworkObject;



import org.apache.uima.resource.ResourceInitializationException;

import gov.va.vinci.model.HistoricalEvidence;



public class FrameworkObject  {

  // -----------------------------------------
  /** 
   * Constructor ToString
   * @throws ResourceInitializationException 
  */
    // -----------------------------------------
    public  FrameworkObject()  {
    
    } // end Constructor 


   



    /**
   * @return the coveredText
   */
  public final String getCoveredText() {
    return coveredText;
  }
  /**
   * @param coveredText the coveredText to set
   */
  public final void setCoveredText(String coveredText) {
    this.coveredText = coveredText;
  }
  /**
   * @return the assertedEvidences
   */
  public final Object[] getAssertedEvidences() {
    return assertedEvidences;
  }
  /**
   * @param assertedEvidences the assertedEvidences to set
   */
  public final void setAssertedEvidences(Object[] assertedEvidences) {
    this.assertedEvidences = assertedEvidences;
  }
  /**
   * @return the negationEvidences
   */
  public final Object[] getNegationEvidences() {
    return negationEvidences;
  }
  /**
   * @param negationEvidences the negationEvidences to set
   */
  public final void setNegationEvidences(Object[] negationEvidences) {
    this.negationEvidences = negationEvidences;
  }
  /**
   * @return the historicalEvidences
   */
  public final HistoricalEvidence[] getHistoricalEvidences() {
    return historicalEvidences;
  }
  /**
   * @param historicalEvidences the historicalEvidences to set
   */
  public final void setHistoricalEvidences(HistoricalEvidence[] historicalEvidences) {
    this.historicalEvidences = historicalEvidences;
  }
  /**
   * @return the conditionalEvidences
   */
  public final Object[] getConditionalEvidences() {
    return conditionalEvidences;
  }
  /**
   * @param conditionalEvidences the conditionalEvidences to set
   */
  public final void setConditionalEvidences(Object[] conditionalEvidences) {
    this.conditionalEvidences = conditionalEvidences;
  }
  /**
   * @return the subjectIsPatientEvidences
   */
  public final Object[] getSubjectIsPatientEvidences() {
    return subjectIsPatientEvidences;
  }
  /**
   * @param subjectIsPatientEvidences the subjectIsPatientEvidences to set
   */
  public final void setSubjectIsPatientEvidences(Object[] subjectIsPatientEvidences) {
    this.subjectIsPatientEvidences = subjectIsPatientEvidences;
  }
  /**
   * @return the subjectIsOtherEvidences
   */
  public final Object[] getSubjectIsOtherEvidences() {
    return subjectIsOtherEvidences;
  }
  /**
   * @param subjectIsOtherEvidences the subjectIsOtherEvidences to set
   */
  public final void setSubjectIsOtherEvidences(Object[] subjectIsOtherEvidences) {
    this.subjectIsOtherEvidences = subjectIsOtherEvidences;
  }
  /**
   * @return the familyHistoryEvidences
   */
  public final Object[] getFamilyHistoryEvidences() {
    return familyHistoryEvidences;
  }
  /**
   * @param familyHistoryEvidences the familyHistoryEvidences to set
   */
  public final void setFamilyHistoryEvidences(Object[] familyHistoryEvidences) {
    this.familyHistoryEvidences = familyHistoryEvidences;
  }
  /**
   * @return the noEvidences
   */
  public final Object[] getNoEvidences() {
    return noEvidences;
  }
  /**
   * @param noEvidences the noEvidences to set
   */
  public final void setNoEvidences(Object[] noEvidences) {
    this.noEvidences = noEvidences;
  }
  /**
   * @return the scopeBreakEvidences
   */
  public final Object[] getScopeBreakEvidences() {
    return scopeBreakEvidences;
  }
  /**
   * @param scopeBreakEvidences the scopeBreakEvidences to set
   */
  public final void setScopeBreakEvidences(Object[] scopeBreakEvidences) {
    this.scopeBreakEvidences = scopeBreakEvidences;
  }
  /**
   * @return the documentHeaders
   */
  public final Object[] getDocumentHeaders() {
    return documentHeaders;
  }
  /**
   * @param documentHeaders the documentHeaders to set
   */
  public final void setDocumentHeaders(Object[] documentHeaders) {
    this.documentHeaders = documentHeaders;
  }
  /**
   * @return the delimiters
   */
  public final Object[] getDelimiters() {
    return delimiters;
  }
  /**
   * @param delimiters the delimiters to set
   */
  public final void setDelimiters(Object[] delimiters) {
    this.delimiters = delimiters;
  }
  /**
   * @return the lists
   */
  public final Object[] getLists() {
    return lists;
  }
  /**
   * @param lists the lists to set
   */
  public final void setLists(Object[] lists) {
    this.lists = lists;
  }
  /**
   * @return the lines
   */
  public final Object[] getLines() {
    return lines;
  }
  /**
   * @param lines the lines to set
   */
  public final void setLines(Object[] lines) {
    this.lines = lines;
  }
  /**
   * @return the listElements
   */
  public final Object[] getListElements() {
    return listElements;
  }
  /**
   * @param listElements the listElements to set
   */
  public final void setListElements(Object[] listElements) {
    this.listElements = listElements;
  }
  /**
   * @return the sectionZones
   */
  public final Object[] getSectionZones() {
    return sectionZones;
  }
  /**
   * @param sectionZones the sectionZones to set
   */
  public final void setSectionZones(Object[] sectionZones) {
    this.sectionZones = sectionZones;
  }
  /**
   * @return the slotValues
   */
  public final Object[] getSlotValues() {
    return slotValues;
  }
  /**
   * @param slotValues the slotValues to set
   */
  public final void setSlotValues(Object[] slotValues) {
    this.slotValues = slotValues;
  }
  /**
   * @return the contentHeadings
   */
  public final Object[] getContentHeadings() {
    return contentHeadings;
  }
  /**
   * @param contentHeadings the contentHeadings to set
   */
  public final void setContentHeadings(Object[] contentHeadings) {
    this.contentHeadings = contentHeadings;
  }
  /**
   * @return the dependentContents
   */
  public final Object[] getDependentContents() {
    return dependentContents;
  }
  /**
   * @param dependentContents the dependentContents to set
   */
  public final void setDependentContents(Object[] dependentContents) {
    this.dependentContents = dependentContents;
  }
  /**
   * @return the phrases
   */
  public final Object[] getPhrases() {
    return phrases;
  }
  /**
   * @param phrases the phrases to set
   */
  public final void setPhrases(Object[] phrases) {
    this.phrases = phrases;
  }
  /**
   * @return the minimalPhrases
   */
  public final Object[] getMinimalPhrases() {
    return minimalPhrases;
  }
  /**
   * @param minimalPhrases the minimalPhrases to set
   */
  public final void setMinimalPhrases(Object[] minimalPhrases) {
    this.minimalPhrases = minimalPhrases;
  }
  /**
   * @return the partsOfSpeech
   */
  public final Object[] getPartsOfSpeech() {
    return partsOfSpeech;
  }
  /**
   * @param partsOfSpeech the partsOfSpeech to set
   */
  public final void setPartsOfSpeech(Object[] partsOfSpeech) {
    this.partsOfSpeech = partsOfSpeech;
  }
  /**
   * @return the sentences
   */
  public final Object[] getSentences() {
    return sentences;
  }
  /**
   * @param sentences the sentences to set
   */
  public final void setSentences(Object[] sentences) {
    this.sentences = sentences;
  }
  /**
   * @return the terms
   */
  public final Object[] getTerms() {
    return terms;
  }
  /**
   * @param terms the terms to set
   */
  public final void setTerms(Object[] terms) {
    this.terms = terms;
  }
  /**
   * @return the wordTokens
   */
  public final Object[] getWordTokens() {
    return wordTokens;
  }
  /**
   * @param wordTokens the wordTokens to set
   */
  public final void setWordTokens(Object[] wordTokens) {
    this.wordTokens = wordTokens;
  }
  /**
   * @return the whiteSpaceTokens
   */
  public final Object[] getWhiteSpaceTokens() {
    return whiteSpaceTokens;
  }
  /**
   * @param whiteSpaceTokens the whiteSpaceTokens to set
   */
  public final void setWhiteSpaceTokens(Object[] whiteSpaceTokens) {
    this.whiteSpaceTokens = whiteSpaceTokens;
  }
  /**
   * @return the concepts
   */
  public final Object[] getConcepts() {
    return concepts;
  }
  /**
   * @param concepts the concepts to set
   */
  public final void setConcepts(Object[] concepts) {
    this.concepts = concepts;
  }
  /**
   * @return the clinicalStatements
   */
  public final Object[] getClinicalStatements() {
    return clinicalStatements;
  }
  /**
   * @param clinicalStatements the clinicalStatements to set
   */
  public final void setClinicalStatements(Object[] clinicalStatements) {
    this.clinicalStatements = clinicalStatements;
  }
  /**
   * @return the codedEntries
   */
  public final Object[] getCodedEntries() {
    return codedEntries;
  }
  /**
   * @param codedEntries the codedEntries to set
   */
  public final void setCodedEntries(Object[] codedEntries) {
    this.codedEntries = codedEntries;
  }
  /**
   * @return the shapes
   */
  public final Object[] getShapes() {
    return shapes;
  }
  /**
   * @param shapes the shapes to set
   */
  public final void setShapes(Object[] shapes) {
    this.shapes = shapes;
  }
  /**
   * @return the unitsOfMeasure
   */
  public final Object[] getUnitsOfMeasure() {
    return unitsOfMeasure;
  }
  /**
   * @param unitsOfMeasure the unitsOfMeasure to set
   */
  public final void setUnitsOfMeasure(Object[] unitsOfMeasure) {
    this.unitsOfMeasure = unitsOfMeasure;
  }
  /**
   * @return the emails
   */
  public final Object[] getEmails() {
    return emails;
  }
  /**
   * @param emails the emails to set
   */
  public final void setEmails(Object[] emails) {
    this.emails = emails;
  }
  /**
   * @return the dateTimes
   */
  public final Object[] getDateTimes() {
    return dateTimes;
  }
  /**
   * @param dateTimes the dateTimes to set
   */
  public final void setDateTimes(Object[] dateTimes) {
    this.dateTimes = dateTimes;
  }
  /**
   * @return the dates
   */
  public final Object[] getDates() {
    return dates;
  }
  /**
   * @param dates the dates to set
   */
  public final void setDates(Object[] dates) {
    this.dates = dates;
  }
  /**
   * @return the times
   */
  public final Object[] getTimes() {
    return times;
  }
  /**
   * @param times the times to set
   */
  public final void setTimes(Object[] times) {
    this.times = times;
  }
  /**
   * @return the urls
   */
  public final Object[] getUrls() {
    return urls;
  }
  /**
   * @param urls the urls to set
   */
  public final void setUrls(Object[] urls) {
    this.urls = urls;
  }
  /**
   * @return the phoneNumbers
   */
  public final Object[] getPhoneNumbers() {
    return phoneNumbers;
  }
  /**
   * @param phoneNumbers the phoneNumbers to set
   */
  public final void setPhoneNumbers(Object[] phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }
  /**
   * @return the faxes
   */
  public final Object[] getFaxes() {
    return faxes;
  }
  /**
   * @param faxes the faxes to set
   */
  public final void setFaxes(Object[] faxes) {
    this.faxes = faxes;
  }
  /**
   * @return the locations
   */
  public final Object[] getLocations() {
    return locations;
  }
  /**
   * @param locations the locations to set
   */
  public final void setLocations(Object[] locations) {
    this.locations = locations;
  }
  /**
   * @return the addresses
   */
  public final Object[] getAddresses() {
    return addresses;
  }
  /**
   * @param addresses the addresses to set
   */
  public final void setAddresses(Object[] addresses) {
    this.addresses = addresses;
  }
  /**
   * @return the streets
   */
  public final Object[] getStreets() {
    return streets;
  }
  /**
   * @param streets the streets to set
   */
  public final void setStreets(Object[] streets) {
    this.streets = streets;
  }
  /**
   * @return the cities
   */
  public final Object[] getCities() {
    return cities;
  }
  /**
   * @param cities the cities to set
   */
  public final void setCities(Object[] cities) {
    this.cities = cities;
  }
  /**
   * @return the states
   */
  public final Object[] getStates() {
    return states;
  }
  /**
   * @param states the states to set
   */
  public final void setStates(Object[] states) {
    this.states = states;
  }
  /**
   * @return the countries
   */
  public final Object[] getCountries() {
    return countries;
  }
  /**
   * @param countries the countries to set
   */
  public final void setCountries(Object[] countries) {
    this.countries = countries;
  }
  /**
   * @return the zipcodes
   */
  public final Object[] getZipcodes() {
    return zipcodes;
  }
  /**
   * @param zipcodes the zipcodes to set
   */
  public final void setZipcodes(Object[] zipcodes) {
    this.zipcodes = zipcodes;
  }
  /**
   * @return the socialMediaHandles
   */
  public final Object[] getSocialMediaHandles() {
    return socialMediaHandles;
  }
  /**
   * @param socialMediaHandles the socialMediaHandles to set
   */
  public final void setSocialMediaHandles(Object[] socialMediaHandles) {
    this.socialMediaHandles = socialMediaHandles;
  }
  /**
   * @return the identifiers
   */
  public final Object[] getIdentifiers() {
    return identifiers;
  }
  /**
   * @param identifiers the identifiers to set
   */
  public final void setIdentifiers(Object[] identifiers) {
    this.identifiers = identifiers;
  }
  /**
   * @return the ssns
   */
  public final Object[] getSSNs() {
    return ssns;
  }
  /**
   * @param ssns the ssns to set
   */
  public final void setSSNs(Object[] ssns) {
    this.ssns = ssns;
  }
  /**
   * @return the numbers
   */
  public final Object[] getNumbers() {
    return numbers;
  }
  /**
   * @param numbers the numbers to set
   */
  public final void setNumbers(Object[] numbers) {
    this.numbers = numbers;
  }
  /**
   * @return the numberRanges
   */
  public final Object[] getNumberRanges() {
    return numberRanges;
  }
  /**
   * @param numberRanges the numberRanges to set
   */
  public final void setNumberRanges(Object[] numberRanges) {
    this.numberRanges = numberRanges;
  }
  /**
   * @return the absoluteDates
   */
  public final Object[] getAbsoluteDates() {
    return absoluteDates;
  }
  /**
   * @param absoluteDates the absoluteDates to set
   */
  public final void setAbsoluteDates(Object[] absoluteDates) {
    this.absoluteDates = absoluteDates;
  }
  /**
   * @return the absoluteTimes
   */
  public final Object[] getAbsoluteTimes() {
    return absoluteTimes;
  }
  /**
   * @param absoluteTimes the absoluteTimes to set
   */
  public final void setAbsoluteTimes(Object[] absoluteTimes) {
    this.absoluteTimes = absoluteTimes;
  }
  /**
   * @return the relativeDates
   */
  public final Object[] getRelativeDates() {
    return relativeDates;
  }
  /**
   * @param relativeDates the relativeDates to set
   */
  public final void setRelativeDates(Object[] relativeDates) {
    this.relativeDates = relativeDates;
  }
  /**
   * @return the relativeTimes
   */
  public final Object[] getRelativeTimes() {
    return relativeTimes;
  }
  /**
   * @param relativeTimes the relativeTimes to set
   */
  public final void setRelativeTimes(Object[] relativeTimes) {
    this.relativeTimes = relativeTimes;
  }
  /**
   * @return the durations
   */
  public final Object[] getDurations() {
    return durations;
  }
  /**
   * @param durations the durations to set
   */
  public final void setDurations(Object[] durations) {
    this.durations = durations;
  }
  /**
   * @return the signals
   */
  public final Object[] getSignals() {
    return signals;
  }
  /**
   * @param signals the signals to set
   */
  public final void setSignals(Object[] signals) {
    this.signals = signals;
  }
  /**
   * @return the set
   */
  public final Object[] getSet() {
    return set;
  }
  /**
   * @param set the set to set
   */
  public final void setSet(Object[] set) {
    this.set = set;
  }
  /**
   * @return the events
   */
  public final Object[] getEvents() {
    return events;
  }
  /**
   * @param events the events to set
   */
  public final void setEvents(Object[] events) {
    this.events = events;
  }
  /**
   * @return the otherTemporalEntities
   */
  public final Object[] getOtherTemporalEntities() {
    return otherTemporalEntities;
  }
  /**
   * @param otherTemporalEntities the otherTemporalEntities to set
   */
  public final void setOtherTemporalEntities(Object[] otherTemporalEntities) {
    this.otherTemporalEntities = otherTemporalEntities;
  }
  /**
   * @return the referenceDates
   */
  public final Object[] getReferenceDates() {
    return referenceDates;
  }
  /**
   * @param referenceDates the referenceDates to set
   */
  public final void setReferenceDates(Object[] referenceDates) {
    this.referenceDates = referenceDates;
  }
  /**
   * @return the collectionDates
   */
  public final Object[] getCollectionDates() {
    return collectionDates;
  }
  /**
   * @param collectionDates the collectionDates to set
   */
  public final void setCollectionDates(Object[] collectionDates) {
    this.collectionDates = collectionDates;
  }
  /**
   * @return the eventDates
   */
  public final Object[] getEventDates() {
    return eventDates;
  }
  /**
   * @param eventDates the eventDates to set
   */
  public final void setEventDates(Object[] eventDates) {
    this.eventDates = eventDates;
  }
  /**
   * @return the golds
   */
  public final Object[] getGolds() {
    return golds;
  }
  /**
   * @param golds the golds to set
   */
  public final void setGolds(Object[] golds) {
    this.golds = golds;
  }
  /**
   * @return the coppers
   */
  public final Object[] getCoppers() {
    return coppers;
  }
  /**
   * @param coppers the coppers to set
   */
  public final void setCoppers(Object[] coppers) {
    this.coppers = coppers;
  }
  /**
   * @return the trues
   */
  public final Object[] getTrues() {
    return trues;
  }
  /**
   * @param trues the trues to set
   */
  public final void setTrues(Object[] trues) {
    this.trues = trues;
  }
  /**
   * @return the falses
   */
  public final Object[] getFalses() {
    return falses;
  }
  /**
   * @param falses the falses to set
   */
  public final void setFalses(Object[] falses) {
    this.falses = falses;
  }
  /**
   * @return the uncertains
   */
  public final Object[] getUncertains() {
    return uncertains;
  }
  /**
   * @param uncertains the uncertains to set
   */
  public final void setUncertains(Object[] uncertains) {
    this.uncertains = uncertains;
  }
  /**
   * @return the documentClassifications
   */
  public final Object[] getDocumentClassifications() {
    return documentClassifications;
  }
  /**
   * @param documentClassifications the documentClassifications to set
   */
  public final void setDocumentClassifications(Object[] documentClassifications) {
    this.documentClassifications = documentClassifications;
  }
  /**
   * @return the anatomicalParts
   */
  public final Object[] getAnatomicalParts() {
    return anatomicalParts;
  }
  /**
   * @param anatomicalParts the anatomicalParts to set
   */
  public final void setAnatomicalParts(Object[] anatomicalParts) {
    this.anatomicalParts = anatomicalParts;
  }
  /**
   * @return the pageHeaders
   */
  public final Object[] getPageHeaders() {
    return pageHeaders;
  }
  /**
   * @param pageHeaders the pageHeaders to set
   */
  public final void setPageHeaders(Object[] pageHeaders) {
    this.pageHeaders = pageHeaders;
  }
  /**
   * @return the pageFooters
   */
  public final Object[] getPageFooters() {
    return pageFooters;
  }
  /**
   * @param pageFooters the pageFooters to set
   */
  public final void setPageFooters(Object[] pageFooters) {
    this.pageFooters = pageFooters;
  }
  /**
   * @return the pageHeaderEvidences
   */
  public final Object[] getPageHeaderEvidences() {
    return pageHeaderEvidences;
  }
  /**
   * @param pageHeaderEvidences the pageHeaderEvidences to set
   */
  public final void setPageHeaderEvidences(Object[] pageHeaderEvidences) {
    this.pageHeaderEvidences = pageHeaderEvidences;
  }
  /**
   * @return the pageFooterEvidences
   */
  public final Object[] getPageFooterEvidences() {
    return pageFooterEvidences;
  }
  /**
   * @param pageFooterEvidences the pageFooterEvidences to set
   */
  public final void setPageFooterEvidences(Object[] pageFooterEvidences) {
    this.pageFooterEvidences = pageFooterEvidences;
  }
  /**
   * @return the persons
   */
  public final Object[] getPersons() {
    return persons;
  }
  /**
   * @param persons the persons to set
   */
  public final void setPersons(Object[] persons) {
    this.persons = persons;
  }
  /**
   * @return the personNamePrefixes
   */
  public final Object[] getPersonNamePrefixes() {
    return PersonNamePrefixes;
  }
  /**
   * @param personNamePrefixes the personNamePrefixes to set
   */
  public final void setPersonNamePrefixes(Object[] personNamePrefixes) {
    PersonNamePrefixes = personNamePrefixes;
  }
  /**
   * @return the personNameSuffixes
   */
  public final Object[] getPersonNameSuffixes() {
    return PersonNameSuffixes;
  }
  /**
   * @param personNameSuffixes the personNameSuffixes to set
   */
  public final void setPersonNameSuffixes(Object[] personNameSuffixes) {
    PersonNameSuffixes = personNameSuffixes;
  }
  /**
   * @return the medications
   */
  public final Object[] getMedications() {
    return medications;
  }
  /**
   * @param medications the medications to set
   */
  public final void setMedications(Object[] medications) {
    this.medications = medications;
  }
  /**
   * @return the activities
   */
  public final Object[] getActivities() {
    return activities;
  }
  /**
   * @param activities the activities to set
   */
  public final void setActivities(Object[] activities) {
    this.activities = activities;
  }
  /**
   * @return the modifiers
   */
  public final Object[] getModifiers() {
    return modifiers;
  }
  /**
   * @param modifiers the modifiers to set
   */
  public final void setModifiers(Object[] modifiers) {
    this.modifiers = modifiers;
  }
  /**
   * @return the severities
   */
  public final Object[] getSeverities() {
    return severities;
  }
  /**
   * @param severities the severities to set
   */
  public final void setSeverities(Object[] severities) {
    this.severities = severities;
  }
  /**
   * @return the nestedSections
   */
  public final Object[] getNestedSections() {
    return nestedSections;
  }
  /**
   * @param nestedSections the nestedSections to set
   */
  public final void setNestedSections(Object[] nestedSections) {
    this.nestedSections = nestedSections;
  }
  /**
   * @return the tableRows
   */
  public final Object[] getTableRows() {
    return tableRows;
  }
  /**
   * @param tableRows the tableRows to set
   */
  public final void setTableRows(Object[] tableRows) {
    this.tableRows = tableRows;
  }
  /**
   * @return the utterances
   */
  public final Object[] getUtterances() {
    return utterances;
  }
  /**
   * @param utterances the utterances to set
   */
  public final void setUtterances(Object[] utterances) {
    this.utterances = utterances;
  }
  /**
   * @return the vAnnotations
   */
  public final Object[] getvAnnotations() {
    return vAnnotations;
  }
  /**
   * @param vAnnotations the vAnnotations to set
   */
  public final void setVAnnotations(Object[] vAnnotations) {
    this.vAnnotations = vAnnotations;
  }
  /**
   * @return the diagnoses
   */
  public final Object[] getDiagnoses() {
    return diagnoses;
  }
  /**
   * @param diagnoses the diagnoses to set
   */
  public final void setDiagnoses(Object[] diagnoses) {
    this.diagnoses = diagnoses;
  }
  /**
   * @return the symptoms
   */
  public final Object[] getSymptoms() {
    return symptoms;
  }
  /**
   * @param symptoms the symptoms to set
   */
  public final void setSymptoms(Object[] symptoms) {
    this.symptoms = symptoms;
  }
  /**
   * @return the problems
   */
  public final Object[] getProblems() {
    return problems;
  }
  /**
   * @param problems the problems to set
   */
  public final void setProblems(Object[] problems) {
    this.problems = problems;
  }
  /**
   * @return the conditions
   */
  public final Object[] getConditions() {
    return conditions;
  }
  /**
   * @param conditions the conditions to set
   */
  public final void setConditions(Object[] conditions) {
    this.conditions = conditions;
  }
  /**
   * @return the procedures
   */
  public final Object[] getProcedures() {
    return procedures;
  }
  /**
   * @param procedures the procedures to set
   */
  public final void setProcedures(Object[] procedures) {
    this.procedures = procedures;
  }
  /**
   * @return the observations
   */
  public final Object[] getObservations() {
    return observations;
  }
  /**
   * @param observations the observations to set
   */
  public final void setObservations(Object[] observations) {
    this.observations = observations;
  }
  /**
   * @return the doseForms
   */
  public final Object[] getDoseForms() {
    return doseForms;
  }
  /**
   * @param doseForms the doseForms to set
   */
  public final void setDoseForms(Object[] doseForms) {
    this.doseForms = doseForms;
  }
  /**
   * @return the clinicalStatusEvidences
   */
  public final Object[] getClinicalStatusEvidences() {
    return clinicalStatusEvidences;
  }
  /**
   * @param clinicalStatusEvidences the clinicalStatusEvidences to set
   */
  public final void setClinicalStatusEvidences(Object[] clinicalStatusEvidences) {
    this.clinicalStatusEvidences = clinicalStatusEvidences;
  }






    // ----------------------------------------
    // Global variables
    // ----------------------------------------
    private String coveredText;
    private Object[] assertedEvidences;
    private Object[] negationEvidences;
    private HistoricalEvidence[] historicalEvidences;
    // private HypotheticalEvidence[] hypotheticalEvidences;
    private Object[] conditionalEvidences;
    private Object[] subjectIsPatientEvidences;
    private Object[] subjectIsOtherEvidences;
    private Object[] familyHistoryEvidences;
    private Object[]            noEvidences;
    private Object[]    scopeBreakEvidences;
   
    private Object[]             documentHeaders;
    private Object[]             delimiters;
    private Object[]             lists;
    private Object[]             lines;
    private Object[]             listElements;
    private Object[]            sectionZones;
    private Object[]              slotValues;
    private Object[]         contentHeadings;
    private Object[]       dependentContents;
  
    private Object[]                 phrases;
    private Object[]          minimalPhrases;
    private Object[]           partsOfSpeech;
    private Object[]               sentences;
    private Object[]         terms;
    private Object[]              wordTokens;
    private Object[]        whiteSpaceTokens;
    
    private Object[]                concepts;
    private Object[]      clinicalStatements;
    private Object[]             codedEntries;
    
    
    private Object[]                 shapes;
    private Object[]         unitsOfMeasure;
    private Object[]                 emails;
    private Object[]              dateTimes;
    private Object[]                  dates;
    private Object[]                  times;
    private Object[]                   urls;
    private Object[]           phoneNumbers;
    private Object[]                   faxes;
    private Object[]              locations;
    private Object[]               addresses;
    private Object[]                streets;
    private Object[]                  cities;
    private Object[]                 states;
    private Object[]               countries;
    private Object[]               zipcodes;
    private Object[]     socialMediaHandles;
    private Object[]            identifiers;
    private Object[]                   ssns;
    private Object[]                numbers;
    private Object[]           numberRanges;
    
    
    private Object[]          absoluteDates;
    // private Date[]                  dates;
    private Object[]          absoluteTimes;
    // private Time[]                  times;
    private Object[]          relativeDates;
    private Object[]          relativeTimes;
    private Object[]              durations;
    private Object[]                signals;
    private Object[]                   set;
    private Object[]                 events;
    private Object[]   otherTemporalEntities;
    private Object[]         referenceDates;
    private Object[]        collectionDates;
    private Object[]             eventDates;
    
    private Object[]                  golds;
    private Object[]                coppers;
    private Object[]                  trues;
    private Object[]                 falses;
    private Object[]             uncertains;
    
    
    private Object[] documentClassifications;
    private Object[]        anatomicalParts;
    private Object[]            pageHeaders;
    private Object[]            pageFooters;
    private Object[]    pageHeaderEvidences;
    private Object[]    pageFooterEvidences;
    private Object[]                persons;
    private Object[]      PersonNamePrefixes;
    private Object[]      PersonNameSuffixes;
    private Object[]            medications;
    private Object[]              activities;
    private Object[]              modifiers;
    private Object[]              severities;
    // private Duration[]              durations;
    private Object[]         nestedSections;
    private Object[]              tableRows;
    private Object[]             utterances;
    private Object[]           vAnnotations;
    
    
    private Object[]             diagnoses;
    private Object[]               symptoms;
    private Object[]               problems;
    private Object[]             conditions;
    private Object[]             procedures;
    private Object[]           observations;
    private Object[]              doseForms;
    private Object[]  clinicalStatusEvidences;
    
   //  private Finding[]            findings;        |  these need to be put in
   // private Treatment[]           treatments;      |
   // private Test[]                tests;           |
    
    /*   These are most of the other Labels that are possible
     *   
     *   I left out categories of symptoms, Diahrea, vitals, IntentToPrecribeAntibiotics, Diabetes, 
     *
    Snippet
    CEMHeader
    CEM
    Acronym
    Fever
    CancerStage
    TumorType
   
   
   
    */
    
    
    
    
    
    
    
    
   
  
   

   
    
    


} // end Class toCommonModel
