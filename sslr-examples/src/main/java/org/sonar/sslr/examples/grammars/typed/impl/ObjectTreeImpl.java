package org.sonar.sslr.examples.grammars.typed.impl;

import javax.annotation.Nullable;
import org.sonar.sslr.examples.grammars.typed.api.ObjectTree;
import org.sonar.sslr.examples.grammars.typed.api.PairTree;
import org.sonar.sslr.examples.grammars.typed.api.SyntaxToken;

public class ObjectTreeImpl implements ObjectTree {

  private SyntaxToken openCurlyBraceToken;
  private SyntaxList<PairTree> pairs;
  private SyntaxToken closeCurlyBraceToken;

  public ObjectTreeImpl(SyntaxToken openCurlyBraceToken, SyntaxList<PairTree> pairs, SyntaxToken closeCurlyBraceToken) {
    this.openCurlyBraceToken = openCurlyBraceToken;
    this.pairs = pairs;
    this.closeCurlyBraceToken = closeCurlyBraceToken;
  }

  @Override
  public SyntaxToken openCurlyBraceToken() {
    return openCurlyBraceToken;
  }

  @Nullable
  @Override
  public SyntaxList<PairTree> pairs() {
    return pairs;
  }

  @Override
  public SyntaxToken closeCurlyBraceToken() {
    return closeCurlyBraceToken;
  }
}
