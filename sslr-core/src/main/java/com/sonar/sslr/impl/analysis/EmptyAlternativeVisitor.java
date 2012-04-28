/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
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
