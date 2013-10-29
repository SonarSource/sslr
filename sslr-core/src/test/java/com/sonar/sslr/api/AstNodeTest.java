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
package com.sonar.sslr.api;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AstNodeTest {

  /**
   * <pre>
   *   A1
   *   |- A2
   *   |   \- B1
   *   |- B2
   *   |- B3
   *   \- C1
   * </pre>
   */
  @Test
  public void test() {
    AstNodeType a = mock(AstNodeType.class);
    AstNodeType b = mock(AstNodeType.class);
    AstNodeType c = mock(AstNodeType.class);
    AstNode a1 = new AstNode(a, "a1", null);
    AstNode a2 = new AstNode(a, "a2", null);
    AstNode b1 = new AstNode(b, "b1", null);
    AstNode b2 = new AstNode(b, "b2", null);
    AstNode b3 = new AstNode(b, "b3", null);
    AstNode c1 = new AstNode(c, "c1", null);
    a1.addChild(a2);
    a1.addChild(b2);
    a1.addChild(b3);
    a1.addChild(c1);
    a2.addChild(b1);

    assertThat(a1.hasChildren()).isTrue();
    assertThat(c1.hasChildren()).isFalse();

    assertThat(a1.getFirstChild()).isSameAs(a2);

    assertThat(a1.getLastChild()).isSameAs(c1);

    assertThat(a1.hasDirectChildren(mock(AstNodeType.class))).isFalse();
    assertThat(a1.hasDirectChildren(a)).isTrue();
    assertThat(a1.hasDirectChildren(a, b)).isTrue();

    assertThat(a1.getFirstChild(mock(AstNodeType.class))).isNull();
    assertThat(a1.getFirstChild(a)).isSameAs(a2);
    assertThat(a1.getFirstChild(b)).isSameAs(b2);
    assertThat(a1.getFirstChild(a, b)).isSameAs(a2);

    assertThat(a1.getChildren(mock(AstNodeType.class))).isEmpty();
    assertThat(a1.getChildren(a)).containsExactly(a2);
    assertThat(a1.getChildren(b)).containsExactly(b2, b3);
    assertThat(a1.getChildren(a, b)).containsExactly(a2, b2, b3);

    assertThat(a1.hasDescendant(mock(AstNodeType.class))).isFalse();
    assertThat(a1.hasDescendant(a)).isTrue();
    assertThat(a1.hasDescendant(a, b)).isTrue();

    assertThat(a1.getFirstDescendant(mock(AstNodeType.class))).isNull();
    assertThat(a1.getFirstDescendant(a)).isSameAs(a2);
    assertThat(a1.getFirstDescendant(b)).isSameAs(b1);
    assertThat(a1.getFirstDescendant(a, b)).isSameAs(a2);

    assertThat(a1.getDescendants(mock(AstNodeType.class))).isEmpty();
    assertThat(a1.getDescendants(a)).containsExactly(a2);
    assertThat(a1.getDescendants(b)).containsExactly(b1, b2, b3);
    assertThat(a1.getDescendants(a, b)).containsExactly(a2, b1, b2, b3);

    assertThat(a1.getNextSibling()).isNull();
    assertThat(c1.getNextSibling()).isNull();
    assertThat(b3.getNextSibling()).isSameAs(c1);

    assertThat(a1.getPreviousSibling()).isNull();
    assertThat(a2.getPreviousSibling()).isNull();
    assertThat(b2.getPreviousSibling()).isSameAs(a2);

    assertThat(a1.getNextAstNode()).isNull();
    assertThat(b1.getNextAstNode()).isSameAs(b2);

    assertThat(a1.getPreviousAstNode()).isNull();
    assertThat(b2.getPreviousAstNode()).isSameAs(a2);

    assertThat(b1.hasAncestor(mock(AstNodeType.class))).isFalse();
    assertThat(b1.hasAncestor(a)).isTrue();
    assertThat(b1.hasAncestor(b)).isFalse();
    assertThat(b1.hasAncestor(a, b)).isTrue();

    assertThat(b1.getFirstAncestor(a)).isSameAs(a2);
    assertThat(b1.getFirstAncestor(b)).isNull();
    assertThat(b1.getFirstAncestor(a, b)).isSameAs(a2);
  }

}
