package com.sonar.sslr.impl.matcher;

/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.ParsingStackTrace;
import com.sonar.sslr.impl.ParsingState;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.List;

import static com.sonar.sslr.test.lexer.TokenUtils.*;

public class HamcrestMatchMatcher extends BaseMatcher<Matcher> {

  private final List<Token> tokens;
  private String parsingStackTrace;
  private Matcher matcher;

  public static HamcrestMatchMatcher match(List<Token> tokens) {
    return new HamcrestMatchMatcher(tokens);
  }

  public static HamcrestMatchMatcher match(String sourceCode) {
    return new HamcrestMatchMatcher(lex(sourceCode));
  }

  public HamcrestMatchMatcher(List<Token> tokens) {
    this.tokens = tokens;
  }

  public boolean matches(Object obj) {
    if (!(obj instanceof Matcher)) {
      return false;
    }
    matcher = (Matcher) obj;
    ParsingState parsingState = new ParsingState(tokens);
    try {
      matcher.match(parsingState);
      if (parsingState.hasNextToken()) {
        return false;
      }
      return true;
    } catch (BacktrackingEvent e) {
      parsingStackTrace = ParsingStackTrace.generate(parsingState);
      return false;
    }
  }

  public void describeTo(Description desc) {
    if (parsingStackTrace != null) {
      desc.appendText("The matcher '" + matcher + "' doesn't match the beginning of '" + merge(tokens) + "'.\n");
      desc.appendText("Parsing stack trace : " + parsingStackTrace);
    } else {
      desc.appendText("The matcher '" + matcher + "' hasn't matched the overall expression '" + merge(tokens) + "'.\n");
    }
  }
}
