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
package com.sonar.sslr.xpath;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.xpath.api.AstNodeXPathQuery;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.test.miniC.MiniCParser.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class BasicQueriesTest {

  private AstNode fileNode;

  @Before
  public void init() {
    fileNode = parseFile("/xpath/basicQueries.mc");
  }

  @Test
  public void compilationUnitTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("/compilationUnit");
    assertThat(xpath.selectSingleNode(fileNode), is(fileNode));
  }

  @Test
  public void anyCompilationUnitTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("//compilationUnit");
    assertThat(xpath.selectSingleNode(fileNode), is(fileNode));
  }

  @Test
  public void compilationUnitWithPredicateWithEOFTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("/compilationUnit[not(not(EOF))]");
    assertThat(xpath.selectSingleNode(fileNode), is(fileNode));
  }

  @Test
  public void compilationUnitWithPredicateWithoutEOFTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("/compilationUnit[not(EOF)]");
    assertThat(xpath.selectSingleNode(fileNode), nullValue());
  }

  @Test
  public void EOFTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("/compilationUnit/EOF");
    assertThat(xpath.selectSingleNode(fileNode), is(fileNode.findFirstChild(EOF)));
  }

  @Test
  public void anyEOFTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("//EOF");
    assertThat(xpath.selectSingleNode(fileNode), is(fileNode.findFirstChild(EOF)));
  }

  @Test
  public void getTokenValueAttributeTest() {
    AstNodeXPathQuery<String> xpath = AstNodeXPathQuery.create("string(/compilationUnit/@tokenValue)");
    assertThat(xpath.selectSingleNode(fileNode), is("int"));
  }

  @Test
  public void getTokenLineAttributeTest() {
    AstNodeXPathQuery<String> xpath = AstNodeXPathQuery.create("string(/compilationUnit/@tokenLine)");
    assertThat(xpath.selectSingleNode(fileNode), is("2"));
  }

  @Test
  public void getTokenColumnAttributeTest() {
    AstNodeXPathQuery<String> xpath = AstNodeXPathQuery.create("string(/compilationUnit/@tokenColumn)");
    assertThat(xpath.selectSingleNode(fileNode), is("0"));
  }

  @Test
  public void getSecondDeclarationTest() {
    AstNodeXPathQuery<AstNode> xpath1 = AstNodeXPathQuery.create("/compilationUnit/definition[@tokenLine=4]");
    AstNodeXPathQuery<AstNode> xpath2 = AstNodeXPathQuery.create("/compilationUnit/definition[2]");
    AstNode declarationAtLineFour = fileNode.getChild(1);
    assertThat(declarationAtLineFour.is(getGrammar().definition), is(true));
    assertThat(declarationAtLineFour.getTokenLine(), is(4));
    assertThat(xpath1.selectSingleNode(fileNode), is(declarationAtLineFour));
    assertThat(xpath1.selectSingleNode(fileNode), is(xpath2.selectSingleNode(fileNode)));
  }

  @Test
  public void identifiersCountTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("/compilationUnit[count(//IDENTIFIER) = 2]");
    assertThat(xpath.selectSingleNode(fileNode), is(fileNode));
  }

  @Test
  public void getIdentifiersTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("//IDENTIFIER");
    List<AstNode> nodes = xpath.selectNodes(fileNode);
    assertThat(nodes.size(), is(2));
    assertThat(nodes.get(0).getTokenValue(), is("a"));
    assertThat(nodes.get(1).getTokenValue(), is("b"));
  }

}
