/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.preprocessor;

import com.sonar.sslr.api.PunctuatorCompositeTokenType;
import com.sonar.sslr.api.PunctuatorTokenType;

public enum MyCharacterComposite implements PunctuatorCompositeTokenType {

  EQ_OP(MyCharacter.EQUAL, MyCharacter.EQUAL), NE_OP(MyCharacter.EXCLAMATION, MyCharacter.EQUAL);

  private PunctuatorTokenType[] characters;
  private String value = "";

  private MyCharacterComposite(MyCharacter... characters) {
    this.characters = characters;
    for (PunctuatorTokenType character : characters) {
      value = value + character.getChar();
    }
  }

  public PunctuatorTokenType[] getCharacters() {
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
