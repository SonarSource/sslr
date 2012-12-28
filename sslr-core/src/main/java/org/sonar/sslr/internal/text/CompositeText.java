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
package org.sonar.sslr.internal.text;

import org.sonar.sslr.text.Text;
import org.sonar.sslr.text.TextCursor;
import org.sonar.sslr.text.TextLocation;

import java.util.List;

public class CompositeText extends AbstractText {

  private final int transformationDepth;
  private final AbstractText[] texts;
  private final int length;

  public CompositeText(List<AbstractText> texts) {
    this.texts = texts.toArray(new AbstractText[texts.size()]);
    int len = 0;
    int depth = 0;
    for (AbstractText text : this.texts) {
      len += text.length();
      depth = Math.max(depth, text.getTransformationDepth());
    }
    this.length = len;
    this.transformationDepth = depth;
  }

  public int length() {
    return length;
  }

  public char[] toChars() {
    int len = 0;
    char[] result = new char[length];
    for (int i = 0; i < texts.length; i++) {
      System.arraycopy(texts[i].toChars(), 0, result, len, texts[i].length());
      len += texts[i].length();
    }
    return result;
  }

  @Override
  protected int getTransformationDepth() {
    return transformationDepth;
  }

  public TextCursor cursor() {
    return new CompositeTextCursor();
  }

  private class CompositeTextCursor implements TextCursor {

    private int skipped = 0;
    private int index = 0;
    private int textIndex = 0;
    private TextCursor innerCursor = texts[textIndex].cursor();

    public Text getText() {
      return CompositeText.this;
    }

    public int length() {
      return length;
    }

    private int getInnerIndex(int index) {
      return index - skipped;
    }

    public char charAt(int index) {
      moveTo(index);
      return innerCursor.charAt(getInnerIndex(index));
    }

    public TextCursor subSequence(int start, int end) {
      return subText(start, end).cursor();
    }

    public Text subText(int start, int end) {
      // TODO can be optimized for regions, which does not span multiple texts
      return new SubText(CompositeText.this, start, end);
    }

    public TextLocation getLocation(int index) {
      moveTo(index);
      return innerCursor.getLocation(getInnerIndex(index));
    }

    private void moveTo(int index) {
      if (this.index == index) {
        return;
      }
      if (!(skipped <= index && index < skipped + texts[textIndex].length())) {
        if (index > this.index) {
          while (skipped + texts[textIndex].length() <= index) {
            skipped += texts[textIndex].length();
            textIndex++;
          }
        } else {
          while (index < skipped) {
            skipped -= texts[textIndex].length();
            textIndex--;
          }
        }
        innerCursor = texts[textIndex].cursor();
      }
      this.index = index;
    }

    @Override
    public String toString() {
      // contract of CharSequence
      return getText().toString();
    }

  }

}
