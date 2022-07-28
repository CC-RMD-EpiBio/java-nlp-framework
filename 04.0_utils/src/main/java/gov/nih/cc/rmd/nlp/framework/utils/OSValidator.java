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
// =======================================================
/**
 * OSValidator figures out what os this running on
 *
 * @author  Mkyong (see http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/)
 * 
 * @created Aug 18, 2015
 *
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.utils;


public final class OSValidator {


  public static final int UNKNOWN =-1;
  public static final int     WIN = 1;
  public static final int     MAC = 2;
  public static final int   LINUX = 3;
  public static final int SOLARIS = 4;

  public static boolean isWindows() {
    return (OS.indexOf("win") >= 0);
  }

  public static boolean isMac() {
    return (OS.indexOf("mac") >= 0);
  }

  public static boolean isUnix() {
    return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
  }

  public static boolean isSolaris() {
    return (OS.indexOf("sunos") >= 0);
  }
  
  public static final int whatOS() {
    int returnVal = UNKNOWN;
    if (isWindows()) 
     returnVal =  WIN;
    else if (isMac()) 
      returnVal = MAC;
    else if (isUnix()) 
     returnVal = LINUX;
    else if (isSolaris()) 
      returnVal = SOLARIS;
   
    return returnVal;
  }
  
  // =======================================================
  /**
   * main 
   * 
   * @param args
   */
  // =======================================================
  public static void main(String[] args) {
    
      System.out.println(OS);
      
      if (isWindows()) {
        System.out.println("This is Windows");
      } else if (isMac()) {
        System.out.println("This is Mac");
      } else if (isUnix()) {
        System.out.println("This is Unix or Linux");
      } else if (isSolaris()) {
        System.out.println("This is Solaris");
      } else {
        System.out.println("Your OS is not support!!");
      }
    
} // End Method main() ======================
  

// -------------------------
// Global Variables 
   private static String OS = System.getProperty("os.name").toLowerCase();
    
  
} // end Class OSValidator() --------------------
