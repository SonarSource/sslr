/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2023 SonarSource SA
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
package com.sonar.sslr.api.typed;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.Test;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;

import static org.fest.assertions.Assertions.assertThat;


/**
 * This test reproduces a common issue, when one of the method in the grammar is private.
 * It happens very often when implementing grammar top down and when IDE generates a method with private scope.
 */
public class ActionParserPrivateMethodTest {

  @Test
  public void test() {
    try {
      parser(MyLexicalGrammar.B).parse("AAA");
    } catch (Exception e) {
      assertThat(((InvocationTargetException)e.getCause()).getTargetException().getMessage())
        .startsWith("Unable to find the method for rule: \"C\" in Grammar. Please check if the method is defined as public.");
    }
  }

  private ActionParser<AstNode> parser(GrammarRuleKey ruleKey) {
    return new ActionParser<>(StandardCharsets.UTF_8, MyLexicalGrammar.builder(), MyGrammar.class, new MyTreeFactory(), new AstNodeBuilder(), ruleKey);
  }

  public enum MyLexicalGrammar implements GrammarRuleKey {
    A, B, C, EOF;

    public static LexerlessGrammarBuilder builder() {
      LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
      b.rule(A).is(b.regexp("A"));
      b.rule(EOF).is(b.token(GenericTokenType.EOF, b.endOfInput())).skip();
      return b;
    }
  }

  public static class MyGrammar {
    private final GrammarBuilder<AstNode> b;
    private final MyTreeFactory f;

    public MyGrammar(GrammarBuilder<AstNode> b, MyTreeFactory f) {
      this.b = b;
      this.f = f;
    }

    public AstNode B() {
      return b.<AstNode>nonterminal(MyLexicalGrammar.B).is(
        f.b(
          b.token(MyLexicalGrammar.A),
          b.oneOrMore(C())
        )
      );
    }

    /**
     * This method is private on purpose, to reproduce the issue
     */
    private AstNode C() {
      return b.<AstNode>nonterminal(MyLexicalGrammar.C).is(
        f.c(
          b.token(MyLexicalGrammar.A)
        )
      );
    }
  }

  public static class MyTreeFactory {

    public AstNode b(AstNode token, List<AstNode> tokens) {
      return new BNode(token, tokens);
    }

    public AstNode c(AstNode token) {
      return new CNode(token);
    }
  }

  public static class BNode extends AstNode {

    public BNode(AstNode token, List<AstNode> tokens) {
      super(token.getToken());
    }
  }

  public static class CNode extends AstNode {

    public CNode(AstNode tokens) {
      super(tokens.getToken());
    }
  }
}
