/*******************************************************************************
 * ---------------------------------------------------------------------------
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                        Epidemiology and Biostatistics Branch 
 *                                         2019
 *    
 *  This work is licensed under the Creative Commons Attribution 4.0 International License. 
 *  
 *  This license allows you to use, share and  adapt for any purpose, provided:
 *     Provide attribution to the creators of this work within your work.
 *     Indicate if changes were made to this work.
 *     No claim to merchantability, implied warranty, or liability can be made.
 *     
 *   When attributing this code, please make reference to
 *    [citation/url here] .  
 *    
 *     In the absence of a specific paper or url listed above, reference http://clinicalcenter.nih.gov/rmd/eb/nlp
 *  
 *  To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 ******************************************************************************/
// =================================================
/**
 * UIMAUtil contains methods useful across the vinci nlp framework.
 *
 *
 * @author  Guy Divita 
 * @created Jan 20, 2011
 * @revised Feb 20, 2017
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils.uima;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.FsIndexDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.TypePriorities_impl;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.TypeSystemUtil;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;

// import gov.va.chir.model.VAnnotation;


import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.va.chir.model.Line;
import gov.va.chir.model.WordToken;


public class UIMAUtil {

  
  // ----------------------------------------------------
  // Public Variables
  // ----------------------------------------------------
  public final static  boolean WITHOUT_SUBCLASSES = false;  // flag to tell get annotations to get this class but NO sub classes
  public final static  boolean WITH_SUBCLASSES    = true;  // flag to tell get annotations to get this class and any sub classes

  // -----------------------------------------
  /**
   * getAnnotationBySpan retrieves annotations bounded by a start span, and an end span,
   *                     additionally filtered by annotation class, for a given CAS.
   *                     
   * Implementation Note:
   * This method could be much better implemented, if what comes back is known to be
   * sorted by offset, and a mechanism could be used to do a binary search to find
   * the first annotation.  As of now, this method iterates through all annotations
   * to find those that are in range. 
   *                     
   * @param pJCas
   * @param pTypeId  (The annotation type id i.e. WhitespaceToken, Token, Sentence ) (this doens't seem to work with typId!)
   * @param pStart  this is an offset 
   * @param pEnd
   * @return List<Annotation> null if there are none in this range.
   * 
   */
  // -----------------------------------------
  public final static List<Annotation> getAnnotationsBySpan(JCas pJCas, int pTypeId, int pStart, int pEnd) {
     
    List<Annotation> annotationArray = null;
    // retrieve an array of all the annotations for the given class
    List<Annotation> annotationz = UIMAUtil.getAnnotations( pJCas, pTypeId );
    annotationArray = filterAnnotationsBySpan( annotationz, pStart, pEnd);
    return ( annotationArray );
    
  } // end Method getAnnotationsBySpan() --------
  
  //-----------------------------------------
  /**
   * getAnnotationBySpan retrieves annotations bounded by a start span, and an end span,
   *                     additionally filtered by annotation class, for a given CAS.
   *                     
   * Implementation Note:
   * This method could be much better implemented, if what comes back is known to be
   * sorted by offset, and a mechanism could be used to do a binary search to find
   * the first annotation.  As of now, this method iterates through all annotations
   * to find those that are in range. 
   *                     
   * @param pJCas
   * @param pTypeId  (The annotation type id i.e. WhitespaceToken, Token, Sentence ) (this doens't seem to work with typId!)
   * @param pStart  this is an offset 
   * @param pEnd
   * @return List<Annotation> null if there are none in this range.
   * 
   */
  // -----------------------------------------
  public final static FSArray getAnnotationFSArrayBySpan(JCas pJCas, int pTypeId, int pStart, int pEnd) {
     
    List<Annotation> annotationList = null;
    FSArray annotationArray = null;
    // retrieve an array of all the annotations for the given class
    List<Annotation> annotationz = UIMAUtil.getAnnotations( pJCas, pTypeId );
    annotationList = filterAnnotationsBySpan( annotationz, pStart, pEnd);
    annotationArray = list2FsArray( pJCas, annotationList );
    
    
    return ( annotationArray );
    
  } // end Method getAnnotationsBySpan() --------
  
//-----------------------------------------
  /**
   * getAnnotationBySpan retrieves annotations bounded by a start span, and an end span,
   *                     additionally filtered by annotation class, for a given CAS.
   *                     
   * Implementation Note:
   * This method could be much better implemented, if what comes back is known to be
   * sorted by offset, and a mechanism could be used to do a binary search to find
   * the first annotation.  As of now, this method iterates through all annotations
   * to find those that are in range. 
   * 
   * @param pJCas
   * @param pType
   * @param pStart
   * @param pEnd
   * @return List<Annotation>
   */
  // -----------------------------------------
  public final static List<Annotation> getAnnotationsBySpan(JCas pJCas, Type pType, int pStart, int pEnd) {
    List<Annotation> annotationArray = null;
    // retrieve an array of all the annotations for the given class
    List<Annotation> annotationz = UIMAUtil.getAnnotations( pJCas, pType );
    annotationArray = filterAnnotationsBySpan( annotationz, pStart, pEnd);
    return ( annotationArray );
    
  } // end Method getAnnotationBySpan() ------

  // =======================================================
  /**
   * getAnnotationsBySpan [Summary here]
   * 
   * @param pJCas
   * @param pStart
   * @param pEnd
   * @return List<Annotation>
   */
  // =======================================================
  public final static List<Annotation> getAnnotationsBySpan(JCas pJCas, int pStart, int pEnd) {

    List<Annotation> annotationz = UIMAUtil.getAnnotations( pJCas  );
    List<Annotation> annotationArray = filterAnnotationsBySpan( annotationz, pStart, pEnd);
    return ( annotationArray );
    
  
  }  // End Method getAnnotationsBySpan() ======================
  

  // =======================================================
  /**
   * getAnnotationsBySpan retrieves annotations bounded by span,
   * and type
   * with an option to retrieve annotations that are subclasses
   * of the passed in type
   * 
   *   [TBD] think of a better way to do this -
   * @param pJCas
   * @param pAnnotationType
   * @param pBegin
   * @param pEnd
   * @param pIncludeSubclasses
   * @return List<Annotation>
   */
  // =======================================================
  public final static List<Annotation> getAnnotationsBySpan(JCas pJCas, Type pAnnotationType, int pBegin, int pEnd, boolean pIncludeSubclasses) {
  
    ArrayList<Annotation> returnVal = null;
    
    List<Annotation> allAnnotations = UIMAUtil.getAnnotationsBySpan(pJCas, pBegin, pEnd);
    
    if ( allAnnotations != null) {
      returnVal = new ArrayList<Annotation>();
      for (Annotation annotation : allAnnotations ) {
        if ( annotation != null ) {
          Type labelType = annotation.getType();
          String typeName = pAnnotationType.getName();
          String annotationName = annotation.getClass().getSimpleName(); 
          if ( typeName.compareTo(annotationName ) == 0  ) {
        
              returnVal.add( annotation);
         
        } else if (  isInstanceOf( annotation, pAnnotationType )) {//  annotation.getClass().isInstance( pAnnotationType.getClass() )) {  
          if ( pIncludeSubclasses )
            returnVal.add( annotation);
        }
      }
      }
      
    }
    if ( returnVal != null && returnVal.size() == 0 ) returnVal = null;
    
    
    
    
    
    return returnVal;
    
  } // End Method getAnnotationsBySpan() ======================


  // =======================================================
  /**
   * getAnnotationsBySpan retrieves annotations bounded by span,
   * and type
   * with an option to retrieve annotations that are subclasses
   * of the passed in type
   * 
   *   [TBD] think of a better way to do this -
   * @param pJCas
   * @param pAnnotationTypeId
   * @param pBegin
   * @param pEnd
   * @param pIncludeSubclasses
   * @return List<Annotation>
   */
  // =======================================================
  public final static List<Annotation> getAnnotationsBySpan(JCas pJCas, int pAnnotationTypeId, int pBegin, int pEnd, boolean pIncludeSubclasses) {
  
    List<Annotation> returnVal = null;
    
    Type annotationType = pJCas.getCasType(pAnnotationTypeId);
    returnVal = getAnnotationsBySpan( pJCas,  annotationType,  pBegin,  pEnd,  pIncludeSubclasses);
      
    
    
    return returnVal;
    
  } // End Method getAnnotationsBySpan() ======================

  
  
  // =======================================================
  /**
   * isInstanceOf 
   * 
   * @param annotation
   * @param pAnnotationType
   * @return boolean
   */
  // =======================================================
  public final static boolean isInstanceOf(Annotation annotation, Type pAnnotationType) {

    String childClassName = annotation.getClass().getName();
    Class<?>  childClass = annotation.getClass();
    String referenceClassName = pAnnotationType.getName();
    boolean returnVal = false;
    boolean notDone = true;
    
    while ( notDone) {
    if ( childClassName.equals( referenceClassName )) {
      returnVal = true;
      break;
    }
     childClass = childClass.getSuperclass();
     
     if ( childClass == null  || childClass.equals(Object.class) || childClass.equals( org.apache.uima.jcas.tcas.Annotation.class )  ){
       notDone = false;
       break;
     }
     childClassName = childClass.getName();
    }
    return returnVal;
  } // End Method isInstanceOf() ======================
  

  // -----------------------------------------
  /**
   * getAnnotationsBySimilarSpan returns annotations
   * that fit within the span of the annotation passed in.
   * 
   * @param pJCas
   * @param pAnnotation
   * @return List<Annotation>
   */
  // -----------------------------------------
  public final static List<Annotation> getAnnotationsBySimilarSpan(JCas pJCas, Annotation pAnnotation) {
    
    ArrayList<Annotation> annotations = new ArrayList<Annotation>();
  
    AnnotationIndex<Annotation> tIndex = pJCas.getAnnotationIndex();
    if ( tIndex != null ) {
      FSIterator<Annotation> tIterator = tIndex.subiterator(pAnnotation);
      if ( tIterator != null ) {
         while ( tIterator.hasNext()) {
           annotations.add( tIterator.next());
         } // end loop thru while
        
      } // end if tIterator != null
    } // end if tIndex is not null
    if ( annotations.size() < 1)
      annotations = null;
    return annotations;
  } // end Method getAnnotationsBySimilarSpan();

// -----------------------------------------
/**
 * getFilteredAnnotations retrieves all annotations minus those annotations that are on the hash set list.
 * 
 * @param pJCas
 * @param excludedTypeHashSet (HashSet of Type)
 * @return List<Annotation>
 */
