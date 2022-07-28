package bioc.test.junit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.io.BioCDocumentReader;
import bioc.io.BioCDocumentWriter;
import bioc.io.BioCFactory;

public class BioCDocumentIOTest {

  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  @BeforeClass
  public static void onlyOnce() {

    XMLUnit.setIgnoreWhitespace(true);
    XMLUnit.setIgnoreComments(true);
    XMLUnit.setIgnoreAttributeOrder(true);

    // don't validate dtd
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setValidating(false);
    try {
      dbf.setFeature("http://xml.org/sax/features/namespaces", false);
      dbf.setFeature("http://xml.org/sax/features/validation", false);
      dbf.setFeature(
          "http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
          false);
      dbf.setFeature(
          "http://apache.org/xml/features/nonvalidating/load-external-dtd",
          false);
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }

    XMLUnit.setTestDocumentBuilderFactory(dbf);
    XMLUnit.setControlDocumentBuilderFactory(dbf);
  }

  @Test
  public void testWoodstox()
      throws Exception {
    File tempFile = testFolder.newFile("test.standard.xml");
    File dir = new File("xml");
    File[] files = dir.listFiles(new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(".xml");
      }

    });
    for (File f : files) {
      System.out.println("testing WOODSTOX IO on " + f + " ...");
      testXMLIdentical(
          BioCFactory.newFactory(BioCFactory.WOODSTOX),
          f,
          tempFile);
    }
  }

  @Test
  public void testStandard()
      throws Exception {
    File tempFile = testFolder.newFile("test.standard.xml");
    File dir = new File("xml");
    File[] files = dir.listFiles(new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(".xml");
      }

    });
    for (File f : files) {
      System.out.println("testing STANDARD IO on " + f + " ...");
      testXMLIdentical(
          BioCFactory.newFactory(BioCFactory.STANDARD),
          f,
          tempFile);
    }
  }

  private void testXMLIdentical(BioCFactory factory, File inXML, File outXML)
      throws Exception {
    BioCDocumentReader reader = factory
        .createBioCDocumentReader(new FileReader(inXML));

    BioCDocumentWriter writer = factory
        .createBioCDocumentWriter(new FileWriter(outXML));

    BioCCollection collection = reader.readCollectionInfo();
    writer.writeCollectionInfo(collection);

    BioCDocument doc = null;
    while ((doc = reader.readDocument()) != null) {
      writer.writeDocument(doc);
    }

    reader.close();
    writer.close();

    FileReader controlReader = new FileReader(inXML);
    FileReader testReader = new FileReader(outXML);

    Diff diff = new Diff(controlReader, testReader);

    try {
      XMLAssert.assertTrue("XML similar " + diff.toString(), diff.similar());
    } catch (AssertionError e) {
      DetailedDiff detDiff = new DetailedDiff(diff);
      @SuppressWarnings("rawtypes")
      List allDifferences = detDiff.getAllDifferences();
      for (Object object : allDifferences) {
        Difference difference = (Difference) object;
        System.out.println(difference);
      }
    }
  }
}
