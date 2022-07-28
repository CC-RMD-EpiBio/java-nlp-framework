package bioc.io.woodstox;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;

import bioc.BioCAnnotation;
import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.BioCLocation;
import bioc.BioCNode;
import bioc.BioCPassage;
import bioc.BioCRelation;
import bioc.BioCSentence;

;

/**
 * Read and write BioC data using the woodstox StAX XML parser. BioCDocument at
 * a time IO avoids using excessive memory.
 */
public class ConnectorWoodstox implements Iterator<BioCDocument> {

  boolean          inDocument;
  boolean          finishedXML;
  XMLStreamReader2 xmlr;

  XMLStreamWriter2 xtw = null;

  /**
   * Call after last document has been written. Performs any needed cleanup and
   * closes the XML file.
   * @throws XMLStreamException 
   */
  public void endWrite() throws XMLStreamException {
      xtw.writeEndElement();
      xtw.writeEndDocument();
      xtw.flush();
      xtw.close();
  }

  void fromXML(BioCDocument document)
      throws XMLStreamException {
    while (xmlr.hasNext()) {
      int eventType = xmlr.next();
      switch (eventType) {
      case XMLEvent.START_ELEMENT:
        String name = xmlr.getName().toString();
        if (name.equals("id")) {
          document.setID(getString("id"));
        } else if (name.equals("infon")) {
            document.putInfon(
                    xmlr.getAttributeValue("", "key"),
                    getString("infon"));
        } else if (name.equals("passage")) {
          document.addPassage(getBioCPassage());
        } else if (name.equals("relation")) {
            document.addRelation(getBioCRelation());
        }
        break;
      case XMLEvent.END_ELEMENT:
        if (xmlr.getName().toString().equals("document")) {
          inDocument = false;
        }
        return;
      }
    }
  }

  BioCAnnotation getBioCAnnotation()
      throws XMLStreamException {

    BioCAnnotation annotation = new BioCAnnotation();

    String id = xmlr.getAttributeValue(null, "id");
    if (id != null) {
      annotation.setID(id);
    }

    while (xmlr.hasNext()) {
      int eventType = xmlr.next();
      switch (eventType) {
      case XMLEvent.START_ELEMENT:
        String name = xmlr.getName().toString();
        if (name.equals("infon")) {
          annotation.putInfon(
              xmlr.getAttributeValue("", "key"),
              getString("infon"));
        } else if (name.equals("location")) {
          annotation.addLocation(getBioCLocation());
        } else if (name.equals("text")) {
          annotation.setText(getString("text"));
        }
        break;
      case XMLEvent.END_ELEMENT:
        if (xmlr.getName().toString().equals("annotation")) {
          return annotation;
        } else if (xmlr.getName().toString().equals("infon")) {
          ;
        }
        throw new XMLStreamException("found at end of annotation: "
            + xmlr.getName().toString());
      }
    }
    return annotation;
  }

  BioCLocation getBioCLocation()
      throws XMLStreamException {
    BioCLocation location = new BioCLocation();
    location
        .setOffset(Integer.parseInt(xmlr.getAttributeValue(null, "offset")));
    location
        .setLength(Integer.parseInt(xmlr.getAttributeValue(null, "length")));
    xmlr.hasNext();
    xmlr.next();
    if (!xmlr.getName().toString().equals("location")) {
      throw new XMLStreamException("found at end of location: "
          + xmlr.getName().toString());
    }
    return location;
  }

  BioCNode getBioCNode()
      throws XMLStreamException {
    BioCNode node = new BioCNode();
    node.setRefid(xmlr.getAttributeValue(null, "refid"));
    node.setRole(xmlr.getAttributeValue(null, "role"));
    xmlr.hasNext();
    xmlr.next();
    if (!xmlr.getName().toString().equals("node")) {
      throw new XMLStreamException("found at end of node: "
          + xmlr.getName().toString());
    }
    return node;
  }

  BioCPassage getBioCPassage()
      throws XMLStreamException {

    BioCPassage passage = new BioCPassage();
    while (xmlr.hasNext()) {
      int eventType = xmlr.next();
      switch (eventType) {
      case XMLEvent.START_ELEMENT:
        String name = xmlr.getName().toString();
        if (name.equals("infon")) {
          passage.putInfon(
              xmlr.getAttributeValue("", "key"),
              getString("infon"));
        } else if (name.equals("offset")) {
          passage.setOffset(getInt("offset"));
        } else if (name.equals("text")) {
          passage.setText(getString("text"));
        } else if (name.equals("sentence")) {
          passage.addSentence(getBioCSentence());
        } else if (name.equals("annotation")) {
          passage.addAnnotation(getBioCAnnotation());
        } else if (name.equals("relation")) {
          passage.addRelation(getBioCRelation());
        }
        break;
      case XMLEvent.END_ELEMENT:
        if (xmlr.getName().toString().equals("passage")) {
          return passage;
        } else if (xmlr.getName().toString().equals("infon")) {
          ;
        }
        throw new XMLStreamException("found at end of passage: "
            + xmlr.getName().toString());
      }
    }
    return passage;
  }

