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
package org.sonar.sslr.internal.ast.query;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Iterator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChildrenOperationTest {

  @Test
  public void should_be_empty_on_empty_input() {
    assertThat(new ChildrenAstQuery().execute(mock(Iterator.class))).isEmpty();
  }

  @Test
  public void should_return_all_children() {
    AstNode child1 = mock(AstNode.class);
    AstNode child2 = mock(AstNode.class);

    AstNode input = mock(AstNode.class);
    when(input.getChildren()).thenReturn(Lists.newArrayList(child1, child2));

    assertThat(new ChildrenAstQuery().execute(new SingletonIterator(input))).containsOnly(child1, child2);
  }

  @Test
  public void should_return_all_children_with_parent() {
    AstNode child1 = mock(AstNode.class);
    AstNode input1 = mock(AstNode.class);
    when(input1.getChildren()).thenReturn(Lists.newArrayList(child1));

    AstNode child2 = mock(AstNode.class);
    AstNode input2 = mock(AstNode.class);
    when(input2.getChildren()).thenReturn(Lists.newArrayList(child2));

    AstQuery parent = mock(AstQuery.class);
    when(parent.execute(Mockito.any(Iterator.class))).thenReturn(ImmutableList.of(input1, input2).iterator());

    assertThat(new ChildrenAstQuery(parent).execute(new SingletonIterator(mock(AstNode.class)))).containsOnly(child1, child2);
  }

  @Test
  public void should_skip_empty_iterators() {
    AstNode input1 = when(mock(AstNode.class).getChildren()).thenReturn(Collections.EMPTY_LIST).getMock();
    AstNode input2 = when(mock(AstNode.class).getChildren()).thenReturn(Collections.EMPTY_LIST).getMock();

    AstNode child3 = mock(AstNode.class);
    AstNode input3 = mock(AstNode.class);
    when(input3.getChildren()).thenReturn(Lists.newArrayList(child3));

    AstNode input4 = when(mock(AstNode.class).getChildren()).thenReturn(Collections.EMPTY_LIST).getMock();

    AstNode child5 = mock(AstNode.class);
    AstNode input5 = mock(AstNode.class);
    when(input5.getChildren()).thenReturn(Lists.newArrayList(child5));

    AstNode input6 = when(mock(AstNode.class).getChildren()).thenReturn(Collections.EMPTY_LIST).getMock();

    AstQuery parent = mock(AstQuery.class);
    when(parent.execute(Mockito.any(Iterator.class))).thenReturn(ImmutableList.of(input1, input2, input3, input4, input5, input6).iterator());

    assertThat(new ChildrenAstQuery(parent).execute(new SingletonIterator(mock(AstNode.class)))).containsOnly(child3, child5);
  }

}
