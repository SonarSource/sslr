/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.adapter;

public interface ConditionalBlockAdapter extends ControlFlowAdapter {

  public boolean shouldExecuteConditionalBlock();

}