// -----------------------------------------
public final static List<Annotation> getFilteredAnnotations(JCas pJCas, HashSet<String> excludedTypeHashSet) {

  ArrayList<Annotation> annotations = null;
  AnnotationIndex<Annotation> annotationz = pJCas.getAnnotationIndex();

  if ( annotationz != null) {
    annotations = new ArrayList<Annotation>(annotationz.size());
    List<Annotation> someAnnotations = UIMAUtil.annotationIndex2List(annotationz);
    for (Annotation annotation: someAnnotations ) {
      if ( !excludedTypeHashSet.contains( annotation.getType().getName() )) 
        annotations.add( annotation);
    } // end loop thru all annotations
  }
  return annotations;
} // end Method getFilteredAnnotations() ---------

  // -----------------------------------------
  /**
   * getAnnotations returns all annotations from this CAS
   * @param pJCas
   * @return List<Annotation>
   * 
   */
  // -----------------------------------------
  public final static List<Annotation> getAnnotations(JCas pJCas) {
    List<Annotation> annotations = null;
    AnnotationIndex<Annotation> annotationz = pJCas.getAnnotationIndex();
    if ( annotationz != null )
      annotations =  UIMAUtil.annotationIndex2List(annotationz);
    
    return annotations;
  } // end Method getAnnotations() -----------------

  // -----------------------------------------
  /**
   * getAnnotation retrieves the first of any annotations 
   * of the type passed in.  Typically, this method is used
   * to pull annotations where it is know that there should
   * only be one per document, like documentHeader.
   * 
   * @param pJCas
   * @param type
   * @return Annotation
   */
  // -----------------------------------------
  public final static Annotation getAnnotation(JCas pJCas, int type) {
    Annotation annotation = null;
    
    List<Annotation> annotations = getAnnotations( pJCas, type);
    if ( annotations != null && annotations.size() > 0 )
      annotation = annotations.get(0);
    
    return annotation;
  } // end Method getAnnotation() ------------

  // -----------------------------------------
  /**
   * filterAnnotationBySpan retrieves annotations bounded by a start span, and an end span from the List<Annotation> passed in.
   * It is assumed that the list is span ordered. 
   * 
   * @param pAnnotations
   * @param pStart
   * @param pEnd
   * @return List<Annotation> (new list), and null if there are none in this range.
   * 
   */
  // -----------------------------------------
    public final static List<Annotation> filterAnnotationsBySpan( List<Annotation> pAnnotations, int pStart, int pEnd ) {
    
      ArrayList<Annotation> annotationArray = null;
    if ( pAnnotations  != null && !pAnnotations.isEmpty() ) {
     
      annotationArray = new ArrayList<Annotation>(pAnnotations.size());
      int listSize = pAnnotations.size();
      for ( int i = 0; i < listSize; i++  ) {
        Annotation anAnnotation = pAnnotations.get(i);
        if (( anAnnotation.getBegin() >= pStart) && (anAnnotation.getEnd() <= pEnd )) 
          annotationArray.add( anAnnotation );
        
      }
    } 
    if (annotationArray != null && annotationArray.isEmpty()) 
      annotationArray = null;
    
    return annotationArray;
  } // end Method filterAnnotationsBySpan() -----

    
 // =================================================
  /**
   * fuzzyFindAnnotationsBySpan 
   * 
   * @param pLikeAnnotations
   * @param begin
   * @param end
   * @param lastSeen
   * @return list<Annotation>
  */
  // =================================================
 public static List<Annotation> fuzzyFindAnnotationsBySpan(List<Annotation> pAnnotations, int pStart, int pEnd, int[] pLastSeen) {

    
       ArrayList<Annotation> annotationArray = null;
     if ( pAnnotations  != null ) {
      
       UIMAUtil.sortByOffset(pAnnotations);
       annotationArray = new ArrayList<Annotation>(pAnnotations.size());
       int lastSeen = pLastSeen[0];
       if ( lastSeen >= pAnnotations.size() || lastSeen < 0 )
         lastSeen = 0;
     
       for ( int i = lastSeen; i < pAnnotations.size(); i++ ) {
       
         pLastSeen[0] = i;
         Annotation anAnnotation = pAnnotations.get(i);
         // ----------------------------------------------
         // Encompassing matches
         if (( anAnnotation.getBegin() >= pStart) && (anAnnotation.getEnd() <= pEnd )) 
           annotationArray.add( anAnnotation );
         
         // ----------------------------------------------
         // overlapping on right
         else if (( anAnnotation.getBegin() >= pStart) && (anAnnotation.getBegin() <= pEnd )) 
           annotationArray.add( anAnnotation );
        
         // ----------------------------------------------
         // overlapping on left
         else if (( anAnnotation.getBegin() <= pStart) && (anAnnotation.getEnd() > pStart )) 
           annotationArray.add( anAnnotation );
         
         // ----------------------------------------------
         // overlapping on both sides 
         else if (( anAnnotation.getBegin() < pStart) && (anAnnotation.getEnd() > pEnd )) 
           annotationArray.add( anAnnotation );
         
         // --------------------------------------------
         // If the begin and end are before this annotation all the rest of the
         // annotations are not going to match, you might as well stop here
         else if ( anAnnotation.getBegin() > pStart && anAnnotation.getEnd() > pEnd ) 
           break;
       }
     }  
     return annotationArray;
   } // end Method fuzzyFindAnnotationsBySpan() -----
  

// -----------------------------------------
    /**
     * fuzzyFindAnnotationBySpan retrieves annotations that overlap in some way by a start span, and an end span.
     *                     
     *    
     * @param pAnnotations
     * @param pStart
     * @param pEnd
     * @return List<Annotation> (new list), and null if there are none in this range.
     * 
     */
    // -----------------------------------------
      public final static List<Annotation> fuzzyFindAnnotationsBySpanAux( List<Annotation> pAnnotations, int pStart, int pEnd ) {
     
        return fuzzyFindAnnotationsBySpan( pAnnotations,  pStart,  pEnd ) ;
      }
      
      
 // -----------------------------------------
    /**
     * fuzzyFindAnnotationBySpan retrieves annotations that overlap in some way by a start span, and an end span.
     *                     
     *    
     * @param pAnnotations
     * @param pStart
     * @param pEnd
     * @return List<Annotation> (new list), and null if there are none in this range.
     * 
     */
    // -----------------------------------------
      public final static List<Annotation> fuzzyFindAnnotationsBySpan( List<Annotation> pAnnotations, int pStart, int pEnd ) {
      
        ArrayList<Annotation> annotationArray = null;
      if ( pAnnotations  != null ) {
       
        UIMAUtil.sortByOffset(pAnnotations);
        annotationArray = new ArrayList<Annotation>(pAnnotations.size());
        for ( Annotation anAnnotation : pAnnotations ) {
        
          // ----------------------------------------------
          // Encompassing matches
          if (( anAnnotation.getBegin() >= pStart) && (anAnnotation.getEnd() <= pEnd )) 
            annotationArray.add( anAnnotation );
          
          // ----------------------------------------------
          // overlapping on right
          else if (( anAnnotation.getBegin() >= pStart) && (anAnnotation.getBegin() <= pEnd )) 
            annotationArray.add( anAnnotation );
         
          // ----------------------------------------------
          // overlapping on left
          else if (( anAnnotation.getBegin() <= pStart) && (anAnnotation.getEnd() > pStart )) 
            annotationArray.add( anAnnotation );
          
          // ----------------------------------------------
          // overlapping on both sides 
          else if (( anAnnotation.getBegin() < pStart) && (anAnnotation.getEnd() > pEnd )) 
            annotationArray.add( anAnnotation );
          
          // --------------------------------------------
          // If the begin and end are before this annotation all the rest of the
          // annotations are not going to match, you might as well stop here
          else if ( anAnnotation.getBegin() > pStart && anAnnotation.getEnd() > pEnd )
            break;
        }
      }  
      return annotationArray;
    } // end Method fuzzyFindAnnotationsBySpan() -----
    
  // -----------------------------------------
  /**
   * fuzzyFindAnnotationsBySpan retrieves annotations that overlap in some way by a start span, and an end span.
   *                     
   * 
   * @param pJCas
   * @param type
   * @param begin
   * @param end
  
   * @return List<Annotation> (new list), and null if there are none in this range.
   */
  // -----------------------------------------
  public final static List<Annotation> fuzzyFindAnnotationsBySpan(JCas pJCas, int type, int begin, int end)  {
  
    List<Annotation> returnValue = null;
    List<Annotation>      bigNet = null;
    try {
      bigNet = getAnnotations( pJCas, type );
      
      returnValue = fuzzyFindAnnotationsBySpan( bigNet, begin, end ) ;
    } catch ( Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,  "issue with fuzzyFindAnnotationBySpan " + e.toString());
      throw e;
    }

      return returnValue;
  } // end Method fuzzyFindAnnotationsBySpan() -----

      
  
 // -----------------------------------------
 /**
  * fuzzyFindAnnotationsBySpan retrieves annotations that overlap in some way by a start span, and an end span.
  *                     
  * 
  * @param pJCas
  * @param type
  * @param begin
  * @param end
  * @param pProcessSubclasses
 
  * @return List<Annotation> (new list), and null if there are none in this range.
  */
 // -----------------------------------------
 public final static List<Annotation> fuzzyFindAnnotationsBySpan(JCas pJCas, int type, int begin, int end, boolean pProcessubClasses)  {
 
   List<Annotation> returnValue = null;
   List<Annotation>      bigNet = null;
   try {
     bigNet = getAnnotations( pJCas, type, pProcessubClasses );
     
     returnValue = fuzzyFindAnnotationsBySpan( bigNet, begin, end ) ;
   } catch ( Exception e) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL,  "issue with fuzzyFindAnnotationBySpan " + e.toString());
     throw e;
   }

     return returnValue;
 } // end Method fuzzyFindAnnotationsBySpan() -----

     
         
  
  
  // -----------------------------------------
    /**
     * fuzzyFindAnnotationsBySpan retrieves annotations that overlap in some way by a start span, and an end span.
     *                     
     * 
     * @param pJCas
     * @param type
     * @param begin
     * @param end
     * @return List<Annotation> (new list), and null if there are none in this range.
     */
    // -----------------------------------------
    public final static List<Annotation> fuzzyFindAnnotationsBySpanObs(JCas pJCas, int type, int begin, int end) {
     
      List<Annotation> returnValue = null;
      List<Annotation> bigNet = null;
      
      try {
       bigNet = getAnnotationsBySpan( pJCas, type, begin-100, end+100);
      
      } catch ( Exception e) {
      try {
        bigNet = getAnnotationsBySpan( pJCas, type, begin, end+100 );
      } catch ( Exception e2) {
        try {
          bigNet = getAnnotationsBySpan( pJCas, type, begin, end );
        } catch (Exception e3) {}
      }
    }
    if ( bigNet != null && !bigNet.isEmpty())
      returnValue = fuzzyFindAnnotationsBySpan(bigNet,begin, end);
      
      return returnValue;
    } // end Method fuzzyFindAnnotationsBySpan() -----

    
 // -----------------------------------------
    /**
     * fuzzyFindAnnotationsBySpan retrieves annotations that overlap in some way by a start span, and an end span.
     *                     
     * 
     * @param pJCas
     * @param pType
     * @param begin
     * @param end
     * @param subClasses
     * @return List<Annotation> (new list), and null if there are none in this range.
     */
    // -----------------------------------------
    public final static List<Annotation> fuzzyFindAnnotationsBySpan(JCas pJCas, Type pType, int pTypeId, int begin, int end, boolean subClasses) {
     
      List<Annotation> returnValue = null;
      List<Annotation> bigNet = null;
      try {
       bigNet = getAnnotationsBySpan( pJCas, begin-100, end+100 );
      } catch ( Exception e) {
        try {
          bigNet = getAnnotationsBySpan( pJCas, pTypeId, begin, end+100 );
        } catch ( Exception e2) {
          try {
            bigNet = getAnnotationsBySpan( pJCas, pTypeId, begin, end );
          } catch (Exception e3) {}
        }
      }
      if ( bigNet != null && !bigNet.isEmpty()) {
      
        ArrayList<Annotation> smallerNet = new ArrayList<Annotation>();
        for ( Annotation annotation : bigNet ) {
          if ( isInstanceOf( annotation, pType ) ) {
            if ( subClasses || annotation.getType().equals( pType ) )
              smallerNet.add( annotation );
          }
        }
          
        returnValue = fuzzyFindAnnotationsBySpan(smallerNet,begin, end);
      }
      return returnValue;
    } // end Method fuzzyFindAnnotationsBySpan() -----
    
  // -----------------------------------------
  /**
   * getContained returns the set of annotations that fall within
   * the begin and end offsets specified.
   * 
   * Note: This method could be better implemented if it was known that
   * the set was in offset order.
   * 
   * @param pSetName
   * @param pStart
   * @param pEnd
   * @return List<Annotation>  (null if empty)
   */
  // -----------------------------------------
  public final static List<Annotation> getContained(List<Annotation> pAnnotations, int pStart, int pEnd) {

     ArrayList<Annotation> annotationArray = null;
  if ( pAnnotations  != null && !pAnnotations.isEmpty() ) {
   
    annotationArray = new ArrayList<Annotation>(pAnnotations.size());
    int listSize = pAnnotations.size();
    for ( int i = 0; i < listSize; i++  ) {
      Annotation anAnnotation = pAnnotations.get(i);
      if (( pStart >= anAnnotation.getBegin() ) && (pEnd <= anAnnotation.getEnd() ))  {
        annotationArray.add( anAnnotation );
      } else if (  anAnnotation.getEnd() > pStart) 
        break;
    } // end loop 
  } 
  if (annotationArray != null && annotationArray.isEmpty())  annotationArray = null;
  
    
    
    return annotationArray;
  } // end Method getContained() ------------
 
  
