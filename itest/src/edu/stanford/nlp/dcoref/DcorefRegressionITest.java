package edu.stanford.nlp.dcoref;

import java.io.File;
import edu.stanford.nlp.io.IOUtils;
import java.util.Properties;

import junit.framework.TestCase;
import edu.stanford.nlp.util.StringUtils;

/**
 * Run the dcoref system using the exact properties we distribute as
 * an example.  Check that the output does not change from expected.
 *
 * @author John Bauer
 */
public class DcorefRegressionITest extends TestCase {
  public void testDcoref() throws Exception {
    final File WORK_DIR_FILE = File.createTempFile("DcorefITest", "");
    WORK_DIR_FILE.delete();
    WORK_DIR_FILE.mkdir();
    // TODO: delete the files left behind?

    String baseLogFile = WORK_DIR_FILE + File.separator + "log";

    System.err.println("Base log file name: " + WORK_DIR_FILE);

    String[] args = { "-props", "edu/stanford/nlp/dcoref/coref.properties",
                      "-" + Constants.LOG_PROP, baseLogFile,
                      "-" + Constants.CONLL_OUTPUT_PROP, WORK_DIR_FILE.toString() };
    Properties props = StringUtils.argsToProperties(args);

    String expectedResults = IOUtils.slurpFile("edu/stanford/nlp/dcoref/expected.txt");

    System.err.println("Running dcoref with properties:");
    System.err.println(props);

    String logFile = SieveCoreferenceSystem.initializeAndRunCoref(props);
    System.err.println(logFile);

    String actualResults = IOUtils.slurpFile(logFile);

    String[] expectedLines = expectedResults.trim().split("[\n\r]+");
    String[] actualLines = actualResults.trim().split("[\n\r]+");

    int line;
    String lastLine = expectedLines[expectedLines.length - 1];
    for (line = actualLines.length - 1; line >= 0; --line) {
      if (actualLines[line].equals(lastLine)) {
        break;
      }
    }
    assertTrue(line >= 0);
    for (int i = 0; i < expectedLines.length; ++i) {
      String expectedLine = expectedLines[expectedLines.length - 1 - i].trim().replaceAll("\\s+", " ");
      String actualLine = actualLines[line - i].trim().replaceAll("\\s+", " ");
      assertEquals(expectedLine, actualLine);
    }
    System.err.println(line);
  }
}
