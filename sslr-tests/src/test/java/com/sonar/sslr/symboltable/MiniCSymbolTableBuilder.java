/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

import com.google.common.collect.Maps;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.AstVisitor;
import com.sonar.sslr.impl.ast.AstWalker;
import com.sonar.sslr.test.miniC.MiniCGrammar;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;

public class MiniCSymbolTableBuilder {

  private static Scope currentScope;
  private static IdentityHashMap<AstNode, Scope> astToScope = Maps.newIdentityHashMap();
  private static IdentityHashMap<AstNode, Symbol> astToSymbol = Maps.newIdentityHashMap();

  private static abstract class AbstractAstVisitor implements AstVisitor {
    private AstNodeType[] nodeTypes;

    public AbstractAstVisitor(AstNodeType... nodeTypes) {
      this.nodeTypes = nodeTypes;
    }

    public List<AstNodeType> getAstNodeTypesToVisit() {
      return Arrays.asList(nodeTypes);
    }

    public void visitFile(AstNode ast) {
      // nop
    }

    public void leaveFile(AstNode ast) {
      // nop
    }

    public void visitNode(AstNode ast) {
      // nop
    }

    public void leaveNode(AstNode ast) {
      // nop
    }

    protected void setCurrentScope(Scope scope) {
      currentScope = scope;
    }

    protected Scope getCurrentScope() {
      return currentScope;
    }
  }

  private static class CompoundStatementVisitor extends AbstractAstVisitor {
    public CompoundStatementVisitor(AstNodeType nodeType) {
      super(nodeType);
    }

    public void visitNode(AstNode ast) {
      setCurrentScope(new LocalScope(ast, getCurrentScope()));
      astToScope.put(ast, getCurrentScope());
    }

    public void leaveNode(AstNode ast) {
      setCurrentScope(getCurrentScope().getEnclosingScope());
    }
  }

  private static class FunctionDefinitionVisitor extends AbstractAstVisitor {
    public FunctionDefinitionVisitor(AstNodeType nodeType) {
      super(nodeType);
    }

    public void visitNode(AstNode ast) {
      String name = ast.getChild(1).getTokenValue();
      MethodSymbol methodSymbol = new MethodSymbol(ast, getCurrentScope(), name);
      getCurrentScope().define(methodSymbol);
      astToSymbol.put(ast, methodSymbol);
      setCurrentScope(methodSymbol);
      astToScope.put(ast, getCurrentScope());
    }

    public void leaveNode(AstNode ast) {
      setCurrentScope(getCurrentScope().getEnclosingScope());
    }
  }

  private static class VariableDefinitionVisitor extends AbstractAstVisitor {
    public VariableDefinitionVisitor(AstNodeType... nodeType) {
      super(nodeType);
    }

    public void visitNode(AstNode ast) {
      String name = ast.getChild(1).getTokenValue();
      VariableSymbol variableSymbol = new VariableSymbol(ast, name);
      getCurrentScope().define(variableSymbol);
      astToSymbol.put(ast, variableSymbol);
    }
  }

  private static class TypeVisitor extends AbstractAstVisitor {
    public TypeVisitor(AstNodeType nodeType) {
      super(nodeType);
    }

    public void visitNode(AstNode ast) {
      Symbol type = resolveType(ast);
      // TODO set resolved type for enclosing symbol
      Symbol enclosingSymbol = astToSymbol.get(ast.getParent());
      System.out.println("line " + ast.getTokenLine() + ": " + enclosingSymbol + " of " + type);
    }
  }

  private static Symbol resolveType(AstNode ast) {
    // Resolution is straightforward for MiniC, because there is no support for custom types such as classes
    String typeName = ast.getTokenValue();
    for (Symbol symbol : globalScope.getMembers()) {
      if (typeName.equals(symbol.getName())) {
        return symbol;
      }
    }
    return null;
  }

  private static class ReferenceVisitor extends AbstractAstVisitor {
    public ReferenceVisitor(AstNodeType... nodeTypes) {
      super(nodeTypes);
    }

    @Override
    public void visitNode(AstNode ast) {
      Scope enclosingScope = findEnclosingScope(ast);
      String referencedName = ast.getTokenValue();
      Symbol referencedSymbol = resolve(enclosingScope, referencedName);
      System.out.println("line " + ast.getTokenLine() + ": usage of " + referencedSymbol);
    }
  }

  private static Scope findEnclosingScope(AstNode ast) {
    Scope result = null;
    while (result == null) {
      result = astToScope.get(ast);
      if (ast != null) {
        ast = ast.getParent();
      }
    }
    return result;
  }

  private static Symbol resolve(Scope scope, String symbolName) {
    while (true) {
      Symbol result = resolveMember(scope, symbolName);
      if (result != null) {
        return result;
      }
      scope = scope.getEnclosingScope();
      if (scope == null) {
        return null;
      }
    }
  }

  private static Symbol resolveMember(Scope scope, String symbolName) {
    for (Symbol member : scope.getMembers()) {
      if (symbolName.equals(member.getName())) {
        return member;
      }
    }
    return null;
  }

  private final CompoundStatementVisitor compoundStatementVisitor;
  private final FunctionDefinitionVisitor functionDefinitionVisitor;
  private final VariableDefinitionVisitor variableDefinitionVisitor;
  private final TypeVisitor binTypeVisitor;
  private final ReferenceVisitor referenceVisitor;

  private static final GlobalScope globalScope;

  static {
    // Build global scope with built-in-types, it should be reusable - no need to build it for each compilation unit
    globalScope = new GlobalScope();
    globalScope.define(new BuiltInType("int"));
    globalScope.define(new BuiltInType("void"));
  }

  public MiniCSymbolTableBuilder(MiniCGrammar grammar) {
    compoundStatementVisitor = new CompoundStatementVisitor(grammar.compoundStatement);
    functionDefinitionVisitor = new FunctionDefinitionVisitor(grammar.functionDefinition);
    variableDefinitionVisitor = new VariableDefinitionVisitor(
        grammar.variableDefinition,
        grammar.parameterDeclaration
        );
    binTypeVisitor = new TypeVisitor(grammar.binType);
    referenceVisitor = new ReferenceVisitor(grammar.binFunctionReference, grammar.binVariableReference);
  }

  /**
   * Builds tree of scopes and populates it with definitions.
   */
  public Scope buildScopesTree(AstNode ast) {
    currentScope = new LocalScope(ast);
    currentScope.importScope(globalScope);
    astToScope.put(null, currentScope);
    AstWalker walker = new AstWalker(compoundStatementVisitor, functionDefinitionVisitor, variableDefinitionVisitor);
    walker.walkAndVisit(ast);
    return currentScope;
  }

  public void resolveReferences(AstNode ast) {
    AstWalker walker = new AstWalker(binTypeVisitor, referenceVisitor);
    walker.walkAndVisit(ast);
  }

}
