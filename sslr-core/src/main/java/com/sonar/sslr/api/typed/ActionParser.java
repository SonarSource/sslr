/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonar.sslr.api.typed;

import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.impl.typed.GrammarBuilderInterceptor;
import com.sonar.sslr.impl.typed.ReflectionUtils;
import com.sonar.sslr.impl.typed.SyntaxTreeCreator;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;
import org.sonar.sslr.internal.matchers.InputBuffer;
import org.sonar.sslr.parser.ParseError;
import org.sonar.sslr.parser.ParseErrorFormatter;
import org.sonar.sslr.parser.ParseRunner;
import org.sonar.sslr.parser.ParsingResult;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @since 1.21
 */
public class ActionParser<N> {

  private final Charset charset;

  private final SyntaxTreeCreator<N> syntaxTreeCreator;
  private final GrammarRuleKey rootRule;
  private final ParseRunner parseRunner;

  public ActionParser(Charset charset, LexerlessGrammarBuilder b, Class grammarClass, Object treeFactory, NodeBuilder nodeBuilder, GrammarRuleKey rootRule) {
    this.charset = charset;

    GrammarBuilderInterceptor grammarBuilderInterceptor = new GrammarBuilderInterceptor(b);
    Enhancer grammarEnhancer = new Enhancer();
    grammarEnhancer.setSuperclass(grammarClass);
    grammarEnhancer.setCallback(grammarBuilderInterceptor);

    ActionMethodInterceptor actionMethodInterceptor = new ActionMethodInterceptor(grammarBuilderInterceptor);
    Enhancer actionEnhancer = new Enhancer();
    actionEnhancer.setSuperclass(treeFactory.getClass());
    actionEnhancer.setCallback(actionMethodInterceptor);

    Object grammar = grammarEnhancer.create(
      new Class[] {GrammarBuilder.class, treeFactory.getClass()},
      new Object[] {grammarBuilderInterceptor, actionEnhancer.create()});

    for (Method method : grammarClass.getMethods()) {
      if (method.getDeclaringClass().equals(Object.class)) {
        continue;
      }

      ReflectionUtils.invokeMethod(method, grammar);
    }

    this.syntaxTreeCreator = new SyntaxTreeCreator<>(treeFactory, grammarBuilderInterceptor, nodeBuilder);

    b.setRootRule(rootRule);
    this.rootRule = rootRule;
    this.parseRunner = new ParseRunner(b.build().getRootRule());
  }

  public N parse(File file) {
    try {
      char[] chars = new String(Files.readAllBytes(Paths.get(file.getPath())), charset).toCharArray();
      return parse(new Input(chars, file.toURI()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public N parse(String source) {
    return parse(new Input(source.toCharArray()));
  }

  private N parse(Input input) {
    ParsingResult result = parseRunner.parse(input.input());

    if (!result.isMatched()) {
      ParseError parseError = result.getParseError();
      InputBuffer inputBuffer = parseError.getInputBuffer();
      int line = inputBuffer.getPosition(parseError.getErrorIndex()).getLine();
      String message = new ParseErrorFormatter().format(parseError);
      throw new RecognitionException(line, message);
    }

    return syntaxTreeCreator.create(result.getParseTreeRoot(), input);
  }

  public GrammarRuleKey rootRule() {
    return rootRule;
  }

  private static class ActionMethodInterceptor implements MethodInterceptor {

    private final GrammarBuilderInterceptor grammarBuilderInterceptor;

    public ActionMethodInterceptor(GrammarBuilderInterceptor grammarBuilderInterceptor) {
      this.grammarBuilderInterceptor = grammarBuilderInterceptor;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
      if (method.getDeclaringClass().equals(Object.class)) {
        return proxy.invokeSuper(obj, args);
      }

      grammarBuilderInterceptor.addAction(method, args.length);

      return null;
    }

  }

}