// =======================================================
  /**
   * getOverlapping retrieves those annotations that overlap in some way
   * to the offsets passed in.  It is assumed that the pSetName passed in
   * is ordered by offset assending.
   * 
   * @param pSetName
   * @param pStart
   * @param pEnd
   * @return List<Annotation>
   */
  // =======================================================
  public final static List<Annotation> getOverlapping(List<Annotation> pSetName, int pStart, int pEnd) {
   
    
    ArrayList<Annotation> resultSet = null;
    
    if ( pSetName != null && pSetName.size() > 0 ) {
    resultSet = new ArrayList<Annotation>( pSetName.size());
    
    for ( Annotation an : pSetName) {
      
      if ( pEnd > an.getBegin()  && pStart < an.getEnd() ) {
        resultSet.add ( an );
      } // end if contained 
      
    } // end loop through annotations
    if ( resultSet.size() == 0)
      resultSet = null;
    
    }
    return resultSet;
  } // End Method getOverlapping() ======================
  

  //-----------------------------------------
  /**
   * list2FsArray converts an arrayList  into an FSArray 
   * The FSArray is added to the indexes.
   * 
   * @param pJCas
   * @param pT
   * @return FSArray
   */
  // -----------------------------------------
  @SuppressWarnings("rawtypes")
  public final static FSArray list2FsArray (JCas pJCas, List pT ) {

    FSArray fsArray = null;
    if (pT != null) {
      fsArray = new FSArray(pJCas, pT.size());
      int i = 0;
      for (Iterator uI = pT.iterator(); uI.hasNext();) {
        Annotation aT = (Annotation) uI.next();
        fsArray.set(i++, aT);
      }
      fsArray.addToIndexes(pJCas);
    } // end loop through Tokens
    return fsArray;
  } //end Method list2FsArray() --------------


//-----------------------------------------
  /**
   * fSArray2List converts an FSArray of Annotation into
   * a List of Annotation.
   * 
   * @param pJCas
   * @param pT
   * @return List
   */
  // -----------------------------------------
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public final static List fSArray2List (JCas pJCas, FSArray pT ) {
      
    ArrayList returnValue = new ArrayList();
    if ( pT != null ) {
    
    for ( int i = 0; i < pT.size(); i++)
      returnValue.add( pT.get(i));
    
    if ( returnValue.size() == 0) returnValue = null;
    }
    return returnValue;
  } //end Method fSArray2List() --------------

  
  // -----------------------------------------
  /**
   * Order this List of Annotation by ascending offset order
   * 
   * @param pTerms
   */
  // -----------------------------------------
  public final static void sortByOffset(List<Annotation> pTerms) {
       
   if ( mOffsetComparator == null)
     mOffsetComparator = new OffsetComparator();
   if (( pTerms != null ) && ( pTerms.size() > 1 ))
   	Collections.sort( pTerms, mOffsetComparator );
    
  } // end Method sortByOffset() ------------------
  
  // ==========================================
  /**
   * sortByOffsetDescending  (reverse order)
   *   note - need to think how overlapping
   *   annotation spans get handled here] 
   *
   * @param pTerms
   */
  // ==========================================
  public final static void sortByOffsetDescending(List<Annotation> pTerms) {
    if ( mOffsetDesendingComparator == null)
      mOffsetDesendingComparator = new OffsetDesendingComparator();
    if (( pTerms != null ) && ( pTerms.size() > 1 ))
     Collections.sort( pTerms, mOffsetDesendingComparator );
  } // end Method sortByOffsetDescending() ====
  

  // -----------------------------------------
  /**
   * annotationIndex2List returns a List<Annotation> from the
   * annotationIndex.  This method uniqs the list by span+annotation name.
   * 
   * @param pAnnotationIndex
   * @return List<Annotation>
   */
  // -----------------------------------------
  public final static List<Annotation> annotationIndex2List(AnnotationIndex<Annotation> pAnnotationIndex) {
   
   
    List<Annotation> returnVal = null;
    int i = 0;
    try {
      Annotation[] buff = null;
    if ( pAnnotationIndex != null  ) {
      HashSet<Annotation> aSet = new HashSet<Annotation>(pAnnotationIndex.size() *2);
      if (( pAnnotationIndex != null ) && ( pAnnotationIndex.size() > 0)) {
        buff  = new Annotation[pAnnotationIndex.size() ];
      
       Iterator<Annotation> itr = pAnnotationIndex.iterator() ;
       if ( itr != null )
         while ( itr.hasNext() ) {
           Annotation a =  itr.next();
           if ( !aSet.contains( a)  ){
             buff[i++] = a;
             aSet.add( a);
          }
          returnVal = Arrays.asList(buff);
         
        }
      // sortByOffset( returnValue); // <--- should not be necessary
      
        aSet = null;
      } 
      
    
    }
    } catch (Exception e) {}
    return returnVal;
  } // end Method annotationInde2List() -----------

  // -----------------------------------------
  /**
   * getAnnotationsFromDocument retrieves a List<Annotation> from the cas
   * of the given type.
   * 
   * @param pJCas
   * @param pType
   * @return List<Annotation>
   */
  // -----------------------------------------
  public final static List<Annotation> getAnnotationsFromDocument(JCas pJCas, int pType) {
 
    List<Annotation> returnValue = null;
    
    if ( pJCas != null && pType != -1) {
      AnnotationIndex<Annotation> index = pJCas.getAnnotationIndex( pType);
     
     returnValue = annotationIndex2List( index);
    }
    return returnValue;
  } // end Method getAnnotationsFromDocument() ----

  // -----------------------------------------
  /**
   * getAnnotations is a synonym for getAnnotationsFromDocument
   * It retrieves a List<Annotation> from the cas, given a typeId.
   * 
   * @param pJCas
   * @param pType
   * @return List<Annotation>
   */
  // -----------------------------------------
  public final static List<Annotation> getAnnotations(JCas pJCas, int pType) {
    return getAnnotationsFromDocument( pJCas, pType);
  } // end Method getAnnotations() ----------------
  

  // -----------------------------------------
  /**
   * getAnnotations retrieves the set of annotations given a list of annotation types.
   * 
   * @param pJCas
   * @param pAnnotationTypes
   * @return List<Annotation>
   */
  // -----------------------------------------
  public final static List<Annotation> getAnnotations(JCas pJCas, int[] pAnnotationTypes) {
   
    ArrayList<Annotation> returnAnnotations = null;
    if ( pAnnotationTypes != null ) {
      returnAnnotations = new ArrayList<Annotation>();
      for ( int i = 0; i < pAnnotationTypes.length; i++ ) {
        List<Annotation> buff = getAnnotationsFromDocument( pJCas, pAnnotationTypes[i]);
        if ( buff != null )
        	returnAnnotations.addAll(buff);        
      } // end loop through annotationTypes
    } // end if there are any annotations to work with
    
    return ( returnAnnotations);
  } // end Method getAnnotations() -----------

  // -----------------------------------------
  /**
   * getAnnotations is a synonym for getAnnotationsFromDocument
   * It retrieves a List<Annotation> from the cas, given a type.
   * Not the subtypes.
   * 
   * 
   * @param pJCas
   * @param pType
   * @return  List<Annotation>
   */
  // -----------------------------------------
  public final static List<Annotation> getAnnotations(JCas pJCas, Type pType) {
    List<Annotation> returnValue = null;
    
    if ( pJCas != null && pType != null  ) {
      
      AnnotationIndex<Annotation> index = pJCas.getAnnotationIndex( pType);
      if (index != null )
        returnValue = annotationIndex2List( index);
    }
    return returnValue;
  } // end Method getAnnotations () --------------------


  // -----------------------------------------
  /**
   * getAnnotations gets annotation of type pType, with the option
   * to get subclasses or not.
   * 
   * @param pJCas
   * @param pType
   * @param pWithSubclasses 
   * @return List
   */
  // -----------------------------------------
  public final static List<Annotation> getAnnotations(JCas pJCas, Type pType, boolean pWithSubclasses) {
   
    List <Annotation> results = null;
    String className = pType.getName();
    
    if ( !pWithSubclasses ) {
      results = getAnnotations( pJCas, pType);
      if (results != null && results.size() > 0 ) {
        ArrayList<Annotation> newResults = new ArrayList<Annotation>( results.size());
        for ( Annotation result: results ) {
           String resultClassname = result.getClass().getName();
           if ( className.equals(resultClassname ))
             newResults.add(result);
        } // end loop through results
        results = newResults;  
      }
    } else {
      results = getAnnotations( pJCas);
     if (results != null && results.size() > 0 ) {
       ArrayList<Annotation> newResults = new ArrayList<Annotation>( results.size());
       for ( Annotation result: results ) {
         if ( isInstanceOf( result, pType ) )
           newResults.add(result);
       } // end loop through results
       results = newResults;
     } // end if there are results
   } // end if you don't want subtypes

   return results; 
  } // end Method getAnnotations() -----------
  
  

  // -----------------------------------------
  /**
   * getAnnotations gets annotation of type pType, with the option
   * to get subclasses or not.
   * 
   * @param pJCas
   * @param pType
   * @param pWithSubclasses 
   * @return List
   */
  // -----------------------------------------
  public final static List<Annotation> getAnnotations(JCas pJCas, int pType, boolean pWithSubclasses) {
   
    List <Annotation> results = null;
    
    Type type = pJCas.getCasType(pType);
    String className = type.getName();
    
    if ( !pWithSubclasses ) {
      results = getAnnotations( pJCas, pType);
      
      if (results != null && results.size() > 0 ) {
        ArrayList<Annotation> newResults = new ArrayList<Annotation>( results.size());
        for ( Annotation result: results ) {
           String resultClassname = result.getClass().getName();
           if ( className.equals(resultClassname ))
             newResults.add(result);
        } // end loop through results
        results = newResults;  
      
      }
    }
    else {
      results = getAnnotations( pJCas);
     if (results != null && results.size() > 0 ) {
       ArrayList<Annotation> newResults = new ArrayList<Annotation>( results.size());
       for ( Annotation result: results ) {
         if ( isInstanceOf( result, type ) )
           newResults.add(result);
       } // end loop through results
       results = newResults;
     } // end if there are results
   } // end if you don't want subtypes

   return results; 
  } // end Method getAnnotations() -----------

  // =======================================================
  /**
   * getAnnotations Retrieves annotations of the class passed in,
   * with the option to get all instances of subclasses or not.
   * 
   * @param pJCas
   * @param pClass
   * @param pWithSubclasses
   * @return List<Annotation>
   */
  // =======================================================
  public final static List<Annotation> getAnnotations(JCas pJCas, Class<?> pClass, boolean pWithSubclasses) throws Exception {
    
    
    List<Annotation> returnValue = null;
    try {
      
    
      Type type = pJCas.getTypeSystem().getType( pClass.getName());
      
      
      returnValue =  getAnnotations( pJCas, type, pWithSubclasses);
    
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue getting the instances of the class " + pClass.getName() + " " + e.getMessage();
      GLog.println(GLog.ERROR_LEVEL, UIMAUtil.class, "getAnnotations", msg);
      throw new Exception (msg);
    }
      
    return returnValue;
  }  // End Method getAnnotations() ======================
  

  // -----------------------------------------
  /**
   * getAnnotations retrieves annotations given a string version of the type name
   * 
   * @param pJCas
   * @param pLabel
   * @return List<Annotation>
   * @throws InvocationTargetException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   * @throws NoSuchMethodException 
   * @throws IllegalArgumentException 
   * @throws SecurityException 
   */
  // -----------------------------------------
  public final static List<Annotation> getAnnotations(JCas pJCas, String pLabel) throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    List<Annotation> list = null;
    
    org.apache.uima.cas.Type zType = getAnnotationType( pJCas, pLabel);
    
    if ( zType != null )
      list = getAnnotations( pJCas, zType);
    
    return list;
  }

  // -----------------------------------------
  /**
   * getAnnotationType retrieves the Type of the Annotation, given a string
   * that can be mapped to an annotation class.  The mapping between string and
   * annotation is done first by looking for exact name space matches, then by
   * names, looking for the first matching that might be in the chir.model, or
   * vinci.model.  
   * 
   * @param pJCas
   * @param pLabelName
   * @return org.apache.uima.cas.Type
   * @throws NoSuchMethodException 
   * @throws SecurityException 
   * @throws InvocationTargetException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   * @throws IllegalArgumentException 
   */
  // -----------------------------------------
  public final static org.apache.uima.cas.Type getAnnotationType(JCas pJCas, String pLabelName) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {

    org.apache.uima.cas.Type  returnType = null;
    TOP            uimaAnnotation = null; 
    Class<?>                  labelClass = UIMAUtil.mapLabelToUIMAClass( pLabelName );
    if ( labelClass != null ) {
    Constructor<?>      labelConstructor = labelClass.getConstructor(new Class[] { JCas.class });
    if ( labelConstructor != null ) {
      try {
      uimaAnnotation = (Annotation) labelConstructor.newInstance(pJCas);
      } catch (Exception e) {
        // e.printStackTrace();
        uimaAnnotation = (TOP) labelConstructor.newInstance(pJCas);
      }
      if ( uimaAnnotation != null)
        returnType = uimaAnnotation.getType();
    }
    }
    return returnType ;
  } // end Method getAnnotationType() --------------


  // -----------------------------------------
  /**
   * getAnnotations retrieves the set of annotations from the set of annotation types
   * 
   * @param pJCas
   * @param pTypes
   * @return List<Annotation>
   */
  // -----------------------------------------
  public final static List<Annotation> getAnnotations(JCas pJCas, Type[] pTypes) {
   
    List<Annotation> allAnnotations = new ArrayList<Annotation>();
    HashSet<Annotation> aSet = new HashSet<Annotation>();
    if ( pTypes != null ) {
      for ( Type aType : pTypes) {
        List<Annotation> someAnnotations = getAnnotations( pJCas, aType);
        if ( someAnnotations != null )
          
          for ( Annotation a : someAnnotations ) {
            
            // ------------------------
            // filter to only those that have not been added yet
            if ( !aSet.contains( a)  ){
              allAnnotations.add( a);
              aSet.add( a);
            }
          } // end loop through some annotations
      } // end loop thru each type
    } // end if there are any types
    
    return allAnnotations;
  } // end Method getAnnotations() -----------------


  
