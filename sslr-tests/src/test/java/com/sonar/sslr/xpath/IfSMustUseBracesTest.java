/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.xpath;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.xpath.api.AstNodeXPathQuery;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.sonar.sslr.test.miniC.MiniCParser.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class IfSMustUseBracesTest {

  private AstNode fileNode;

  @Before
  public void init() {
    fileNode = parseFile("/xpath/ifSMustUseBraces.mc");
  }

  @Test
  public void firstValueEqualsOnlyValueTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create(
        "//ifStatement/statement[not(compoundStatement)]/..|//elseClause/statement[not(compoundStatement)]/..");

    List<AstNode> nodes = xpath.getValues(fileNode);

    assertThat(nodes.size(), is(2));
    assertThat(nodes.get(0), is(xpath.getValue(fileNode)));
  }

  @Test
  public void valuesTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create(
        "//ifStatement/statement[not(compoundStatement)]/..|//elseClause/statement[not(compoundStatement)]/..");

    List<AstNode> nodes = xpath.getValues(fileNode);

    assertThat(nodes.size(), is(2));
    assertThat(nodes.get(0).is(getGrammar().ifStatement), is(true));
    assertThat(nodes.get(0).getTokenLine(), is(3));
    assertThat(nodes.get(1).is(getGrammar().elseClause), is(true));
    assertThat(nodes.get(1).getTokenLine(), is(16));
  }

}
