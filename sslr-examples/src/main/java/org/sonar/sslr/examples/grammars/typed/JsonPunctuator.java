package org.sonar.sslr.examples.grammars.typed;

import org.sonar.sslr.grammar.GrammarRuleKey;

enum JsonPunctuator implements GrammarRuleKey {

  LCURLYBRACE("{"),
  RCURLYBRACE("}"),
  LBRACKET("["),
  RBRACKET("]"),
  COMMA(","),
  COLON(":");

  private final String value;

  JsonPunctuator(String word) {
    this.value = word;
  }

  public String getName() {
    return name();
  }

  public String getValue() {
    return value;
  }

}
