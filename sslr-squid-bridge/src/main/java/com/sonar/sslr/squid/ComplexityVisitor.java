/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import java.util.Collection;
import java.util.LinkedList;

import org.sonar.squid.measures.MetricDef;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.squid.SquidAstVisitor;

public class ComplexityVisitor<GRAMMAR extends Grammar> extends SquidAstVisitor<GRAMMAR> {
	
	private MetricDef metric;
	private LinkedList<AstNodeType> astNodeTypes = new LinkedList<AstNodeType>();
	
	public static <GRAMMAR extends Grammar> Builder<GRAMMAR> builder(MetricDef metric) {
		return new Builder<GRAMMAR>(metric);
	}
	
	public static class Builder<GRAMMAR extends Grammar> {
		
		private MetricDef metric;
		private LinkedList<AstNodeType> astNodeTypes = new LinkedList<AstNodeType>();
		
		private Builder(MetricDef metric) {
			this.metric = metric;
		}
		
		public Builder<GRAMMAR> withAstNodeType(AstNodeType astNodeType) {
			this.astNodeTypes.add(astNodeType);
			return this;
		}
		
		public Builder<GRAMMAR> withAstNodeTypes(AstNodeType... astNodeTypes) {
			this.astNodeTypes = new LinkedList<AstNodeType>();
			
			for (AstNodeType astNodeType : astNodeTypes) {
				this.astNodeTypes.add(astNodeType);
			}
			
			return this;
		}
		
		public Builder<GRAMMAR> withAstNodeTypes(Collection<AstNodeType> astNodeTypes) {
			this.astNodeTypes = new LinkedList<AstNodeType>(astNodeTypes);
			return this;
		}
		
		public ComplexityVisitor<GRAMMAR> build() {
			return new ComplexityVisitor<GRAMMAR>(this);
		}

	}
	
	private ComplexityVisitor() { }
	
	private ComplexityVisitor(Builder<GRAMMAR> builder) {
		this.metric = builder.metric;
		this.astNodeTypes = builder.astNodeTypes;
	}
	
  @Override
	public void init() {
  	for (AstNodeType astNodeType: astNodeTypes) {
			subscribeTo(astNodeType);
		}
	}

	@Override
	public void visitNode(AstNode astNode) {
		peekSourceCode().add(metric, 1);
	}
	
}
