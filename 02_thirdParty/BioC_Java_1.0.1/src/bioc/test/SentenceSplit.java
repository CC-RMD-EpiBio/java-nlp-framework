package bioc.test;

/*
 * Split passages into sentences. This is a demonstration of what a useful
 *  tool would look like, not a genuine tool. It uses the naive period-space
 *  pattern to split sentences, not a reliable algorithm as in the C++ tool.
 */

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.xml.stream.XMLStreamException;

import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.BioCPassage;
import bioc.BioCSentence;
import bioc.io.BioCDocumentReader;
import bioc.io.BioCDocumentWriter;
import bioc.io.BioCFactory;
import bioc.util.CopyConverter;

class SentenceConverter extends CopyConverter {

    @Override
        public BioCPassage getPassage(BioCPassage in) {
        BioCPassage out = new BioCPassage();
        out.setOffset( in.getOffset() );
        out.setInfons( in.getInfons() );

        String text = in.getText();
        int current = 0;
        int period = text.indexOf(". ", current);
        while (period > -1) {

            BioCSentence sentence = new BioCSentence();
            sentence.setOffset( out.getOffset() + current );
            sentence.setText( text.substring(current, period + 1) );
            out.addSentence(sentence);

            current = period + 2; // advance to next sentence
            while (current < text.length() && text.charAt(current) == ' ') {
                ++current; // skip extra spaces
            }
            if (current >= text.length()) {
                current = -1; // flag for fell off end
                period = -1;
            } else {
                period = text.indexOf(". ", current);
            }
        }

        if (current > -1) {
            BioCSentence sentence = new BioCSentence();
            sentence.setOffset( out.getOffset() + current);
            sentence.setText( text.substring(current) );
            out.addSentence(sentence);
        }

        return out;
    }
}

public class SentenceSplit {

    public static void main(String[] args)
        throws XMLStreamException, IOException {

        if (args.length != 2) {
            System.err.println("usage: java --jar SentenceSplit in.xml out.xml");
            System.exit(-1);
        }

        SentenceSplit split = new SentenceSplit();
        split.split(args[0], args[1]);
    }

    public void split(String inXML, String outXML)
        throws XMLStreamException, IOException {

    	BioCFactory factory = BioCFactory.newFactory(BioCFactory.WOODSTOX);
    	BioCDocumentReader reader =
    			factory.createBioCDocumentReader(new FileReader(inXML));
    	BioCDocumentWriter writer =
    			factory.createBioCDocumentWriter(
    					new OutputStreamWriter(
    							new FileOutputStream(outXML), "UTF-8"));

    	BioCCollection collection = reader.readCollectionInfo();

    	SentenceConverter converter = new SentenceConverter();
    	BioCCollection outCollection = converter.getCollection(collection);
        outCollection.setKey("sentence.key");
    	writer.writeCollectionInfo(outCollection);

        for ( BioCDocument document : reader ) {
        	BioCDocument outDocument = converter.getDocument(document);
        	writer.writeDocument(outDocument);
        }

        reader.close();
        writer.close();
    }
}
