/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.sonar.graph.DirectedGraph;

import com.sonar.sslr.api.AstNode;

public class ControlFlowGraph {

  private DirectedGraph<Block, Path> graph = new DirectedGraph<Block, Path>();
  private Map<AstNode, Block> statments = new HashMap<AstNode, Block>();

  private Block entryBlock;

  public void setEntryBlock(Block block) {
    this.entryBlock = block;
  }

  public Block getEntryBlock() {
    return entryBlock;
  }

  public void addPath(Path path) {
    graph.addEdge(path);
  }

  public Collection<Path> getOutgoingPaths(Block from) {
    return graph.getOutgoingEdges(from);
  }

  public Collection<Path> getIncomingPaths(Block to) {
    return graph.getIncomingEdges(to);
  }

  public Block createBlock(AstNode firstStatement) {
    Block block = new Block(firstStatement);
    graph.addVertex(block);
    statments.put(firstStatement, block);
    return block;
  }

  public Set<Block> getBlocks() {
    return graph.getVertices();
  }

  public Block getBlockStartingAt(AstNode statement) {
    Block requestedBlock = new Block(statement);
    if (graph.getVertices().contains(requestedBlock)) {
      for (Block block : graph.getVertices()) {
        if (block.equals(requestedBlock)) {
          return block;
        }
      }
    }
    return null;
  }
}
