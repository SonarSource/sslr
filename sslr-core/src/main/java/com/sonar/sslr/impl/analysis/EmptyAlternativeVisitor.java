/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.google.common.collect.Sets;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.OrMatcher;

import java.util.Collections;
import java.util.Set;

import static com.sonar.sslr.impl.analysis.EmptyVisitor.*;

public class EmptyAlternativeVisitor extends RuleMatchersVisitor {

  private final Set<EmptyAlternative> emptyAlternatives = Sets.newHashSet();

  @Override
  public void process(OrMatcher matcher) {
    for (Matcher child : matcher.children) {
      if (empty(child)) {
        emptyAlternatives.add(new EmptyAlternative(matcher, child));
      }
    }
  }

  public Set<EmptyAlternative> getEmptyAlternatives() {
    return Collections.unmodifiableSet(emptyAlternatives);
  }

}
