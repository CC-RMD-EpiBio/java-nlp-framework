package bioc.io;

import java.io.Closeable;

import javax.xml.stream.XMLStreamException;

import bioc.BioCCollection;
import bioc.BioCDocument;

public interface BioCDocumentWriter extends Closeable {

  public void writeCollectionInfo(BioCCollection collection)
      throws XMLStreamException;

  public void writeDocument(BioCDocument document)
      throws XMLStreamException;

  public void setDTD(String dtd);
}
