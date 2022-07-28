package bioc;

public class BioCNode {

  /**
   * Id of an annotated object or another relation. Typically there will be one
   * label for each ref_id.
   */
  protected String refid;

  protected String role;

  public BioCNode() {
    refid = "";
    role = "";
  }

  public BioCNode(BioCNode node) {
    refid = node.refid;
    role = node.role;
  }

  public BioCNode(String refid, String role) {
    this.refid = refid;
    this.role = role;
  }

  public String getRefid() {
    return refid;
  }

  public String getRole() {
    return role;
  }

  public void setRefid(String refid) {
    this.refid = refid;
  }

  public void setRole(String role) {
    this.role = role;
  }

  @Override
  public String toString() {
    String s = "refid: " + refid;
    s += "\n";
    s += "role: " + role;
    s += "\n";
    return s;
  }
}