  BioCRelation getBioCRelation()
      throws XMLStreamException {

    BioCRelation relation = new BioCRelation();

    String id = xmlr.getAttributeValue(null, "id");
    if (id != null) {
      relation.setID(id);
    }

    while (xmlr.hasNext()) {
      int eventType = xmlr.next();
      switch (eventType) {
      case XMLEvent.START_ELEMENT:
        String name = xmlr.getName().toString();
        if (name.equals("infon")) {
          relation.putInfon(
              xmlr.getAttributeValue("", "key"),
              getString("infon"));
        } else if (name.equals("node")) {
          relation.addNode(getBioCNode());
        }
        break;
      case XMLEvent.END_ELEMENT:
        if (xmlr.getName().toString().equals("relation")) {
          return relation;
        } else if (xmlr.getName().toString().equals("infon")) {
          ;
        }
        throw new XMLStreamException("found at end of relation: "
            + xmlr.getName().toString());
      }
    }
    return relation;
  }

  BioCSentence getBioCSentence()
      throws XMLStreamException {

    BioCSentence sentence = new BioCSentence();

    while (xmlr.hasNext()) {
      int eventType = xmlr.next();
      switch (eventType) {
      case XMLEvent.START_ELEMENT:
        String name = xmlr.getName().toString();
        if (name.equals("infon")) {
          sentence.putInfon(
              xmlr.getAttributeValue("", "key"),
              getString("infon"));
        } else if (name.equals("offset")) {
          sentence.setOffset(getInt("offset"));
        } else if (name.equals("text")) {
          sentence.setText(getString("text"));
        } else if (name.equals("annotation")) {
          sentence.addAnnotation(getBioCAnnotation());
        } else if (name.equals("relation")) {
          sentence.addRelation(getBioCRelation());
        }
        break;
      case XMLEvent.END_ELEMENT:
        if (xmlr.getName().toString().equals("sentence")) {
          return sentence;
        } else if (xmlr.getName().toString().equals("infon")) {
          ;
        }
        throw new XMLStreamException("found at end of sentence: "
            + xmlr.getName().toString());
      }
    }
    return sentence;
  }

  int getInt(String name)
      throws XMLStreamException {
    String strValue = getString(name);
    return Integer.parseInt(strValue);
  }

  String getString(String name)
      throws XMLStreamException {

    StringBuilder buf = new StringBuilder();
    while (xmlr.hasNext()) {
      int eventType = xmlr.next();
      switch (eventType) {
      case XMLEvent.CHARACTERS:
        buf.append(xmlr.getText());
        break;
      case XMLEvent.END_ELEMENT:
        if (xmlr.getName().toString().equals(name)) {
          return buf.toString();
        }
      }
    }

    return ""; // should NOT be here
  }

  /**
   * Returns true if the collection has more documents.
   */
  @Override
  public boolean hasNext() {
    if (finishedXML) {
      return false;
    }
    if (inDocument) {
      return true;
    }

    try {
      while (xmlr.hasNext()) {
        int eventType = xmlr.next();
        switch (eventType) {
        case XMLEvent.START_ELEMENT:
          if (xmlr.getName().toString().equals("document")) {
            inDocument = true;
            return true;
          }
          break;
        case XMLEvent.END_DOCUMENT:
          finishedXML = true;
          inDocument = false;
          return false;
        }
      }
    } catch (XMLStreamException ex) {
      /* This loses the exception, but hasNext is not allowed to throw
       * an exception. Could store the exception so it could be retrieved
       * later, but calling code would never know to retrieve the 
       * exception.
       */
      inDocument = false;
      return false;
    }

    // end of XML
    finishedXML = true;
    inDocument = false;
    return false;
  }

  /**
   * Returns the document in the collection.
   */
  @Override
  public BioCDocument next() {
    BioCDocument document = new BioCDocument();
    try {
      if (finishedXML) {
        return null;
      }
      if (!inDocument) {
        if (!hasNext()) {
          return null;
        }
        if (!inDocument) {
          throw new XMLStreamException(
              "*** impossible after hasNext() true; inDocument false");
        }
      }

      fromXML(document);
    } catch (XMLStreamException ex) {
      throw new NoSuchElementException( ex.getMessage() );
    }
    return document;
  }

