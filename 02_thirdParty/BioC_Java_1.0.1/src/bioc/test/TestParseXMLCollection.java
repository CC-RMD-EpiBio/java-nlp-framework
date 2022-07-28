package bioc.test;

/**
 * Another copy the XML program. Whole Collection IO is used.
 **/

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.xml.stream.XMLStreamException;

import bioc.BioCCollection;
import bioc.io.BioCCollectionWriter;
import bioc.io.BioCFactory;
import bioc.io.woodstox.ConnectorWoodstox;
import bioc.util.CopyConverter;

public class TestParseXMLCollection {

  public static void main(String[] args)
      throws XMLStreamException, IOException {

    if (args.length != 2) {
      System.err.println("usage: java TestParseXMLCollection in.xml out.xml");
      System.exit(-1);
    }

    TestParseXMLCollection copy_xml = new TestParseXMLCollection();
    copy_xml.copy(args[0], args[1]);
  }

  public void copy(String inXML, String outXML)
      throws XMLStreamException, IOException {
	  
    ConnectorWoodstox connector = new ConnectorWoodstox();
    
    BioCCollection collection = 
        connector.parseXMLCollection( new FileReader(inXML) );

    BioCFactory factory = BioCFactory.newFactory(BioCFactory.WOODSTOX);
    BioCCollectionWriter writer =
			  factory.createBioCCollectionWriter(
					  new OutputStreamWriter(
							  new FileOutputStream(outXML), "UTF-8"));

    CopyConverter converter = new CopyConverter();
    BioCCollection outCollection = converter.getCollection(collection);
    writer.writeCollection(outCollection);

    writer.close();
  }
}
