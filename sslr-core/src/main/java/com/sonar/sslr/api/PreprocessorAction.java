/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * This class encapsulates the actions to be performed by a preprocessor.
 * @deprecated in 1.20, use your own preprocessor API instead.
 */
@Deprecated
public class PreprocessorAction {

  /**
   * <p>
   * Use this no operation preprocessor action for improved readability and performances.
   * </p>
   *
   * <p>
   * Equivalent to: <tt>new PreprocessorAction(0, new ArrayList&lt;Trivia&gt;(), new ArrayList&lt;Token&gt;());</tt>
   * </p>
   */
  public static final PreprocessorAction NO_OPERATION = new PreprocessorAction(0, new ArrayList<>(), new ArrayList<>());

  private final int numberOfConsumedTokens;
  private final List<Trivia> triviaToInject;
  private final List<Token> tokensToInject;

  /**
   * <p>
   * Construct a preprocessor action.
   * </p>
   *
   * <p>
   * The actions are executed in this order:
   * <ol>
   * <li>Deletions of tokens, handled by numberOfConsumedTokens</li>
   * <li>Injections of trivia, handled by triviaToInject</li>
   * <li>Injections of tokens, handled by tokensToInject</li>
   * </ol>
   * </p>
   *
   * <p>
   * Preprocessor actions are executed as follows:
   * <ol>
   * <li>If numberOfConsumedTokens is greater than 0, then this number of tokens are deleted. Their trivia is added to a pending list of
   * trivia. The preprocessor will not be called on deleted tokens.</li>
   * <li>All trivia from triviaToInject are added to the same pending list of trivia</li>
   * <li>All tokens from tokensToInject are injected. If present, the first token of tokensToInject is augmented with the pending trivia,
   * which is then cleared. If not present, the pending trivia is left unchanged.</li>
   * <li>Finally, if numberOfConsumedTokens was 0, the current token is injected, with any pending trivia which is then cleared.</li>
   * </ol>
   * </p>
   *
   * A few examples:
   * <ul>
   * <li>No operation action: <tt>new PreprocessorAction(0, new ArrayList&lt;Trivia&gt;(), new ArrayList&lt;Token&gt;());</tt></li>
   * <li>Delete current token action: <tt>new PreprocessorAction(1, new ArrayList&lt;Trivia&gt;(), new ArrayList&lt;Token&gt;());</tt></li>
   * <li>Modify current token action: <tt>new PreprocessorAction(1, new ArrayList&lt;Trivia&gt;(), Arrays.asList(modifiedToken));</tt></li>
   * <li>Inject trivia to current token action: <tt>new PreprocessorAction(0, Arrays.asList(newTrivia), new ArrayList&lt;Token&gt;());</tt></li>
   * </ul>
   *
   * @param numberOfConsumedTokens
   *          Number of tokens consumed by the preprocessor, which can be 0. Consumed tokens are deleted and will not lead to successive
   *          calls to the preprocessor.
   * @param triviaToInject
   *          Trivia to inject.
   * @param tokensToInject
   *          Tokens to inject. Injected tokens will not lead to successive calls to the preprocessor.
   */
  public PreprocessorAction(int numberOfConsumedTokens, List<Trivia> triviaToInject, List<Token> tokensToInject) {
    if (numberOfConsumedTokens < 0) {
      throw new IllegalArgumentException("numberOfConsumedTokens(" + numberOfConsumedTokens + ") must be greater or equal to 0");
    }
    Objects.requireNonNull(triviaToInject, "triviaToInject cannot be null");
    Objects.requireNonNull(tokensToInject, "tokensToInject cannot be null");

    this.numberOfConsumedTokens = numberOfConsumedTokens;
    this.triviaToInject = Collections.unmodifiableList(new ArrayList<>(triviaToInject));
    this.tokensToInject = Collections.unmodifiableList(new ArrayList<>(tokensToInject));
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
