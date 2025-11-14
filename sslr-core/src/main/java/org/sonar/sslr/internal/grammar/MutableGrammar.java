/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.sslr.internal.grammar;

import com.sonar.sslr.api.Rule;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.vm.CompilableGrammarRule;
import org.sonar.sslr.parser.LexerlessGrammar;

import java.util.Map;

public class MutableGrammar extends LexerlessGrammar {

  private final Map<GrammarRuleKey, ? extends CompilableGrammarRule> rules;
  private final GrammarRuleKey rootRuleKey;

  public MutableGrammar(Map<GrammarRuleKey, ? extends CompilableGrammarRule> rules, GrammarRuleKey rootRuleKey) {
    this.rules = rules;
    this.rootRuleKey = rootRuleKey;
  }

  @Override
  public Rule rule(GrammarRuleKey ruleKey) {
    return rules.get(ruleKey);
  }

  @Override
  public Rule getRootRule() {
    return rule(rootRuleKey);
  }

}
