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

/**
 * This is a very rudimentary logging system. It will store information based on certain log levels and will
 * print it based on certain print levels. The stream that it prints it to can be manually set. This was created to
 * ensure that logging information could be displayed to a GUI instead of just printed to console.
 * @author Calvin
 *
 */
public class Log {
	
	/**
	 * This is the actual log file the Log internally stores.
	 */
	private String logFile;

	/**
	 * This is the stream where information can be printed to.
	 */
	private PrintStream stream = System.out;
	
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
	
	/**
	 * Describes how much information the Log will store in its internal log. It should be set using the 
	 * constants the Log class contains.
	 */
	private int logLevel = Log.LEVEL_ERROR;
	
	/**
	 * Described how much information the Log will print to its printStream. It should be set using the constants
	 * the Log class contains.
	 */
	private int printLevel = Log.LEVEL_NONE;
	
	/**
	 * Creates a log with an empty log.
	 */
	public Log() {
		logFile = "";
	}

	/**
	 * This will add a String on a new line to the log file if the Log level is LEVEL_LOG.
	 * This will print a String to a new line using the print stream if the Log level is LEVEL_LOG
	 * @param s the String to be printed or added to the log
	 */
	public void newLine(String s) {
		if(logLevel >= Log.LEVEL_LOG) 
			logFile += "\n" + getTimeStamp() + s;
		if(printLevel >= Log.LEVEL_LOG && stream != null) 
			stream.println(getTimeStamp() + s);

	}
	
	/**
	 * This will add a blank line to the log file if the Log level is LEVEL_LOG.
	 * This will print a blank line using the print stream if the Log level is LEVEL_LOG
	 */
	public void blankLine() {
		if(logLevel >= Log.LEVEL_LOG) 
			logFile += "\n";
		if(printLevel >= Log.LEVEL_LOG && stream != null)
			stream.println();
	}
	
	/**
	 * This will add the printStream of the error passed this method to the log if the Log level is at least LEVEL_ERROR. LEVEL_LOG will work as well.
	 * This will print the printStream of the error passed this method to the printStream if the Log level is at least LEVEL_ERROR. LEVEL_LOG will work as well.
	 * @param e the error to be printed or added to the log
	 */
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
	
	/**
	 * @return the log file.
	 */
	public String getLog() {
		return logFile;
	}
	
	private String getTimeStamp() {
		return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + ": ";
	}
	
	/**
	 * Can be used to set an alternative print stream for the log to print to, such as a Socket
	 * @param stream the new stream for the log to print to
	 */
	public void setPrintStream(PrintStream stream) {
		this.stream = stream;
	}
	
	/**
	 * There are three possible log levels. Each is accessed through the constants the Log class contains. LEVEL_NONE is the lowest log level
	 * and will add nothing to the log. LEVEL_ERROR is a medium log level that adds only errors to the log. LEVEL_LOG adds everything to the log.
	 * @param level the new log level
	 */
	public void setLogLevel(int level) {
		logLevel = level;
	}
	
	/**
	 * @return the log level of this Log
	 */
	public int getLogLevel() {
		return logLevel;
	}
	
	/**
	 * There are three possible print levels. Each is accessed through the constants the Log class contains. LEVEL_NONE is the lowest print level
	 * and will print nothing to the printStream. LEVEL_ERROR is a medium print level that print only errors to the printStream. LEVEL_LOG prints 
	 * everything to the printStream.
	 * @param level the print level of this log
	 */
	public void setPrintLevel(int level) {
		printLevel = level;
	}
	
	/**
	 * @return the print level of this log
	 */
	public int getPrintLevel() {
		return printLevel;
	}

}
