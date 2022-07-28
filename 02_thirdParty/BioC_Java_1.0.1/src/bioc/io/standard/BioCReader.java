package bioc.io.standard;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import bioc.BioCAnnotation;
import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.BioCLocation;
import bioc.BioCNode;
import bioc.BioCPassage;
import bioc.BioCRelation;
import bioc.BioCSentence;

/**
 * can only read collection DTD or sentence DTD
 */
abstract class BioCReader implements Closeable {

  BioCCollection  collection;
  BioCDocument    document;
  BioCPassage     passage;
  BioCSentence    sentence;
  XMLStreamReader reader;
  int             state;

  boolean         atSentenceLevel = false;
  boolean         atDocumentLevel = false;
  boolean         atPassageLevel  = false;

  public BioCReader(Reader reader)
      throws FactoryConfigurationError, XMLStreamException {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
    factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    factory.setProperty(XMLInputFactory.IS_COALESCING, false);
    factory.setProperty(XMLInputFactory.IS_VALIDATING, false);
    factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    this.reader = factory.createXMLStreamReader(reader);
    state = 0;
  }

  public BioCReader(InputStream inputStream)
      throws FactoryConfigurationError, XMLStreamException {
    this(new InputStreamReader(inputStream));
  }

  public BioCReader(File inputFile)
      throws FactoryConfigurationError, XMLStreamException,
      FileNotFoundException {
    this(new FileInputStream(inputFile));
  }

  public BioCReader(String inputFilename)
      throws FactoryConfigurationError, XMLStreamException,
      FileNotFoundException {
    this(new FileInputStream(inputFilename));
  }

  @Override
  public void close()
      throws IOException {
    try {
      reader.close();
    } catch (XMLStreamException e) {
      throw new IOException(e.getMessage(),e);
    }
  }

  protected void read()
      throws XMLStreamException {

    String localName = null;

    while (reader.hasNext()) {
      int type = reader.next();
      switch (state) {
      case 0:
        if (type == XMLStreamConstants.START_ELEMENT) {
          if (reader.getLocalName().equals("collection")) {
            collection = new BioCCollection();
            state = 1;
          }
        }
        break;
      case 1:
        switch (type) {
        case XMLStreamConstants.START_ELEMENT:
          localName = reader.getLocalName();
          if (localName.equals("source")) {
            collection.setSource(readText());
          } else if (localName.equals("date")) {
            collection.setDate(readText());
          } else if (localName.equals("key")) {
            collection.setKey(readText());
          } else if (localName.equals("infon")) {
            String infonKey = reader.getAttributeValue("", "key");
            collection.putInfon(infonKey, readText());
          } else if (localName.equals("document")) {
            // read document
            document = new BioCDocument();
            state = 2;
          } else {
            ;
          }
          break;
        case XMLStreamConstants.END_ELEMENT:
          if (reader.getLocalName().equals("collection")) {
            sentence = null;
            passage = null;
            document = null;
            state = 0;
          }
          break;
        }
        break;
      case 2:
        switch (type) {
        case XMLStreamConstants.START_ELEMENT:
          localName = reader.getLocalName();
          if (localName.equals("id")) {
            document.setID(readText());
          } else if (localName.equals("infon")) {
            String infonKey = reader.getAttributeValue("", "key");
            document.putInfon(infonKey, readText());
          } else if (localName.equals("passage")) {
            // read passage
            passage = new BioCPassage();
            state = 3;
          } else if (localName.equals("relation")) {
            // read relation
            BioCRelation rel = readRelation();
            document.addRelation(rel);
          } else {
            ;
          }
          break;
        case XMLStreamConstants.END_ELEMENT:
          if (reader.getLocalName().equals("document")) {
            state = 1;
            if (atDocumentLevel) {
              return;
            } else if (document != null) {
              collection.addDocument(document);
            }
          }
          break;
        }
        break;
      case 3:
        switch (type) {
        case XMLStreamConstants.START_ELEMENT:
          localName = reader.getLocalName();
          if (localName.equals("offset")) {
            passage.setOffset(Integer.parseInt(readText()));
          } else if (localName.equals("text")) {
            passage.setText(readText());
          } else if (localName.equals("infon")) {
            String infonKey = reader.getAttributeValue("", "key");
            passage.putInfon(infonKey, readText());
          } else if (localName.equals("annotation")) {
            BioCAnnotation ann = readAnnotation();
            passage.addAnnotation(ann);
          } else if (localName.equals("relation")) {
            BioCRelation rel = readRelation();
            passage.addRelation(rel);
          } else if (localName.equals("sentence")) {
            // read sentence
            sentence = new BioCSentence();
            state = 4;
          } else {
            ;
          }
          break;
        case XMLStreamConstants.END_ELEMENT:
          if (reader.getLocalName().equals("passage")) {
            state = 2;
            if (atPassageLevel) {
              return;
            } else if (passage != null) {
              document.addPassage(passage);
            }
          }
          break;
        }
        break;
      case 4:
        switch (type) {
        case XMLStreamConstants.START_ELEMENT:
          localName = reader.getLocalName();
          if (localName.equals("offset")) {
            sentence.setOffset(Integer.parseInt(readText()));
          } else if (localName.equals("text")) {
            sentence.setText(readText());
          } else if (localName.equals("infon")) {
            String infonKey = reader.getAttributeValue("", "key");
            sentence.putInfon(infonKey, readText());
          } else if (localName.equals("annotation")) {
            BioCAnnotation ann = readAnnotation();
            sentence.addAnnotation(ann);
          } else if (localName.equals("relation")) {
            BioCRelation rel = readRelation();
            sentence.addRelation(rel);
          } else {
            ;
          }
          break;
        case XMLStreamConstants.END_ELEMENT:
          if (reader.getLocalName().equals("sentence")) {
            state = 3;
            if (atSentenceLevel) {
              return;
            } else if (sentence != null) {
              passage.addSentence(sentence);
            }
          }
          break;
        }
      }
    }
  }

