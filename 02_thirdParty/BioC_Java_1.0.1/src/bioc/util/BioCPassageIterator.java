package bioc.util;

import java.util.Iterator;

import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.BioCPassage;

public class BioCPassageIterator implements Iterator<BioCPassage> {

  private Iterator<BioCDocument> documentItr;
  private BioCDocument           currentDocument;
  private Iterator<BioCPassage>  passageItr;

  public BioCPassageIterator(BioCCollection collection) {
    documentItr = collection.getDocuments().iterator();
    if (documentItr.hasNext()) {
      currentDocument = documentItr.next();
      passageItr = currentDocument.getPassages().iterator();
    } else {
      currentDocument = null;
      passageItr = null;
    }
  }

  public BioCDocument getDocument() {
    return currentDocument;
  }

  @Override
  public boolean hasNext() {
    if (passageItr == null) {
      return false;
    } else if (passageItr.hasNext()) {
      return true;
    } else if (documentItr.hasNext()) {
      currentDocument = documentItr.next();
      passageItr = currentDocument.getPassages().iterator();
      return hasNext();
    } else {
      return false;
    }
  }

  @Override
  public BioCPassage next() {
    return passageItr.next();
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
