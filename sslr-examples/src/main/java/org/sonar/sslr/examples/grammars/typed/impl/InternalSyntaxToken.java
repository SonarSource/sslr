package org.sonar.sslr.examples.grammars.typed.impl;

import org.sonar.sslr.examples.grammars.typed.api.SyntaxToken;

public class InternalSyntaxToken implements SyntaxToken {

  private String value;

  public InternalSyntaxToken(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
