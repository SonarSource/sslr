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
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;

import static org.fest.assertions.Assertions.assertThat;


/**
 * This test reproduces a common issue, when one of the rule is not defined.
 */
public class ActionParserRuleNotDefinedTest {

  @Test
  public void test() {
    GrammarException exception = Assert.assertThrows(GrammarException.class, () -> parser(MyLexicalGrammar.A));
    assertThat(exception)
      .hasMessage("The expression for rule \"C\" is undefined.");
  }

  private ActionParser<AstNode> parser(GrammarRuleKey ruleKey) {
    return new ActionParser<>(StandardCharsets.UTF_8, MyLexicalGrammar.builder(), MyGrammar.class, new MyTreeFactory(), new AstNodeBuilder(), ruleKey);
  }

  public enum MyLexicalGrammar implements GrammarRuleKey {
    A, B, C, EOF;

    public static LexerlessGrammarBuilder builder() {
      LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
      b.rule(B).is("B").skip();
      // The rule C is not defined. Uncommenting next line fixes the issue.
//      b.rule(C).is("C").skip();
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

    public AstNode A() {
      return b.<AstNode>nonterminal(MyLexicalGrammar.A).is(
        f.a(
          b.token(MyLexicalGrammar.B),
          b.oneOrMore(b.token(MyLexicalGrammar.C))
        )
      );
    }

  }

  public static class MyTreeFactory {

    public AstNode a(AstNode token, List<AstNode> tokens) {
      return new ANode(token, tokens);
    }

    public AstNode b(AstNode token) {
      return new BNode(token);
    }
  }

  public static class ANode extends AstNode {

    public ANode(AstNode token, List<AstNode> tokens) {
      super(token.getToken());
    }
  }

  public static class BNode extends AstNode {

    public BNode(AstNode tokens) {
      super(tokens.getToken());
    }
  }
}
