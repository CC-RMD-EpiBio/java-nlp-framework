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
import bioc.io.BioCCollectionReader;
import bioc.io.BioCCollectionWriter;
import bioc.io.BioCFactory;
import bioc.util.CopyConverter;

public class CopyCollectionXML {

  public static void main(String[] args)
      throws XMLStreamException, IOException {

    if (args.length != 2) {
      System.err.println("usage: java Copy4_XML in.xml out.xml");
      System.exit(-1);
    }

    CopyCollectionXML copy_xml = new CopyCollectionXML();
    copy_xml.copy(args[0], args[1]);
  }

  public void copy(String inXML, String outXML)
      throws XMLStreamException, IOException {
	  
	  BioCFactory factory = BioCFactory.newFactory(BioCFactory.WOODSTOX);
	  BioCCollectionReader reader =
			  factory.createBioCCollectionReader(new FileReader(inXML));
	  BioCCollectionWriter writer =
			  factory.createBioCCollectionWriter(
					  new OutputStreamWriter(
							  new FileOutputStream(outXML), "UTF-8"));
    
    BioCCollection collection = reader.readCollection();

    CopyConverter converter = new CopyConverter();
    BioCCollection outCollection = converter.getCollection(collection);
    writer.writeCollection(outCollection);

    reader.close();
    writer.close();
  }
}
