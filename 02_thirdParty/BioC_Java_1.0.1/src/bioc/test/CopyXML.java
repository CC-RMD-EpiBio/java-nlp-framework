package bioc.test;

/**
 * Copy the XML program. Document at-a-time IO is used.
 **/

import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.io.BioCDocumentReader;
import bioc.io.BioCDocumentWriter;
import bioc.io.BioCFactory;

public class CopyXML {

  public static void main(String[] args)
      throws XMLStreamException, IOException {

    if (args.length < 2 ) {
      usage();
    }

    int fileArgs = 0;
    String parser = BioCFactory.STANDARD;
    if ( args[0].equals("-p") ) {
      parser = args[1];
      fileArgs += 2;
    }
    
    if ( args.length != fileArgs+2 )
      usage();
    
    CopyXML copy_xml = new CopyXML();
    copy_xml.copy(parser, args[fileArgs], args[fileArgs+1]);
  }

  protected static void usage() {
    System.err.println("usage: java Copy_XML [-p parser] in.xml out.xml");
    System.exit(-1);
  }
 
  public void copy(String parser, String inXML, String outXML)
      throws XMLStreamException, IOException {

	  BioCFactory factory = BioCFactory.newFactory(parser);
	  BioCDocumentReader reader =
			  factory.createBioCDocumentReader(new FileReader(inXML));
	  BioCDocumentWriter writer =
			  factory.createBioCDocumentWriter(
					  new OutputStreamWriter(
							  new FileOutputStream(outXML), "UTF-8"));
//	  factory.createBioCDocumentWriter(new FileWriter(outXML));

    BioCCollection collection = reader.readCollectionInfo();
    writer.writeCollectionInfo(collection);

    for (BioCDocument document : reader) {
    	writer.writeDocument(document);
    }
    reader.close();
    writer.close();
  }
}
