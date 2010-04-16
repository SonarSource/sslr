/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

public interface CharacterCompositeTokenType extends TokenType {

  public CharacterTokenType[] getCharacters();

}
