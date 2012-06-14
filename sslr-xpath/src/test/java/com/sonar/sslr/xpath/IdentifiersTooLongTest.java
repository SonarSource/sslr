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

import static com.sonar.sslr.test.miniC.MiniCParser.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.xpath.api.AstNodeXPathQuery;

public class IdentifiersTooLongTest {

  private AstNode fileNode;

  @Before
  public void init() {
    fileNode = parseFile("/xpath/identifiersTooLong.mc");
  }

  @Test
  public void valuesTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("//IDENTIFIER[string-length(@tokenValue) > 10]");

    List<AstNode> nodes = xpath.selectNodes(fileNode);

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
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create("//IDENTIFIER[string-length(@tokenValue) > 50]");

    List<AstNode> nodes = xpath.selectNodes(fileNode);

    assertThat(nodes.size(), is(0));
  }

}
