/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2021 SonarSource SA
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
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import org.junit.Test;

import static com.sonar.sslr.test.lexer.MockHelper.mockToken;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class RuleDefinitionTest {

  @Test
  public void testEmptyIs() {
    RuleDefinition javaClassDefinition = new RuleDefinition("JavaClassDefinition");
    IllegalStateException thrown = assertThrows(IllegalStateException.class,
      javaClassDefinition::is);
    assertEquals("The rule 'JavaClassDefinition' should at least contains one matcher.", thrown.getMessage());
  }

  @Test
  public void testMoreThanOneDefinitionForASigleRuleWithIs() {
    RuleDefinition javaClassDefinition = new RuleDefinition("JavaClassDefinition");
    javaClassDefinition.is("option1");
    IllegalStateException thrown = assertThrows(IllegalStateException.class,
      () -> javaClassDefinition.is("option2"));
    assertEquals("The rule 'JavaClassDefinition' has already been defined somewhere in the grammar.", thrown.getMessage());
  }

  @Test
  public void testSkipFromAst() {
    RuleDefinition ruleBuilder = new RuleDefinition("MyRule");
    assertThat(ruleBuilder.hasToBeSkippedFromAst(null)).isFalse();

    ruleBuilder.skip();
    assertThat(ruleBuilder.hasToBeSkippedFromAst(null)).isTrue();
  }

  @Test
  public void testSkipFromAstIf() {
    RuleDefinition ruleBuilder = new RuleDefinition("MyRule");
    ruleBuilder.skipIfOneChild();

    AstNode parent = new AstNode(mockToken(GenericTokenType.IDENTIFIER, "parent"));
    AstNode child1 = new AstNode(mockToken(GenericTokenType.IDENTIFIER, "child1"));
    AstNode child2 = new AstNode(mockToken(GenericTokenType.IDENTIFIER, "child2"));
    parent.addChild(child1);
    parent.addChild(child2);
    child1.addChild(child2);

    assertThat(ruleBuilder.hasToBeSkippedFromAst(parent)).isFalse();
    assertThat(ruleBuilder.hasToBeSkippedFromAst(child2)).isFalse();
    assertThat(ruleBuilder.hasToBeSkippedFromAst(child1)).isTrue();
  }
}
