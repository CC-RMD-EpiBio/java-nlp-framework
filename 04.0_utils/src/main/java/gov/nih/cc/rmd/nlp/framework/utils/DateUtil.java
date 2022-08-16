// =======================================================
/**
 * DateUtils includes generic date utililities 
 *
 * @author  Divita
 * @created Jan 13, 2015
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.utils;

import java.util.GregorianCalendar;


public class DateUtil {

  
  
  // =======================================================
  /**
   * numberOfDays - given to dates in the form of dd/mm/yyyy, figure out date2 - date1 in days.
   * 
   * @param pDate1
   * @param pDate2
   * @return int
   */
  // =======================================================
  public static int numberOfDays(String pDate1, String pDate2) {
   
    java.util.Date d1 = parseStringDate( pDate1);
    java.util.Date d2 = parseStringDate( pDate2);

    int returnVal = (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
      
    return returnVal;
    
  }
 // =======================================================
  /**
   * parseStringDate returns a java.util.Date from strings formatted
   * as DD_MM_YYYY or 
   *    DD-MM-YYYY or 
   *    DD.MM.YYYY or 
   *    DD/MM/YYYY
   * 
   * @param pDate
   * @return java.util.Date
   */
  // =======================================================
  public static java.util.Date parseStringDate(String pDate) {
    GregorianCalendar cal = new GregorianCalendar();

    String buf = pDate.replace('/', '_');
    buf = buf.replace('.', '_');
    buf = buf.replace('-', '_');
    
    int   dd = getDD(buf);
    int   mm = getMM(buf);
    int yyyy = getYYYY(buf);
    cal.set(yyyy, mm, dd); 
    java.util.Date date = cal.getTime();
    
    return date;
    
  } // End Method parseStringDate() ======================
  



  // =======================================================
  /**
   * getYYYY retrieves the YYYY from the note date
   * which is formated as DD_MM_YYYY
   * 
   * @param pDate
   * @return int
   */
  // =======================================================
  private static int getYYYY(String pDate) {
   
    int returnVal = 0;
    
    try {
      String[] datePieces = U.split( pDate, "_");
      Integer.parseInt(datePieces[2]);
    } catch(Exception e) {}
      
    return returnVal;
      
  } // End Method getYYYY() ======================
  
//=======================================================
 /**
  * getDD retrieves the DD from the note date
  * which is formated as MM_dd_YYYY
  * 
  * @param pDate
  * @return int
  */
 // =======================================================
 private static int getDD(String pDate) {
  
   int returnVal = 0;
   try {
     String[] datePieces = U.split( pDate, "_");
     returnVal = Integer.parseInt(datePieces[1]);
   } catch (Exception e) {}
 
   return returnVal;  
 } // End Method getDD() ======================
  
//=======================================================
/**
 * getMM retrieves the MM from the note date
 * which is formated as MM_dd_YYYY
 * 
 * @param pDate
 * @return int
 */
// =======================================================
private static int getMM(String pDate) {
 
  int returnVal = 0;
  try {
    String[] datePieces = U.split( pDate, "_");
    returnVal = Integer.parseInt(datePieces[0]);
  } catch (Exception e) {}
   
 
  return returnVal;
} // End Method getMM() ======================

} // end Class DateUtils()
