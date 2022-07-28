package gov.nih.nlm.nls.vtt.util;

import gov.nih.nlm.nls.vtt.model.Markup;
import gov.nih.nlm.nls.vtt.model.Markups;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WriteExcel {
	
public String InputFile;
public File OutputFile;
public Markups markups;
public String result = "";
File targetFile = null;
private static final Pattern SNIPPET_TEXT_PATTERN = Pattern.compile("columnName=\\\"Snippet\\s?Text\\\"");

public void setInputFile(String InputFile, Markups markups){
	this.InputFile = InputFile;
	this.markups = markups;
}

public void setOutputFile(File OutputFile){
	this.OutputFile = OutputFile;
}

public File write () throws Exception {
	
	XSSFWorkbook wb = new XSSFWorkbook();
    XSSFSheet ws = wb.createSheet();   
    int outputRow = 0;
    int numberOfColumns = 1;
    boolean foundColumns = false;
	for (int i=0; i<markups.getSize(); i++){
		Markup m = markups.getMarkup(i);
		
		if (!m.getTagName().equals("SnippetColumn")) {
			Markup snippetMarkup = getSnippetForOffset(m.getOffset());
			if (snippetMarkup!=null) 
			{
				
				if (!foundColumns) {
					foundColumns = true;
					// Print out a header row
					XSSFRow row = ws.createRow(outputRow);
					XSSFCell cell;
					boolean notDone = true;
					while (notDone) {
						Markup markup = getColMarkupForSnippetNumber(1, numberOfColumns);
						if (markup!=null) {
							cell = row.createCell(numberOfColumns-1);
							String columnText = getAnnotationFieldValue(markup.getAnnotation(), "columnName");
							cell.setCellValue(columnText);		
							numberOfColumns++;
						} else {
							notDone = false;
						}
					}
					numberOfColumns--;
					cell = row.createCell(numberOfColumns);
					cell.setCellValue("Annotation Text");		
					cell = row.createCell(numberOfColumns+1);
					cell.setCellValue("Annotation");							
				}
	
				outputRow++;
				XSSFRow row = ws.createRow(outputRow);
				for (int columnNumber = 1; (columnNumber<=numberOfColumns); columnNumber++) {
					XSSFCell cell = row.createCell(columnNumber-1);
					String cellValue = getColStringForSnippetNumber(getSnippetNumberForOffset(m.getOffset()), 
							columnNumber, InputFile);
					cell.setCellValue(cellValue);
					
				}
				XSSFCell cell = row.createCell(numberOfColumns);
				cell.setCellValue(m.getTaggedText(InputFile));
				cell = row.createCell(numberOfColumns+1);
				cell.setCellValue(m.getTagName());
			} else 
			{
				System.out.println("Error: Could not find snippet for offset: "+m.getOffset());
			}
		}		
	}
    FileOutputStream fos = null;
    try {
        fos = new FileOutputStream(OutputFile);
       // fos.write(result.getBytes());
        wb.write(fos);
    } catch (IOException e) {
        e.printStackTrace();
        throw e;
    } finally {
        if (fos != null) {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
     
    return OutputFile;
  }

public Markup getColMarkupForSnippetNumber(int snippetNumber, int columnNumber){
	Markup result = null;
	for (int i=0; i<markups.getSize(); i++){
		Markup m = markups.getMarkup(i);
		if(m.getAnnotation().contains("snippetNumber=\""+snippetNumber+"\"") 
				&& m.getAnnotation().contains("columnNumber=\""+columnNumber+"\"")) {
			result = m;
			break;
		}
	}
	
	return result;
}

public String getColStringForSnippetNumber(int snippetNumber, int columnNumber, String InputFile){
	Markup m = getColMarkupForSnippetNumber(snippetNumber, columnNumber);
	String result = "";
	if (m!=null) {
		result =  InputFile.substring(m.getOffset(), m.getOffset()+m.getLength()).replace("\n", "");
	}
	return result;
}

public Markup getSnippetForOffset(int offset){
	Markup result = null;
	for (int i=0; i<markups.getSize(); i++){
		Markup m = markups.getMarkup(i);
		if(SNIPPET_TEXT_PATTERN.matcher(m.getAnnotation()).find()) {
			if (offset>=m.getOffset() && (offset<(m.getOffset()+m.getLength())))
			{
				result = m;
				break;				
			}
		}
	}
	
	return result;
}

public int getSnippetNumberForOffset(int offset){
	int snippetNumber = -1;
	Markup snippet = null;
	snippet = getSnippetForOffset(offset);
	if (snippet!=null) {
		try {
			snippetNumber = Integer.parseInt(getAnnotationFieldValue(snippet.getAnnotation(), "snippetNumber"));
			} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}	
	}
	return snippetNumber;
}

public String getAnnotationFieldValue(String annotation, String field) {
	String fieldValue = "Unknown";
	int beginIndex = annotation.indexOf(field+"=\"")+field.length()+2;
	int endIndex = -1;
	if (beginIndex>-1) {
		endIndex = beginIndex + annotation.substring(beginIndex).indexOf("\"");
		if (endIndex>-1) {
			fieldValue = annotation.substring(
					beginIndex, endIndex);
		}
	}
	return fieldValue;
}

public static String cellToString (XSSFCell cell){

int type;
Object result = null;
String returnVal = null;
type = cell.getCellType();

    switch(type) {
    case 0://numeric value in excel
        result = cell.getNumericCellValue();
        break;
    case 1: //string value in excel
        result = cell.getStringCellValue();
        break;
    default:
    	 System.out.println("There are not support for this type of cell");
        }

    if ( result != null )
      returnVal = result.toString();
return returnVal;
}

public static void main(String[] args) throws Exception {
	
}

}
