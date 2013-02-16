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
package org.sonar.sslr.internal.vm;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.matchers.GrammarElementMatcher;
import org.sonar.sslr.internal.matchers.ImmutableInputBuffer;
import org.sonar.sslr.internal.matchers.InputBuffer;
import org.sonar.sslr.internal.matchers.Matcher;
import org.sonar.sslr.internal.matchers.MatcherPathElement;
import org.sonar.sslr.internal.matchers.ParseNode;
import org.sonar.sslr.parser.ParseError;
import org.sonar.sslr.parser.ParsingResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Machine implements CharSequence {

  private final char[] input;

  private MachineStack stack;
  private int index;
  private int address;
  private boolean matched = true;

  private final ParseNode[] memos;

  // Number of instructions in grammar for Java is about 2000.
  private final int[] calls;

  private final MachineHandler handler;

  private boolean ignoreErrors = false;

  public static ParsingResult parse(String input, CompiledGrammar grammar, GrammarRuleKey ruleKey) {
    Instruction[] instructions = grammar.getInstructions();

    ErrorLocatingHandler errorLocatingHandler = new ErrorLocatingHandler();
    Machine machine = new Machine(input, instructions, errorLocatingHandler);
    machine.execute(grammar.getMatcher(ruleKey), grammar.getOffset(ruleKey), instructions);

    if (machine.matched) {
      return new ParsingResult(
          new ImmutableInputBuffer(machine.input),
          machine.matched,
          // TODO what if there is no nodes, or more than one?
          machine.stack.subNodes.get(0),
          null);
    } else {
      // Perform second run in order to collect information for error report
      ErrorReportingHandler errorReportingHandler = new ErrorReportingHandler(errorLocatingHandler.getErrorIndex());
      machine = new Machine(input, instructions, errorReportingHandler);
      machine.execute(grammar.getMatcher(ruleKey), grammar.getOffset(ruleKey), instructions);

      // failure should be permanent, otherwise something generally wrong
      Preconditions.checkState(!machine.matched);

      StringBuilder sb = new StringBuilder("failed to match");
      if (errorReportingHandler.getFailedPaths().size() > 1) {
        sb.append(" all of");
      }
      sb.append(':');
      for (List<MatcherPathElement> failedPath : errorReportingHandler.getFailedPaths()) {
        Matcher failedMatcher = Iterables.getLast(failedPath).getMatcher();
        sb.append(' ').append(((GrammarElementMatcher) failedMatcher).getName());
      }
      InputBuffer inputBuffer = new ImmutableInputBuffer(machine.input);
      ParseError parseError = new ParseError(
          inputBuffer,
          errorLocatingHandler.getErrorIndex(),
          sb.toString(),
          errorReportingHandler.getFailedPaths());
      return new ParsingResult(inputBuffer, machine.matched, null, parseError);
    }
  }

  private void execute(Matcher matcher, int offset, Instruction[] instructions) {
    // Place first rule on top of stack
    push(-1);
    stack.matcher = matcher;
    jump(offset);

    execute(instructions);
  }

  @VisibleForTesting
  public static boolean execute(String input, Instruction[] instructions) {
    Machine machine = new Machine(input, instructions);
    while (machine.address != -1 && machine.address < instructions.length) {
      instructions[machine.address].execute(machine);
    }
    return machine.matched;
  }

  public Machine(String input, Instruction[] instructions, MachineHandler handler) {
    this.input = input.toCharArray();
    this.handler = handler;
    this.memos = new ParseNode[this.input.length + 1];
    this.stack = new MachineStack(null);
    stack.child = new MachineStack(this.stack);
    stack = stack.child;
    stack.index = -1;
    calls = new int[instructions.length];
    Arrays.fill(calls, -1);
  }

  @VisibleForTesting
  public Machine(String input, Instruction[] instructions) {
    this(input, instructions, new MachineHandler() {
      public void onBacktrack(Machine machine) {
        // nop
      }
    });
  }

  private void execute(Instruction[] instructions) {
    while (address != -1) {
      instructions[address].execute(this);
    }
  }

  public int getAddress() {
    return address;
  }

  public void setAddress(int address) {
    this.address = address;
  }

  public void jump(int offset) {
    address += offset;
  }

  private void push(int address) {
    // reuse elements of stack
    if (stack.child == null) {
      stack.child = new MachineStack(stack);
    }
    stack = stack.child;
    stack.subNodes.clear();
    stack.address = address;
    stack.index = index;
    stack.ignoreErrors = ignoreErrors;
  }

  public void popReturn() {
    calls[stack.calledAddress] = stack.leftRecursion;
    stack = stack.parent;
  }

  public void pushReturn(int returnOffset, Matcher matcher, int callOffset) {
    ParseNode memo = memos[index];
    if (memo != null && memo.getMatcher() == matcher) {
      stack.parent.subNodes.add(memo);
      index = memo.getEndIndex();
      address += returnOffset;
    } else {
      push(address + returnOffset);
      stack.matcher = matcher;
      address += callOffset;

      if (calls[address] == index) {
        String ruleName = ((GrammarElementMatcher) matcher).getName();
        throw new GrammarException("Left recursion has been detected, involved rule: " + ruleName);
      }
      calls[address] = index;
      stack.calledAddress = address;
    }
  }

  public void pushBacktrack(int offset) {
    push(address + offset);
    stack.matcher = null;
  }

  public void pop() {
    stack = stack.parent;
  }

  public MachineStack peek() {
    return stack;
  }

  public void setIgnoreErrors(boolean ignoreErrors) {
    this.ignoreErrors = ignoreErrors;
  }

  public void backtrack() {
    if (!ignoreErrors) {
      handler.onBacktrack(this);
    }

    // pop any return addresses from the top of the stack
    while (stack.isReturn()) {
      popReturn();
    }

    if (stack.isEmpty()) {
      // input does not match
      address = -1;
      matched = false;
    } else {
      // restore state
      index = stack.index;
      address = stack.address;
      ignoreErrors = stack.ignoreErrors;
      stack = stack.parent;
    }
  }

  public void createNode() {
    ParseNode node = new ParseNode(stack.index, index, stack.subNodes, stack.matcher);
    stack.parent.subNodes.add(node);
    memos[stack.index] = node;
  }

  public void createLeafNode(Matcher matcher) {
    ParseNode node = new ParseNode(stack.index, index, Collections.EMPTY_LIST, matcher);
    stack.subNodes.add(node);
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public void advanceIndex(int offset) {
    index += offset;
  }

  public int length() {
    return input.length - index;
  }

  public char charAt(int offset) {
    return input[index + offset];
  }

  /**
   * Not supported.
   *
   * @throws UnsupportedOperationException always
   */
  public CharSequence subSequence(int start, int end) {
    throw new UnsupportedOperationException();
  }

}
