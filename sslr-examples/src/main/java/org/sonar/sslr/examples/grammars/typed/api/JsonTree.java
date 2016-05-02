package org.sonar.sslr.examples.grammars.typed.api;

import org.sonar.sslr.examples.grammars.typed.Tree;

public interface JsonTree extends Tree {

  Tree arrayOrObject();

}
