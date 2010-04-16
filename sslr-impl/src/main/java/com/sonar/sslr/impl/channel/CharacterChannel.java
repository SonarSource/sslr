/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import java.util.HashMap;

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.CharacterTokenType;
import com.sonar.sslr.impl.LexerOutput;

public class CharacterChannel implements Channel<LexerOutput> {

  HashMap<Character, CharacterTokenType> specialChars;

  public CharacterChannel(CharacterTokenType... characters) {
    specialChars = new HashMap<Character, CharacterTokenType>();
    for (CharacterTokenType specialChar : characters) {
      specialChars.put(Character.valueOf(specialChar.getChar()), specialChar);
    }
  }

  public boolean consum(CodeReader code, LexerOutput output) {
    Character nextChar = Character.valueOf((char) code.peek());
    if (specialChars.containsKey(nextChar)) {
      CharacterTokenType specialChar = specialChars.get(nextChar);
      code.pop();
      output.addToken(specialChar, specialChar.getValue(), code.getLinePosition(), code.getColumnPosition());
      return true;
    }
    return false;
  }
}
