package bot

import java.io.*

/**
 * my logger, if u want to use something else heck off
 *
 * messages sent to the logger will be output to the log file in the working directory. Each line will be prefixed with
 * a logger level followed by a space and a colon, for example
 *
 *
 * `VERB : verbose debug message`
 *
 * `INFO : info debug message`
 *
 * `WARN : warning debug message`
 *
 * `DEBUG : remove these calls when you are done with them please`
 *
 * `ERROR : you don't want to see this`
 *
 * errors will also be printed to standard error.
 *
 * I highly recommend using grep when trying to read the logs
 *
 * @author Varas#5480
 */
object Logger : FileWriter(File("log")) {

	/**
	 * logs a message with the level of verbose
	 * @param message message sent to the log file
	 */
	fun verbose(message : String) {
		writeWithLinePrefix("VERB : ", message)
	}

	/**
	 * logs a message with the level of info
	 * @param message message sent to the log file
	 */
	fun info(message : String) {
		writeWithLinePrefix("INFO : ", message)
	}

	/**
	 * logs a message with the level of warning
	 * @param message message sent to the log file
	 */
	fun warn(message : String) {
		writeWithLinePrefix("WARN : ", message)
	}

	/**
	 * Logs a message with the level of debug and prints it to standard out. Please clean up these calls once you are
	 * done with them.
	 * @param message message sent to the log file and standard out
	 */
	fun debug(message: String) {
		writeWithLinePrefix("DEBUG : ", message)
		printWithLinePrefixToStream("DEBUG : ", message, System.out)
	}

	/**
	 * logs a message with the level of error and prints it to standard error
	 * @param message message sent to the log file and standard error
	 */
	fun error(message : String) {
		writeWithLinePrefix("ERROR : ", message)
		printWithLinePrefixToStream("ERROR : ", message, System.err)
	}

	/**
	 * prints out an error to the logger with the level of error, including the stack trace
	 * @param exception exception that has been thrown
	 */
	fun logError(exception : Throwable) {
		if (exception.localizedMessage != null) {
			error(exception.localizedMessage)
		}
		exception.printStackTrace(PrintWriter(this))
	}

	override fun write(str: String, off: Int, len: Int) {
		error(str.substring(off, off + len))
	}

	private fun writeWithLinePrefix(prefix : String, message : String) {
		val lines = message.split(Regex("([${System.lineSeparator()}])"))
		for (line in lines) {
			if (line.isNotEmpty()) {
				val totalLine = "${prefix}${line}${System.lineSeparator()}"
				super.write(totalLine, 0, totalLine.length)
			}
		}
		flush()
	}

	private fun printWithLinePrefixToStream(prefix: String, message: String, stream : PrintStream) {
		val lines = message.split(Regex("([${System.lineSeparator()}])"))
		for (line in lines) {
			if (line.isNotEmpty()) {
				stream.println("${prefix}${line}")
			}
		}
	}
}