/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import java.util.List;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.xpath.api.AstNodeXPathQuery;

public abstract class AbstractXPathCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> {

  // See SONAR-3164
  public abstract String getXPathQuery();

  // See SONAR-3164
  public abstract String getMessage();

  @Override
  public void visitFile(AstNode fileNode) {
    AstNodeXPathQuery<Object> query = AstNodeXPathQuery.create(getXPathQuery());
    List<Object> objects = query.getValues(fileNode);

    for (Object object : objects) {
      if (object instanceof AstNode) {
        AstNode astNode = (AstNode) object;
        getContext().log(this, getMessage(), astNode.getTokenLine());
      } else if (object instanceof Boolean) {
        if ((Boolean) object) {
          getContext().log(this, getMessage(), -1);
        }
      }
    }
  }

}
