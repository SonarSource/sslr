/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.sonar.sslr.test.lexer.MockHelper.mockToken;
import static org.fest.assertions.Assertions.assertThat;

public class RuleDefinitionTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testEmptyIs() {
    RuleDefinition javaClassDefinition = new RuleDefinition("JavaClassDefinition");
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("The rule 'JavaClassDefinition' should at least contains one matcher.");
    javaClassDefinition.is();
  }

  @Test
  public void testMoreThanOneDefinitionForASigleRuleWithIs() {
    RuleDefinition javaClassDefinition = new RuleDefinition("JavaClassDefinition");
    javaClassDefinition.is("option1");
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("The rule 'JavaClassDefinition' has already been defined somewhere in the grammar.");
    javaClassDefinition.is("option2");
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
