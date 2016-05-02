package org.sonar.sslr.examples.grammars.typed.api;

import org.sonar.sslr.examples.grammars.typed.Tree;

public interface SyntaxToken extends Tree {

  String value();
}
