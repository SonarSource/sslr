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
package org.sonar.sslr.parser;

import com.sonar.sslr.api.Rule;
import org.sonar.sslr.internal.vm.CompilableGrammarRule;
import org.sonar.sslr.internal.vm.CompiledGrammar;
import org.sonar.sslr.internal.vm.Machine;
import org.sonar.sslr.internal.vm.MutableGrammarCompiler;

import java.util.Objects;

/**
 * Performs parsing of a given grammar rule on a given input text.
 *
 * <p>This class is not intended to be subclassed by clients.</p>
 *
 * @since 1.16
 */
public class ParseRunner {

  private final CompiledGrammar compiledGrammar;

  public ParseRunner(Rule rule) {
    compiledGrammar = MutableGrammarCompiler.compile((CompilableGrammarRule) Objects.requireNonNull(rule, "rule"));
  }

  public ParsingResult parse(char[] input) {
    return Machine.parse(input, compiledGrammar);
  }

}
