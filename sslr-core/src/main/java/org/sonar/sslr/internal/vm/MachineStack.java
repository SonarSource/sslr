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
package org.sonar.sslr.internal.vm;

import com.google.common.collect.Lists;
import org.sonar.sslr.internal.matchers.Matcher;
import org.sonar.sslr.internal.matchers.ParseNode;

import java.util.List;

public class MachineStack {

  public final MachineStack parent;
  public MachineStack child;

  public final List<ParseNode> subNodes;
  public int address;
  public int index;
  public boolean ignoreErrors;
  public Matcher matcher;

  public int leftRecursion;
  public int calledAddress;

  public MachineStack(MachineStack parent) {
    this.parent = parent;
    this.subNodes = Lists.newArrayList();
  }

  public boolean isReturn() {
    return matcher != null;
  }

  /**
   * @return true, if this object denotes an empty stack
   */
  public boolean isEmpty() {
    return index == -1;
  }

  public int getAddress() {
    return address;
  }

  public int getIndex() {
    return index;
  }

  public boolean isIgnoreErrors() {
    return ignoreErrors;
  }

}
