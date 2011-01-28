/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.dsl;

import java.util.List;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.containers.TransientPicoContainer;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.dsl.internal.AstToStatementTransformer;
import com.sonar.sslr.dsl.internal.DefaultDslParser;
import com.sonar.sslr.dsl.internal.Reflexion;
import com.sonar.sslr.impl.Parser;

public class DslRunner {

  private Parser<Grammar> parser;
  private Grammar dsl;
  private MutablePicoContainer pico = new TransientPicoContainer();

  private DslRunner(Dsl dsl) {
    this.dsl = dsl;
  }

  public DslRunner addComponent(Object obj) {
    pico.addComponent(obj);
    return this;
  }

  public static DslRunner create(Dsl dsl) {
    return new DslRunner(dsl);
  }

  public void execute(String source) {
    parser = new DefaultDslParser(dsl);
    AstNode ast = parser.parse(source);
    List<Object> stmts = new AstToStatementTransformer(pico).transform(ast);
    for (Object stmt : stmts) {
      Reflexion.call(stmt, "execute");
    }
  }

}
