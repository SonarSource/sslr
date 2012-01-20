/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.metrics;

import static com.sonar.sslr.squid.metrics.ResourceParser.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.sonar.squid.api.SourceFile;

import com.sonar.sslr.test.miniC.MiniCAstScanner.MiniCMetrics;

public class CommentsVisitorTest {

  @Test
  public void empty() {
    SourceFile sourceFile = scanFile("/metrics/comments_none.mc");

    assertThat(sourceFile.getInt(MiniCMetrics.BLANK_COMMENT_LINES), is(0));
    assertThat(sourceFile.getInt(MiniCMetrics.COMMENT_LINES), is(0));

    assertThat(sourceFile.getNoSonarTagLines().size(), is(0));
  }

  @Test
  public void comments() {
    SourceFile sourceFile = scanFile("/metrics/comments.mc");

    assertThat(sourceFile.getInt(MiniCMetrics.BLANK_COMMENT_LINES), is(3));
    assertThat(sourceFile.getInt(MiniCMetrics.COMMENT_LINES), is(3));

    assertThat(sourceFile.getNoSonarTagLines().size(), is(2));
    assertThat(5, isIn(sourceFile.getNoSonarTagLines()));
    assertThat(6, isIn(sourceFile.getNoSonarTagLines()));
  }

  @Test
  public void headerComments() {
    SourceFile sourceFile = scanFileIgnoreHeaderComments("/metrics/header_comments.mc");

    assertThat(sourceFile.getInt(MiniCMetrics.BLANK_COMMENT_LINES), is(1));
    assertThat(sourceFile.getInt(MiniCMetrics.COMMENT_LINES), is(1));
    assertThat(sourceFile.getNoSonarTagLines().size(), is(0));
  }

}
