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
package org.sonar.sslr.internal.ast.select;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SingleAstSelectTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private AstNode node;
  private SingleAstSelect select;

  @Before
  public void init() {
    node = mock(AstNode.class);
    select = new SingleAstSelect(node);
  }

  @Test
  public void test_children_when_no_children() {
    assertThat((Object) select.children()).isSameAs(AstSelectFactory.empty());
    assertThat((Object) select.children(mock(AstNodeType.class))).isSameAs(AstSelectFactory.empty());
    assertThat((Object) select.children(mock(AstNodeType.class), mock(AstNodeType.class))).isSameAs(AstSelectFactory.empty());
  }

  @Test
  public void test_children_when_one_child() {
    AstNodeType type1 = mock(AstNodeType.class);
    AstNodeType type2 = mock(AstNodeType.class);
    AstNode child = mock(AstNode.class);
    when(node.getNumberOfChildren()).thenReturn(1);

    when(node.getFirstChild()).thenReturn(child);
    AstSelect children = select.children();
    assertThat((Object) children).isInstanceOf(SingleAstSelect.class);
    assertThat(children).containsOnly(child);

    when(node.getChildren()).thenReturn(ImmutableList.of(child));

    children = select.children(type1);
    assertThat((Object) children).isSameAs(AstSelectFactory.empty());

    when(child.getType()).thenReturn(type1);
    children = select.children(type1);
    assertThat((Object) children).isInstanceOf(SingleAstSelect.class);
    assertThat(children).containsOnly(child);

    children = select.children(type1, type2);
    assertThat((Object) children).isSameAs(AstSelectFactory.empty());

    when(child.is(type1, type2)).thenReturn(true);
    children = select.children(type1, type2);
    assertThat((Object) children).isInstanceOf(SingleAstSelect.class);
    assertThat(children).containsOnly(child);
  }

  @Test
  public void test_chilren_when_more_than_one_child() {
    AstNodeType type1 = mock(AstNodeType.class);
    AstNodeType type2 = mock(AstNodeType.class);
    AstNode child1 = mock(AstNode.class);
    AstNode child2 = mock(AstNode.class);
    when(node.getNumberOfChildren()).thenReturn(2);
    when(node.getChildren()).thenReturn(ImmutableList.of(child1, child2));

    AstSelect children = select.children();
    assertThat((Object) children).isInstanceOf(ListAstSelect.class);
    assertThat(children).containsOnly(child1, child2);

    children = select.children(type1);
    assertThat((Object) children).isSameAs(AstSelectFactory.empty());

    when(child1.getType()).thenReturn(type1);
    children = select.children(type1);
    assertThat((Object) children).isInstanceOf(SingleAstSelect.class);
    assertThat(children).containsOnly(child1);

    when(child2.getType()).thenReturn(type1);
    children = select.children(type1);
    assertThat((Object) children).isInstanceOf(ListAstSelect.class);
    assertThat(children).containsOnly(child1, child2);

    children = select.children(type1, type2);
    assertThat((Object) children).isSameAs(AstSelectFactory.empty());

    when(child1.is(type1, type2)).thenReturn(true);
    children = select.children(type1, type2);
    assertThat((Object) children).isInstanceOf(SingleAstSelect.class);
    assertThat(children).containsOnly(child1);

    when(child2.is(type1, type2)).thenReturn(true);
    children = select.children(type1, type2);
    assertThat((Object) children).isInstanceOf(ListAstSelect.class);
    assertThat(children).containsOnly(child1, child2);
  }

  @Test
  public void test_descendants() {
    assertThat((Object) select.descendants(mock(AstNodeType.class))).isSameAs(AstSelectFactory.empty());
    assertThat((Object) select.descendants(mock(AstNodeType.class), mock(AstNodeType.class))).isSameAs(AstSelectFactory.empty());
  }

  @Test
  public void test_isEmpty() {
    assertThat(select.isEmpty()).isFalse();
  }

  @Test
  public void test_isNotEmpty() {
    assertThat(select.isNotEmpty()).isTrue();
  }

  @Test
  public void test_filter() {
    Predicate<AstNode> predicate = mock(Predicate.class);
    assertThat((Object) select.filter(predicate)).isSameAs(AstSelectFactory.empty());
    when(predicate.apply(node)).thenReturn(true);
    assertThat((Object) select.filter(predicate)).isSameAs(select);
  }

  @Test
  public void test_get0() {
    assertThat(select.get(0)).isSameAs(node);
  }

  @Test
  public void test_get_non_existing() {
    thrown.expect(IndexOutOfBoundsException.class);
    select.get(1);
  }

  @Test
  public void test_size() {
    assertThat(select.size()).isEqualTo(1);
  }

  @Test
  public void test_iterator() {
    assertThat(select.iterator()).containsOnly(node);
  }

}
