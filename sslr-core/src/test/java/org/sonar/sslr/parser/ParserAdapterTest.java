/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2024 SonarSource SA
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
package org.sonar.sslr.parser;

import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Parser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.sslr.internal.matchers.ExpressionGrammar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ParserAdapterTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private ExpressionGrammar grammar;
  private ParserAdapter parser;

  @Before
  public void setUp() {
    grammar = new ExpressionGrammar();
    parser = new ParserAdapter(Charset.forName("UTF-8"), grammar);
  }

  @Test
  public void should_return_grammar() {
    assertThat(parser.getGrammar()).isSameAs(grammar);
  }

  @Test
  public void should_parse_string() {
    parser.parse("1+1");
  }

  @Test
  public void should_not_parse_invalid_string() {
    RecognitionException thrown = assertThrows(RecognitionException.class,
      () -> parser.parse(""));
    assertEquals("Parse error at line 1 column 1:\n" +
        "\n" +
        "1: \n" +
        "   ^\n",
      thrown.getMessage());
  }

  @Test
  public void should_parse_file() throws Exception {
    File file = temporaryFolder.newFile();
    try (
      FileOutputStream fileOutputStream = new FileOutputStream(file);
      OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
    ) {
      writer.write("1+1");
    }
    parser.parse(file);
  }

  @Test
  public void should_not_parse_invalid_file() {
    File file = new File("notfound");
    assertThrows(RecognitionException.class,
      () -> parser.parse(file));
  }

  @Test
  public void builder_should_not_create_new_instance_from_adapter() {
    assertThat(Parser.builder(parser).build()).isSameAs(parser);
  }

  @Test
  public void parse_tokens_unsupported() {
    List<Token> tokens = Collections.emptyList();
    assertThrows(UnsupportedOperationException.class,
      () -> parser.parse(tokens));
  }

  @Test
  public void getRootRule_unsupported() {
    assertThrows(UnsupportedOperationException.class,
      () -> parser.getRootRule());
  }

}
