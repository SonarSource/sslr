/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.sslr.internal.ast;

import com.google.common.base.Predicate;
import com.sonar.sslr.api.AstNodeType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.sslr.ast.AstSelect;
import org.sonar.sslr.internal.ast.select.EmptyAstSelect;

import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class EmptyAstSelectTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private AstSelect select = new EmptyAstSelect();

  @Test
  public void test_children() {
    assertThat((Object) select.children()).isSameAs(select);
    assertThat((Object) select.children(mock(AstNodeType.class))).isSameAs(select);
    assertThat((Object) select.children(mock(AstNodeType.class), mock(AstNodeType.class))).isSameAs(select);
  }

  @Test
  public void test_nextSibling() {
    assertThat((Object) select.nextSibling()).isSameAs(select);
  }

  @Test
  public void test_previousSibling() {
    assertThat((Object) select.previousSibling()).isSameAs(select);
  }

  @Test
  public void test_parent() {
    assertThat((Object) select.parent()).isSameAs(select);
  }

  @Test
  public void test_firstAncestor() {
    assertThat((Object) select.firstAncestor(mock(AstNodeType.class))).isSameAs(select);
    assertThat((Object) select.firstAncestor(mock(AstNodeType.class), mock(AstNodeType.class))).isSameAs(select);
  }

  @Test
  public void test_descendants() {
    assertThat((Object) select.descendants(mock(AstNodeType.class))).isSameAs(select);
    assertThat((Object) select.descendants(mock(AstNodeType.class), mock(AstNodeType.class))).isSameAs(select);
  }

  @Test
  public void test_isEmpty() {
    assertThat(select.isEmpty()).isTrue();
  }

  @Test
  public void test_isNotEmpty() {
    assertThat(select.isNotEmpty()).isFalse();
  }

  @Test
  public void test_filter() {
    assertThat((Object) select.filter(mock(AstNodeType.class))).isSameAs(select);
    assertThat((Object) select.filter(mock(AstNodeType.class), mock(AstNodeType.class))).isSameAs(select);
    assertThat((Object) select.filter(mock(Predicate.class))).isSameAs(select);
  }

  @Test
  public void test_get_non_existing() {
    thrown.expect(IndexOutOfBoundsException.class);
    select.get(0);
  }

  @Test
  public void test_size() {
    assertThat(select.size()).isEqualTo(0);
  }

  @Test
  public void test_iterator() {
    assertThat((Object) select.iterator()).isSameAs(Collections.emptyIterator());
  }

}
