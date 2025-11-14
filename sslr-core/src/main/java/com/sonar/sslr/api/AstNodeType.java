/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.api;

/**
 * Each AST Node has a type which can be for instance the Rule of a Grammar, a language keyword or a language special token like a WORD.
 * When implementing the AstVisitor interface, its necessary to subscribe to a set of AstNodeType to be notified each time an AstNode with
 * the desired type is visited.
 * 
 * 
 * @see AstVisitor
 * @see Grammar
 * @see AstNode
 */
public interface AstNodeType {
}
