/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.sslr.examples.grammars.typed;

import com.sonar.sslr.api.typed.Optional;
import org.sonar.sslr.examples.grammars.typed.api.ArrayTree;
import org.sonar.sslr.examples.grammars.typed.api.BuiltInValueTree;
import org.sonar.sslr.examples.grammars.typed.api.JsonTree;
import org.sonar.sslr.examples.grammars.typed.api.LiteralTree;
import org.sonar.sslr.examples.grammars.typed.api.ObjectTree;
import org.sonar.sslr.examples.grammars.typed.api.PairTree;
import org.sonar.sslr.examples.grammars.typed.api.ValueTree;
import org.sonar.sslr.examples.grammars.typed.impl.ArrayTreeImpl;
import org.sonar.sslr.examples.grammars.typed.impl.BuiltInValueTreeImpl;
import org.sonar.sslr.examples.grammars.typed.impl.InternalSyntaxToken;
import org.sonar.sslr.examples.grammars.typed.impl.JsonTreeImpl;
import org.sonar.sslr.examples.grammars.typed.impl.LiteralTreeImpl;
import org.sonar.sslr.examples.grammars.typed.impl.ObjectTreeImpl;
import org.sonar.sslr.examples.grammars.typed.impl.PairTreeImpl;
import org.sonar.sslr.examples.grammars.typed.impl.SyntaxList;

public class TreeFactory {

  public JsonTree json(Tree arrayOrObject, InternalSyntaxToken eof) {
    return new JsonTreeImpl(arrayOrObject);
  }

  public BuiltInValueTree buildInValue(InternalSyntaxToken token) {
    return new BuiltInValueTreeImpl(token);
  }

  public LiteralTree number(InternalSyntaxToken token) {
    return new LiteralTreeImpl(token);
  }

  public LiteralTree string(InternalSyntaxToken token) {
    return new LiteralTreeImpl(token);
  }

  public SyntaxList<ValueTree> valueList(ValueTree value) {
    return new SyntaxList<>(value, null, null);
  }

  public SyntaxList<ValueTree> valueList(ValueTree value, InternalSyntaxToken commaToken, SyntaxList<ValueTree> next) {
    return new SyntaxList<>(value, commaToken, next);
  }

  public ArrayTree array(InternalSyntaxToken openBracketToken, Optional<SyntaxList<ValueTree>> values, InternalSyntaxToken closeBracketToken) {
    return new ArrayTreeImpl(openBracketToken, values.orNull(), closeBracketToken);
  }

  public PairTree pair(LiteralTree string, InternalSyntaxToken colonToken, ValueTree value) {
    return new PairTreeImpl(string, colonToken, value);
  }

  public SyntaxList<PairTree> pairList(PairTree pair) {
    return new SyntaxList<>(pair, null, null);
  }


  public SyntaxList<PairTree> pairList(PairTree pair, InternalSyntaxToken commaToken, SyntaxList<PairTree> next) {
    return new SyntaxList<>(pair, commaToken, next);
  }

  public ObjectTree object(InternalSyntaxToken openCurlyBraceToken, Optional<SyntaxList<PairTree>> pairs, InternalSyntaxToken closeCurlyBraceToken) {
    return new ObjectTreeImpl(openCurlyBraceToken, pairs.orNull(), closeCurlyBraceToken);
  }
}
