package gov.nih.nlm.nls.vtt.libs;
import java.util.*;
import java.text.*;
/*****************************************************************************
* This class utilize GregorianCalendar class to get the tiem from computer.
*
* <p><b>History:</b>
* <ul>
* <li>SCR-98, chlu, 02/01/10, add time stamp to VTT file
* </ul>
*
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class DateObj
{
	public static String getTime()
	{
		GregorianCalendar cal = new GregorianCalendar();
		Date date = cal.getTime();
		return toDayTimeString(date);
	}

	private static String toDayTimeString(Date date)
    {
        DateFormat dateTime = DateFormat.getDateTimeInstance(
            DateFormat.SHORT, DateFormat.MEDIUM);
        return dateTime.format(date);
    }
}
