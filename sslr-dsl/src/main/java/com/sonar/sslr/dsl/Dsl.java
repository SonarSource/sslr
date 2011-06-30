/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.dsl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.dsl.bytecode.Bytecode;
import com.sonar.sslr.dsl.internal.Compiler;
import com.sonar.sslr.dsl.internal.DefaultDslLexer;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.Parser;

import static com.sonar.sslr.api.GenericTokenType.EOF;

public class Dsl {

  private Bytecode bytecode;
  private Compiler compiler;
  private DslContext context;

  private Dsl(Builder builder) {
    if (builder.source != null) {
      compiler = Compiler.create(builder.getParser(), builder.source);
    } else {
      compiler = Compiler.create(builder.getParser(), builder.sourceFile);
    }
    for (Object component : builder.componentsToInject) {
      compiler.inject(component);
    }
    this.context = builder.context;
    compiler.inject(context);
    bytecode = compiler.compile();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Dsl create(Grammar grammar, String source) {
    return new Builder().withSource(source).setGrammar(grammar).compile();
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
    private File sourceFile;
    private List<Object> componentsToInject = new ArrayList<Object>();
    private DslContext context = new DslContext();
    private Lexer lexer;

    private Builder() {
    }

    public Builder setGrammar(Grammar grammar) {
      this.grammar = grammar;
      grammar.getRootRule().and(EOF);
      return this;
    }

    public Builder withSource(String source) {
      this.source = source;
      return this;
    }

    public Builder withSource(File sourceFile) {
      this.sourceFile = sourceFile;
      return this;
    }

    public Builder withLexer(Lexer lexer) {
      this.lexer = lexer;
      return this;
    }

    public Builder inject(Object component) {
      componentsToInject.add(component);
      return this;
    }

    public Builder put(String variableName, Object value) {
      context.put(variableName, value);
      return this;
    }

    private Parser<Grammar> getParser() {
      if (lexer == null) {
        lexer = DefaultDslLexer.create();
      }
      return Parser.builder(grammar).withLexer(lexer).build();
    }

    public Dsl compile() {
      return new Dsl(this);
    }

  }
}
