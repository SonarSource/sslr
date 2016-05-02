package org.sonar.sslr.examples.grammars.typed.api;

import org.sonar.sslr.examples.grammars.typed.Tree;

public interface PairTree extends Tree {

  LiteralTree name();

  SyntaxToken colonToken();

  ValueTree value();

}
