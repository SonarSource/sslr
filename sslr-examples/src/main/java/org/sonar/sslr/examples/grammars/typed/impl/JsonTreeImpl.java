package org.sonar.sslr.examples.grammars.typed.impl;

import org.sonar.sslr.examples.grammars.typed.Tree;
import org.sonar.sslr.examples.grammars.typed.api.JsonTree;

public class JsonTreeImpl implements JsonTree {

  private Tree arrayOrObject;

  public JsonTreeImpl(Tree arrayOrObject) {
    this.arrayOrObject = arrayOrObject;
  }

  @Override
  public Tree arrayOrObject() {
    return arrayOrObject;
  }
}
