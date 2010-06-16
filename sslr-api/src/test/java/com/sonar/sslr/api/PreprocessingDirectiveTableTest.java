/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class PreprocessingDirectiveTableTest {

  PreprocessingDirectiveTable table = new PreprocessingDirectiveTable();
  ConditionDirective condition1 = new ConditionDirective(new File("Dummy"), 1);
  ConditionDirective condition2 = new ConditionDirective(new File("Dummy"), 2);
  IncludeDirective include1 = new IncludeDirective(new File("Dummy"), 1);
  IncludeDirective include2 = new IncludeDirective(new File("Dummy"), 2);

  @Test
  public void testGetLastDirective() {
    table.add(condition1);
    assertThat(table.getLast(), is((PreprocessingDirective) condition1));
    table.add(include1);
    assertThat(table.getLast(), is((PreprocessingDirective) include1));
  }

  @Test
  public void testGetFirst() {
    table.add(condition1);
    table.add(condition2);
    assertThat(table.getFirst(ConditionDirective.class), is(condition1));
  }

  @Test
  public void testGetLast() {
    table.add(condition1);
    table.add(condition2);
    assertThat(table.getLast(ConditionDirective.class), is(condition2));
  }

  @Test
  public void testGetAll() {
    table.add(condition1);
    table.add(include2);
    table.add(condition2);
    table.add(include1);

    List<ConditionDirective> conditions = table.getAll(ConditionDirective.class);
    assertThat(conditions.size(), is(2));
    assertThat(conditions, hasItem(condition2));
  }

  private class ConditionDirective extends PreprocessingDirective {

    public ConditionDirective(File definitionFile, int definitionLine) {
      super(definitionFile, definitionLine);
    }

  }

  private class IncludeDirective extends PreprocessingDirective {

    public IncludeDirective(File definitionFile, int definitionLine) {
      super(definitionFile, definitionLine);
    }

  }

}
