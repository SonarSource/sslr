/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.preprocessor;

import com.sonar.sslr.api.CharacterCompositeTokenType;
import com.sonar.sslr.api.CharacterTokenType;

public enum MyCharacterComposite implements CharacterCompositeTokenType {

  EQ_OP(MyCharacter.EQUAL, MyCharacter.EQUAL), NE_OP(MyCharacter.EXCLAMATION, MyCharacter.EQUAL);

  private CharacterTokenType[] characters;
  private String value = "";

  private MyCharacterComposite(MyCharacter... characters) {
    this.characters = characters;
    for (CharacterTokenType character : characters) {
      value = value + character.getChar();
    }
  }

  public CharacterTokenType[] getCharacters() {
    return characters;
  }

  public String getName() {
    return name();
  }

  public String getValue() {
    return value;
  }

  public boolean hasToBeSkippedFromAst() {
    return false;
  }

}
