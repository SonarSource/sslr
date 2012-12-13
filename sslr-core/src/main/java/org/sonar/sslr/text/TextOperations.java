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
package org.sonar.sslr.text;

/**
 * Common text operations to the {@link Text}, {@link TextCursor} and {@link TextLine} interfaces.
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 *
 * @since 1.17
 */
public interface TextOperations extends CharSequence {

  Text subSequence(int start, int end);

  boolean startsWith(CharSequence charSequence);

  boolean isEmpty();

}
