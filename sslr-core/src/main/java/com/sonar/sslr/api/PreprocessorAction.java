/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreprocessorAction {

  private final int numberOfConsumedTokens;
  private final List<Trivia> triviaToInject;
  private final List<Token> tokensToInject;

  public PreprocessorAction(int numberOfConsumedTokens, List<Trivia> triviaToInject, List<Token> tokensToInject) {
    this.numberOfConsumedTokens = numberOfConsumedTokens;
    this.triviaToInject = Collections.unmodifiableList(new ArrayList<Trivia>(triviaToInject));
    this.tokensToInject = Collections.unmodifiableList(new ArrayList<Token>(tokensToInject));
  }

  public int getNumberOfConsumedTokens() {
    return numberOfConsumedTokens;
  }

  public List<Trivia> getTriviaToInject() {
    return triviaToInject;
  }

  public List<Token> getTokensToInject() {
    return tokensToInject;
  }

}
