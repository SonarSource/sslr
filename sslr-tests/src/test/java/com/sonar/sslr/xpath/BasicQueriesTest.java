/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.xpath;

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.test.miniC.MiniCParser.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.xpath.api.AstNodeXPathQuery;

public class BasicQueriesTest {

  private AstNode fileNode;

  @Before
  public void init() {
    fileNode = parseFile("/xpath/basicQueries.mc");
  }

  @Test
  public void compilationUnitTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("/compilationUnit");
    assertThat(xpath.getValue(fileNode), is(fileNode));
  }

  @Test
  public void anyCompilationUnitTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("//compilationUnit");
    assertThat(xpath.getValue(fileNode), is(fileNode));
  }

  @Test
  public void relativeCompilationUnitTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("./compilationUnit");
    assertThat(xpath.getValue(fileNode), is(fileNode));
  }

  @Test
  public void compilationUnitWithPredicateWithEOFTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("/compilationUnit[not(not(EOF))]");
    assertThat(xpath.getValue(fileNode), is(fileNode));
  }

  @Test
  public void compilationUnitWithPredicateWithoutEOFTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("/compilationUnit[not(EOF)]");
    assertThat(xpath.getValue(fileNode), nullValue());
  }

  @Test
  public void EOFTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("/compilationUnit/EOF");
    assertThat(xpath.getValue(fileNode), is(fileNode.findFirstChild(EOF)));
  }

  @Test
  public void anyEOFTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("//EOF");
    assertThat(xpath.getValue(fileNode), is(fileNode.findFirstChild(EOF)));
  }

  @Test
  public void getTokenValueAttributeTest() {
    AstNodeXPathQuery<String> xpath = AstNodeXPathQuery.create("/compilationUnit/@tokenValue");
    assertThat(xpath.getValue(fileNode), is("int"));
  }

  @Test
  public void getTokenLineAttributeTest() {
    AstNodeXPathQuery<Integer> xpath = AstNodeXPathQuery.create("/compilationUnit/@tokenLine");
    assertThat(xpath.getValue(fileNode), is(2));
  }

  @Test
  public void getTokenColumnAttributeTest() {
    AstNodeXPathQuery<Integer> xpath = AstNodeXPathQuery.create("/compilationUnit/@tokenColumn");
    assertThat(xpath.getValue(fileNode), is(0));
  }

  @Test
  public void getSecondDeclarationTest() {
    AstNodeXPathQuery<AstNode> xpath1 = AstNodeXPathQuery.create("/compilationUnit/definition[@tokenLine=4]");
    AstNodeXPathQuery<AstNode> xpath2 = AstNodeXPathQuery.create("/compilationUnit/definition[2]");
    AstNode declarationAtLineFour = fileNode.getChild(1);
    assertThat(declarationAtLineFour.is(getGrammar().definition), is(true));
    assertThat(declarationAtLineFour.getTokenLine(), is(4));
    assertThat(xpath1.getValue(fileNode), is(declarationAtLineFour));
    assertThat(xpath1.getValue(fileNode), is(xpath2.getValue(fileNode)));
  }

  @Test
  public void identifiersCountTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("/compilationUnit[count(//IDENTIFIER) = 2]");
    assertThat(xpath.getValue(fileNode), is(fileNode));
  }

  @Test
  public void getIdentifiersTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("//IDENTIFIER");
    List<AstNode> nodes = xpath.getValues(fileNode);
    assertThat(nodes.size(), is(2));
    assertThat(nodes.get(0).getTokenValue(), is("a"));
    assertThat(nodes.get(1).getTokenValue(), is("b"));
  }

}
