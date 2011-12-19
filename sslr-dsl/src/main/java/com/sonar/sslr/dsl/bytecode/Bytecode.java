/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.bytecode;

import java.util.*;

import com.sonar.sslr.dsl.DslException;

public class Bytecode {

  private final InstructionBlock mainBlock = new InstructionBlock();

  private final Stack<ControlFlowInstruction> pendingControlFlowInstructions = new Stack<ControlFlowInstruction>();
  private final Map<ControlFlowInstruction, InstructionBlock> controlFlowBlocks = new HashMap<ControlFlowInstruction, Bytecode.InstructionBlock>();

  private final Stack<String> pendingProdecureDefinitions = new Stack<String>();
  private final Map<String, InstructionBlock> procedureBlocks = new HashMap<String, Bytecode.InstructionBlock>();

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
      } else if (adapter instanceof ProcedureCallInstruction) {
        ProcedureCallInstruction procedureCall = (ProcedureCallInstruction) adapter;
        execute(procedureBlocks.get(procedureCall.getProcedureNameToCall()));
      } else if (adapter instanceof ExitFlowInstruction) {
        throw new ExitFlow();
      }
    }
  }

  public void addInstruction(Object adapter) {
    if (adapter instanceof Instruction) {
      if (pendingControlFlowInstructions.empty() && pendingProdecureDefinitions.empty()) {
        mainBlock.add((Instruction) adapter);
      } else if ( !pendingControlFlowInstructions.empty()) {
        controlFlowBlocks.get(pendingControlFlowInstructions.peek()).add((Instruction) adapter);
      } else if ( !pendingProdecureDefinitions.empty()) {
        procedureBlocks.get(pendingProdecureDefinitions.peek()).add((Instruction) adapter);
      }
    }
  }

  public void startControlFlowInstruction(Object adapter) {
    if (adapter instanceof ControlFlowInstruction) {
      ControlFlowInstruction controlFlowAdapter = (ControlFlowInstruction) adapter;
      pendingControlFlowInstructions.push(controlFlowAdapter);
      controlFlowBlocks.put(controlFlowAdapter, new InstructionBlock());
    } else if (adapter instanceof ProcedureDefinition) {
      ProcedureDefinition procedureDefinition = (ProcedureDefinition) adapter;
      if ( !pendingControlFlowInstructions.empty()) {
        throw new DslException("It' forbidden to define the procedure '" + procedureDefinition.getProcedureName()
            + "' inside a control flow instruction.");
      }
      pendingProdecureDefinitions.push(procedureDefinition.getProcedureName());
      procedureBlocks.put(procedureDefinition.getProcedureName(), new InstructionBlock());
    }
  }

  public void endControlFlowInstruction(Object adapter) {
    if (adapter instanceof ControlFlowInstruction) {
      pendingControlFlowInstructions.pop();
    } else if (adapter instanceof ProcedureDefinition) {
      pendingProdecureDefinitions.pop();
    }
  }

  private static class InstructionBlock implements Iterable<Instruction> {

    private final List<Instruction> instructions = new ArrayList<Instruction>();

    public Iterator<Instruction> iterator() {
      return instructions.iterator();
    }

    public void add(Instruction adapter) {
      instructions.add(adapter);
    }
  }

  private static class ExitFlow extends RuntimeException {
  }
}
