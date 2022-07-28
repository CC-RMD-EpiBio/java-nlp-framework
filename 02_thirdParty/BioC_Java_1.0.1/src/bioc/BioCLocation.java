package bioc;

/**
 * The connection to the original text can be made through the {@code offset},
 * {@code length}, and possibly the {@code text} fields.
 */
public class BioCLocation {

  /**
   * Type of annotation. Options include "token", "noun phrase", "gene", and
   * "disease". The valid values should be described in the {@code key} file.
   */
  protected int offset;
  /**
   * The length of the annotated text. While unlikely, this could be zero to
   * describe an annotation that belongs between two characters.
   */
  protected int length;

  public BioCLocation() {
  }

  public BioCLocation(BioCLocation location) {
    this(location.offset, location.length);
  }

  public BioCLocation(int offset, int length) {
    this.offset = offset;
    this.length = length;
  }

  public int getLength() {
    return length;
  }

  public int getOffset() {
    return offset;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  @Override
  public String toString() {
    return offset + ":" + length;
  }
}
