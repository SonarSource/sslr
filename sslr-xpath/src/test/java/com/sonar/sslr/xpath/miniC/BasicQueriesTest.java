/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.xpath.miniC;

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.xpath.miniC.CheckUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.xpath.AstNodeXpathQuery;

public class BasicQueriesTest {

  private AstNode fileNode;

  @Before
  public void init() {
    fileNode = parse("/checks/basicQueries.mc");
  }

  @Test
  public void compilationUnitTest() {
    AstNodeXpathQuery<AstNode> xpath = AstNodeXpathQuery.create("/compilationUnit");
    assertThat(xpath.getValue(fileNode), is(fileNode));
  }

  @Test
  public void anyCompilationUnitTest() {
    AstNodeXpathQuery<AstNode> xpath = AstNodeXpathQuery.create("//compilationUnit");
    assertThat(xpath.getValue(fileNode), is(fileNode));
  }

  @Test
  public void relativeCompilationUnitTest() {
    AstNodeXpathQuery<AstNode> xpath = AstNodeXpathQuery.create("./compilationUnit");
    assertThat(xpath.getValue(fileNode), is(fileNode));
  }

  @Test
  public void compilationUnitWithPredicateWithEOFTest() {
    AstNodeXpathQuery<AstNode> xpath = AstNodeXpathQuery.create("/compilationUnit[not(not(EOF))]");
    assertThat(xpath.getValue(fileNode), is(fileNode));
  }

  @Test
  public void compilationUnitWithPredicateWithoutEOFTest() {
    AstNodeXpathQuery<AstNode> xpath = AstNodeXpathQuery.create("/compilationUnit[not(EOF)]");
    assertThat(xpath.getValue(fileNode), nullValue());
  }

  @Test
  public void EOFTest() {
    AstNodeXpathQuery<AstNode> xpath = AstNodeXpathQuery.create("/compilationUnit/EOF");
    assertThat(xpath.getValue(fileNode), is(fileNode.findFirstChild(EOF)));
  }

  @Test
  public void anyEOFTest() {
    AstNodeXpathQuery<AstNode> xpath = AstNodeXpathQuery.create("//EOF");
    assertThat(xpath.getValue(fileNode), is(fileNode.findFirstChild(EOF)));
  }

  @Test
  public void getTokenValueAttributeTest() {
    AstNodeXpathQuery<String> xpath = AstNodeXpathQuery.create("/compilationUnit/@tokenValue");
    assertThat(xpath.getValue(fileNode), is("int"));
  }

  @Test
  public void getTokenLineAttributeTest() {
    AstNodeXpathQuery<Integer> xpath = AstNodeXpathQuery.create("/compilationUnit/@tokenLine");
    assertThat(xpath.getValue(fileNode), is(2));
  }

  @Test
  public void getTokenColumnAttributeTest() {
    AstNodeXpathQuery<Integer> xpath = AstNodeXpathQuery.create("/compilationUnit/@tokenColumn");
    assertThat(xpath.getValue(fileNode), is(0));
  }

  @Test
  public void getSecondDeclarationTest() {
    AstNodeXpathQuery<AstNode> xpath1 = AstNodeXpathQuery.create("/compilationUnit/declaration[@tokenLine=4]");
    AstNodeXpathQuery<AstNode> xpath2 = AstNodeXpathQuery.create("/compilationUnit/declaration[2]");
    AstNode declarationAtLineFour = fileNode.getChild(1);
    assertThat(declarationAtLineFour.is(getGrammar().declaration), is(true));
    assertThat(declarationAtLineFour.getTokenLine(), is(4));
    assertThat(xpath1.getValue(fileNode), is(declarationAtLineFour));
    assertThat(xpath1.getValue(fileNode), is(xpath2.getValue(fileNode)));
  }

  @Test
  public void identifiersCountTest() {
    AstNodeXpathQuery<AstNode> xpath = AstNodeXpathQuery.create("/compilationUnit[count(//IDENTIFIER) = 2]");
    assertThat(xpath.getValue(fileNode), is(fileNode));
  }

  @Test
  public void getIdentifiersTest() {
    AstNodeXpathQuery<AstNode> xpath = AstNodeXpathQuery.create("//IDENTIFIER");
    List<AstNode> nodes = xpath.getValues(fileNode);
    assertThat(nodes.size(), is(2));
    assertThat(nodes.get(0).getTokenValue(), is("a"));
    assertThat(nodes.get(1).getTokenValue(), is("b"));
  }

}
