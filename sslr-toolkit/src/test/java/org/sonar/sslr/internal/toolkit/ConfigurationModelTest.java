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
package org.sonar.sslr.internal.toolkit;

import com.google.common.collect.Lists;
import com.sonar.sslr.impl.Parser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.colorizer.Tokenizer;
import org.sonar.sslr.toolkit.ConfigurationCallback;
import org.sonar.sslr.toolkit.ConfigurationProperty;

import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConfigurationModelTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void getParser_should_return_same_instance_as_long_as_not_updated() {
    Parser p = mock(Parser.class);
    ConfigurationCallback callback = mock(ConfigurationCallback.class);
    when(callback.getParser(Collections.EMPTY_LIST)).thenReturn(p);

    ConfigurationModel model = new ConfigurationModel(Collections.EMPTY_LIST, callback);

    assertThat(model.getParser()).isEqualTo(p);

    Parser p2 = mock(Parser.class);
    when(callback.getParser(Collections.EMPTY_LIST)).thenReturn(p2);

    assertThat(model.getParser()).isEqualTo(p);
  }

  @Test
  public void getParser_should_request_new_parser_if_update_flag_is_set() {
    Parser p = mock(Parser.class);
    ConfigurationCallback callback = mock(ConfigurationCallback.class);
    when(callback.getParser(Collections.EMPTY_LIST)).thenReturn(p);

    ConfigurationModel model = new ConfigurationModel(Collections.EMPTY_LIST, callback);

    assertThat(model.getParser()).isEqualTo(p);

    Parser p2 = mock(Parser.class);
    when(callback.getParser(Collections.EMPTY_LIST)).thenReturn(p2);

    model.setUpdatedFlag();

    assertThat(model.getParser()).isEqualTo(p2);
  }

  @Test
  public void getTokenizers_should_return_same_instance_as_long_as_not_updated() {
    List<Tokenizer> t = Lists.newArrayList();
    ConfigurationCallback callback = mock(ConfigurationCallback.class);
    when(callback.getTokenizers(Collections.EMPTY_LIST)).thenReturn(t);

    ConfigurationModel model = new ConfigurationModel(Collections.EMPTY_LIST, callback);

    assertThat(model.getTokenizers()).isEqualTo(t);

    List<Tokenizer> t2 = Lists.newArrayList();
    when(callback.getTokenizers(Collections.EMPTY_LIST)).thenReturn(t2);

    assertThat(model.getTokenizers()).isEqualTo(t);
  }

  @Test
  public void getTokenizers_should_request_new_parser_if_update_flag_is_set() {
    List<Tokenizer> t = Lists.newArrayList();
    ConfigurationCallback callback = mock(ConfigurationCallback.class);
    when(callback.getTokenizers(Collections.EMPTY_LIST)).thenReturn(t);

    ConfigurationModel model = new ConfigurationModel(Collections.EMPTY_LIST, callback);

    assertThat(model.getTokenizers()).isEqualTo(t);

    List<Tokenizer> t2 = Lists.newArrayList();
    when(callback.getTokenizers(Collections.EMPTY_LIST)).thenReturn(t2);

    model.setUpdatedFlag();

    assertThat(model.getTokenizers()).isEqualTo(t);
  }

  @Test
  public void getProperties() {
    assertThat(new ConfigurationModel(Collections.EMPTY_LIST, mock(ConfigurationCallback.class)).getProperties()).isEmpty();
    ConfigurationProperty property = mock(ConfigurationProperty.class);
    assertThat(new ConfigurationModel(Lists.newArrayList(property), mock(ConfigurationCallback.class)).getProperties()).containsExactly(property);
  }

}
