/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.xpath;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.test.minic.MiniCGrammar;
import com.sonar.sslr.xpath.api.AstNodeXPathQuery;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.test.minic.MiniCParser.parseFile;
import static org.fest.assertions.Assertions.assertThat;

public class BasicQueriesTest {

  private AstNode fileNode;

  @Before
  public void init() {
    fileNode = parseFile("/xpath/basicQueries.mc");
  }

  @Test
  public void compilationUnitTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("/COMPILATION_UNIT");
    assertThat(xpath.selectSingleNode(fileNode)).isEqualTo(fileNode);
  }

  @Test
  public void anyCompilationUnitTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("//COMPILATION_UNIT");
    assertThat(xpath.selectSingleNode(fileNode)).isEqualTo(fileNode);
  }

  @Test
  public void compilationUnitWithPredicateWithEOFTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("/COMPILATION_UNIT[not(not(EOF))]");
    assertThat(xpath.selectSingleNode(fileNode)).isEqualTo(fileNode);
  }

  @Test
  public void compilationUnitWithPredicateWithoutEOFTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("/COMPILATION_UNIT[not(EOF)]");
    assertThat(xpath.selectSingleNode(fileNode)).isNull();
  }

  @Test
  public void EOFTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("/COMPILATION_UNIT/EOF");
    assertThat(xpath.selectSingleNode(fileNode)).isEqualTo(fileNode.findFirstChild(EOF));
  }

  @Test
  public void anyEOFTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("//EOF");
    assertThat(xpath.selectSingleNode(fileNode)).isEqualTo(fileNode.findFirstChild(EOF));
  }

  @Test
  public void getTokenValueAttributeTest() {
    AstNodeXPathQuery<String> xpath = AstNodeXPathQuery.create("string(/COMPILATION_UNIT/@tokenValue)");
    assertThat(xpath.selectSingleNode(fileNode)).isEqualTo("int");
  }

  @Test
  public void getTokenLineAttributeTest() {
    AstNodeXPathQuery<String> xpath = AstNodeXPathQuery.create("string(/COMPILATION_UNIT/@tokenLine)");
    assertThat(xpath.selectSingleNode(fileNode)).isEqualTo("2");
  }

  @Test
  public void getTokenColumnAttributeTest() {
    AstNodeXPathQuery<String> xpath = AstNodeXPathQuery.create("string(/COMPILATION_UNIT/@tokenColumn)");
    assertThat(xpath.selectSingleNode(fileNode)).isEqualTo("0");
  }

  @Test
  public void getSecondDeclarationTest() {
    AstNodeXPathQuery<AstNode> xpath1 = AstNodeXPathQuery.create("/COMPILATION_UNIT/DEFINITION[@tokenLine=4]");
    AstNodeXPathQuery<AstNode> xpath2 = AstNodeXPathQuery.create("/COMPILATION_UNIT/DEFINITION[2]");
    AstNode declarationAtLineFour = fileNode.getChildren().get(1);
    assertThat(declarationAtLineFour.is(MiniCGrammar.DEFINITION)).isTrue();
    assertThat(declarationAtLineFour.getTokenLine()).isEqualTo(4);
    assertThat(xpath1.selectSingleNode(fileNode)).isEqualTo(declarationAtLineFour);
    assertThat(xpath1.selectSingleNode(fileNode)).isEqualTo(xpath2.selectSingleNode(fileNode));
  }

  @Test
  public void identifiersCountTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("/COMPILATION_UNIT[count(//IDENTIFIER) = 2]");
    assertThat(xpath.selectSingleNode(fileNode)).isEqualTo(fileNode);
  }

  @Test
  public void getIdentifiersTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("//IDENTIFIER");
    List<AstNode> nodes = xpath.selectNodes(fileNode);
    assertThat(nodes.size()).isEqualTo(2);
    assertThat(nodes.get(0).getTokenValue()).isEqualTo("a");
    assertThat(nodes.get(1).getTokenValue()).isEqualTo("b");
  }

}
