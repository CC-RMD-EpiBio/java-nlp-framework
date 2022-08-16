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
null