// =======================================================
  /**
   * getEnclosingAnnotation gets the annotation of the passed in type
   * that overlaps in some way.  This is s 
   * 
   * @param pJCas
   * @param pAnnotation
   * @param pTypeIndexId
   * @return List<Annotation>
   */
  // =======================================================
  public final static List<Annotation> getEnclosingAnnotation(JCas pJCas, Annotation pAnnotation, int pTypeIndexId) {
   
    List<Annotation> returnVal = null;
    if ( pAnnotation != null)
      returnVal = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, pTypeIndexId, pAnnotation.getBegin(), pAnnotation.getEnd());
      
    return returnVal;
  }  // End Method getEnclosingAnnotation() ======================
  

  //------------------------------------------
  /**
   * getAnnotationFromList returns the index within the given list of the
   * annotation by span.  This algorithm uses a dumb and stupid iterate through
   * the list until you find the annotation, or have gone by the offset
   * of the annotation in the index.  
   * 
   * One can pass in the last seen index id, so you don't have to always
   * start from the beginning of the list.  This is helpful if you are
   * getting annotations that are themselves sequential.
   * 
   * The assumption is that the list passed in is sorted by offset.
   * 
   * If the an acceptable annotation is not found, the negative of the annotation
   * offset beyond the one being looked for is returned.
   * 
   *
   * @param pList        (offset sorted list of annotations)
   * @param pConcept     (the offsets of this annotation are used to find annotations in the set) 
   * @param pLastSeen
   * @return int         (the index of the annotation in the list passed in)
   */
  // ------------------------------------------
  public final static int getAnnotationFromList(List<Annotation> pList, Annotation pConcept, int pLastSeen) {
   
    int  returnVal = -1;
    int  lastSeen = pLastSeen;
    if (pLastSeen < 0 ) lastSeen = -pLastSeen;
    if (pList != null && pList.size() > 0 ) {
      Annotation anAnnotation = null;
      for ( int i = lastSeen; i < pList.size(); i++ ) {
        anAnnotation = pList.get(i);
        int goldBegin = anAnnotation.getBegin();
      //  int goldEnd   = anAnnotation.getEnd();
        // GLog.println(GLog.ERROR_LEVEL, this.getClass(), "method", " Looking at token " + goldBegin + "|" + goldEnd + "|" + "against |" + pConcept.getBegin());
        if ( goldBegin == pConcept.getBegin() )
             {
        	// GLog.println(GLog.ERROR_LEVEL, this.getClass(), "method", "found " + i + "|" + pList.get(i).getCoveredText() + "|");
          returnVal =  i;
          
          //  GLog.println(GLog.ERROR_LEVEL, this.getClass(), "method", " ----------------- found section " + returnVal);
          break;
        } // end if this annotation is surrounded by this section 
        else if ( anAnnotation.getEnd() > pConcept.getEnd()) {
          // GLog.println(GLog.ERROR_LEVEL, this.getClass(), "method", "Went beyond");
          returnVal = -i;
          break;
        }
      } // end loop through the annotations of the list
    } // end if there are any golds
    
    return returnVal;
    
  }  // End Method getSentence() -----------------------
  

  // ------------------------------------------
  /**
   * getText returns the text between the begin and end offsets
   *   (if the pEnd is beyond the content of the document,
   *    the content from pBegin to the end of the document is returned).
   *    
   * @param pJCas
   * @param pBegin
   * @param pEnd
   * @return String
   */
  // ------------------------------------------
  public final static String getText(JCas pJCas, int pBegin, int pEnd) {
    
    String returnVal = null;
    int end = 0;
    String docText = pJCas.getDocumentText();
    
    if ( docText != null ) {
      int docTextLength = docText.length();
      end = docTextLength;
      if ( docTextLength > 0  && docTextLength > pBegin ) {
        if ( pEnd <= docTextLength)
          end = pEnd;
        returnVal = docText.substring( pBegin, end);
      }
    }
    
    return returnVal;
    
  }  // End Method getText() -----------------------
  

  // -----------------------------------------
  /**
   * stringArrayToString converts the UIMA StringArray to a String
   * with pipeDelimited values.
   * 
   * @param pArray
   * @return String null if empty
   */
  // -----------------------------------------
  public final static String stringArrayToString(StringArray pArray) {
    String returnValue = null;
    
    if ( pArray != null ) {
      StringBuffer buff = new StringBuffer();
    
      for ( int i = 0; i < pArray.size(); i++ ) {
        buff.append( pArray.get(i));
        if ( i < pArray.size() -1)
          buff.append("|");
      }
      returnValue = buff.toString();
    }
    return returnValue;
  } // end Method stringArrayToString() -----------

  // -----------------------------------------
  /**
   * getInputFile retrieves the source file name from the cas
   * 
   * (thanks to Tom Ginter for the code snippet)
   * 
   * 
   * @param pJCas
   * @return File
   */
  // -----------------------------------------
  public final static File getInputFile(JCas pJCas) {
  //Get the original Source document Name.
    
    File srcFile = null;
    String docUri = null;

    Iterator<Annotation> it = pJCas.getAnnotationIndex(SourceDocumentInformation.type).iterator();
    if (it.hasNext()) {
      SourceDocumentInformation srcDocInfo = (SourceDocumentInformation) it.next();
      docUri = srcDocInfo.getUri();
      try {
        srcFile = new File(new URL(docUri).getPath());
      } catch (Exception e) {/** handle errors below **/}
    }//if it.hasNext()
    
    return srcFile;
  } // end Method getInputFile() -------------

  
 
  // -----------------------------------------
  /**
   * stringArrayToArrayOfString returns a String[] from the stringArray passed in.
   * 
   * @param pStringz
   * @return String[]
   */
  // -----------------------------------------
  public final static String[] stringArrayToArrayOfString(StringArray pStringz) {
	  String [] returnValue  = null;
	  if (pStringz != null ) {
		  returnValue = new String[ pStringz.size()];
    for ( int i = 0; i< pStringz.size(); i++ )
      returnValue[i] = pStringz.get(i);
    }
    return returnValue;
  } // end Method stringArrayToArrayOfString() -----

  // -----------------------------------------
  /** 
   * list2StringArray converts a list<String> to a uima StringArray
   * 
   * @param pJCas
   * @param pStrings
   * @return StringArray
   */
  // -----------------------------------------
  public final static StringArray list2StringArray(JCas pJCas, List<String> pStrings) {
   StringArray returnValue = null;
   
   returnValue = new StringArray( pJCas, pStrings.size() );
   for ( int i = 0; i < pStrings.size(); i++ ) 
     returnValue.set(i, pStrings.get(i));
   
    return returnValue ;
  } // end Method list2StringArray() ---------------

  // ----------------------------------------------
  /**
   * string2StringArray
   * 
   * @param pJCas
   * @param pStrings
   * @return StringArray
   */
  // ----------------------------------------------
  public final static StringArray string2StringArray(JCas pJCas, String[] pStrings) {
    StringArray returnValue = new StringArray( pJCas, pStrings.length );
    for ( int i = 0; i < pStrings.length; i++ ) 
      returnValue.set(i, pStrings[i]);
    return returnValue;
  }
  

  

  // ----------------------------------------------
  /**
   * mapLabelToUIMAClass 
   * 
   * N.B.  This has the descriptor gov.va.chir.model and gov.nih.cc.rmd.model
   *       hardwired in.  If need be, this can be parameterized.
   * 
   * @param label
   * @return Class
   *
   */
  // ----------------------------------------------
  public final static Class<?> mapLabelToUIMAClass(String label) {
    
    Class<?> returnValue = null;
  
    if ( label != null ) 
      if ( label.indexOf(".") == -1) { 
        
    
        
        if      ( (returnValue = mapLabelToUIMAClass( "gov.nih.cc.rmd.gate.",         label)) != null );
        else if ( (returnValue = mapLabelToUIMAClass( "gov.nih.cc.rmd.framework.model." , label)) != null);
        else 
          if ( (returnValue = mapLabelToUIMAClass( "gov.nih.cc.rmd.model.",        label)) != null ) ;
                                                   
        else 
          if ( (returnValue = mapLabelToUIMAClass( "gov.nih.cc.rmd.framework.",    label)) != null ) ; 
        else if ( (returnValue = mapLabelToUIMAClass( "gov.nih.cc.rmd.mentalFunction.",    label)) != null ) ; 
         
        else if ( (returnValue = mapLabelToUIMAClass( "gov.nih.cc.rmd.",              label)) != null ) ;
        else if ( (returnValue = mapLabelToUIMAClass( "gov.va.chir.model.",           label)) != null ) ;
        else if ( (returnValue = mapLabelToUIMAClass( "gov.va.chir.model." + label)) != null );
        else if ( (returnValue = mapLabelToUIMAClass( "gov.va.vinci.model.",          label)) != null ) ;
        else if ( (returnValue = mapLabelToUIMAClass( "gov.va.vinci.model.temporal.", label)) != null ) ; 
        else if ( (returnValue = mapLabelToUIMAClass( "gov.va.vinci." + label)) != null );
        else if ( (returnValue = mapLabelToUIMAClass( "gov.va.vinci.model." + label)) != null );
        else if ( (returnValue = mapLabelToUIMAClass( "gov.va.vinci.model.temporal." + label)) != null );
        else if ( (returnValue = mapLabelToUIMAClass( "gov.va.vinci.vitals." + label)) != null );
        else if ( (returnValue = mapLabelToUIMAClass( "org.apache.ctakes.typesystem.type.refsem." + label)) != null );
        else if ( (returnValue = mapLabelToUIMAClass( "org.apache.ctakes.typesystem.type.relation." + label)) != null );
        else if ( (returnValue = mapLabelToUIMAClass( "org.apache.ctakes.typesystem.type.structured." + label)) != null );
        else if ( (returnValue = mapLabelToUIMAClass( "org.apache.ctakes.typesystem.type.syntax." + label)) != null );
        else if ( (returnValue = mapLabelToUIMAClass( "org.apache.ctakes.typesystem.type.textsem." + label)) != null );
        else if ( (returnValue = mapLabelToUIMAClass( "org.apache.ctakes.typesystem.type.textspan." + label)) != null );
        else if ( (returnValue = mapLabelToUIMAClass( "org.apache.ctakes.typesystem.type.util." + label)) != null );
        else
          returnValue = mapLabelToUIMAClass((String) null, label) ;
        
        
        
        
      } else 
        returnValue = mapLabelToUIMAClass((String) null , label) ;
                                                     
        
      return returnValue;
      
    }  // end Method mapLabelToUIMAClass() -------
  
  
 // ----------------------------------------------
    /**
     * mapLabelToUIMAClass This method removes spaces in the label
     *
     * If the incoming label starts with a lowercase, I match the
     * starts with uppercase if one exists .
     * @param pNameSpace
     * @param pLabel
     * @return Class<?>
     *
     */
    // ----------------------------------------------   
  public final static Class<?> mapLabelToUIMAClass(String pNameSpace, String pLabel) {
    
    Class<?> returnValue = null;
    try {
      
      if ( pNameSpace != null && !pNameSpace.isEmpty() ) {
        String label = pLabel.replaceAll("Type", "TType");
        label = label.replaceAll(" ", "_");
        char[] labelChars = label.toCharArray();
        labelChars[0] = Character.toUpperCase(labelChars[0] );
        label = new String (labelChars);
       
        if ( label.contentEquals("Score_definition"))  label = "Score_Definition"; //<--- total hack!
       
        label = pNameSpace + "." + label;
        label = label.replaceAll("\\.\\.",  "\\.");   // <----- fix to issue where some of the namespaces coming in have a period at the end
        
        returnValue = Class.forName(label);
      } else {
        returnValue = Class.forName(pLabel);
      }
    } catch (Exception e) {
     
    }
    
    return returnValue;
  } // end Method mapLabelToUIMAClass() -----------
  
  // ------------------------------------------
  /**
   * mapLabelToUIMAClass
   *
   *
   * @param pJCas
   * @param featureName
   * @return Class<?>
   * @throws ClassNotFoundException 
   */
  // ------------------------------------------
  public final static Class<?> mapLabelToUIMAClass(JCas pJCas, String featureName) throws ClassNotFoundException {
  
    if (!SEEN ) {
     TypeSystem typeSystem = pJCas.getTypeSystem();
     Iterator<Type> itr = typeSystem.getTypeIterator();
     types = new ArrayList<Type>();
     while( itr.hasNext()) {
       types.add(itr.next());
     }
     SEEN = true;
    } 
    Class<?> returnValue = null;
    

    
    for ( Type type : types) {
      String lastName = type.getName();
      int i = -1;
      if ( (i =  lastName.lastIndexOf('.')) > 0 )
        lastName = lastName.substring(i+1 );
      
      if ( lastName.equals(featureName)) {
        try {
          returnValue = Class.forName( type.getName() );
        } catch ( Exception e) {
          // ---------------------------------
          // Test for uima internal types.  These come back as "uima.tcas.XXXX"
          // but the class name is "org.apache.uima.jcas.tcas.XXXXX
            // org.apache.uima.jcas.tcas.DocumentAnnotation b = null;
            String typeName = type.getName();
            typeName = typeName.substring(typeName.lastIndexOf('.') + 1);
            returnValue = Class.forName("org.apache.uima.jcas.tcas." + typeName);
        }
      }
    }
    return returnValue;
    
    
  }  // End Method mapLabelToUIMAClass() -----------------------

  
  
  // ----------------------------------------------
  /**
   * mapFeatureToUIMAFeature This method takes out spaces
   *              
   * 
   * @param pClass
   * @param pFeature
   * @return Method
   */
  // ----------------------------------------------
  public final static Method mapFeatureToUIMAFeature(Class<?> pClass, String pFeature) {
    
    Method setBla = null;
   
    Method[] methodNames = pClass.getMethods();

    String feature = pFeature.trim().replaceAll(" ", "_");
    feature = feature.toLowerCase();
    
    if ( methodNames != null ) {
      for ( int i = 0; i < methodNames.length; i++ ) {
        String methodName = methodNames[i].getName().toLowerCase();
       // String methodSignature = methodNames[i].toString();
        if( methodName.contains("add" + feature  )) {
          setBla = methodNames[i]; 
          break;
         }
               
        if ( methodName.contains("set" + feature ) )
         
        {
         setBla = methodNames[i]; 
         break;
        }
        
      }
    }
    
    
    
    return setBla;
    
  } // end Method mapFeatureToUIMAClass
  
  
  // ----------------------------------------------
  /**
   * getAnnotationFeatureNames returns the feature names
   * for this annotation type
   *              
   * 
   * @param pClass
   * @return List<String>
   */
  // ----------------------------------------------
  public final static List<String> getAnnotationFeatureNames(String pLabel ) {
    
    List<String> returnVal = null;
    Class<?> annotationClass = mapLabelToUIMAClass( pLabel);
    
       if ( annotationClass != null )
      returnVal = getAnnotationFeatures( annotationClass );
    
    return returnVal;
    
  } // end Method getAnnotationFeatures() ---------
  
  // ----------------------------------------------
  /**
   * getAnnotationFeatures returns the feature names
   * for this annotation type
   *              
   * 
   * @param pClass
   * @return List<String>
   */
  // ----------------------------------------------
  public final static List<String> getAnnotationFeatures(Class<?> pClass ) {
    
 
    List<String> featureNames = new ArrayList<String>();
    Method[] methodNames = pClass.getMethods();

   
    if ( methodNames != null ) 
      for ( int i = 0; i < methodNames.length; i++ ) {
        String methodName = methodNames[i].getName();
      
        
        // Strip off uima attributes
        if (! methodName.startsWith("get") &&
            ! methodName.contentEquals("setBegin") && 
            ! methodName.contentEquals("setEnd") &&   
            ! methodName.contentEquals("addToIndexes") && 
            ! methodName.contentEquals("setFeatureValueFromString") &&    
            ! methodName.contentEquals("setShortValue") && 
            ! methodName.contentEquals("setDoubleValue") && 
            ! methodName.contentEquals("setIntValue") && 
            ! methodName.contentEquals("setByteValue") && 
            ! methodName.contentEquals("setLongValue") && 
            ! methodName.contentEquals("setFloatValue") && 
            ! methodName.contentEquals("setStringValue") && 
            ! methodName.contentEquals("setFeatureValue") && 
            ! methodName.contentEquals("setBooleanValue") && 
            ! methodName.contentEquals("setMarked") && 
            ! methodName.contentEquals("setOtherInfo") && 
            ! methodName.contentEquals("otherEvidence") &&
            
             (methodName.startsWith("add"  )   || methodName.startsWith( "set") )) {
          char[] featureName = methodName.substring(3).toCharArray();
          featureName[0] = Character.toLowerCase(featureName[0]);
          featureNames.add( new String (featureName));
        }
      }
    
    
    if ( featureNames.isEmpty()) featureNames = null;
    
    return featureNames ;
    
  } // end Method mapFeatureToUIMAClass

  // -------------------------------------------------------
  /**
   * getFeatureValuePairs retrieves featureName,veatureValue tuples
   * from UIMA features off an annotation.  This does involve some amount
   * of sausage making. 
   *
   * @param pAnnotation
   * @return List<FeatureValuePair>
   */
  // -------------------------------------------------------
  public final static List<FeatureValuePair> getFeatureValuePairs( Annotation pAnnotation) {
   

    ArrayList<FeatureValuePair> featureValuePairs = new ArrayList<FeatureValuePair>();
    Type annotationType = pAnnotation.getType();
    List<org.apache.uima.cas.Feature> features = annotationType.getFeatures();
  
   
    for (org.apache.uima.cas.Feature feature : features) {
      
           String featureName = feature.getShortName();
      String  featureElementName = feature.getDomain().getName();
      String        featureRange = feature.getRange().getName();
  
      if ( !featureElementName.contains("uima.cas.") &&
           !featureElementName.contains("uima.tcas.") ) {
        
        
      
      boolean        isPrimitive = feature.getRange().isPrimitive();
      // --------------------------------------------
      // add primitive features as cm featureElements
      // --------------------------------------------
      if ( isPrimitive) {
        
        try {
        featureValuePairs.add( new FeatureValuePair( featureName, pAnnotation.getFeatureValueAsString(feature)));
        } catch ( Exception e) {
          e.printStackTrace();
          GLog.println(GLog.ERROR_LEVEL, UIMAUtil.class, "getFeatureValuePairs", "Something wrong: " + e.toString());
        }
        
    
      // -----------------------------------------------------
      // Attach string arrays as a feature off the parent feature, with
      // featureElements for each of the string array elements
      // -----------------------------------------------------
      } else if ( featureRange.contains("uima.cas.StringArray"))  {
        
       
        StringArray val = (StringArray) pAnnotation.getFeatureValue(feature);
        String stringPipe = UIMAUtil.stringArrayToString(val);
        featureValuePairs.add( new FeatureValuePair( featureName, stringPipe));
       
        
      // -----------------------------------------------------
      // Attach arrays of other objects as a feature off the parent feature, with
      // featureElements for each of the elements
      // Here's the deail: These arrays of object, say arrays of token of a term
      // are FSArray's off the annotation. There's no way to implesitly get
      // FSArrays from an annotation 
      // -----------------------------------------------------
      } else if ( featureRange.endsWith ("[]" )) {
        ;;
       // addFeatureReferencesToAnnotation( pJCas, cmFeature, feature, pAnnotation );
      // -----------------------------------------------------
      // References to other objects such as the part of speech
      // attached to the token
      // -----------------------------------------------------
      } else {
        ;;
        // addFeatureReferenceToAnnotation( cmFeature, feature, pAnnotation );
      }
      } // end if this is a feature to pass along
    } // end loop through features  
    return featureValuePairs;
     
  } // end Method getFeatureValuePairs() -------------------


