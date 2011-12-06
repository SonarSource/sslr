/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.xpath.miniC.checks;

import static com.sonar.sslr.api.GenericTokenType.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.xpath.AstNodeXpathQuery;

public class BasicQueriesCheckTest extends AbstractCheck {

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
  @Ignore
  public void identifiersCountTest() {
    AstNodeXpathQuery<AstNode> xpath = AstNodeXpathQuery.create("/compilationUnit[count(//IDENTIFIER) = 2]");
    assertThat(xpath.getValue(fileNode), is(fileNode));
  }

  @Test
  @Ignore
  public void getIdentifiersTest() {
    AstNodeXpathQuery<AstNode> xpath = AstNodeXpathQuery.create("//IDENTIFIER");
    List<AstNode> nodes = xpath.getValues(fileNode);
    System.out.println("how many? " + nodes.size());
    // assertThat(xpath.query(fileNode), is(0));
  }

}
