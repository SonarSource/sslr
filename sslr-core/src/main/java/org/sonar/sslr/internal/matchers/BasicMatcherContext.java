/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
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
package org.sonar.sslr.internal.matchers;

import com.google.common.collect.Lists;

import java.util.List;

public class BasicMatcherContext extends MatcherContext {

  private BasicMatcherContext parent;
  private BasicMatcherContext subContext;

  private ParseNode node;
  private final List<ParseNode> subNodes = Lists.newArrayList();

  public BasicMatcherContext(char[] input, MatchHandler matchHandler, Matcher matcher) {
    super(input, matchHandler);
    this.parent = null;
    this.matcher = matcher;
  }

  public BasicMatcherContext(BasicMatcherContext parent) {
    super(parent.input, parent.getMatchHandler());
    this.parent = parent;
  }

  public MatcherContext getSubContext(Matcher matcher) {
    // No need to create new subContext, when can be reused:
    if (subContext == null) {
      subContext = new BasicMatcherContext(this);
    }
    subContext.input = input;
    subContext.matcher = matcher;
    subContext.startIndex = currentIndex;
    subContext.currentIndex = currentIndex;
    subContext.node = null;
    return subContext;
  }

  @Override
  public void retire() {
    super.retire();
    // For performance reasons - reuse list, instead of re-creation,
    // but GC should be able to eliminate elements:
    subNodes.clear();
  }

  public boolean runMatcher() {
    try {
      if (matcher.match(this)) {
        if (parent != null) {
          parent.currentIndex = currentIndex;
        }
        retire();
        return true;
      }
      retire();
      return false;
    } catch (ParserRuntimeException e) {
      // propagate as-is
      throw e;
    } catch (Throwable e) {
      // TODO Godin: here we know context, where exception occurred,
      // and it can be attached to exception in order to improve exception handling
      throw new ParserRuntimeException(e);
    }
  }

  public void createNode() {
    // new node for parse tree
    node = new ParseNode(startIndex, currentIndex, subNodes, matcher);
    if (parent != null) {
      parent.subNodes.add(node);
    }
  }

  @Override
  public void createNode(ParseNode node) {
    this.node = node;
    if (parent != null) {
      parent.subNodes.add(node);
    }
  }

  public void skipNode() {
    // node skipped - contribute all children to parent
    parent.subNodes.addAll(subNodes);
  }

  public ParseNode getNode() {
    return node;
  }

}
