/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.dsl;

import java.util.ArrayList;
import java.util.List;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.dsl.bytecode.Bytecode;
import com.sonar.sslr.dsl.internal.Compiler;
import com.sonar.sslr.dsl.internal.DefaultDslLexer;
import com.sonar.sslr.impl.Parser;

public class DslRunner {

  private Bytecode bytecode;
  private Compiler compiler;

  private DslRunner(Builder builder) {
    compiler = new Compiler(builder.parser, builder.source);
    for (Object component : builder.componentsToInject) {
      compiler.inject(component);
    }
    compile();
  }

  public static Builder builder(Grammar dsl, String source) {
    return new Builder(dsl, source);
  }

  private void compile() {
    bytecode = compiler.compile();
  }

  public void execute() {
    bytecode.execute();
  }

  public static class Builder {

    private Grammar grammar;
    private String source;
    private List<Object> componentsToInject = new ArrayList<Object>();
    private Parser<Grammar> parser;

    private Builder(Grammar grammar, String source) {
      this.grammar = grammar;
      this.source = source;
      this.parser = Parser.builder(grammar).optSetLexer(new DefaultDslLexer()).build();
    }

    public Builder inject(Object component) {
      componentsToInject.add(component);
      return this;
    }

    public Builder withParser(Parser<Grammar> parser) {
      this.parser = parser;
      return this;
    }

    public DslRunner build() {
      return new DslRunner(this);
    }

  }
}
