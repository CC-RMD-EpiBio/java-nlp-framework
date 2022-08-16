/*
 *
 */
package hitex.gate.regex.util;

/**
 * The Class RuleValidationException.
 */
public class RuleValidationException extends ConceptFinderException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4180882300036201374L;

  /**
   * Instantiates an empty {@link RuleValidationException}.
   */
  public RuleValidationException() {
    super();
  }

  /**
   * Instantiates a {@link RuleValidationException} from the specified
   * parameters.
   *
   * @param message the message
   */
  public RuleValidationException(String message) {
    super(message);
  }

  /**
   * Instantiates a {@link RuleValidationException} from the specified
   * parameters.
   *
   * @param message the message
   * @param cause the cause
   */
  public RuleValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a {@link RuleValidationException} from the specified
   * parameters.
   *
   * @param cause the cause
   */
  public RuleValidationException(Throwable cause) {
    super(cause);
  }

}
null
