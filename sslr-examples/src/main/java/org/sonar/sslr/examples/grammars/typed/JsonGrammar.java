package org.sonar.sslr.examples.grammars.typed;

import com.sonar.sslr.api.typed.GrammarBuilder;
import org.sonar.sslr.examples.grammars.typed.api.ArrayTree;
import org.sonar.sslr.examples.grammars.typed.api.BuiltInValueTree;
import org.sonar.sslr.examples.grammars.typed.api.JsonTree;
import org.sonar.sslr.examples.grammars.typed.api.LiteralTree;
import org.sonar.sslr.examples.grammars.typed.api.ObjectTree;
import org.sonar.sslr.examples.grammars.typed.api.PairTree;
import org.sonar.sslr.examples.grammars.typed.api.ValueTree;
import org.sonar.sslr.examples.grammars.typed.impl.InternalSyntaxToken;
import org.sonar.sslr.examples.grammars.typed.impl.SyntaxList;

public class JsonGrammar {

  private final GrammarBuilder<InternalSyntaxToken> b;
  private final TreeFactory f;

  public JsonGrammar(GrammarBuilder<InternalSyntaxToken> b, TreeFactory f) {
    this.b = b;
    this.f = f;
  }

  public JsonTree JSON() {
    return b.<JsonTree>nonterminal(JsonLexer.JSON).is(
      f.json(
        b.firstOf(
          ARRAY(),
          OBJECT()),
        b.token(JsonLexer.EOF)));
  }

  public ObjectTree OBJECT() {
    return b.<ObjectTree>nonterminal(JsonLexer.OBJECT).is(
      f.object(
        b.token(JsonPunctuator.LCURLYBRACE),
        b.optional(PAIR_LIST()),
        b.token(JsonPunctuator.RCURLYBRACE)));
  }

  public SyntaxList<PairTree> PAIR_LIST() {
    return b.<SyntaxList<PairTree>>nonterminal().is(
      b.firstOf(
        f.pairList(PAIR(), b.token(JsonPunctuator.COMMA), PAIR_LIST()),
        f.pairList(PAIR())));
  }

  public PairTree PAIR() {
    return b.<PairTree>nonterminal().is(
      f.pair(STRING(), b.token(JsonPunctuator.COLON), VALUE()));
  }

  public ArrayTree ARRAY() {
    return b.<ArrayTree>nonterminal(JsonLexer.ARRAY).is(
      f.array(
        b.token(JsonPunctuator.LBRACKET),
        b.optional(VALUE_LIST()),
        b.token(JsonPunctuator.RBRACKET)));
  }

  public SyntaxList<ValueTree> VALUE_LIST() {
    return b.<SyntaxList<ValueTree>>nonterminal().is(
      b.firstOf(
        f.valueList(VALUE(), b.token(JsonPunctuator.COMMA), VALUE_LIST()),
        f.valueList(VALUE())));
  }

  public ValueTree VALUE() {
    return b.<ValueTree>nonterminal(JsonLexer.VALUE).is(
      b.firstOf(
        STRING(),
        NUMBER(),
        OBJECT(),
        ARRAY(),
        BUILT_IN_VALUE()));
  }

  public LiteralTree STRING() {
    return b.<LiteralTree>nonterminal().is(
      f.string(b.token(JsonLexer.STRING)));
  }

  public LiteralTree NUMBER() {
    return b.<LiteralTree>nonterminal().is(
      f.number(b.token(JsonLexer.NUMBER)));
  }

  public BuiltInValueTree BUILT_IN_VALUE() {
    return b.<BuiltInValueTree>nonterminal().is(
      f.buildInValue(b.firstOf(
        b.token(JsonLexer.TRUE),
        b.token(JsonLexer.FALSE),
        b.token(JsonLexer.NULL))));
  }
}
