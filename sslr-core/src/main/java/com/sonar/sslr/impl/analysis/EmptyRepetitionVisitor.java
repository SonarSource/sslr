/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.google.common.collect.Sets;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.OneToNMatcher;

import java.util.Collections;
import java.util.Set;

import static com.sonar.sslr.impl.analysis.EmptyVisitor.*;

public class EmptyRepetitionVisitor extends RuleMatchersVisitor {

  private final Set<OneToNMatcher> emptyRepetitions = Sets.newHashSet();

  @Override
  public void process(OneToNMatcher matcher) {
    Matcher child = matcher.children[0];

    if (empty(child)) {
      emptyRepetitions.add(matcher);
    }
  }

  public Set<OneToNMatcher> getEmptyRepetitions() {
    return Collections.unmodifiableSet(emptyRepetitions);
  }

}
