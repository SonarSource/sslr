/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2024 SonarSource SA
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
package org.sonar.sslr.examples.grammars.typed;

import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.api.typed.ActionParser;
import org.junit.Test;
import org.sonar.sslr.examples.grammars.typed.api.ArrayTree;
import org.sonar.sslr.examples.grammars.typed.api.BuiltInValueTree;
import org.sonar.sslr.examples.grammars.typed.api.JsonTree;
import org.sonar.sslr.examples.grammars.typed.api.LiteralTree;
import org.sonar.sslr.examples.grammars.typed.api.ObjectTree;
import org.sonar.sslr.examples.grammars.typed.api.PairTree;
import org.sonar.sslr.examples.grammars.typed.api.ValueTree;

import java.nio.charset.StandardCharsets;

import static org.fest.assertions.Assertions.assertThat;

public class JsonGrammarTest {

  private static ActionParser<Tree> parser = new ActionParser<>(
      StandardCharsets.UTF_8,
      JsonLexer.createGrammarBuilder(),
      org.sonar.sslr.examples.grammars.typed.JsonGrammar.class,
      new TreeFactory(),
      new JsonNodeBuilder(),
      JsonLexer.JSON);

  @Test
  public void number() throws Exception {
    assertLiteral("1234567890");
    assertLiteral("-1234567890");
    assertLiteral("0");
    assertLiteral("0.0123456789");
    assertLiteral("1E2");
    assertLiteral("1e2");
    assertLiteral("1E+2");
    assertLiteral("1E-2");
  }

  @Test(expected = RecognitionException.class)
  public void number_not_parsed() throws Exception {
    parser.parse("[ 01 ]");
  }

  @Test
  public void string() throws Exception {
    assertLiteral("\"\"");
    assertLiteral("\"\\\"\"");
    assertLiteral("\"\\\\\"");
    assertLiteral("\"\\/\"");
    assertLiteral("\"\\b\"");
    assertLiteral("\"\\f\"");
    assertLiteral("\"\\n\"");
    assertLiteral("\"\\r\"");
    assertLiteral("\"\\t\"");
    assertLiteral("\"\\uFFFF\"");
    assertLiteral("\"string\"");
  }

  @Test
  public void value() throws Exception {
    assertValue("\"string\"", LiteralTree.class);
    assertValue("{}", ObjectTree.class);
    assertValue("[]", ArrayTree.class);
    assertValue("42", LiteralTree.class);
    assertValue("true", BuiltInValueTree.class);
    assertValue("false", BuiltInValueTree.class);
    assertValue("null", BuiltInValueTree.class);
  }

  @Test
  public void object() throws Exception {
    ObjectTree tree = (ObjectTree) ((JsonTree) parser.parse("{}")).arrayOrObject();
    assertThat(tree.openCurlyBraceToken().value()).isEqualTo("{");
    assertThat(tree.closeCurlyBraceToken().value()).isEqualTo("}");
    assertThat(tree.pairs()).isNull();

    tree = (ObjectTree) ((JsonTree) parser.parse("{ \"string\" : true }")).arrayOrObject();
    assertThat(tree.pairs()).isNotNull();
    assertThat(tree.pairs().next()).isNull();
    PairTree pair = tree.pairs().element();
    assertThat(pair.name().token().value()).isEqualTo("\"string\"");
    assertThat(((BuiltInValueTree) pair.value()).token().value()).isEqualTo("true");
    assertThat(pair.colonToken().value()).isEqualTo(":");

    parser.parse("{ \"string\" : true, \"string\" : false }");
  }

  @Test
  public void array() {
    ArrayTree tree = (ArrayTree) ((JsonTree) parser.parse("[]")).arrayOrObject();
    assertThat(tree.openBracketToken().value()).isEqualTo("[");
    assertThat(tree.closeBracketToken().value()).isEqualTo("]");
    assertThat(tree.values()).isNull();

    tree =(ArrayTree) ((JsonTree) parser.parse("[ true, false ]")).arrayOrObject();
    assertThat(tree.values()).isNotNull();
    assertThat(tree.values().element()).isInstanceOf(BuiltInValueTree.class);
    assertThat(tree.values().commaToken().value()).isEqualTo(",");
    assertThat(tree.values().next().element()).isInstanceOf(BuiltInValueTree.class);
  }

  @Test
  public void json() {
    Tree tree = parser.parse("{}");
    assertThat(tree).isInstanceOf(JsonTree.class);
    assertThat(((JsonTree) tree).arrayOrObject()).isInstanceOf(ObjectTree.class);

    tree = parser.parse("[]");
    assertThat(tree).isInstanceOf(JsonTree.class);
    assertThat(((JsonTree) tree).arrayOrObject()).isInstanceOf(ArrayTree.class);
  }

  private void assertValue(String code, Class c) {
    JsonTree tree = (JsonTree) parser.parse("[ " + code + " ]");
    ValueTree value = ((ArrayTree) tree.arrayOrObject()).values().element();
    assertThat(value).isInstanceOf(c);
  }

  private void assertLiteral(String code) {
    JsonTree tree = (JsonTree) parser.parse("[ " + code + " ]");
    ValueTree value = ((ArrayTree) tree.arrayOrObject()).values().element();
    assertThat(value).isInstanceOf(LiteralTree.class);
    assertThat(((LiteralTree) value).token().value()).isEqualTo(code);
  }

}
