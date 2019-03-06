package paralaks_gmail_com.simpleorm.helper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogHelper {
  public static Logger getLogSingleLineOutput(String name) {
    Logger logger = Logger.getLogger(name);
    logger.setUseParentHandlers(false);
    SingleLineLogFormatter formatter = new SingleLineLogFormatter();
    ConsoleHandler handler = new ConsoleHandler();
    handler.setFormatter(formatter);
    logger.addHandler(handler);

    return logger;
  }

  private static final class SingleLineLogFormatter extends Formatter {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    public String format(LogRecord record) {
      StringBuilder sb = new StringBuilder(100);

      sb.append(new Date(record.getMillis())).append(" ").append(record.getLevel().getLocalizedName())
          .append(" |").append(record.getLoggerName()).append("| ").append(formatMessage(record))
          .append(LINE_SEPARATOR);

      if (record.getThrown() != null) {
        try {
          StringWriter sw = new StringWriter();
          PrintWriter pw = new PrintWriter(sw);
          record.getThrown().printStackTrace(pw);
          pw.close();
          sb.append(sw.toString());
        } catch (Exception ex) {
          // ignore
        }
      }

      return sb.toString();
    }
  }
}
