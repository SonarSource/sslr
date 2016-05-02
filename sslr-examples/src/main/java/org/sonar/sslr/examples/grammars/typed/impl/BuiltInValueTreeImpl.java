package org.sonar.sslr.examples.grammars.typed.impl;

import org.sonar.sslr.examples.grammars.typed.api.BuiltInValueTree;
import org.sonar.sslr.examples.grammars.typed.api.SyntaxToken;

public class BuiltInValueTreeImpl implements BuiltInValueTree {

  private SyntaxToken token;

  public BuiltInValueTreeImpl(SyntaxToken token) {
    this.token = token;
  }

  @Override
  public SyntaxToken token() {
    return token;
  }
}
