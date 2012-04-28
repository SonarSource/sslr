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
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.xpath.AstNodeNavigator;
import com.sonar.sslr.impl.xpath.AstNodeNavigator.Attribute;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URI;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
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
    assertThat(navigator.getAttributeStringValue(new Attribute("tokenLine", astNode)), is("1"));
    assertThat(navigator.getAttributeStringValue(new Attribute("tokenColumn", astNode)), is("2"));
    assertThat(navigator.getAttributeStringValue(new Attribute("tokenValue", astNode)), is("bar"));
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
    assertThat(navigator.getAttributeName(attribute), is("foo"));
  }

  @Test
  public void getAttributeQName() {
    Attribute attribute = mock(Attribute.class);
    when(attribute.getName()).thenReturn("foo");
    assertThat(navigator.getAttributeQName(attribute), is("foo"));
  }

  /* Elements */

  @Test
  public void getAttributeNamespaceUri() {
    assertThat(navigator.getAttributeNamespaceUri(null), is(""));
  }

  @Test
  public void getElementName() {
    AstNode astNode = mock(AstNode.class);
    when(astNode.getName()).thenReturn("foo");
    assertThat(navigator.getElementName(astNode), is("foo"));
  }

  @Test
  public void getElementQName() {
    AstNode astNode = mock(AstNode.class);
    when(astNode.getName()).thenReturn("foo");
    assertThat(navigator.getElementQName(astNode), is("foo"));
  }

  @Test
  public void getElementNamespaceUri() {
    assertThat(navigator.getElementNamespaceUri(null), is(""));
  }

  /* Types */

  @Test
  public void isAttribute() {
    assertThat(navigator.isAttribute(mock(AstNodeNavigator.Attribute.class)), is(true));
    assertThat(navigator.isAttribute(null), is(false));
  }

  @Test
  public void isComment() {
    assertThat(navigator.isComment(null), is(false));
  }

  @Test
  public void isDocument() {
    AstNode astNode = mock(AstNode.class);
    Attribute attribute = mock(Attribute.class);
    when(attribute.getAstNode()).thenReturn(astNode);
    assertThat(navigator.isDocument(attribute), is(false));
    assertThat(navigator.isDocument(astNode), is(false));
    assertThat(navigator.isDocument(navigator.getDocumentNode(astNode)), is(true));
  }

  @Test
  public void isDocument2() {
    assertThat(navigator.isDocument(null), is(false));
  }

  @Test
  public void isElement() {
    assertThat(navigator.isElement(mock(AstNode.class)), is(true));
    assertThat(navigator.isElement(null), is(false));
  }

  @Test
  public void isNamespace() {
    assertThat(navigator.isNamespace(null), is(false));
  }

  @Test
  public void isProcessingInstruction() {
    assertThat(navigator.isProcessingInstruction(null), is(false));
  }

  @Test
  public void isText() {
    assertThat(navigator.isText(null), is(false));
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
    assertThat(documentNode.getName(), is("[root]"));
  }

  @Test
  public void getChildAxisIterator() {
    Attribute attribute = mock(Attribute.class);
    assertThat(navigator.getChildAxisIterator(attribute).hasNext(), is(false));
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
    assertThat(navigator.getParentNode(attribute), sameInstance((Object) astNode));
    assertThat(navigator.getParentNode(astNode), sameInstance((Object) rootAstNode));
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
    assertThat(navigator.parseXPath(null), nullValue());
  }

}
