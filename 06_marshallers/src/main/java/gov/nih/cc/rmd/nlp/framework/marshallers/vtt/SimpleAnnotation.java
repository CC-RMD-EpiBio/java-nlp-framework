package gov.nih.cc.rmd.nlp.framework.marshallers.vtt;

public class SimpleAnnotation {

  public SimpleAnnotation(String pLabelName, int pBeginOffset, int pEndOffset) {
   this.labelName = pLabelName;
   this.beginOffset = pBeginOffset;
   this.endOffset = pEndOffset;
   this.attributes = null;
  }

  // =================================================
  /**
   * Constructor
   *
   * @param pLabelName
   * @param pBeginOffset
   * @param pEndOffset
   * @param pAttributes
   * 
  **/
  // =================================================
  public SimpleAnnotation(String pLabelName, int pBeginOffset, int pEndOffset, String pAttributes) {
    this.labelName = pLabelName;
    this.beginOffset = pBeginOffset;
    this.endOffset = pEndOffset;
    this.attributes = pAttributes;
        
  }

  public final String getLabelName() {
    return labelName;
  }

  public final void setLabelName(String labelName) {
    this.labelName = labelName;
  }

  public final int getBeginOffset() {
    return beginOffset;
  }

  public final void setBeginOffset(int beginOffset) {
    this.beginOffset = beginOffset;
  }

  public final int getEndOffset() {
    return endOffset;
  }

  public final void setEndOffset(int endOffset) {
    this.endOffset = endOffset;
  }

  /**
   * @return the attributes
   */
  public String getAttributes() {
    return attributes;
  }

  /**
   * @param attributes the attributes to set
   */
  public void setAttributes(String attributes) {
    this.attributes = attributes;
  }

  private String labelName = null;
  private int beginOffset = -1;
  private int endOffset = -1;
  private String attributes = null;

}
