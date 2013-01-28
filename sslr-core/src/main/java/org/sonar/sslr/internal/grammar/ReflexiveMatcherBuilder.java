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
package org.sonar.sslr.internal.grammar;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.sonar.sslr.grammar.Grammar;

import java.lang.reflect.Constructor;

public class ReflexiveMatcherBuilder implements MatcherBuilder {

  private final Class<?> matcherClass;
  private final Object[] arguments;

  public ReflexiveMatcherBuilder(Class<?> matcherClass, Object[] params) {
    this.matcherClass = matcherClass;
    this.arguments = params;
  }

  public Object build(Grammar g) {
    try {
      Constructor[] constructors = matcherClass.getConstructors();
      Preconditions.checkState(constructors.length == 1, "The matcher class has " + constructors.length + " constructors, but 1 expected: " + matcherClass.getName());
      Constructor constructor = constructors[0];

      if (isLexerlessArrayOfMatchers(constructor)) {
        Object[] actualArguments = getActualArguments(g, arguments);
        org.sonar.sslr.internal.matchers.Matcher[] actualMatcherArguments = new org.sonar.sslr.internal.matchers.Matcher[actualArguments.length];
        System.arraycopy(actualArguments, 0, actualMatcherArguments, 0, actualMatcherArguments.length);
        Object actualMatcherArgument = actualMatcherArguments;

        return constructor.newInstance(actualMatcherArgument);
      } else if (isLexerfulArrayOfMatchers(constructor)) {
        Object[] actualArguments = getActualArguments(g, arguments);
        com.sonar.sslr.impl.matcher.Matcher[] actualMatcherArguments = new com.sonar.sslr.impl.matcher.Matcher[actualArguments.length];
        System.arraycopy(actualArguments, 0, actualMatcherArguments, 0, actualMatcherArguments.length);
        Object actualMatcherArgument = actualMatcherArguments;

        return constructor.newInstance(actualMatcherArgument);
      } else {
        return constructor.newInstance(getActualArguments(g, arguments));
      }
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  private boolean isLexerlessArrayOfMatchers(Constructor constructor) {
    Class[] parameters = constructor.getParameterTypes();
    return parameters.length == 1 && parameters[0].equals(org.sonar.sslr.internal.matchers.Matcher[].class);
  }

  private boolean isLexerfulArrayOfMatchers(Constructor constructor) {
    Class[] parameters = constructor.getParameterTypes();
    return parameters.length == 1 && parameters[0].equals(com.sonar.sslr.impl.matcher.Matcher[].class);
  }

  private Object[] getActualArguments(Grammar g, Object[] arguments) {
    Object[] actualArguments = new Object[arguments.length];
    for (int i = 0; i < actualArguments.length; i++) {
      actualArguments[i] = getActualArgument(g, arguments[i]);
    }
    return actualArguments;
  }

  public Object getActualArgument(Grammar g, Object argument) {
    if (argument instanceof MatcherBuilder) {
      Object o = ((MatcherBuilder) argument).build(g);
      return o instanceof RuleDefinition ? ((RuleDefinition) o).getRule() : o;
    } else {
      return argument;
    }
  }

}
