package bioc.io.standard;

import java.io.Closeable;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import bioc.BioCAnnotation;
import bioc.BioCDocument;
import bioc.BioCLocation;
import bioc.BioCNode;
import bioc.BioCPassage;
import bioc.BioCRelation;
import bioc.BioCSentence;

abstract class BioCAllWriter implements Closeable {

  XMLStreamWriter writer;

  protected BioCAllWriter(Writer writer)
      throws FactoryConfigurationError, XMLStreamException {
    XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
    // this.writer = new IndentingXMLStreamWriter(
    // xmlOutputFactory.createXMLStreamWriter(writer));
    this.writer = xmlOutputFactory.createXMLStreamWriter(writer);
    this.writer.writeStartDocument("UTF-8", "1.0");
  }

  protected BioCAllWriter(OutputStream outputStream)
      throws FactoryConfigurationError, XMLStreamException {
    this(new OutputStreamWriter(outputStream));
  }

  protected void write(BioCPassage passage)
      throws XMLStreamException {
    writer.writeStartElement("passage");
    // infon
    write(passage.getInfons());
    // offset
    writer.writeStartElement("offset");
    writer.writeCharacters(Integer.toString(passage.getOffset()));
    writer.writeEndElement();
    // text
    if (passage.getText() != null && !passage.getText().isEmpty()) {
      writer.writeStartElement("text");
      writer.writeCharacters(passage.getText());
      writer.writeEndElement();
    }
    // sen
    for (BioCSentence sen : passage.getSentences()) {
      write(sen);
    }
    // ann
    for (BioCAnnotation ann : passage.getAnnotations()) {
      write(ann);
    }
    // rel
    for (BioCRelation rel : passage.getRelations()) {
      write(rel);
    }

    writer.writeEndElement();
  }

  protected void write(BioCSentence sentence)
      throws XMLStreamException {
    writer.writeStartElement("sentence");
    // infon
    write(sentence.getInfons());
    // offset
    writer.writeStartElement("offset");
    writer.writeCharacters(Integer.toString(sentence.getOffset()));
    writer.writeEndElement();
    // text
    if (sentence.getText() != null && !sentence.getText().isEmpty()) {
      writer.writeStartElement("text");
      writer.writeCharacters(sentence.getText());
      writer.writeEndElement();
    }
    // ann
    for (BioCAnnotation ann : sentence.getAnnotations()) {
      write(ann);
    }
    // rel
    for (BioCRelation rel : sentence.getRelations()) {
      write(rel);
    }

    writer.writeEndElement();
  }

  protected final void write(BioCAnnotation annotation)
      throws XMLStreamException {
    writer.writeStartElement("annotation");
    // id
    if ( annotation.getID() != null) 
      writer.writeAttribute("id", annotation.getID());
    // infon
    write(annotation.getInfons());
    // location
    for (BioCLocation loc : annotation.getLocations()) {
      writer.writeStartElement("location");
      writer.writeAttribute("offset", Integer.toString(loc.getOffset()));
      writer.writeAttribute("length", Integer.toString(loc.getLength()));
      writer.writeEndElement();
    }
    // text
    writer.writeStartElement("text");
    writer.writeCharacters(annotation.getText());
    writer.writeEndElement();
    //
    writer.writeEndElement();
  }

  protected final void write(BioCRelation relation)
      throws XMLStreamException {
    writer.writeStartElement("relation");
    // id
    writer.writeAttribute("id", relation.getID());
    // infon
    write(relation.getInfons());
    // labels
    for (BioCNode label : relation.getNodes()) {
      write(label);
    }
    //
    writer.writeEndElement();
  }

  protected final void write(BioCNode node)
      throws XMLStreamException {
    writer.writeStartElement("node");
    // id
    writer.writeAttribute("refid", node.getRefid());
    // role
    writer.writeAttribute("role", node.getRole());
    //
    writer.writeEndElement();
  }

  protected final void write(BioCDocument document)
      throws XMLStreamException {

    writer.writeStartElement("document");
    // id
    writer.writeStartElement("id");
    writer.writeCharacters(document.getID());
    writer.writeEndElement();
    // infon
    write(document.getInfons());
    // passages
    for (BioCPassage passage : document.getPassages()) {
      write(passage);
    }
    // relations
    for (BioCRelation rel : document.getRelations()) {
      write(rel);
    }
    writer.writeEndElement();
  }

  protected final void write(Map<String, String> infons)
      throws XMLStreamException {
    for (Entry<String, String> infon : infons.entrySet()) {
      writer.writeStartElement("infon");
      writer.writeAttribute("key", infon.getKey());
      writer.writeCharacters(infon.getValue());
      writer.writeEndElement();
    }
  }
}
