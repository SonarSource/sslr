/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2017 SonarSource SA
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
package com.sonar.sslr.impl.xpath;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.xpath.AstNodeNavigator.Attribute;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URI;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AstNodeNavigatorTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private AstNodeNavigator navigator;

  @Before
  public void setUp() {
    navigator = new AstNodeNavigator();
  }

  @Test
  public void getTextStringValue() {
    thrown.expect(UnsupportedOperationException.class);
    navigator.getTextStringValue(null);
  }

  @Test
  public void getCommentStringValue() {
    thrown.expect(UnsupportedOperationException.class);
    navigator.getCommentStringValue(null);
  }

  @Test
  public void getAttributeStringValue() throws Exception {
    AstNode astNode = new AstNode(Token.builder()
      .setURI(new URI("tests://unittest"))
      .setType(GenericTokenType.IDENTIFIER)
      .setLine(1)
      .setColumn(2)
      .setValueAndOriginalValue("foo", "bar")
      .build());
    assertThat(navigator.getAttributeStringValue(new Attribute("tokenLine", astNode))).isEqualTo("1");
    assertThat(navigator.getAttributeStringValue(new Attribute("tokenColumn", astNode))).isEqualTo("2");
    assertThat(navigator.getAttributeStringValue(new Attribute("tokenValue", astNode))).isEqualTo("foo");
  }

  @Test
  public void getAttributeStringValue2() {
    Attribute attribute = mock(Attribute.class);
    when(attribute.getName()).thenReturn("foo");
    thrown.expect(UnsupportedOperationException.class);
    navigator.getAttributeStringValue(attribute);
  }

  @Test
  public void getElementStringValue() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage("Implicit nodes to string conversion is not supported. Use the tokenValue attribute instead.");
    navigator.getElementStringValue(null);
  }

  /* Namespaces */

  @Test
  public void getNamespacePrefix() {
    thrown.expect(UnsupportedOperationException.class);
    navigator.getNamespacePrefix(null);
  }

  @Test
  public void getNamespaceStringValue() {
    thrown.expect(UnsupportedOperationException.class);
    navigator.getNamespaceStringValue(null);
  }

  /* Attributes */

  @Test
  public void getAttributeName() {
    Attribute attribute = mock(Attribute.class);
    when(attribute.getName()).thenReturn("foo");
    assertThat(navigator.getAttributeName(attribute)).isEqualTo("foo");
  }

  @Test
  public void getAttributeQName() {
    Attribute attribute = mock(Attribute.class);
    when(attribute.getName()).thenReturn("foo");
    assertThat(navigator.getAttributeQName(attribute)).isEqualTo("foo");
  }

  /* Elements */

  @Test
  public void getAttributeNamespaceUri() {
    assertThat(navigator.getAttributeNamespaceUri(null)).isEqualTo("");
  }

  @Test
  public void getElementName() {
    AstNode astNode = mock(AstNode.class);
    when(astNode.getName()).thenReturn("foo");
    assertThat(navigator.getElementName(astNode)).isEqualTo("foo");
  }

  @Test
  public void getElementQName() {
    AstNode astNode = mock(AstNode.class);
    when(astNode.getName()).thenReturn("foo");
    assertThat(navigator.getElementQName(astNode)).isEqualTo("foo");
  }

  @Test
  public void getElementNamespaceUri() {
    assertThat(navigator.getElementNamespaceUri(null)).isEqualTo("");
  }

  /* Types */

  @Test
  public void isAttribute() {
    assertThat(navigator.isAttribute(mock(AstNodeNavigator.Attribute.class))).isTrue();
    assertThat(navigator.isAttribute(null)).isFalse();
  }

  @Test
  public void isComment() {
    assertThat(navigator.isComment(null)).isFalse();
  }

  @Test
  public void isDocument() {
    AstNode astNode = mock(AstNode.class);
    Attribute attribute = mock(Attribute.class);
    when(attribute.getAstNode()).thenReturn(astNode);
    assertThat(navigator.isDocument(attribute)).isFalse();
    assertThat(navigator.isDocument(astNode)).isFalse();
    assertThat(navigator.isDocument(navigator.getDocumentNode(astNode))).isTrue();
  }

  @Test
  public void isDocument2() {
    assertThat(navigator.isDocument(null)).isFalse();
  }

  @Test
  public void isElement() {
    assertThat(navigator.isElement(mock(AstNode.class))).isTrue();
    assertThat(navigator.isElement(null)).isFalse();
  }

  @Test
  public void isNamespace() {
    assertThat(navigator.isNamespace(null)).isFalse();
  }

  @Test
  public void isProcessingInstruction() {
    assertThat(navigator.isProcessingInstruction(null)).isFalse();
  }

  @Test
  public void isText() {
    assertThat(navigator.isText(null)).isFalse();
  }

  /* Navigation */

  @Test
  public void getDocumentNode() {
    AstNode rootAstNode = mock(AstNode.class);
    AstNode astNode = mock(AstNode.class);
    when(astNode.getParent()).thenReturn(rootAstNode);
    Attribute attribute = mock(Attribute.class);
    when(attribute.getAstNode()).thenReturn(astNode);
    AstNode documentNode = (AstNode) navigator.getDocumentNode(attribute);
    assertThat(documentNode.getName()).isEqualTo("[root]");
  }

  @Test
  public void getChildAxisIterator() {
    Attribute attribute = mock(Attribute.class);
    assertThat(navigator.getChildAxisIterator(attribute).hasNext()).isFalse();
  }

  @Test
  public void getChildAxisIterator2() {
    thrown.expect(UnsupportedOperationException.class);
    navigator.getChildAxisIterator(new Object());
  }

  @Test
  public void getParentNode() {
    AstNode rootAstNode = mock(AstNode.class);
    AstNode astNode = mock(AstNode.class);
    when(astNode.getParent()).thenReturn(rootAstNode);
    Attribute attribute = mock(Attribute.class);
    when(attribute.getAstNode()).thenReturn(astNode);
    assertThat(navigator.getParentNode(attribute)).isSameAs(astNode);
    assertThat(navigator.getParentNode(astNode)).isSameAs(rootAstNode);
  }

  @Test
  public void getParentNode2() {
    thrown.expect(UnsupportedOperationException.class);
    navigator.getParentNode(new Object());
  }

  @Test
  public void getParentAxisIterator() {
    thrown.expect(UnsupportedOperationException.class);
    navigator.getParentAxisIterator(new Object());
  }

  @Test
  public void getAttributeAxisIterator() {
    thrown.expect(UnsupportedOperationException.class);
    navigator.getAttributeAxisIterator(new Object());
  }

  /* Unknown */

  @Test
  public void parseXPath() {
    assertThat(navigator.parseXPath(null)).isNull();
  }

}
