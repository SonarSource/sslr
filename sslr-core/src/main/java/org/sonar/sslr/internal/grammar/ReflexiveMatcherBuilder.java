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
import org.sonar.sslr.grammar.Grammar;
import org.sonar.sslr.internal.matchers.Matcher;

import java.lang.reflect.Constructor;

public class ReflexiveMatcherBuilder implements MatcherBuilder {

  private final Class<? extends Matcher> matcherClass;
  private final Object[] arguments;

  public ReflexiveMatcherBuilder(Class<? extends Matcher> matcherClass, Object[] params) {
    this.matcherClass = matcherClass;
    this.arguments = params;
  }

  public Matcher build(Grammar g) {
    try {
      Constructor[] constructors = matcherClass.getConstructors();
      Preconditions.checkState(constructors.length == 1, "The matcher class has " + constructors.length + " constructors, but 1 expected: " + matcherClass.getName());
      Constructor constructor = constructors[0];

      if (hasOneArrayOfMatcherParameter(constructor)) {
        Object[] actualArguments = getActualArguments(g, arguments);
        Matcher[] actualMatcherArguments = new Matcher[actualArguments.length];
        System.arraycopy(actualArguments, 0, actualMatcherArguments, 0, actualMatcherArguments.length);
        Object actualMatcherArgument = actualMatcherArguments;

        return (Matcher) constructor.newInstance(actualMatcherArgument);
      } else {
        return (Matcher) constructor.newInstance(getActualArguments(g, arguments));
      }
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  private boolean hasOneArrayOfMatcherParameter(Constructor constructor) {
    Class[] parameters = constructor.getParameterTypes();

    return parameters.length == 1 && parameters[0].equals(Matcher[].class);
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
      return ((MatcherBuilder) argument).build(g);
    } else {
      return argument;
    }
  }

}
