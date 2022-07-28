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
/*
 *
 */
package hitex.gate.regex.util;

/**
 * The Class RuleLoadingException.
 */
public class RuleLoadingException extends ConceptFinderException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6066907737176106068L;

  /**
   * Instantiates an empty {@link RuleLoadingException}.
   */
  public RuleLoadingException() {
    super();
  }

  /**
   * Instantiates a {@link RuleLoadingException} from the specified parameters.
   *
   * @param message the message
   */
  public RuleLoadingException(String message) {
    super(message);
  }

  /**
   * Instantiates a {@link RuleLoadingException} from the specified parameters.
   *
   * @param message the message
   * @param cause the cause
   */
  public RuleLoadingException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a {@link RuleLoadingException} from the specified parameters.
   *
   * @param cause the cause
   */
  public RuleLoadingException(Throwable cause) {
    super(cause);
  }

}
