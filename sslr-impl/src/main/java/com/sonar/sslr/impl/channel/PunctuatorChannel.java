/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import java.util.HashMap;

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.PunctuatorTokenType;
import com.sonar.sslr.impl.LexerOutput;

public class PunctuatorChannel implements Channel<LexerOutput> {

  HashMap<Character, PunctuatorTokenType> specialChars;

  public PunctuatorChannel(PunctuatorTokenType... characters) {
    specialChars = new HashMap<Character, PunctuatorTokenType>();
    for (PunctuatorTokenType specialChar : characters) {
      specialChars.put(Character.valueOf(specialChar.getChar()), specialChar);
    }
  }

  public boolean consum(CodeReader code, LexerOutput output) {
    Character nextChar = Character.valueOf((char) code.peek());
    if (specialChars.containsKey(nextChar)) {
      PunctuatorTokenType specialChar = specialChars.get(nextChar);
      code.pop();
      output.addToken(specialChar, specialChar.getValue(), code.getLinePosition(), code.getColumnPosition());
      return true;
    }
    return false;
  }
}
