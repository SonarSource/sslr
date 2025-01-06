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
package org.sonar.sslr.internal.vm;

import org.sonar.sslr.grammar.GrammarRuleKey;

public class RuleRefExpression extends NativeExpression {

  private final GrammarRuleKey ruleKey;

  public RuleRefExpression(GrammarRuleKey ruleKey) {
    this.ruleKey = ruleKey;
  }

  public GrammarRuleKey getRuleKey() {
    return ruleKey;
  }

  @Override
  public void execute(Machine machine) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return "Ref " + ruleKey;
  }

}
