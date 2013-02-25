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
package com.sonar.sslr.impl.events;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.StatelessMatcher;
import org.sonar.sslr.internal.vm.CompilationHandler;
import org.sonar.sslr.internal.vm.Instruction;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;

public class DelayMatcher extends StatelessMatcher {

  private final long delay;

  public static Matcher delay(long delay, Object... matchers) {
    if (matchers.length == 0) {
      throw new IllegalArgumentException("At least one matcher must be given.");
    }

    return new DelayMatcher(delay, and(matchers));
  }

  public DelayMatcher(long delay, Matcher matcher) {
    super(matcher);

    this.delay = delay;
  }

  @Override
  public AstNode matchWorker(ParsingState parsingState) {
    long startTime = getCpuTime();
    while (getCpuTime() < startTime + delay * 1000000L) {
      ;
    }

    return super.children[0].match(parsingState);
  }

  private static long getCpuTime() {
    ThreadMXBean bean = ManagementFactory.getThreadMXBean();
    return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;
  }

  public Instruction[] compile(CompilationHandler compiler) {
    throw new UnsupportedOperationException();
  }

}
