/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.preprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sonar.sslr.api.PunctuatorCompositeTokenType;
import com.sonar.sslr.api.PunctuatorTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.LexerOutput;
import com.sonar.sslr.impl.Preprocessor;

public class CharacterCompositeProprocessor extends Preprocessor {

  private Map<String, PunctuatorCompositeTokenType> maps = new HashMap<String, PunctuatorCompositeTokenType>();

  private List<Token> pendingComposite = new ArrayList<Token>();

  public CharacterCompositeProprocessor(PunctuatorCompositeTokenType... characterComposites) {
    for (PunctuatorCompositeTokenType composite : characterComposites) {
      maps.put(composite.getValue(), composite);
    }
  }

  @Override
  public boolean process(Token token, LexerOutput output) {
    if (token.getType() instanceof PunctuatorTokenType && !token.getValue().equals(",")) {
      pendingComposite.add(token);
    } else {
      endLexing(output);
    }
    return false;
  }

  public void endLexing(LexerOutput output) {
    if (pendingComposite.size() != 0) {
      String value = "";
      for (Token character : pendingComposite) {
        value = value + character.getValue();
      }
      if (maps.containsKey(value)) {
        Token firstCharacter = pendingComposite.get(0);
        PunctuatorCompositeTokenType tokenType = maps.get(value);
        Token composite = new Token(tokenType, value, firstCharacter.getLine(), firstCharacter.getColumn(), firstCharacter.getFile());
        output.removeLastTokens(pendingComposite.size());
        output.addToken(composite);
      } 
      pendingComposite.clear();
    }
  }
}
