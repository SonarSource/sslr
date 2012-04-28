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
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import org.junit.Test;

import static com.sonar.sslr.test.lexer.MockHelper.mockToken;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RuleDefinitionTest {

  @Test(expected = IllegalStateException.class)
  public void testEmptyIs() {
    RuleDefinition javaClassDefinition = RuleDefinition.newRuleBuilder("JavaClassDefinition");
    javaClassDefinition.is();
  }

  @Test(expected = IllegalStateException.class)
  public void testMoreThanOneDefinitionForASigleRuleWithIs() {
    RuleDefinition javaClassDefinition = RuleDefinition.newRuleBuilder("JavaClassDefinition");
    javaClassDefinition.is("option1");
    javaClassDefinition.is("option2");
  }

  @Test
  public void testIs() {
    RuleDefinition myRule = RuleDefinition.newRuleBuilder("MyRule");
    myRule.is("option1");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(\"option1\")"));
  }

  @Test
  public void testOverride() {
    RuleDefinition myRule = RuleDefinition.newRuleBuilder("MyRule");
    myRule.is("option1");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(\"option1\")"));
    myRule.override("option2");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(\"option2\")"));
  }

  @Test
  public void testSkipFromAst() {
    RuleDefinition ruleBuilder = RuleDefinition.newRuleBuilder("MyRule");
    assertThat(ruleBuilder.hasToBeSkippedFromAst(null), is(false));

    ruleBuilder.skip();
    assertThat(ruleBuilder.hasToBeSkippedFromAst(null), is(true));
  }

  @Test
  public void testSkipFromAstIf() {
    RuleDefinition ruleBuilder = RuleDefinition.newRuleBuilder("MyRule");
    ruleBuilder.skipIfOneChild();

    AstNode parent = new AstNode(mockToken(GenericTokenType.IDENTIFIER, "parent"));
    AstNode child1 = new AstNode(mockToken(GenericTokenType.IDENTIFIER, "child1"));
    AstNode child2 = new AstNode(mockToken(GenericTokenType.IDENTIFIER, "child2"));
    parent.addChild(child1);
    parent.addChild(child2);
    child1.addChild(child2);

    assertThat(ruleBuilder.hasToBeSkippedFromAst(parent), is(false));
    assertThat(ruleBuilder.hasToBeSkippedFromAst(child2), is(false));
    assertThat(ruleBuilder.hasToBeSkippedFromAst(child1), is(true));
  }
}