  private String readText()
      throws XMLStreamException {
    StringBuilder sb = new StringBuilder();
    while (reader.next() == XMLStreamConstants.CHARACTERS) {
      sb.append(reader.getText());
    }
    return sb.toString();
  }

  private BioCAnnotation readAnnotation()
      throws XMLStreamException {
    BioCAnnotation ann = new BioCAnnotation();
    ann.setID(reader.getAttributeValue("", "id"));

    String localName = null;

    while (reader.hasNext()) {
      int type = reader.next();
      switch (type) {
      case XMLStreamConstants.START_ELEMENT:
        localName = reader.getLocalName();
        if (localName.equals("text")) {
          ann.setText(readText());
        } else if (localName.equals("infon")) {
          String infonKey = reader.getAttributeValue("", "key");
          ann.putInfon(infonKey, readText());
        } else if (localName.equals("location")) {
          BioCLocation loc = new BioCLocation();
          loc.setOffset(Integer.parseInt(reader.getAttributeValue("", "offset")));
          loc.setLength(Integer.parseInt(reader.getAttributeValue("", "length")));
          ann.addLocation(loc);
        } else {
          ;
        }
        break;
      case XMLStreamConstants.END_ELEMENT:
        if (reader.getLocalName().equals("annotation")) {
          return ann;
        }
        break;
      }
    }
    assert false;
    return null;
  }

  private BioCRelation readRelation()
      throws XMLStreamException {
    BioCRelation rel = new BioCRelation();
    rel.setID(reader.getAttributeValue("", "id"));

    String localName = null;

    while (reader.hasNext()) {
      int type = reader.next();
      switch (type) {
      case XMLStreamConstants.START_ELEMENT:
        localName = reader.getLocalName();
        if (localName.equals("infon")) {
          String infonKey = reader.getAttributeValue("", "key");
          rel.putInfon(infonKey, readText());
        } else if (localName.equals("node")) {
          BioCNode node = new BioCNode();
          node.setRefid(reader.getAttributeValue("", "refid"));
          node.setRole(reader.getAttributeValue("", "role"));
          rel.addNode(node);
        } else {
          ;
        }
        break;
      case XMLStreamConstants.END_ELEMENT:
        if (reader.getLocalName().equals("relation")) {
          return rel;
        }
        break;
      }
    }
    assert false;
    return null;
  }
}
