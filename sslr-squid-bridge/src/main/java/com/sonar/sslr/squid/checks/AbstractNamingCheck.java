/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import org.sonar.api.utils.SonarException;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.*;

public abstract class AbstractNamingCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> {

  private Pattern pattern;

  public abstract Rule[] getRules();

  public abstract String getName(AstNode astNode);

  public abstract String getRegexp();

  public abstract String getMessage(String name);

  public abstract boolean isExcluded(AstNode astNode);

  @Override
  public void init() {
    Rule[] rules = getRules();
    checkNotNull(rules, "getRules() must not return null");
    checkArgument(rules.length > 0, "getRules() must return at least one rule");

    subscribeTo(getRules());

    String regexp = getRegexp();
    checkNotNull(regexp, "getRegexp() must not return null");

    try {
      this.pattern = Pattern.compile(regexp);
    } catch (Exception e) {
      throw new SonarException("[" + this.getClass().getSimpleName() + "] Unable to compile the regular expression \"" + regexp + "\"", e);
    }
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (!isExcluded(astNode)) {
      String name = getName(astNode);
      checkNotNull(name, "getName() must not return null");

      if (!pattern.matcher(name).matches()) {
        getContext().createLineViolation(this, getMessage(name), astNode);
      }
    }
  }

}
