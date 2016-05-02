package org.sonar.sslr.examples.grammars.typed.api;

import javax.annotation.Nullable;
import org.sonar.sslr.examples.grammars.typed.impl.SyntaxList;

public interface ArrayTree extends ValueTree {

  SyntaxToken openBracketToken();

  @Nullable
  SyntaxList<ValueTree> values();

  SyntaxToken closeBracketToken();

}
