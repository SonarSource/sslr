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
package org.sonar.sslr.grammar;

import com.sonar.sslr.api.Rule;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GrammarAdapterTest {

  @Test
  public void should_return_rule() {
    Rule rule = mock(Rule.class);
    GrammarRule grammarRule = mock(GrammarRule.class);
    Grammar grammar = mock(Grammar.class);
    when(grammar.rule(grammarRule)).thenReturn(rule);

    assertThat(new GrammarAdapter(grammar, mock(GrammarRule.class)).rule(grammarRule)).isSameAs(rule);
  }

  @Test
  public void should_return_root_rule() {
    Rule rule = mock(Rule.class);
    GrammarRule grammarRule = mock(GrammarRule.class);
    Grammar grammar = mock(Grammar.class);
    when(grammar.rule(grammarRule)).thenReturn(rule);

    assertThat(new GrammarAdapter(grammar, grammarRule).getRootRule()).isSameAs(rule);
  }

}
