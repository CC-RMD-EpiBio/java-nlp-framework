package bioc.io.standard;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.io.BioCDocumentWriter;

class BioCDocumentWriterImpl extends BioCAllWriter implements
    BioCDocumentWriter {

  String  dtd;
  boolean hasWrittenCollectionInfo;

  BioCDocumentWriterImpl(OutputStream out)
      throws FactoryConfigurationError, XMLStreamException {
    this(new OutputStreamWriter(out));
  }

  BioCDocumentWriterImpl(Writer out)
      throws FactoryConfigurationError, XMLStreamException {
    super(out);
    dtd = "BioC.dtd";
    hasWrittenCollectionInfo = false;
  }

  @Override
  public void setDTD(String dtd) {
    this.dtd = dtd;
  }

  @Override
  public final void close()
      throws IOException {

    try {
      // end collection
      writer.writeEndElement();

      writer.writeEndDocument();
      writer.flush();
      writer.close();
    } catch (XMLStreamException e) {
      throw new IOException(e.getMessage());
    }
  }

  @Override
  public void writeCollectionInfo(BioCCollection collection)
      throws XMLStreamException {

    if (hasWrittenCollectionInfo) {
      throw new IllegalStateException(
          "writeCollectionInfo can only be invoked once.");
    }

    hasWrittenCollectionInfo = true;

    writer.writeDTD("<!DOCTYPE collection SYSTEM \"BioC.dtd\">");
    writer.writeStartElement("collection");
    // source
    writer.writeStartElement("source");
    writer.writeCharacters(collection.getSource());
    writer.writeEndElement();
    // date
    writer.writeStartElement("date");
    writer.writeCharacters(collection.getDate());
    writer.writeEndElement();
    // key
    writer.writeStartElement("key");
    writer.writeCharacters(collection.getKey());
    writer.writeEndElement();
    // infon
    write(collection.getInfons());
  }

  @Override
  public void writeDocument(BioCDocument document)
      throws XMLStreamException {
    if (!hasWrittenCollectionInfo) {
      throw new IllegalStateException(
          "writeCollectionInfo should be invoked before.");
    }
    write(document);
  }
}
