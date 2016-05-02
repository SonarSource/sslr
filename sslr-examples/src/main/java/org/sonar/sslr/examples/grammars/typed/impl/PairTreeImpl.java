package org.sonar.sslr.examples.grammars.typed.impl;

import org.sonar.sslr.examples.grammars.typed.api.LiteralTree;
import org.sonar.sslr.examples.grammars.typed.api.PairTree;
import org.sonar.sslr.examples.grammars.typed.api.SyntaxToken;
import org.sonar.sslr.examples.grammars.typed.api.ValueTree;

public class PairTreeImpl implements PairTree {

  private LiteralTree name;
  private SyntaxToken colonToken;
  private ValueTree value;

  public PairTreeImpl(LiteralTree name, SyntaxToken colonToken, ValueTree value) {
    this.name = name;
    this.colonToken = colonToken;
    this.value = value;
  }

  @Override
  public LiteralTree name() {
    return name;
  }

  @Override
  public SyntaxToken colonToken() {
    return colonToken;
  }

  @Override
  public ValueTree value() {
    return value;
  }
}
