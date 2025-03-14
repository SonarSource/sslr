/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.sslr.toolkit;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import org.junit.Test;
import org.sonar.colorizer.Tokenizer;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AbstractConfigurationModelTest {

  @Test
  public void getParser_should_return_parser_instance() {
    MyConfigurationModel model = new MyConfigurationModel();
    Parser<? extends Grammar> p = mock(Parser.class);
    model.setParser(p);
    assertThat(model.getParser()).isEqualTo(p);
  }

  @Test
  public void getParser_should_return_same_parser_instance_when_flag_not_set() {
    MyConfigurationModel model = new MyConfigurationModel();
    Parser<? extends Grammar> p = mock(Parser.class);
    model.setParser(p);
    assertThat(model.getParser()).isEqualTo(p);
    Parser<? extends Grammar> p2 = mock(Parser.class);
    model.setParser(p2);
    assertThat(model.getParser()).isEqualTo(p);
  }

  @Test
  public void getParser_should_return_different_parser_instance_when_flag_set() {
    MyConfigurationModel model = new MyConfigurationModel();
    Parser<? extends Grammar> p = mock(Parser.class);
    model.setParser(p);
    assertThat(model.getParser()).isEqualTo(p);
    Parser<? extends Grammar> p2 = mock(Parser.class);
    model.setParser(p2);

    model.setUpdatedFlag();

    assertThat(model.getParser()).isEqualTo(p2);
  }

  @Test
  public void getTokenizers_should_return_parser_instance() {
    MyConfigurationModel model = new MyConfigurationModel();
    List<Tokenizer> t = mock(List.class);
    model.setTokenizers(t);
    assertThat(model.getTokenizers()).isEqualTo(t);
  }

  @Test
  public void getTokenizers_should_return_same_parser_instance_when_flag_not_set() {
    MyConfigurationModel model = new MyConfigurationModel();
    List<Tokenizer> t = mock(List.class);
    model.setTokenizers(t);
    assertThat(model.getTokenizers()).isEqualTo(t);
    List<Tokenizer> t2 = mock(List.class);
    model.setTokenizers(t2);
    assertThat(model.getTokenizers()).isEqualTo(t);
  }

  @Test
  public void getTokenizers_should_return_different_parser_instance_when_flag_set() {
    MyConfigurationModel model = new MyConfigurationModel();
    List<Tokenizer> t = mock(List.class);
    model.setTokenizers(t);
    assertThat(model.getTokenizers()).isEqualTo(t);
    List<Tokenizer> t2 = mock(List.class);
    model.setTokenizers(t2);

    model.setUpdatedFlag();

    assertThat(model.getTokenizers()).isEqualTo(t2);
  }

  private static class MyConfigurationModel extends AbstractConfigurationModel {

    private Parser<? extends Grammar> parser;
    private List<Tokenizer> tokenizers;

    @Override
    public List<ConfigurationProperty> getProperties() {
      return null;
    }

    public void setParser(Parser<? extends Grammar> parser) {
      this.parser = parser;
    }

    @Override
    public Parser<? extends Grammar> doGetParser() {
      return parser;
    }

    public void setTokenizers(List<Tokenizer> tokenizers) {
      this.tokenizers = tokenizers;
    }

    @Override
    public List<Tokenizer> doGetTokenizers() {
      return tokenizers;
    }

  }

}