  public BioCCollection parseXMLCollection(Reader xmlReader) 
      throws XMLStreamException {
    XMLInputFactory2 xmlif = null;
    xmlif = (XMLInputFactory2) XMLInputFactory2.newInstance();
    xmlif.setProperty(
        XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
        Boolean.FALSE);
    xmlif.setProperty(
        XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
        Boolean.FALSE);
    xmlif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    xmlif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
    xmlif.configureForSpeed();
    BioCCollection collection = new BioCCollection();

    xmlr = (XMLStreamReader2) xmlif.createXMLStreamReader(xmlReader);
    int eventType = xmlr.getEventType();
    String curElement = "";
    finishedXML = false;
    inDocument = false;

    while (xmlr.hasNext() && !inDocument) {
      eventType = xmlr.next();
      switch (eventType) {
      case XMLEvent.START_ELEMENT:
        curElement = xmlr.getName().toString();
        if (curElement.equals("document")) {
          inDocument = true;
        } else if (curElement.equals("source")) {
          collection.setSource(getString("source"));
        } else if (curElement.equals("date")) {
          collection.setDate(getString("date"));
        } else if (curElement.equals("key")) {
          collection.setKey(getString("key"));
        } else if (curElement.equals("infon")) {
          collection.putInfon(
              xmlr.getAttributeValue("", "key"),
              getString("infon"));
        }
        break;
      case XMLEvent.END_ELEMENT:
        if (curElement.equals("collection")) {
          inDocument = false;
          finishedXML = true;
        }
        break;
      case XMLEvent.END_DOCUMENT:
        inDocument = false;
        finishedXML = true;
        break;
      }
    }
    while (hasNext()) {
      BioCDocument document = next();
      collection.addDocument(document);
    }
    return collection;
  }

