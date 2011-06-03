/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.bytecode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;


public class Bytecode {

  private InstructionBlock mainBlock = new InstructionBlock();
  private Stack<ControlFlowInstruction> pendingControlFlowInstructions = new Stack<ControlFlowInstruction>();
  private Map<ControlFlowInstruction, InstructionBlock> controlFlowBlocks = new HashMap<ControlFlowInstruction, Bytecode.InstructionBlock>();

  public void execute() {
    try {
      execute(mainBlock);
    } catch (ExitFlow exitFlow) {
    }

  }

  private void execute(InstructionBlock block) {
    for (Instruction adapter : block) {
      if (adapter instanceof ExecutableInstruction) {
        ((ExecutableInstruction) adapter).execute();
      } else if (adapter instanceof ConditionalBlockInstruction) {
        ConditionalBlockInstruction conditionalBlockAdapter = (ConditionalBlockInstruction) adapter;
        if (conditionalBlockAdapter.shouldExecuteConditionalBlock()) {
          execute(controlFlowBlocks.get(conditionalBlockAdapter));
        }
      } else if (adapter instanceof LoopBlockInstruction) {
        LoopBlockInstruction loopBlockAdapter = (LoopBlockInstruction) adapter;
        loopBlockAdapter.initLoopState();
        while (loopBlockAdapter.shouldExecuteLoopBlockIteration()) {
          execute(controlFlowBlocks.get(loopBlockAdapter));
        }
      } else if (adapter instanceof ExitFlowInstruction) {
        throw new ExitFlow();
      }
    }
  }

  public void addInstruction(Object adapter) {
    if (adapter instanceof Instruction) {
      if (pendingControlFlowInstructions.empty()) {
        mainBlock.add((Instruction) adapter);
      } else {
        controlFlowBlocks.get(pendingControlFlowInstructions.peek()).add((Instruction) adapter);
      }
    }
  }

  public void startControlFlowInstruction(Object adapter) {
    if (adapter instanceof ControlFlowInstruction) {
      ControlFlowInstruction controlFlowAdapter = (ControlFlowInstruction) adapter;
      pendingControlFlowInstructions.push(controlFlowAdapter);
      controlFlowBlocks.put(controlFlowAdapter, new InstructionBlock());
    }
  }

  public void endControlFlowInstruction(Object controlFlowAdapter) {
    if (controlFlowAdapter instanceof ControlFlowInstruction) {
      pendingControlFlowInstructions.pop();
    }
  }

  private class InstructionBlock implements Iterable<Instruction> {

    private List<Instruction> instructions = new ArrayList<Instruction>();

    public Iterator<Instruction> iterator() {
      return instructions.iterator();
    }

    public void add(Instruction adapter) {
      instructions.add(adapter);
    }
  }

  private class ExitFlow extends RuntimeException {
  }
}
