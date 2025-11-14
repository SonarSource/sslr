/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.sslr.internal.ast;

import com.sonar.sslr.api.AstNode;
import org.junit.Test;
import org.sonar.sslr.internal.ast.select.AstSelectFactory;
import org.sonar.sslr.internal.ast.select.EmptyAstSelect;
import org.sonar.sslr.internal.ast.select.ListAstSelect;
import org.sonar.sslr.internal.ast.select.SingleAstSelect;

import java.util.Arrays;
import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AstSelectFactoryTest {

  @Test
  public void test_select() {
    assertThat((Object) AstSelectFactory.select(null)).isInstanceOf(EmptyAstSelect.class);
    assertThat((Object) AstSelectFactory.select(mock(AstNode.class))).isInstanceOf(SingleAstSelect.class);
  }

  @Test
  public void test_create() {
    AstNode node1 = mock(AstNode.class);
    AstNode node2 = mock(AstNode.class);
    assertThat((Object) AstSelectFactory.create(Collections.emptyList())).isSameAs(AstSelectFactory.empty());
    assertThat((Object) AstSelectFactory.create(Arrays.asList(node1))).isInstanceOf(SingleAstSelect.class);
    assertThat((Object) AstSelectFactory.create(Arrays.asList(node1, node2))).isInstanceOf(ListAstSelect.class);
  }

  @Test
  public void test_empty() {
    assertThat((Object) AstSelectFactory.empty()).isInstanceOf(EmptyAstSelect.class);
    assertThat((Object) AstSelectFactory.empty()).isSameAs(AstSelectFactory.empty());
  }

}
