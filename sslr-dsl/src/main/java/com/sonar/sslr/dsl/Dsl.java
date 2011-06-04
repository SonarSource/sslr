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

public class Dsl {

  private Bytecode bytecode;
  private Compiler compiler;
  private DslContext context;

  private Dsl(Builder builder) {
    compiler = new Compiler(builder.parser, builder.source);
    for (Object component : builder.componentsToInject) {
      compiler.inject(component);
    }
    this.context = builder.context;
    compiler.inject(context);
    bytecode = compiler.compile();
  }

  public static Builder builder(Grammar dsl, String source) {
    return new Builder(dsl, source);
  }

  public void execute() {
    bytecode.execute();
  }

  public void put(String variableName, Object value) {
    context.put(variableName, value);
  }

  public Object get(String variableName) {
    return context.get(variableName);
  }

  public static class Builder {

    private Grammar grammar;
    private String source;
    private List<Object> componentsToInject = new ArrayList<Object>();
    private Parser<Grammar> parser;
    private DslContext context = new DslContext();

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

    public Builder put(String variableName, Object value) {
      context.put(variableName, value);
      return this;
    }

    public Dsl compile() {
      return new Dsl(this);
    }

  }
}
