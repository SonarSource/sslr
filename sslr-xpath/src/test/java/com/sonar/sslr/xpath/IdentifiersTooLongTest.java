/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.xpath;

import static com.sonar.sslr.test.miniC.MiniCParser.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sonar.sslr.api.AstNode;

public class IdentifiersTooLongTest {

  private AstNode fileNode;

  @Before
  public void init() {
    fileNode = parse("/checks/identifiersTooLong.mc");
  }

  @Test
  public void valuesTest() {
    AstNodeXpathQuery<AstNode> xpath = AstNodeXpathQuery.create("//IDENTIFIER[string-length(@tokenValue) > 10]");

    List<AstNode> nodes = xpath.getValues(fileNode);

    assertThat(nodes.size(), is(3));
    assertThat(nodes.get(0).getTokenValue(), is("aaaaaaaaa11"));
    assertThat(nodes.get(0).getTokenLine(), is(3));
    assertThat(nodes.get(1).getTokenValue(), is("bbbbbbbbbbbbb15"));
    assertThat(nodes.get(1).getTokenLine(), is(10));
    assertThat(nodes.get(2).getTokenValue(), is("ccccccccc11"));
    assertThat(nodes.get(2).getTokenLine(), is(12));
  }

  @Test
  public void noResultValuesTest() {
    AstNodeXpathQuery<AstNode> xpath = AstNodeXpathQuery.create("//IDENTIFIER[string-length(@tokenValue) > 50]");

    List<AstNode> nodes = xpath.getValues(fileNode);

    assertThat(nodes.size(), is(0));
  }

}
