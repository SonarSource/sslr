/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
package org.sonar.sslr.internal.vm;

import org.sonar.sslr.internal.matchers.MatcherPathElement;

import java.util.ArrayList;
import java.util.List;

public class ErrorTreeNode {

  public MatcherPathElement pathElement;
  public List<ErrorTreeNode> children = new ArrayList<>();

  public static ErrorTreeNode buildTree(List<List<MatcherPathElement>> paths) {
    ErrorTreeNode root = new ErrorTreeNode();
    root.pathElement = paths.get(0).get(0);
    for (List<MatcherPathElement> path : paths) {
      addToTree(root, path);
    }
    return root;
  }

  private static void addToTree(ErrorTreeNode root, List<MatcherPathElement> path) {
    ErrorTreeNode current = root;
    int i = 1;
    boolean found = true;
    while (found && i < path.size()) {
      found = false;
      for (ErrorTreeNode child : current.children) {
        if (child.pathElement.equals(path.get(i))) {
          current = child;
          i++;
          found = true;
          break;
        }
      }
    }
    while (i < path.size()) {
      ErrorTreeNode child = new ErrorTreeNode();
      child.pathElement = path.get(i);
      current.children.add(child);
      current = child;
      i++;
    }
  }

}
