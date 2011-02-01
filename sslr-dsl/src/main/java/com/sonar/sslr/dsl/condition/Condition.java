/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.condition;

public class Condition implements AbstractCondition {

  public AbstractCondition nestedCondition;
  
  public void setNestedCondition(AbstractCondition condition){
    this.nestedCondition = condition;
  }

  public boolean value() {
    return nestedCondition.value();
  }
}
