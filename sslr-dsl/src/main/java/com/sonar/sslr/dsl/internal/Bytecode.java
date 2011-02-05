/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.sonar.sslr.dsl.adapter.Adapter;
import com.sonar.sslr.dsl.adapter.ConditionalBlockAdapter;
import com.sonar.sslr.dsl.adapter.ControlFlowAdapter;
import com.sonar.sslr.dsl.adapter.ExecutableAdapter;
import com.sonar.sslr.dsl.adapter.ExitFlowAdapter;
import com.sonar.sslr.dsl.adapter.LoopBlockAdapter;

public class Bytecode {

  private AdapterBlock mainBlock = new AdapterBlock();
  private Stack<ControlFlowAdapter> pendingControlFlowAdapters = new Stack<ControlFlowAdapter>();
  private Map<ControlFlowAdapter, AdapterBlock> controlFlowBlocks = new HashMap<ControlFlowAdapter, Bytecode.AdapterBlock>();

  public void execute() {
    try {
      execute(mainBlock);
    } catch (ExitFlow exitFlow) {
    }

  }

  private void execute(AdapterBlock block) {
    for (Adapter adapter : block) {
      if (adapter instanceof ExecutableAdapter) {
        ((ExecutableAdapter) adapter).execute();
      } else if (adapter instanceof ConditionalBlockAdapter) {
        ConditionalBlockAdapter conditionalBlockAdapter = (ConditionalBlockAdapter) adapter;
        if (conditionalBlockAdapter.shouldExecuteConditionalBlock()) {
          execute(controlFlowBlocks.get(conditionalBlockAdapter));
        }
      } else if (adapter instanceof LoopBlockAdapter) {
        LoopBlockAdapter loopBlockAdapter = (LoopBlockAdapter) adapter;
        loopBlockAdapter.initLoopState();
        while (loopBlockAdapter.shouldExecuteLoopBlockIteration()) {
          execute(controlFlowBlocks.get(loopBlockAdapter));
        }
      } else if (adapter instanceof ExitFlowAdapter) {
        throw new ExitFlow();
      }
    }
  }

  public void addAdapter(Object adapter) {
    if (adapter instanceof Adapter) {
      if (pendingControlFlowAdapters.empty()) {
        mainBlock.add((Adapter) adapter);
      } else {
        controlFlowBlocks.get(pendingControlFlowAdapters.peek()).add((Adapter) adapter);
      }
    }
  }

  public void startControlFlowAdapter(Object adapter) {
    if (adapter instanceof ControlFlowAdapter) {
      ControlFlowAdapter controlFlowAdapter = (ControlFlowAdapter) adapter;
      pendingControlFlowAdapters.push(controlFlowAdapter);
      controlFlowBlocks.put(controlFlowAdapter, new AdapterBlock());
    }
  }

  public void endControlFlowAdapter(Object controlFlowAdapter) {
    if (controlFlowAdapter instanceof ControlFlowAdapter) {
      pendingControlFlowAdapters.pop();
    }
  }

  private class AdapterBlock implements Iterable<Adapter> {

    private List<Adapter> instructions = new ArrayList<Adapter>();

    public Iterator<Adapter> iterator() {
      return instructions.iterator();
    }

    public void add(Adapter adapter) {
      instructions.add(adapter);
    }
  }

  private class ExitFlow extends RuntimeException {
  }
}
