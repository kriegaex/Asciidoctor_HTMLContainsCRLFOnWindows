package dev.aspectj;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AsciidoctorDemoTest {
  private final String INPUT_FLE_NAME = "src/test/resources/test.adoc";
  private final int MAX_FILE_SIZE = 4096;

  private static Asciidoctor asciidoctor;

  @BeforeClass
  public static void beforeClass() {
    asciidoctor = Asciidoctor.Factory.create();
  }

  @AfterClass
  public static void afterClass() {
    asciidoctor.close();
  }

  @Test
  public void testStringToStringConversion() throws IOException {
    try (Asciidoctor asciidoctor = Asciidoctor.Factory.create()) {
      byte[] buffer = new byte[MAX_FILE_SIZE];
      int length;
      try (FileInputStream inputStream = new FileInputStream(INPUT_FLE_NAME)) {
        length = inputStream.read(buffer);
      }
      String html = asciidoctor.convert(
        new String(buffer, 0, length),
        Options.builder().build()
      );
      checkHTML(html);
    }
  }

  @Test
  public void testFileToFileConversion() throws IOException {
    try (Asciidoctor asciidoctor = Asciidoctor.Factory.create()) {
      asciidoctor.convertFile(
        new File(INPUT_FLE_NAME),
        // TODO: Find a way to tell AsciidoctorJ to write UNIX files
        Options.builder().inPlace(true).build()
      );
      byte[] buffer = new byte[MAX_FILE_SIZE];
      int length;
      try (FileInputStream inputStream = new FileInputStream(INPUT_FLE_NAME.replace(".adoc", ".html"))) {
        length = inputStream.read(buffer);
      }
      String html = new String(buffer, 0, length);
      checkHTML(html);
    }
  }

  private void checkHTML(String html) {
    assertTrue("generated HTML must contain LF characters", html.contains("\n"));
    assertFalse("generated HTML must not contain CRLF sequences", html.contains("\r\n"));
  }
}
