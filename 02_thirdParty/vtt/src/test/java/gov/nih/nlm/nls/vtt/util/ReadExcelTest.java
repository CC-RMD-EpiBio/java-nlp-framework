/*
 *  Copyright 2014 United States Department of Veterans Affairs
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */
package gov.nih.nlm.nls.vtt.util;

import static org.junit.Assert.*;
import gov.nih.nlm.nls.vtt.model.Markup;
import gov.nih.nlm.nls.vtt.model.VttDocument;
import gov.nih.nlm.nls.vtt.model.VttObj;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author VHAISLREDDD
 *
 */
public class ReadExcelTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link gov.nih.nlm.nls.vtt.util.ReadExcel#read(gov.nih.nlm.nls.vtt.model.Markups)}.
	 */
	@Test
	public void testRead() {
		final Pattern snippetNumberPattern = Pattern.compile("snippetNumber=\"(\\d+)\"");
		try {
			URL testExcelFile = this.getClass().getClassLoader().getResource("lorem ipsum snippets.xlsx");
			Assert.assertNotNull(testExcelFile);
			final VttObj vttObj = new VttObj(new File(testExcelFile.toURI()));
			Assert.assertNotNull(vttObj);
			vttObj.getMainFrame().setVisible(false);
			// Wait for file load to complete.  There has to be a better way to detect it...
			Thread.sleep(5000);
			SwingWorker<Integer, ?> sw = new SwingWorker<Integer, Object>() {
				@Override
				protected Integer doInBackground() throws Exception {
					int numMarkupsProcessed = 0;
					try {
						vttObj.getMainFrame().setVisible(false);
						VttDocument vttDoc = vttObj.getVttDocument();
						for (Markup m : vttDoc.getMarkups().getMarkups()) {
							numMarkupsProcessed++;
							if (ReadExcel.SNIPPET_COLUMN_TAGNAME.equals(m.getTagName())) {
								String annotation = m.getAnnotation();
								Matcher matcher = snippetNumberPattern.matcher(annotation);
								Assert.assertTrue(matcher.find());
								Assert.assertEquals(1, matcher.groupCount());
								int snippetNumber = Integer.parseInt(matcher.group(1));
								if (snippetNumber > 9) {
									continue;
								}
								if (annotation.contains("columnNumber=\"1\"")) {
									Assert.assertTrue(annotation.contains("columnName=\"Patient ID\""));
									switch (snippetNumber) {
									case 1:
									case 2:
									case 3:
									case 4:
									case 5:
									case 6:
										Assert.assertEquals("p1", m.getTaggedText(vttDoc.getText())); break;
									case 7:
									case 8:
									case 9:
										Assert.assertEquals("p2", m.getTaggedText(vttDoc.getText())); break;
									default:
										throw new AssertionError("Unhandled snippet number: " + snippetNumber);
									}
								} else if (annotation.contains("columnNumber=\"2\"")) {
									Assert.assertTrue(annotation.contains("columnName=\"Document ID\""));
									switch (snippetNumber) {
									case 1:
									case 2:
										Assert.assertEquals("d1-CRLF", m.getTaggedText(vttDoc.getText())); break;
									case 3:
									case 4:
									case 5:
									case 6:
										Assert.assertEquals("d2-CRLF", m.getTaggedText(vttDoc.getText())); break;
									case 7:
									case 8:
									case 9:
										Assert.assertEquals("d1-LF", m.getTaggedText(vttDoc.getText())); break;
									default:
										throw new AssertionError("Unhandled snippet number: " + snippetNumber);
									}
								} else if (annotation.contains("columnNumber=\"3\"")) {
									Assert.assertTrue(annotation.contains("columnName=\"Snippet Number\""));
									switch (snippetNumber) {
									case 1:
									case 3:
									case 7:
										Assert.assertEquals("1", m.getTaggedText(vttDoc.getText())); break;
									case 2:
									case 4:
									case 8:
										Assert.assertEquals("2", m.getTaggedText(vttDoc.getText())); break;
									case 5:
									case 9:
										Assert.assertEquals("3", m.getTaggedText(vttDoc.getText())); break;
									case 6:
										Assert.assertEquals("4", m.getTaggedText(vttDoc.getText())); break;
									default:
										throw new AssertionError("Unhandled snippet number: " + snippetNumber);
									}
								} else if (annotation.contains("columnNumber=\"4\"")) {
									Assert.assertTrue(annotation.contains("columnName=\"Snippet Text\""));
									switch (snippetNumber) {
									case 1:
										Assert.assertEquals(
												"Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod"
												+ " tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,"
												+ " quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo"
												+ " consequat. Duis aute irure dolor", m.getTaggedText(vttDoc.getText()));
										break;
									case 2:
										Assert.assertEquals("nisi ut aliquip ex ea commodo"
												+ " consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse"
												+ " cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non"
												+ " proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", m.getTaggedText(vttDoc.getText()));
										break;
									case 3:
										Assert.assertEquals("Sed ut perspiciatis, unde omnis iste natus error sit voluptatem accusantium"
												+ " doloremque laudantium, totam rem aperiam eaque ipsa, quae ab illo inventore"
												+ " veritatis et quasi architecto beatae vitae dicta sunt, explicabo. Nemo enim"
												+ " ipsam voluptatem, quia voluptas sit, aspernatur aut", m.getTaggedText(vttDoc.getText()));
										break;
									case 4:
										Assert.assertEquals("odit aut fugit, sed quia"
												+ " consequuntur magni dolores eos, qui ratione voluptatem sequi nesciunt, neque"
												+ " porro quisquam est, qui dolorem ipsum, quia dolor sit amet consectetur"
												+ " adipiscing velit, sed quia non numquam do eius modi tempora incididunt, ut"
												+ " labore et", m.getTaggedText(vttDoc.getText()));
										break;
									case 5:
										Assert.assertEquals("dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam,"
												+ " quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut"
												+ " aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit, qui"
												+ " in ea voluptate velit esse, quam nihil molestiae consequatur", m.getTaggedText(vttDoc.getText()));
										break;
									case 6:
										Assert.assertEquals("vel illum, qui"
												+ " dolorem eum fugiat, quo voluptas nulla pariatur?", m.getTaggedText(vttDoc.getText()));
										break;
									case 7:
										Assert.assertEquals("At vero eos et accusamus et iusto odio dignissimos ducimus, qui blanditiis"
												+ " praesentium voluptatum deleniti atque corrupti, quos dolores et quas molestias"
												+ " excepturi sint, obcaecati cupiditate non provident, similique sunt in culpa,"
												+ " qui officia deserunt mollitia animi, id est laborum", m.getTaggedText(vttDoc.getText()));
										break;
									case 8:
										Assert.assertEquals("et dolorum fuga. Et harum"
												+ " quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum"
												+ " soluta nobis est eligendi optio, cumque nihil impedit, quo minus id, quod"
												+ " maxime placeat, facere possimus, omnis voluptas assumenda est, omnis dolor"
												+ " repellendus. Temporibus", m.getTaggedText(vttDoc.getText()));
										break;
									case 9:
										Assert.assertEquals("autem quibusdam et aut officiis debitis aut rerum"
												+ " necessitatibus saepe eveniet, ut et voluptates repudiandae sint et molestiae"
												+ " non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut"
												+ " reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus"
												+ " asperiores repellat", m.getTaggedText(vttDoc.getText()));
										break;
									default:
										throw new AssertionError("Unhandled snippet number: " + snippetNumber);
									}
								} else {
									System.out.println("Could not recognize column number in annotation: " + annotation);
								}
							}
						}
					} finally {
						WindowEvent ev = new WindowEvent(vttObj.getMainFrame(), WindowEvent.WINDOW_CLOSING);
						Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(ev);
					}
					return Integer.valueOf(numMarkupsProcessed);
				}
			};
			sw.execute();
			Integer numProcessed = sw.get();
			Assert.assertNotNull(numProcessed);
			Assert.assertTrue(numProcessed.intValue() > 10);
		} catch (HeadlessException e) {
			System.out.println("Warning: Failed to run test of ReadExcel.read() because there is no GUI");
		} catch (URISyntaxException | ExecutionException | InterruptedException e) {
			throw new AssertionError(e);
		}
	}

}