//-----------------------------------------
 /**
  * getFeatureFalueByName
  * 
  * @param pAnnotation
  * @param pFeatureValue
  * @return String
  */
 // -----------------------------------------
  public final static String getFeatureValueByName(Annotation pAnnotation, String pFeatureName) {
	String returnVal = null;
	
	List<FeatureValuePair> featureValuePairs = getFeatureValuePairs(pAnnotation);
	
	if ( featureValuePairs != null && !featureValuePairs.isEmpty()) {
		for ( FeatureValuePair fvp : featureValuePairs ) {
			if ( pFeatureName.compareTo(fvp.getFeatureName()) == 0 ) {
				returnVal = fvp.getFeatureValue();
				break;
			}
		}  // end loop thru feature value pairs
	}
	
	return returnVal;
}

// -----------------------------------------
  /**
   * get3LinesOfContext returns three lines of context, the line
   * before, the line, and the line after
   * 
   * @param pJCas
   * @param pBegin
   * @return String[]
   */
  // -----------------------------------------
  public final static String[] get3LinesOfContext(JCas pJCas, int pBegin) {
    String[] context = new String[3];
    int   contextLine = 0;
    
    
   
    // ----------------------------------
    // Get lines and offsets of each line in the document
    //  Each line is in the form offset|line
    // ----------------------------------
    String[] lines = getLinesFromDocument( pJCas);
    
    // -------------------------------------------
    // create an index hash of the lines by beginning offset
    // -------------------------------------------
    int offsetArray[] = new int[lines.length];
    for ( int i = 0; i < lines.length; i++ ) {
      offsetArray[i] = getLineOffsetFromRow( lines[i] );
    } // end loop through lines of the document;
    
    // -----------------------------------------
    // Find the line number that the context is in
    // -----------------------------------------
    int nearOffset = Arrays.binarySearch( offsetArray, pBegin);
    
    if ( nearOffset > 0) {
      contextLine = nearOffset;
    } else {
      contextLine = -nearOffset;
    }
    
    // --------------------------------
    // Extract the line before, the line, and the line after
    // --------------------------------
    for ( int i = 0; i < context.length; i++)  context[i] = "";
    if ( contextLine > 0 )  
      context[0] = getLineContentFromRow( lines[contextLine -1]);
    context[1] = getLineContentFromRow( lines[contextLine]);
    if ( contextLine +1  < lines.length)
      context[2] = getLineContentFromRow(lines[contextLine + 1]);
    
    return context;
  } // end Method get3LinesOfContext() -----------------

  // =======================================================
  /**
   * get3LinesBefore returns the 3 lines before the focus line
   * 
   * @param pJCas
   * @param pLinesBefore
   * @param pAnnotation
   * @return getLinesBefore
   */
  // =======================================================
  public final static String getLinesBefore(JCas pJCas, int pLinesBefore, Annotation pAnnotation, String[] lines) {
   
    int focusLine = getLineAndOffset(pJCas, pAnnotation, lines);
    String context = getLinesBefore(pLinesBefore,  focusLine, lines);
    

    return context;
    
    
  } // End Method get3LinesBefore() ======================
  
  // =======================================================
  /**
   * getLinesBefore returns the N lines before or those that exist before the focus line
   * 
   * @param pLinesBefore
   * @param pFocusLineIndex
   * @param lines (sorted array of offset|line)
   * @return String
   */
  // =======================================================
  public final static String getLinesBefore( int pLinesBefore, int pFocusLineIndex, String[] lines) {
    String returnVal = null;
    
    // --------------------------------
    // Extract three lines before
    // --------------------------------
    StringBuffer contextBuff = new StringBuffer();
    int start = pFocusLineIndex - pLinesBefore;
    if ( start < 0)
      start = 0;
        
    for ( int u = start; u < pFocusLineIndex;  u++) {
     
        contextBuff.append( getLineContentFromRow( lines[u]) + '\n' );
    }
    returnVal = contextBuff.toString();
    
    return returnVal;
  } // End Method get3LinesBefore() ======================

  // =======================================================
  /**
   * getLinesAfter (or only those lines that exist after the focus line)
   * 
   * @param pJCas
   * @param pLinesAfter
   * @param pAnnotation
   * @param lines (sorted array of offset|line )
   * @return String
   */
  // =======================================================
  public final static String getLinesAfter(JCas pJCas, int pLinesAfter, Annotation pAnnotation, String[] lines) {
   
    
    int focusLineIndex = getLineAndOffset( pJCas,  pAnnotation,  lines);
    String     context = getLinesAfter( pLinesAfter, focusLineIndex, lines );

    return context;
    
  } // End Method get3LinesAfter() ======================
  
