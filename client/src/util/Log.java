package util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	
	private String logFile;
	public static final Log log = new Log();
	
	private Log() {
		logFile = "";
	}
	
	public void newLine(String s) {
		logFile.concat("\n" + getTimeStamp() + s);
	}
	
	public void addError(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);
		logFile += getTimeStamp() + sw.getBuffer().toString();
	}
	
	public String getLog() {
		return logFile;
	}
	
	private String getTimeStamp() {
		return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + ": ";
	}

}
