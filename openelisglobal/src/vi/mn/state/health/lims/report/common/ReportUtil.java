/**
 * 
 */
package vi.mn.state.health.lims.report.common;

import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;

import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;

/**
 * @author nhuql.gv
 * Common function for processing the reports
 */
public class ReportUtil 
{
	/**
	 * Convert string to SQL date by local of system
	 * @param recievedDate
	 * @return
	 */
	public static Calendar convertToSqlDateByLocal(String recievedDate) {
		String localeName = SystemConfiguration.getInstance().getDefaultLocale().toString();
		Locale locale = new Locale(localeName);
		Calendar calendar = Calendar.getInstance(locale);

		Date date = DateUtil.convertStringDateToSqlDate(recievedDate, localeName);
		calendar.setTime(date);
		return calendar;
	}
	
	/**
	 * Convert to SQL date
	 * might be missed comparing to midnight (00:00:00.00) on the last day of range.
	 * @param inDateTo
	 * @return
	 */
	public static Calendar GetExactDate(String inDateTo){
        Calendar cal  = convertToSqlDateByLocal(inDateTo);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal;
	}
}
