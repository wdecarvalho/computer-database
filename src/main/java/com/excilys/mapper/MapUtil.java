package main.java.com.excilys.mapper;

import java.sql.Timestamp;
import java.time.LocalDate;

public abstract class MapUtil {
	
	/**
	 * Convertit un Timestamp en une LocalDate
	 * @param time Timestamp a convertir
	 * @return LocalDate
	 */
	public static LocalDate convertTimeStampToLocal(final Timestamp time) {
		if(time != null) {
			return time.toLocalDateTime().toLocalDate();
		}
		return null;
		
	}
	
	/**
	 * Convertit une LocalDate en un Timestamp
	 * @param date LocalDate a convertir
	 * @return TimeStamp
	 */
	public static Timestamp convertLocalDateToTimeStamp(final LocalDate date) {
		if(date != null) {
			return Timestamp.valueOf(date.atStartOfDay());
		}
		return null;
		
	}

}
