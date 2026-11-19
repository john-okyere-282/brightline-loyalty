package com.brightline.loyalty;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.parallel.Resources.SYSTEM_ERR;
import static org.junit.jupiter.api.parallel.Resources.SYSTEM_OUT;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

class DemoTest {
  @Test
  @ResourceLock(SYSTEM_OUT)
  @ResourceLock(SYSTEM_ERR)
  void printsAwardsAndRejectionsToTheirRespectiveStreams() {
    var awards = new ByteArrayOutputStream();
    var rejections = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    PrintStream originalErr = System.err;
    try (var out = new PrintStream(awards, true, UTF_8);
        var err = new PrintStream(rejections, true, UTF_8)) {
      System.setOut(out);
      System.setErr(err);
      Demo.main(new String[0]);
    } finally {
      System.setOut(originalOut);
      System.setErr(originalErr);
    }

    assertAll(
        () -> assertEquals(List.of(
            "STANDARD, GHS 100 -> 10",
            "GOLD, GHS 250 -> 50",
            "SILVER, GHS 200 -> 30",
            "STANDARD, GHS 500 -> 50",
            "STANDARD, GHS 500.01 -> 100",
            "GOLD, GHS 600 -> 170"), awards.toString(UTF_8).lines().toList()),
        () -> assertEquals(List.of(
            "Unknown member rejected: Member was not found.",
            "Invalid purchase rejected: Purchase amount cannot be negative."),
            rejections.toString(UTF_8).lines().toList()));
  }
}
