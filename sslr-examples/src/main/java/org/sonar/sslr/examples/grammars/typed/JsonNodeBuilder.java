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
package org.sonar.sslr.examples.grammars.typed;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.api.Trivia;
import com.sonar.sslr.api.typed.Input;
import com.sonar.sslr.api.typed.NodeBuilder;
import java.util.List;
import org.sonar.sslr.examples.grammars.typed.impl.InternalSyntaxToken;
import org.sonar.sslr.grammar.GrammarRuleKey;

public class JsonNodeBuilder implements NodeBuilder {

  /**
   * This methods is called for every rule defined in JsonLexer (i.e. TRUE, NUMBER, etc.).
   * Despite the fact that these rules are supposed to define tokens (as {@link JsonNodeBuilder#createTerminal} method does), this method is called due to whitespaces.
   * The whitespace token is dropped, and only the first one is returned.
   */
  @Override
  public Object createNonTerminal(GrammarRuleKey ruleKey, Rule rule, List<Object> children, int startIndex, int endIndex) {
    for (Object child : children) {
      if (child instanceof InternalSyntaxToken) {
        return child;
      }
    }

    throw new IllegalStateException();
  }

  @Override
  public Object createTerminal(Input input, int startIndex, int endIndex, List<Trivia> trivias, TokenType type) {
    String value = input.substring(startIndex, endIndex);
    return new InternalSyntaxToken(value);
  }

}
