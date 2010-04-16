/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.preprocessor;

import org.junit.Test;

import static org.junit.Assert.fail;

public class CharacterCompositeProprocessorTest {

  private CharacterCompositeProprocessor preprocessor = new CharacterCompositeProprocessor(MyCharacterComposite.values());

  @Test
  public void testProcess() {
    
  }

}
