package org.sonar.sslr.examples.grammars.typed.impl;

import javax.annotation.Nullable;
import org.sonar.sslr.examples.grammars.typed.api.ArrayTree;
import org.sonar.sslr.examples.grammars.typed.api.SyntaxToken;
import org.sonar.sslr.examples.grammars.typed.api.ValueTree;

public class ArrayTreeImpl implements ArrayTree {

  private SyntaxToken openBracketToken;
  private SyntaxList<ValueTree> values;
  private SyntaxToken closeBracketToken;

  public ArrayTreeImpl(SyntaxToken openBracketToken, @Nullable SyntaxList<ValueTree> values, SyntaxToken closeBracketToken) {
    this.openBracketToken = openBracketToken;
    this.values = values;
    this.closeBracketToken = closeBracketToken;
  }

  @Override
  public SyntaxToken openBracketToken() {
    return openBracketToken;
  }

  @Nullable
  @Override
  public SyntaxList<ValueTree> values() {
    return values;
  }

  @Override
  public SyntaxToken closeBracketToken() {
    return closeBracketToken;
  }
}