  public BioCCollection parseXMLCollection(String xml)
      throws Exception {
    return parseXMLCollection(new StringReader(xml));
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  /**
   * Start reading XML file
   * 
   * @param in Reader with XML to read
   * @throws XMLStreamException 
   */
  public BioCCollection startRead(Reader in) 
      throws XMLStreamException {
    XMLInputFactory2 xmlif = null;
    xmlif = (XMLInputFactory2) XMLInputFactory2.newInstance();
    xmlif.setProperty(
        XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
        Boolean.FALSE);
    xmlif.setProperty(
        XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
        Boolean.FALSE);
    //      xmlif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    xmlif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
    xmlif.configureForSpeed();

    BioCCollection collection = new BioCCollection();

    xmlr = (XMLStreamReader2) xmlif.createXMLStreamReader(in);
    int eventType = xmlr.getEventType();
    String curElement = "";
    finishedXML = false;
    inDocument = false;

    while (xmlr.hasNext() && !inDocument) {
      eventType = xmlr.next();
      switch (eventType) {
      case XMLEvent.START_ELEMENT:
        curElement = xmlr.getName().toString();
        if (curElement.equals("document")) {
          inDocument = true;
        } else if (curElement.equals("source")) {
          collection.setSource(getString("source"));
        } else if (curElement.equals("date")) {
          collection.setDate(getString("date"));
        } else if (curElement.equals("key")) {
          collection.setKey(getString("key"));
        } else if (curElement.equals("infon")) {
          collection.putInfon(
              xmlr.getAttributeValue("", "key"),
              getString("infon"));
        }
        break;
      case XMLEvent.END_ELEMENT:
        if (curElement.equals("collection")) {
          inDocument = false;
          finishedXML = true;
        }
        break;
      case XMLEvent.END_DOCUMENT:
        inDocument = false;
        finishedXML = true;
        break;
      }
    }

    return collection;
  }

  /**
   * Starting writing an XML document.
   * 
   * @param out Writer to write XML to
   * @param collection collection to write
   * 
   *          Since this class is for document at a time IO, any documents in
   *          the collection are ignored.
   * @throws XMLStreamException 
   */
  public void startWrite(Writer out, BioCCollection collection)
      throws XMLStreamException {
    
    // ?? if filename == '-', write to Print.out ??
    XMLOutputFactory xof = XMLOutputFactory.newInstance();
    xtw = null;
    xtw = (XMLStreamWriter2) xof.createXMLStreamWriter(out);
    // new FileWriter(filename));

    // xtw.writeStartBioCDocument(null,"1.0");
    xtw.writeStartDocument();
    xtw.writeDTD("collection", "BioC.dtd", null, null);
    xtw.writeStartElement("collection");
    writeXML("source", collection.getSource());
    writeXML("date", collection.getDate());
    writeXML("key", collection.getKey());
    writeXML(collection.getInfons());

  }

  public String toXML(BioCCollection collection)
      throws Exception {
    XMLOutputFactory xof = XMLOutputFactory.newInstance();
    StringWriter xml = new StringWriter();
    xtw = (XMLStreamWriter2) xof.createXMLStreamWriter(xml);
    xtw.writeStartDocument();
    xtw.writeDTD("collection", "BioC.dtd", null, null);
    xtw.writeStartElement("collection");
    writeXML("source", collection.getSource());
    writeXML("date", collection.getDate());
    writeXML("key", collection.getKey());
    writeXML(collection.getInfons());
    // /////////////////////////////////
    // Now, cycle thru each Document
    // /////////////////////////////////
    List<BioCDocument> readDocuments = collection.getDocuments();
    for (int i = 0; i < readDocuments.size(); i++) {
      BioCDocument readDocument = readDocuments.get(i);
      writeNext(readDocument);
    }
    endWrite();
    return xml.toString();
  }

  /**
   * Write the next document to the XML file.
   * 
   * @param document document to write
   * @throws XMLStreamException 
   */
  public void writeNext(BioCDocument document)
      throws XMLStreamException {
    writeXML(document);
  }

  void writeXML(BioCAnnotation annotation)
      throws XMLStreamException {
    xtw.writeStartElement("annotation");
    if (annotation.getID().length() > 0) {
      xtw.writeAttribute("id", annotation.getID());
    }
    writeXML(annotation.getInfons());
    for (BioCLocation location : annotation.getLocations()) {
      writeXML(location);
    }
    writeXML("text", annotation.getText());
    xtw.writeEndElement();
  }

  void writeXML(BioCDocument document)
      throws XMLStreamException {
    xtw.writeStartElement("document");
    writeXML("id", document.getID());
    writeXML(document.getInfons());
    for (BioCPassage passage : document.getPassages()) {
      writeXML(passage);
    }
    for (BioCRelation relation : document.getRelations()) {
    	writeXML(relation);
    }
    xtw.writeEndElement();
  }

  void writeXML(BioCLocation location)
      throws XMLStreamException {
    xtw.writeStartElement("location");
    xtw.writeAttribute("offset", Integer.toString(location.getOffset()));
    xtw.writeAttribute("length", Integer.toString(location.getLength()));
    xtw.writeEndElement();
  }

  void writeXML(BioCNode node)
      throws XMLStreamException {
    xtw.writeStartElement("node");
    xtw.writeAttribute("refid", node.getRefid());
    xtw.writeAttribute("role", node.getRole());
    xtw.writeEndElement();
  }

  void writeXML(BioCPassage passage)
      throws XMLStreamException {
    xtw.writeStartElement("passage");
    writeXML(passage.getInfons());
    writeXML("offset", passage.getOffset());
    if (passage.getText().length() > 0) {
      writeXML("text", passage.getText());
    }
    for (BioCSentence sentence : passage.getSentences()) {
      writeXML(sentence);
    }
    for (BioCAnnotation annotation : passage.getAnnotations()) {
      writeXML(annotation);
    }
    for (BioCRelation relation : passage.getRelations()) {
      writeXML(relation);
    }
    xtw.writeEndElement();
  }

  void writeXML(BioCRelation relation)
      throws XMLStreamException {
    xtw.writeStartElement("relation");
    if (relation.getID().length() > 0) {
      xtw.writeAttribute("id", relation.getID());
    }
    writeXML(relation.getInfons());
    for (BioCNode node : relation.getNodes()) {
      writeXML(node);
    }
    xtw.writeEndElement();
  }

  void writeXML(BioCSentence sentence)
      throws XMLStreamException {
    xtw.writeStartElement("sentence");
    writeXML(sentence.getInfons());
    writeXML("offset", sentence.getOffset());
    if (sentence.getText().length() > 0) {
      writeXML("text", sentence.getText());
    }
    for (BioCAnnotation annotation : sentence.getAnnotations()) {
      writeXML(annotation);
    }
    for (BioCRelation relation : sentence.getRelations()) {
      writeXML(relation);
    }
    xtw.writeEndElement();
  }

  void writeXML(Map<String, String> infons)
      throws XMLStreamException {
    for (Entry<String, String> entry : infons.entrySet()) {
      xtw.writeStartElement("infon");
      xtw.writeAttribute("key", entry.getKey());
      xtw.writeCharacters(entry.getValue());
      xtw.writeEndElement();
    }
  }

  void writeXML(String element, int contents)
      throws XMLStreamException {
    writeXML(element, new Integer(contents).toString());
  }

  void writeXML(String element, String contents)
      throws XMLStreamException {
    xtw.writeStartElement(element);
    xtw.writeCharacters(contents);
    xtw.writeEndElement();
  }

}
