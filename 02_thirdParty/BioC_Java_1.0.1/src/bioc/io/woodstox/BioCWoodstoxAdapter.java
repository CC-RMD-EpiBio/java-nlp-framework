package bioc.io.woodstox;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.io.BioCCollectionReader;
import bioc.io.BioCCollectionWriter;
import bioc.io.BioCDocumentReader;
import bioc.io.BioCDocumentWriter;

class BioCWoodstoxAdapter implements BioCCollectionReader, BioCDocumentWriter,
    BioCCollectionWriter, BioCDocumentReader {

  ConnectorWoodstox inConnector;
  ConnectorWoodstox outConnector;
  Reader            in;
  Writer            out;

  BioCWoodstoxAdapter(Reader in) {
    inConnector = new ConnectorWoodstox();
    this.in = in;
  }

  BioCWoodstoxAdapter(Writer out) {
    outConnector = new ConnectorWoodstox();
    this.out = out;
  }

  @Override
  public void close()
      throws IOException {
    if (outConnector != null) {
      try {
        outConnector.endWrite();
      } catch (XMLStreamException e) {
        throw new IOException( e.getMessage(), e );
      }
    }
  }

  @Override
  public BioCDocument readDocument()
      throws XMLStreamException {
    if (inConnector.hasNext()) {
      return inConnector.next();
    } else {
      return null;
    }
  }

  @Override
  public BioCCollection readCollectionInfo()
      throws XMLStreamException {
    return inConnector.startRead(in);
  }

  @Override
  public void writeCollection(BioCCollection collection)
      throws XMLStreamException {
    writeCollectionInfo(collection);
    for (BioCDocument doc : collection.getDocuments()) {
      writeDocument(doc);
    }
  }

  @Override
  public void writeCollectionInfo(BioCCollection collection)
      throws XMLStreamException {
    outConnector.startWrite(out, collection);
  }

  @Override
  public void writeDocument(BioCDocument document)
      throws XMLStreamException {
    outConnector.writeNext(document);
  }

  @Override
  public void setDTD(String dtd) {
  }

  @Override
  public BioCCollection readCollection()
      throws XMLStreamException {
    BioCCollection collection = readCollectionInfo();
    BioCDocument doc = null;
    while ((doc = readDocument()) != null) {
      collection.addDocument(doc);
    }
    return collection;
  }

  @Override
  public Iterator<BioCDocument> iterator() {
    return inConnector;
  }

}