//=======================================================
 /**
  * get3LinesAfter (or only those lines that exist after the focus line)
  * 
  * @param pLinesAfter
  * @param pFocusLineIndex
  * @param lines (sorted array of offset|line)  
  * @return String
  */
 // =======================================================
 public final static String getLinesAfter(int pLinesAfter, int pFocusLineIndex, String[] lines) {
   String returnVal = null;
   
   // --------------------------------
   // Extract three lines after
   // --------------------------------
   StringBuffer contextBuff = new StringBuffer();
   int end = pFocusLineIndex + 2 + pLinesAfter;  // + 1 for the line after, and +1 for the < end condition
   if ( end > lines.length)
     end = lines.length;
   
       
   for ( int u = pFocusLineIndex +1; u < end;  u++) {
    
       contextBuff.append( getLineContentFromRow( lines[u]) + '\n' );
   }
   returnVal = contextBuff.toString();
   
   return returnVal;
 } // End Method get3LinesAfter() ======================
  
  // =======================================================
  /**
   * getLineAndOffset returns offset|line for the line that contains this annotation
   * 
   * @param pJCas
   * @param pAnnotation
   * @param lines (array of offset|line)
   * @return String offset|line
   */
  // =======================================================
  public final static int getLineAndOffset(JCas pJCas, Annotation pAnnotation, String[] lines) {
  
    int pBegin = pAnnotation.getBegin();
   // String context = null;
    int   contextLine = 0;
    // -------------------------------------------
    // create an index hash of the lines by beginning offset
    // -------------------------------------------
    int offsetArray[] = new int[lines.length];
    for ( int i = 0; i < lines.length; i++ ) {
      offsetArray[i] = getLineOffsetFromRow( lines[i] );
    } // end loop through lines of the document;
    
    // -----------------------------------------
    // Find the line number that the context is in
    // -----------------------------------------
    int nearOffset = Arrays.binarySearch( offsetArray, pBegin);
    
    if ( nearOffset > 0) {
      contextLine = nearOffset-2;
    } else {
      contextLine = -nearOffset -2;
    }
    if (contextLine < 0)
      contextLine = 0;
    
   // context = lines[contextLine];
    
    return contextLine;
  }  // End Method getLine() ======================
  
  
//=======================================================
 /**
  * getLineOffsets returns an array of the offsets of the end of lines
  *
  * 
  * @param pJCas
  *
  * @return int[] of offsets
  */
 // =======================================================
 public final static int[] getLineOffsets(JCas pJCas ) {
 
   String buff = pJCas.getDocumentText();
   ArrayList<Integer> offsetList = new ArrayList<Integer>();
   int[] offsets = null;
   if ( buff != null ) {
     
     char [] chars = buff.toCharArray();
   
    
     for ( int offset = 0; offset < chars.length; offset++) {
     
       // -----------------------
       // if you hit a line ending, make a row of the previous line
       if ( chars[offset] == '\n') 
        offsetList.add( offset);
         
      
     } // end loop thru chars of the doc
       offsets = new int[ offsetList.size()];
       for ( int i = 0; i < offsetList.size(); i++ ) offsets[i] = offsetList.get(i);
        
     
   } //end if there is content
     
   return offsets;
 }  // End Method getLine() ======================
  
  // =======================================================
  /**
   * getLineNumber returns the line number that this annotation sits within from annotation.begin().
   * 
  
   * @param pAnnotation
   * @param lineEndOffsets an array of line end offsets (sorted uniq'd list)
   * @return int (the first line == 0)
   */
  // =======================================================
  public final static int getLineNumber( Annotation pAnnotation, int[] lineEndOffsets) {
  
    int pBegin = pAnnotation.getBegin();
  
    int   contextLine = 0;
    
    
    // -----------------------------------------
    // Find the line number that the context is in
    // -----------------------------------------
    int nearOffset = Arrays.binarySearch( lineEndOffsets, pBegin);
    
    if ( nearOffset >= 0) {
      contextLine = nearOffset;
    } else {
      contextLine = -nearOffset -1;
    }
    if (contextLine < 0)
      contextLine = 0;
    
   
    
    return contextLine;
  }  // End Method getLine() ======================
  
  
  
  
  // -----------------------------------------
  /**
   * getLineOffsetFromRow returns the offset from the string field
   * that includes offset|line content
   * 
   * @param pOffsetAndLineRow
   * @return int
   */
  // -----------------------------------------
  public final static int getLineOffsetFromRow(String pOffsetAndLineRow) {
    int offset = -1;
    String cols[] = U.split ( pOffsetAndLineRow);
    offset = Integer.parseInt(cols[0]);
    
    return offset;
  } // end Method getLineOffsetFromRow () ----
  

  

  //-----------------------------------------
  /**
   * getLineContentFromRow returns the content from the string field
   * that includes offset|line content
   * 
   * @param pOffsetAndLineRow
   * @return String
   */
  // -----------------------------------------
  public final static String getLineContentFromRow(String pOffsetAndLineRow) {
    String content = "";
    String cols[] = U.split ( pOffsetAndLineRow);
    content = cols[1];
    
    return content;
  } // end Method getLineOffsetFromRow () ----

  // -----------------------------------------
  /**
   * getLinesFromDocument puts each line of the document
   * into a string array.  The format of each array element
   * is 
   *    offset|line contents
   * 
   * @param pJCas
   * @return String[]
   */
  // -----------------------------------------
  public final static String[] getLinesFromDocumentBad(JCas pJCas) {
        
    String documentContent = pJCas.getDocumentText();
    
    String[] docLines = U.split(documentContent, "\n");
    int lineEndSize = U.getLineEndSize( documentContent);
    int offset = 0;
    
    String[] revisedLines = new String[ docLines.length ];
    String buff = offset + "|" + docLines[0];
    revisedLines[0] = buff;
    
    for ( int i = 1; i < docLines.length; i++ ) {
      offset += docLines[i-1].length() + lineEndSize;
      buff = offset + "|" + docLines[i];
      revisedLines[i] = buff;
    }
        
    return revisedLines;
  }
  // -----------------------------------------
  /**
   * getLinesFromDocument puts each line of the document
   * into a string array.  The format of each array element
   * is 
   *    offset|line contents
   * 
   * @param pJCas
   * @return String[]
   */
  // -----------------------------------------
  public final static String[] getLinesFromDocument(JCas pJCas) {
        
    String buff = pJCas.getDocumentText();
    String[] lines = null;
    if ( buff != null ) {
      
      char [] chars = buff.toCharArray();
      StringBuffer lineBuff = new StringBuffer();
      ArrayList<String>linez = new ArrayList<String>();
      lineBuff.append("0|");
      for ( int offset = 0; offset < chars.length; offset++) {
      
        // -----------------------
        // if you hit a line ending, make a row of the previous line
        if ( chars[offset] == '\n') {
          linez.add( lineBuff.toString());
          lineBuff = new StringBuffer();
          lineBuff.append(offset +1);
          lineBuff.append("|");
        } else {
          lineBuff.append( chars[offset]);
        }
        
      } // end loop thru chars of the doc
      lines = linez.toArray( new String[ linez.size()]);
      
    } //end if there is content
    
    
    return lines;
    
  }
  
  // =======================================================
  /**
   * getLine returns the line that includes the (beginning of) the annotation
   * 
   * @param pJCas
   * @param pAnnotation
   * @param lineEndOffsets
   * @return String
   */
  // =======================================================
  public final static String getLine(JCas pJCas, Annotation pAnnotation, int[] ineEndOffsets) {
    String line = null;
  
    // int lineNumber = getLineNumber(  pAnnotation, lineEndOffsets);
    
    String buffz = pJCas.getDocumentText();
    char[] buff = buffz.toCharArray();
    
    int marker1= pAnnotation.getBegin();
    int marker2 = pAnnotation.getEnd();
    int beginLine = 0;
    int endLine = buff.length -1;
    for (int i = marker1 -1; i >=0; i-- ) {
      if ( buff[i] == '\n') {
        beginLine = i +1;
        break;
      }
    }
    for ( int i = marker2 + 1; i < buff.length; i++) {
      if ( buff[i] == '\n' ) {
        endLine = i;
        break;
      }
    }
    line = buffz.substring(beginLine, endLine);
   
    
    return line;
  }  // End Method getLine() ======================
  
