package util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	
	private String logFile;
	public static final Log log = new Log();
	private PrintStream stream = null;
	
	/**
	 * prints nothing
	 */
	public static final int LEVEL_NONE = -1;
	
	/**
	 * prints only error messages
	 */
	public static final int LEVEL_ERROR = 0;
	
	/**
	 * prints error messages and log messages
	 */
	public static final int LEVEL_LOG = 1;
	
	private int logLevel = Log.LEVEL_ERROR;
	private int printLevel = Log.LEVEL_NONE;
	
	/**
	 * singleton design to that even when a user switched from client to server, it has the same
	 * log. An effect of this is that, when multiple instances of this program are run on the same
	 * computer, they add to the same log.
	 */
	private Log() {
		logFile = "";
	}
	
	public void newLine(String s) {
		if(logLevel >= Log.LEVEL_LOG) 
			logFile += "\n" + getTimeStamp() + s;
		if(printLevel >= Log.LEVEL_LOG && stream != null) 
			stream.println(getTimeStamp() + s);

	}
	
	public void blankLine() {
		if(logLevel >= Log.LEVEL_LOG) 
			logFile += "\n";
		if(printLevel >= Log.LEVEL_LOG && stream != null)
			stream.println();
	}
	
	public void addError(Exception e) {
		if(logLevel >= Log.LEVEL_ERROR) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw, true);
			e.printStackTrace(pw);
			logFile += "\n";
			logFile += getTimeStamp() + sw.getBuffer().toString();
			logFile += "\n";
		}
		if(printLevel >= Log.LEVEL_ERROR) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw, true);
			e.printStackTrace(pw);
			stream.println();
			stream.print(getTimeStamp() + sw.getBuffer().toString());
			stream.println();
		}

	}
	
	public String getLog() {
		return logFile;
	}
	
	private String getTimeStamp() {
		return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + ": ";
	}
	
	public void setPrintStream(PrintStream stream) {
		this.stream = stream;
	}
	
	public void setLogLevel(int level) {
		logLevel = level;
	}
	
	public int getLogLevel(int level) {
		return logLevel;
	}
	
	public void setPrintLevel(int level) {
		printLevel = level;
	}
	
	public int getPrintLevel() {
		return printLevel;
	}

}
