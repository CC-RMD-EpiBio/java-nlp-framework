package bioc.io.woodstox;

import java.io.Reader;
import java.io.Writer;

import bioc.io.BioCCollectionReader;
import bioc.io.BioCCollectionWriter;
import bioc.io.BioCDocumentReader;
import bioc.io.BioCDocumentWriter;
import bioc.io.BioCFactory;

public class BioCFactoryImpl extends BioCFactory {

  @Override
  public BioCCollectionWriter createBioCCollectionWriter(Writer out) {
    return new BioCWoodstoxAdapter(out);
  }

  @Override
  public BioCDocumentWriter createBioCDocumentWriter(Writer out) {
    return new BioCWoodstoxAdapter(out);
  }

  @Override
  public BioCCollectionReader createBioCCollectionReader(Reader in) {
    return new BioCWoodstoxAdapter(in);
  }

  @Override
  public BioCDocumentReader createBioCDocumentReader(Reader in) {
    return new BioCWoodstoxAdapter(in);
  }

}
