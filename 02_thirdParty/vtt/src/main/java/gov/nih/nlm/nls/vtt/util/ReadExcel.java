package gov.nih.nlm.nls.vtt.util;

import gov.nih.nlm.nls.vtt.model.Markup;
import gov.nih.nlm.nls.vtt.model.Markups;
import gov.nih.nlm.nls.vtt.model.VttObj;

import java.io.*;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadExcel {

	public static final String SNIPPET_SEPARATOR = "\n----------------------------------------------------------------------------------\n";
	public static final String SNIPPET_COLUMN_TAGNAME = "SnippetColumn";
	public static final String SNIPPET_NUMBER_ATTRIBUTE = "snippetNumber";
	public static final String COLUMN_NUMBER_ATTRIBUTE = "columnNumber";
	public static final String COLUMN_NAME_ATTRIBUTE = "columnName";
	public static final String COLUMN_HEADER_SNIPPET_TEXT = "Snippet Text";

	public String InputFile;
	public String result = "";

	public void setInputFile(String InputFile) {
		this.InputFile = InputFile;
	}

	public FileContentsAndMarkups read() throws Exception {
		String[] headerRow = new String[4];
		OPCPackage pkg = OPCPackage.open(InputFile);
		XSSFWorkbook wb = new XSSFWorkbook(pkg);
		XSSFSheet ws = wb.getSheetAt(0);

		int rowNum = ws.getLastRowNum() + 1;
		int colNum = 4;
		String[][] data = new String[rowNum][colNum];
		String value = "";
		
		Markups snippetColumnMarkups = new Markups();
		for (int i = 0; i < rowNum; i++) {
			XSSFRow row = ws.getRow(i);
			if (row != null) {
				for (int j = 0; j < colNum; j++) {
					XSSFCell cell = row.getCell(j);
					value = cellToString(cell);
					data[i][j] = value;
					if (i == 0) {
						if (j < 4) {
							if (j == 3) {
								headerRow[j] = COLUMN_HEADER_SNIPPET_TEXT;
							} else {
								headerRow[j] = data[i][j];
							}
						}
					} else if (i != 0 && j <= colNum - 1) {
						String columnNameText = (j < 4 ? headerRow[j] : "")
								+ (j == 3 ? ": \n\n" : ": ");
						String cleanedDataValue = data[i][j] == null ? "" : (j < 4 ? data[i][j].replace("\r", "") : "");
						String annotation = SNIPPET_NUMBER_ATTRIBUTE + "=\"" + i + "\"<::>"
								+ COLUMN_NUMBER_ATTRIBUTE + "=\"" + (j + 1)
								+ "\"<::>" + COLUMN_NAME_ATTRIBUTE + "=\""
								+ (j < 4 ? headerRow[j] : "") + "\"";
						Markup m = new Markup(result.length()
								+ columnNameText.length(),
								cleanedDataValue.length(), SNIPPET_COLUMN_TAGNAME,
								"", annotation);
						snippetColumnMarkups.addMarkup(m);
						result = result + columnNameText + cleanedDataValue + "\n";
					}
	
				}
				if (i != 0 && i < rowNum - 1) {
					result = result + SNIPPET_SEPARATOR;
	
				}
			}
		}
		pkg.close();
//		// DEBUG
//		for (Markup mu : snippetColumnMarkups.getMarkups()) {
//			System.out.println(mu.toString());
//		}
//		// END DEBUG
		return new FileContentsAndMarkups(result, snippetColumnMarkups);
	}

	public static String cellToString(XSSFCell cell) throws IOException {
		if (cell == null) {
			return null;
		}

		int type;
		Object result = null;
		type = cell.getCellType();

		switch (type) {
		case 0:// numeric value in excel
			result = cell.getRawValue();
			break;
		case 1: // string value in excel
			result = cell.getStringCellValue();
			break;
		case 2: // formula, use formula result
			result = cell.getRawValue();
			break;
		default:
			throw new IOException("Unsupported cell type: " + type);
		}

		return result.toString();
	}

	public static void main(String[] args) throws Exception {

	}
	
	public class FileContentsAndMarkups {
		private String fileContents;
		private Markups markups;
		/**
		 * @param fileContents
		 * @param markups
		 */
		public FileContentsAndMarkups(String fileContents, Markups markups) {
			super();
			this.fileContents = fileContents;
			this.markups = markups;
		}
		public String getFileContents() {
			return fileContents;
		}
		public void setFileContents(String fileContents) {
			this.fileContents = fileContents;
		}
		public Markups getMarkups() {
			return markups;
		}
		public void setMarkups(Markups markups) {
			this.markups = markups;
		}
		
	}

}