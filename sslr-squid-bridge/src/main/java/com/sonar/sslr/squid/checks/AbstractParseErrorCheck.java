/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.sonar.sslr.api.AuditListener;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.RecognitionException;

public abstract class AbstractParseErrorCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> implements AuditListener {

  public void processRecognitionException(RecognitionException e) {
    getContext().createLineViolation(this, e.getMessage(), e.getLine());
  }

  public void processException(Exception e) {
    StringWriter exception = new StringWriter();
    e.printStackTrace(new PrintWriter(exception));
    getContext().createLineViolation(this, exception.toString(), 1);
  }

}
