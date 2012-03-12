/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.test.miniC;

import org.sonar.colorizer.CDocTokenizer;
import org.sonar.colorizer.CppDocTokenizer;
import org.sonar.colorizer.KeywordsTokenizer;
import org.sonar.colorizer.Tokenizer;

import java.util.Arrays;
import java.util.List;

public final class MiniCColorizer {

  private MiniCColorizer() {
  }

  public static List<Tokenizer> getTokenizers() {
    return Arrays.asList(
        new CDocTokenizer("<span class=\"cd\">", "</span>"),
        new CppDocTokenizer("<span class=\"cppd\">", "</span>"),
        new KeywordsTokenizer("<span class=\"k\">", "</span>", MiniCLexer.Keywords.keywordValues())
        );
  }

}
