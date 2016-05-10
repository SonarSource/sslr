/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