//=======================================================
 /**
  * getLine returns the first line that includes the (beginning of) the annotation
  * 
  * @param pJCas
  * @param pAnnotation
  * 
  * @return Annotation
  */
 // =======================================================
 public final static Annotation getLine(JCas pJCas, Annotation pAnnotation ) {
   
   Annotation returnVal = null;
   List<Annotation> lines = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Line.typeIndexID, pAnnotation.getBegin(), pAnnotation.getEnd());
  
   if ( lines != null && !lines.isEmpty())
     returnVal = lines.get(0);
     
   return returnVal;
 }  // End Method getLine() ======================

  // =======================================================
  /**
   * getLeftContext retrieves the X number of chars to the 
   * left of this annotation - and pretty prints it
   * 
   * @param pJCas
   * @param pAnnotation
   * @param pWindowSize
   * @return String
   */
  // =======================================================
  public final static String getLeftContext(JCas pJCas, Annotation pAnnotation, int pWindowSize) {
  
    String returnVal = null;
    String content = pJCas.getDocumentText();
    int start = (pAnnotation.getBegin() -1) - pWindowSize;
    int stop = pAnnotation.getBegin();
    if ( start < 0) start = 0;
      returnVal = U.spacePadLeft(pWindowSize, U.display(content.substring(start,  stop )));
    
    return returnVal; 
  } // End Method getLeftContext() ======================

  //=======================================================
   /**
    * getRightContext retrieves the X number of cahrs tot
    * the right of annotation and pretty prints it
    * 
    * @param pJCas
    * @param pAnnotation
    * @param pWindowSize
    * @return String
    */
   // =======================================================
   public final static String getRightContext(JCas pJCas, Annotation pAnnotation, int pWindowSize) {
  
     String returnVal = null;
     String content = pJCas.getDocumentText();
     int stop = pAnnotation.getEnd() + 1 + pWindowSize;
     int start = pAnnotation.getEnd() + 1;
     if ( start >= content.length() ) start = content.length() -1;
     if (  stop >= content.length() ) stop = content.length() -1;
       returnVal = U.spacePadRight(pWindowSize, U.display(content.substring(start,  stop )));
     
     return returnVal; 
   } // End Method getLeftContext() ======================

  // -----------------------------------------
  /**
   * getLabelType returns the label's int type given
   * the the label's full name space name. 
   *  
   * 
   * @param pJCas
   * @param pLabel
   *
   */
  // -----------------------------------------
  public final static org.apache.uima.cas.Type getLabelType(JCas pJCas, String pLabel) {
    Type aType = null;
    TypeSystem typeSystem = pJCas.getTypeSystem();
    aType = typeSystem.getType(pLabel) ;
    return aType;
    
    
  } // end Method getLabelType() -------------------

  // -----------------------------------------
  /**
   * getLabelTypes returns a List of Types associated with the Label Names passed in
   * A null will be in those cells where a type could not be found.
   * 
   * @param pJCas
   * @param labelNames
   * @return Type[]
   */
  // -----------------------------------------
  public final static org.apache.uima.cas.Type[] getLabelTypes(JCas pJCas, String[] labelNames) {
    Type[] types = null;
      
    if ( labelNames != null ) {
      types = new org.apache.uima.cas.Type[labelNames.length];
      for ( int i = 0; i< labelNames.length; i++ )
        types[i] = getLabelType( pJCas, labelNames[i]);
    }
  
    return types;
  } // end Method getLabelTypes() ------------------

  // =================================================
  /**
   * getLabelTypeId returns the typeId from a UIMA type
   * 
   * Note that this method relies on mapLabelToUMAClass 
   * which only maps labels with specific name spaces. Those
   * name spaces do not include cTakes, or basic UIMA
   * labels.  They pretty much are the gov.  labels.
   * 
   * 
   * @param pLabelName
   * @return int  (-1 if not found )
  */
  // =================================================
  public static int getLabelTypeId( String pLabelName ) {
    
    int returnVal = -1;
    
    try {
      Class<?> aLabelClass = UIMAUtil.mapLabelToUIMAClass( pLabelName );
      
      if ( aLabelClass != null ) {
        Field f = aLabelClass.getField("typeIndexID");
        Class<?> t = f.getType();
        if(t == int.class)
          returnVal =  f.getInt(null);
      } // end if we found a class 
      else {
        System.err.println("Not able to find label id for " + pLabelName );
      }
      } catch (Exception e) {
        e.printStackTrace();
        GLog.warn_println("UIMAUtil: getLabelTypeId: Issue getting the typeId from " + pLabelName + " " + e.toString());
      }
    
    
      
      return returnVal;
  } // end Method getLabelTypeId() -----------

  // -----------------------------------------
  /**
   * getAttributeByName returns a Feature from this annotation if it exists
   * 
   * @param annotation
   * @param featureName
   * @return Feature
   */
  // -----------------------------------------
  public final static Feature getAttributeByName(Annotation annotation, String featureName) {
    Feature aFeature = null;
    
    Type aType = annotation.getType();
    aFeature = aType.getFeatureByBaseName(featureName);
    
    return aFeature;
  } // end Method getAttributeByName() -------------  // End Method mapLabelToUIMAClass() -----------------------
  
  
  // ------------------------------------------
  /**
   * removeAnnotations
   *  removes the set of annotations from the cas index.
   *  
   *  (This does not remove the fsarrays that could be attached - a small flaw)
   *
   * @param pJCas
   * @param pOldAnnotations
   */
  // ------------------------------------------
  public final static void removeAnnotations(JCas pJCas, List<?> pOldAnnotations) {
  
    if ( pOldAnnotations != null && pOldAnnotations.size() > 0 )
      
      for ( Object annotation: pOldAnnotations ) {
    	  if (annotation != null )
    		  ((Annotation) annotation).removeFromIndexes(pJCas);
      }
  }  // End Method removeAnnotations() -----------------------


  // =======================================================
  /**
   * copyAnnotation creates a copy of this annotation (usually from a different cas
   * attributes included
   * 
   * Caveat - this does not replicate complex annotation features:
   *    features that 
   * 
   * @param pJCas
   * @param pAnnotation
   * @return Annotation
   */
  // =======================================================
  public final static Annotation copyAnnotation(JCas pJCas, Annotation pAnnotation) {
  
    Annotation uimaAnnotation = null;
    Class<? extends Annotation> uimaLabelClass = pAnnotation.getClass();
  
    // ----------------------------------------------
    // create a UIMA annotation from this label
    // ----------------------------------------------
    try {
  
      Constructor<?> c = uimaLabelClass.getConstructor(new Class[] { JCas.class });
      uimaAnnotation = (Annotation) c.newInstance(pJCas);
      
  
      // --------------------------------------------------
      // Add span and slot attributes to the uimaAnnotation
      // --------------------------------------------------
     
       uimaAnnotation.setBegin(pAnnotation.getBegin() );
       uimaAnnotation.setEnd(pAnnotation.getEnd() );
       uimaAnnotation.addToIndexes(pJCas);
       
       // ----------------------------------
       // Iterate through the features of the annotation
       List<FeatureValuePair> valuePairs = UIMAUtil.getFeatureValuePairs(pAnnotation);
       
       if ( valuePairs != null ) {
         for (FeatureValuePair valuePair : valuePairs) {
           
           Method uimaSetFeatureMethod = UIMAUtil.mapFeatureToUIMAFeature(uimaLabelClass, valuePair.getFeatureName());
           if (uimaSetFeatureMethod != null) try {
             if (valuePair.getFeatureValue() != null) uimaSetFeatureMethod.invoke(valuePair.getFeatureValue(), uimaAnnotation);
           } catch (Exception e) {}
          
           
         } // end loop through each feature
       }
       
       
       
       
       
    } catch (Exception e ) {
      e.getStackTrace();
      GLog.println(GLog.ERROR_LEVEL, UIMAUtil.class, "copyAnnotation", "Issue with copying the annotation " + e.getMessage());
     
     
    }
      
      return ( uimaAnnotation);
      
  } // End Method copyAnnotation() ======================


  // =======================================================
  /**
   * removeArrayElements removes any array element attributes (fsArray, StringArray)
   *  This is useful when rendering a cas out to xmi or other serialized form.
   *  These array elements refer to other annotations. The other annotations
   *  won't be removed.  The elements of the arrays are pointers that
   *  get lost in the serialization process for all the marshallers, so
   *  we might as well remove them so we can save on space and the
   *  messages that the pointers cannot be preserved that happens
   *  when they are present.
   * 
   * @param pJCas
   */
  // =======================================================
  public final static void removeArrayElements(JCas pJCas) {
    
    
     
     
    // ----------------------------------------------
    // Iterate through all the annotations of the cas
    List<Annotation> annotations = UIMAUtil.getAnnotations( pJCas);
    
    if ( annotations != null && annotations.size() > 0 ) 
     for ( Annotation annotation : annotations )
      removeArrayElements( pJCas, annotation);
      
    
  } // end Method removeArrayElements() ----------------


  // =======================================================
  /**
   * removeArrayElements removes any array element attributes (fsArray, StringArray)
   *  This is useful when rendering a cas out to xmi or other serialized form.
   *  These array elements refer to other annotations. The other annotations
   *  won't be removed.  The elements of the arrays are pointers that
   *  get lost in the serialization process for all the marshallers, so
   *  we might as well remove them so we can save on space and the
   *  messages that the pointers cannot be preserved that happens
   *  when they are present.
   * 
   * @param pJCas
   * @param pAnnotation
   */
  // =======================================================
  public final static void removeArrayElements(JCas pJCas, Annotation pAnnotation) {
    
    
    Type annotationType = pAnnotation.getType();
    
    List<org.apache.uima.cas.Feature> features = annotationType.getFeatures();
    
    
    for (org.apache.uima.cas.Feature feature : features) {
      
      boolean    isPrimitive = feature.getRange().isPrimitive();
      String         featureName = feature.getShortName();
      String  featureElementName = feature.getDomain().getName();
      String        featureRange = feature.getRange().getName();
     
      // --------------------------------------------
      // add primitive features as cm featureElements
      // --------------------------------------------
      if ( isPrimitive) { 
  
  
      // -----------------------------------------------------
      // StringArrays  keep these
      // -----------------------------------------------------
      } else if ( featureRange.contains("uima.cas.StringArray"))  {
        
        //StringArray val = (StringArray) pAnnotation.getFeatureValue(feature);
       // if ( val != null)
       // val.removeFromIndexes();
       
        
      // -----------------------------------------------------
      // fsArrays  remove them - makes no difference, so keep
      // ----------------------------------
      } else if ( featureRange.endsWith ("[]" )) {
       // try {
       //   FSArray val = (FSArray) pAnnotation.getFeatureValue(feature);
       //   if ( val != null)
       //     val.removeFromIndexes();
       // } catch (Exception e) {
       //   e.printStackTrace();
       //   String msg = "Issue with removing fs arrays from the annotation " + e.toString();
       //   GLog.println(GLog.ERROR_LEVEL, this.getClass(), "method", msg);
       // }
  
      // ----------------------------------------
      // Don't touch the sofa!
      } else if ( featureRange.contains("Sofa")) {
        
      // ----------------------------------------
      // Pointers to other annotations: null it out
      } else if ( featureRange.contains("Annotation")) {
        FeatureStructure pionterVal = pAnnotation.getFeatureValue(feature);
        pAnnotation.setFeatureValue(feature, null);
      // GLog.println(GLog.ERROR_LEVEL, this.getClass(), "method", "nulling out this feature " + featureName);
      // GLog.println(GLog.ERROR_LEVEL, this.getClass(), "method",  "Feature Range = " +  featureRange );
       // GLog.println(GLog.ERROR_LEVEL, this.getClass(), "method",  "feature domain = " + featureElementName );
        
     // ----------------------------------------
     // This is a hack - but should work
     } else if ( featureRange.contains("gov") || featureRange.contains("org") || featureRange.contains("edu") || featureRange.contains("com")) {
       FeatureStructure pionterVal = pAnnotation.getFeatureValue(feature);
       pAnnotation.setFeatureValue(feature, null);
      // GLog.println(GLog.ERROR_LEVEL, this.getClass(), "method", "nulling out this feature " + featureName);
      // GLog.println(GLog.ERROR_LEVEL, this.getClass(), "method",  "Feature Range = " +  featureRange );
      // GLog.println(GLog.ERROR_LEVEL, this.getClass(), "method",  "feature domain = " + featureElementName );
        
      } else {
       GLog.println(GLog.ERROR_LEVEL, UIMAUtil.class, "removeArrayElements", "Not sure what this is " + featureName);
       GLog.println(GLog.ERROR_LEVEL, UIMAUtil.class, "removeArrayElements",  "Feature Range = " +  featureRange );
       GLog.println(GLog.ERROR_LEVEL, UIMAUtil.class, "removeArrayElements",  "feature domain = " + featureElementName );
        
      }
      } // end if this is a feature to pass along
  
  
  } // End Method removeArrayElements() ======================

  
  // =======================================================
  /**
   * createNewJCAS  creates a new blank jcas using the
   * pJCas's type system.
   * 
   * @param pJCas
   * @return JCas
   * @throws Exception 
   */
  // =======================================================
  public final static synchronized JCas createNewJCAS(JCas pJCas ) throws Exception {
    
  CAS  aCas = null;
  JCas aJCas = null;
  TypeSystem currentTypeSystem = pJCas.getTypeSystem();
  TypeSystemDescription currentTypeSystemDescription = TypeSystemUtil.typeSystem2TypeSystemDescription(currentTypeSystem);
  FsIndexDescription[] indexes = null;
  try {
    
    aCas = CasCreationUtils.createCas( currentTypeSystemDescription, new TypePriorities_impl(), indexes);
  
    aJCas = aCas.getJCas();
    
    return aJCas;
  } catch (Exception e) {
    e.printStackTrace();
    String msg = "Issue with creating a new cas " + e.toString();
    throw new Exception ();
  }
    
  } // End Method ceateNewJCAS() ======================

