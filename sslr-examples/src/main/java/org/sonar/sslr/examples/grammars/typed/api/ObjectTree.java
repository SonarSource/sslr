package org.sonar.sslr.examples.grammars.typed.api;

import javax.annotation.Nullable;
import org.sonar.sslr.examples.grammars.typed.impl.SyntaxList;

public interface ObjectTree extends ValueTree {

  SyntaxToken openCurlyBraceToken();

  @Nullable
  SyntaxList<PairTree> pairs();

  SyntaxToken closeCurlyBraceToken();
}
