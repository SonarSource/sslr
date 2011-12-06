/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.xpath.jaxen;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

public class AstNodeXPath extends BaseXPath {

  public AstNodeXPath(String xpathExpr) throws JaxenException {
    super(xpathExpr, DocumentNavigator.getInstance());
  }
}