//=======================================================
 /**
  * uniqueAnnotations uniq's the annotations (assumes you've
  * passed in a non-unique set of the same type).  Keeps the first
  * seen.  This method removes the non-unique versions from the
  * 
  *    (offset order is not guaranteed.)
  * @param pAnnotations
  * @return List<Annotation>
  *
  */
 // =======================================================
public final static List<Annotation> uniqueAnnotations(List<Annotation> pAnnotations) {
	
	ArrayList<Annotation> returnVal = null;
	HashMap<String, Annotation> aHash  = null;
	if ( pAnnotations != null && !pAnnotations.isEmpty()) {
		aHash = new HashMap<String,Annotation>( pAnnotations.size());
		returnVal = new ArrayList<Annotation>();
		for ( Annotation annotation: pAnnotations ) {
			String key = annotation.getBegin() + "|" + annotation.getEnd() + "|" + annotation.getClass().getSimpleName();
			Annotation anAnnotation = aHash.get(key);
			if ( anAnnotation == null ){
				aHash.put(key, annotation);
				returnVal.add(annotation);
			} else {
			//	GLog.println(GLog.ERROR_LEVEL, this.getClass(), "method", "Removing duplicate " + annotation.getCoveredText() + key);
				annotation.removeFromIndexes();
			}
		} // end loop thru annotations
		
	}
	
	return returnVal;
} // End Method uniqueAnnotations() =======================
 
//=======================================================
/**
* uniqueAnnotationList uniq's the list of annotations (assumes you've
* passed in a non-unique set of the same type).  Keeps the first
* seen.
*    (offset order is not guaranteed.)
* @param pAnnotations
* @return List<Annotation>
*
*/
// =======================================================
public final static List<Annotation> uniqueAnnotationList(List<Annotation> pAnnotations) {

ArrayList<Annotation> returnVal = null;
HashMap<String, Annotation> aHash  = null;
if ( pAnnotations != null && !pAnnotations.isEmpty()) {
  aHash = new HashMap<String,Annotation>( pAnnotations.size());
  returnVal = new ArrayList<Annotation>();
  for ( Annotation annotation: pAnnotations ) {
    String key = annotation.getBegin() + "|" + annotation.getEnd() + "|" + annotation.getClass().getSimpleName();
    Annotation anAnnotation = aHash.get(key);
    if ( anAnnotation == null ){
      aHash.put(key, annotation);
      returnVal.add(annotation);
    } 
  } // end loop thru annotations
  
}

return returnVal;
} // End Method uniqueAnnotations() =======================
  
// =================================================
/**
 * isSameSpan returns true if the begin and end spans are the the same 
 * 
 * @param pAnnotation1
 * @param pAnnotation2
 * @return boolean
*/
// =================================================
public static final boolean isSameSpan(Annotation pAnnotation1, Annotation pAnnotation2) {
  boolean returnVal = false;

  if ( pAnnotation1 != null && pAnnotation2 != null )
    if ( pAnnotation1.getBegin() == pAnnotation2.getBegin() )
      if ( pAnnotation1.getEnd() == pAnnotation2.getEnd() )
        returnVal = true;
  
  return returnVal;
} // end Method isSameSpan() -----------------------
  

  // =================================================
/**
 * annotationContainsAnnotation returns true if annotation1 
 * is within the bounds of annotation2
 * 
 * @param pAnnotation1
 * @param pAnnotation2
 * @return boolean
*/
// =================================================
public static final boolean annotationContainsAnnotation(Annotation pAnnotation1, Annotation pAnnotation2) {

  boolean returnVal = false;
  if ( pAnnotation1 != null && pAnnotation2 != null )
    if ( pAnnotation1.getBegin() >= pAnnotation2.getBegin() )
      if ( pAnnotation1.getEnd() <= pAnnotation2.getEnd() )
        returnVal = true;
  
  return returnVal;
  
} // end Method() ---------------------------------

// =================================================
/**
 * isAtTopOfDocument returns true if the the annotation is
 * within the top 5% of the document in terms of
 * offset sizes.
 * 
 * @param pJCas
 * @param pAnnotation
 * @return boolean
*/
// =================================================
public static final boolean isAtTopOfDocument(JCas pJCas, Annotation pAnnotation) {
  boolean returnVal = false;
  
  String docText = pJCas.getDocumentText();
  if ( docText != null && pAnnotation != null ) {
    int docLength = docText.length();
    int location = pAnnotation.getBegin();
  
    if ( docLength > location )
      if( ((location/docLength) * 100 ) <= 5.0 )
        returnVal = true;
    }

  return returnVal;
} // end Method isAtTopOfDocument() ---------------



// =================================================
/**
 * isAtBottomOfDocument returns true if the annotation
 * is within the bottom 5% of the document
 * 
 * @param pJCas
 * @param aDate
 * @return boolean
*/
// =================================================
public static final boolean isAtBottomOfDocument(JCas pJCas, Annotation pAnnotation) {
 boolean returnVal = false;
  
 String docText = pJCas.getDocumentText();
 if ( docText != null && pAnnotation != null ) {
   int docLength = docText.length();
   int location = pAnnotation.getBegin();
 
   if ( docLength > location )
     if( ((location/docLength) * 100 ) >= 95.0 )
       returnVal = true;
   }
 return returnVal; 
} // end Method isAtBottomOfDocument() ------------


// =================================================
/**
 * setTypeDescriptor sets the type descriptor  
 *  This adds a system properties so that the uima
 *  engine can pick it up.
 *  
 * @param pTypeDescriptorPath   (the path to the file)
 * 
*
// =================================================
public static final void setTypeDescriptor(String pTypeDescriptorPath ) {
 
  GLog.println(GLog.STD___LEVEL, "UIMAUtil:setTypeDecriptor:The type descriptor is " + pTypeDescriptorPath);
  String returnVal = setClassPathToTypeDescriptors( pTypeDescriptorPath);

  Properties props = System.getProperties();
  props.setProperty("org.apache.uima.fit.type.import_pattern",  "classpath*:" + returnVal);
  
  try {
    TypeSystemDescription tsd =  TypeSystemDescriptionFactory.createTypeSystemDescription();
    System.err.println( tsd.toString());
  } catch (ResourceInitializationException e) {
  e.printStackTrace();
   
  }
    
 
} // end Method setTypeDescriptor() ------------
*/

//=======================================================
/**
* setclassPathToTypeDescriptors .
*
* @param pClassPathToTypeDescriptors the classPathToTypeDescriptors to set if
*          you pass in a dot path'd classpath, this routine will convert the
*          dot path to a relative file path and add the extension .xml on to
*          it. That's what UIMAFit needs
*/
// =======================================================
public final static String setClassPathToTypeDescriptors(String pClassPathToTypeDescriptors) {

 String classPathToTypeDescriptors = pClassPathToTypeDescriptors;
 if (pClassPathToTypeDescriptors.contains(".")) {
   classPathToTypeDescriptors = convertDotPathToFilePath(pClassPathToTypeDescriptors);
 }

 return  classPathToTypeDescriptors;
} // end Method setClassPathToTypeDescriptors() ----------

/**
* Convert dot path to file path.
*
* @param pClasspathToTypeDescriptors the classpath to type descriptors
* @return the string
*/
private static String convertDotPathToFilePath(String pClasspathToTypeDescriptors) {
 String returnVal = pClasspathToTypeDescriptors;
 returnVal = returnVal.replace('.', '/');
 returnVal = returnVal + ".xml";
 return returnVal;
} // End Method convertDotPathToFilePath() ======================



// ==========================================
/**
 * getMaxSpans 
 *
 * @param pListOfListOfAnnotations 
 * @return int[]  [begin,end]
 */
// =========================================
@SuppressWarnings("unchecked")
public static int[] getMaxSpans(Object... pListOfListOfAnnotations ) {

  int[] span = new int[2];
  int maxBegin = 999999;
  int maxEnd   = 0;
  
  if ( pListOfListOfAnnotations != null ) {
    
    if ( pListOfListOfAnnotations.length == 1) {
    
      span[0] = ((Annotation)((List)pListOfListOfAnnotations[0]).get(0)).getBegin();
      span[1] = ((Annotation)((List)pListOfListOfAnnotations[0]).get(((List)pListOfListOfAnnotations[0]).size() -1)).getEnd();
    } else {
  
      for ( Object listOfAnnotations : pListOfListOfAnnotations ) {
    if ( listOfAnnotations != null ) {
      if ( !((List<Annotation>) listOfAnnotations).isEmpty() ) {
        span = getMaxSpansAux( (List<Annotation>)listOfAnnotations );
        if ( span[0] <= maxBegin)
          maxBegin = span[0];
        if ( span[1] < maxBegin)
          maxBegin = span[1];
        
        if ( span[1] > maxEnd)
          maxEnd   = span[1];
        if ( span[0] > maxEnd)
          maxEnd = span[0];
      }
    }
  }
  span[0] = maxBegin;
  span[1] = maxEnd;
    }
  }
  
  if ( span[0] == 9999 )  
    throw new RuntimeException();
  return span;
} // end Method getMaxSpans() ===============


// ==========================================
/**
 * getMaxSpans 
 *
 * @param pAnnotations
 * @return int[]
 */
// =========================================
private static int[] getMaxSpansAux(List<Annotation> pAnnotations ) {

  int[] span = new int[2];
  int maxBegin = 9999999;
  int maxEnd   = 0;
  if ( pAnnotations != null ) {
    for ( Annotation annotation : pAnnotations ) {
      if ( maxBegin > annotation.getBegin() ) maxBegin = annotation.getBegin();
      if ( maxEnd   < annotation.getEnd()   ) maxEnd   = annotation.getEnd();
      
    }
  }
  span[0] = maxBegin;
  span[1] = maxEnd;
  return span;
} // end Method getMaxSpansAux() ===============



//----------------------------------
/**
* doesWhiteSpaceFollow returns true if the character after this annotation
* is whitespace. This method will return false if there is no trailing
* character.
* 
* @param pCAS
* @param pBeginOffset
* @param pValue
* @return boolean true if the character after this annotation is whitespace.
* 
*/
// ----------------------------------
public static boolean doesWhiteSpaceFollow(JCas pCAS, int pBeginOffset, String pValue) {
 boolean returnValue = false;
 String docText = pCAS.getDocumentText(); // is this efficient? Is this a
 // pointer or is space
 // being allocated to the docText?
 int nextChar = pBeginOffset + pValue.length();
 if (nextChar < docText.length()) {
   char c = docText.charAt(pBeginOffset + pValue.length());
   returnValue = Character.isWhitespace(c);
 }
 return returnValue;
} // end method doesWhiteSpaceFollow() -


  //-----------------------------------------------
  // Static global variables
  // -----------------------------------------------
  private static OffsetComparator mOffsetComparator = null;
  private static OffsetDesendingComparator mOffsetDesendingComparator = null;
  private static boolean SEEN = false;
  private static ArrayList<Type> types = null;

  

  
  

  
  
} // end Class UIMAUtil ----------------------------
