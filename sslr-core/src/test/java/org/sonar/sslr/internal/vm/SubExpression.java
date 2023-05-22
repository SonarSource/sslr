/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2023 SonarSource SA
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

/**
 * For unit tests.
 */
public class SubExpression implements ParsingExpression {

  private final int[] ids;

  public SubExpression(int... ids) {
    this.ids = ids;
  }

  @Override
  public Instruction[] compile(CompilationHandler compiler) {
    Instruction[] result = new Instruction[ids.length];
    for (int i = 0; i < ids.length; i++) {
      result[i] = mockInstruction(ids[i]);
    }
    return result;
  }

  public static Instruction mockInstruction(int id) {
    return new MockInstruction(id);
  }

  private static class MockInstruction extends Instruction {
    private final int id;

    public MockInstruction(int id) {
      this.id = id;
    }

    @Override
    public String toString() {
      return "Mock " + id;
    }

    @Override
    public void execute(Machine machine) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj) {
      return (obj instanceof MockInstruction) && (this.id == ((MockInstruction) obj).id);
    }

    @Override
    public int hashCode() {
      return id;
    }
  }

  @Override
  public String toString() {
    return "SubExpression";
  }

}
