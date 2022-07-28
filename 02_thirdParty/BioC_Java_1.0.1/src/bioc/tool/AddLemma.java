package bioc.tool;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.xml.stream.XMLStreamException;

import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.io.BioCDocumentReader;
import bioc.io.BioCDocumentWriter;
import bioc.io.BioCFactory;

/**
 * Test BioCDocumentReader and BioCDocumentWriter
 */
public class AddLemma {
  
  private LemmaConverter converter; 
  
  public AddLemma() {
    converter = new LemmaConverter();   
  }
  
  public static void main(String[] args)
      throws IOException, XMLStreamException {

    if (args.length != 2) {
      System.err.println("usage: java -jar ModifyXML in.xml out.xml");
      System.exit(-1);
    }
    
    AddLemma copy_xml = new AddLemma();

    copy_xml.copy(args[0], args[1], BioCFactory.WOODSTOX);
  }

  public void copy(String inXML, String outXML, String flags)
      throws XMLStreamException, IOException {
    BioCFactory factory = BioCFactory.newFactory(flags);
    BioCDocumentReader reader = factory
        .createBioCDocumentReader(new FileReader(inXML));
    BioCDocumentWriter writer = factory.createBioCDocumentWriter(
			  new OutputStreamWriter(
					  new FileOutputStream(outXML), "UTF-8"));

    BioCCollection collection = reader.readCollectionInfo();
    
    
    writer.writeCollectionInfo(collection);
    BioCDocument doc = null;
    while ((doc = reader.readDocument()) != null) { 
      BioCDocument outDoc = converter.getDocument(doc);
      writer.writeDocument(outDoc);
    }
    reader.close();
    writer.close();

  }
  
    
}
