/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2019 SonarSource SA
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
package com.sonar.sslr.xpath;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.test.minic.MiniCGrammar;
import com.sonar.sslr.xpath.api.AstNodeXPathQuery;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.sonar.sslr.test.minic.MiniCParser.parseFile;
import static org.fest.assertions.Assertions.assertThat;

public class IfSMustUseBracesTest {

  private AstNode fileNode;

  @Before
  public void init() {
    fileNode = parseFile("/xpath/ifSMustUseBraces.mc");
  }

  @Test
  public void firstValueEqualsOnlyValueTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create(
        "//IF_STATEMENT/STATEMENT[not(COMPOUND_STATEMENT)]/..|//ELSE_CLAUSE/STATEMENT[not(COMPOUND_STATEMENT)]/..");

    List<AstNode> nodes = xpath.selectNodes(fileNode);

    assertThat(nodes.size()).isEqualTo(2);
    assertThat(nodes.get(0)).isEqualTo(xpath.selectSingleNode(fileNode));
  }

  @Test
  public void valuesTest() {
    AstNodeXPathQuery<AstNode> xpath = AstNodeXPathQuery.create(
        "//IF_STATEMENT/STATEMENT[not(COMPOUND_STATEMENT)]/..|//ELSE_CLAUSE/STATEMENT[not(COMPOUND_STATEMENT)]/..");

    List<AstNode> nodes = xpath.selectNodes(fileNode);

    assertThat(nodes.size()).isEqualTo(2);
    assertThat(nodes.get(0).is(MiniCGrammar.IF_STATEMENT)).isTrue();
    assertThat(nodes.get(0).getTokenLine()).isEqualTo(3);
    assertThat(nodes.get(1).is(MiniCGrammar.ELSE_CLAUSE)).isTrue();
    assertThat(nodes.get(1).getTokenLine()).isEqualTo(16);
  }

}
