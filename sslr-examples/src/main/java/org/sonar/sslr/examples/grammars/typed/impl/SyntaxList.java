package org.sonar.sslr.examples.grammars.typed.impl;

import javax.annotation.Nullable;
import org.sonar.sslr.examples.grammars.typed.api.SyntaxToken;

public class SyntaxList<T> {

  private T element;
  private SyntaxToken commaToken;
  private SyntaxList<T> next;

  public SyntaxList(T element, @Nullable SyntaxToken commaToken, @Nullable SyntaxList<T> next) {
    this.element = element;
    this.commaToken = commaToken;
    this.next = next;
  }

  public T element() {
    return element;
  }

  @Nullable
  public SyntaxToken commaToken() {
    return commaToken;
  }

  @Nullable
  public SyntaxList next() {
    return next;
  }
}
