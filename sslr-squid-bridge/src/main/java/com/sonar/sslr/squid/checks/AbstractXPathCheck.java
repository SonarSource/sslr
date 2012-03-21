/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import java.util.List;

import org.sonar.api.utils.SonarException;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.xpath.api.AstNodeXPathQuery;

public abstract class AbstractXPathCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> {

  // See SONAR-3164
  public abstract String getXPathQuery();

  // See SONAR-3164
  public abstract String getMessage();

  private AstNodeXPathQuery<Object> query = null;

  @Override
  public void init() {
    String xpath = getXPathQuery();

    if ( !"".equals(xpath)) {
      try {
        query = AstNodeXPathQuery.create(getXPathQuery());
      } catch (RuntimeException e) {
        throw new SonarException("[AbstractXPathCheck] Unable to initialize the XPath engine, perhaps because of an invalid query ("
            + xpath
            + " given).", e);
      }
    }
  }

  @Override
  public void visitFile(AstNode fileNode) {
    if (query != null) {
      List<Object> objects = query.selectNodes(fileNode);

      for (Object object : objects) {
        if (object instanceof AstNode) {
          AstNode astNode = (AstNode) object;
          getContext().createLineViolation(this, getMessage(), astNode.getTokenLine());
        } else if (object instanceof Boolean) {
          if ((Boolean) object) {
            getContext().createFileViolation(this, getMessage());
          }
        }
      }
    }
  }

}
