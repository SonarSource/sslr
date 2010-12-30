/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sonar.graph.DirectedGraph;

import com.sonar.sslr.api.AstNode;

public class ControlFlowGraph {

  private DirectedGraph<Block, Path> graph = new DirectedGraph<Block, Path>();
  private Map<AstNode, Block> statments = new HashMap<AstNode, Block>();

  private Set<Block> entryBlocks = new HashSet<Block>();

  public void addEntryBlock(Block block) {
    entryBlocks.add(block);
  }

  public Set<Block> getEntryBlocks() {
    return entryBlocks;
  }

  public void addPath(Path path) {
    graph.addEdge(path);
  }

  public boolean hasPath(Block from, Block to) {
    return graph.hasEdge(from, to);
  }

  public Collection<Path> getOutgoingPaths(Block from) {
    return graph.getOutgoingEdges(from);
  }

  public Collection<Path> getIncomingPaths(Block to) {
    return graph.getIncomingEdges(to);
  }

  public Block createBlock(AstNode firstStmt) {
    if (oneBlockContains(firstStmt)) {
      throw new IllegalStateException("One block already contains the statement : " + firstStmt);
    }
    Block block = new Block(firstStmt);
    graph.addVertex(block);
    statments.put(firstStmt, block);
    return block;
  }

  public Block getBlockContaining(AstNode stmt) {
    return statments.get(stmt);
  }

  public boolean oneBlockContains(AstNode stmt) {
    return statments.containsKey(stmt);
  }

  public void addStatement(Block block, AstNode stmt) {
    if (oneBlockContains(stmt)) {
      throw new IllegalStateException("One block already contains the statement : " + stmt);
    }
    statments.put(stmt, block);
    block.addStatement(stmt);
  }

  public Set<Block> getBlocks() {
    return graph.getVertices();
  }
}
