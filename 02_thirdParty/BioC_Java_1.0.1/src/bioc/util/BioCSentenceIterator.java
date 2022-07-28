package bioc.util;

import java.util.Iterator;

import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.BioCPassage;
import bioc.BioCSentence;

public class BioCSentenceIterator implements Iterator<BioCSentence> {

  private Iterator<BioCDocument> documentItr;
  private BioCDocument           currentDocument;
  private Iterator<BioCPassage>  passageItr;
  private BioCPassage            currentPassage;
  private Iterator<BioCSentence> sentenceItr;

  public BioCSentenceIterator(BioCCollection collection) {
    documentItr = collection.getDocuments().iterator();
    init();
  }

  private void init() {
    if (documentItr.hasNext()) {
      currentDocument = documentItr.next();
      passageItr = currentDocument.getPassages().iterator();
      if (passageItr.hasNext()) {
        currentPassage = passageItr.next();
        sentenceItr = currentPassage.getSentences().iterator();
      } else {
        init();
      }
    } else {
      currentDocument = null;
      passageItr = null;
      currentPassage = null;
      sentenceItr = null;
    }
  }

  public BioCDocument getDocument() {
    return currentDocument;
  }

  public BioCPassage getPassage() {
    return currentPassage;
  }

  @Override
  public boolean hasNext() {
    if (sentenceItr == null) {
      return false;
    } else if (sentenceItr.hasNext()) {
      return true;
    } else if (passageItr.hasNext()) {
      currentPassage = passageItr.next();
      sentenceItr = currentPassage.getSentences().iterator();
      return hasNext();
    } else if (documentItr.hasNext()) {
      currentDocument = documentItr.next();
      passageItr = currentDocument.getPassages().iterator();
      return hasNext();
    } else {
      return false;
    }
  }

  @Override
  public BioCSentence next() {
    return sentenceItr.next();
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

}
