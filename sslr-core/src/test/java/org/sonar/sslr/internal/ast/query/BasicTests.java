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
import com.sonar.sslr.api.AstNode;
import org.junit.Test;

import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BasicTests {

  @Test
  public void should_return_grand_children_of_root() {
    AstNode child1 = mock(AstNode.class);
    AstNode grandChild11 = mock(AstNode.class);
    AstNode grandChild12 = mock(AstNode.class);
    when(child1.getChildren()).thenReturn(ImmutableList.of(grandChild11, grandChild12));

    AstNode child2 = mock(AstNode.class);
    when(child2.getChildren()).thenReturn(Collections.EMPTY_LIST);

    AstNode child3 = mock(AstNode.class);
    AstNode grandChild31 = mock(AstNode.class);
    AstNode grandChild32 = mock(AstNode.class);
    AstNode grandChild33 = mock(AstNode.class);
    when(child3.getChildren()).thenReturn(ImmutableList.of(grandChild31, grandChild32, grandChild33));

    AstNode root = mock(AstNode.class);
    when(root.getChildren()).thenReturn(ImmutableList.of(child1, child2, child3));

    NonEmptyAstResultSet resultSet = new NonEmptyAstResultSet(root, new AstQuery().children().children());
    assertThat(resultSet.hasSize(5)).isEqualTo(true);
  }

}
