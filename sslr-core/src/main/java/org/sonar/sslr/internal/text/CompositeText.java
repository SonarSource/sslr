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
import org.sonar.sslr.text.TextCharSequence;
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

  @Override
  public int length() {
    return length;
  }

  @Override
  public void toCharArray(int srcPos, char[] dest, int destPos, int length) {
    CompositeTextCharSequence sequence = (CompositeTextCharSequence) sequence();
    sequence.moveTo(srcPos);

    int skipped = sequence.skipped;
    final int fromText = sequence.textIndex;
    final int toText;
    if (srcPos + length < this.length) {
      sequence.moveTo(srcPos + length);
      toText = sequence.textIndex;
    } else {
      toText = texts.length - 1;
    }

    if (fromText == toText) {
      texts[toText].toCharArray(srcPos - skipped, dest, destPos, length);
    } else {
      int startPos = srcPos - skipped;
      texts[fromText].toCharArray(startPos, dest, destPos, texts[fromText].length() - startPos);
      destPos += texts[fromText].length() - startPos;
      skipped += texts[fromText].length();

      for (int i = fromText + 1; i <= toText - 1; i++) {
        texts[i].toCharArray(0, dest, destPos, texts[i].length());
        destPos += texts[i].length();
        skipped += texts[i].length();
      }

      texts[toText].toCharArray(0, dest, destPos, srcPos + length - skipped);
    }
  }

  @Override
  protected int getTransformationDepth() {
    return transformationDepth;
  }

  @Override
  public TextCharSequence sequence() {
    return new CompositeTextCharSequence();
  }

  public class CompositeTextCharSequence implements TextCharSequence {

    private int skipped = 0;
    private int index = 0;
    private int textIndex = 0;
    private TextCharSequence innerSequence = texts[textIndex].sequence();

    @Override
    public Text getText() {
      return CompositeText.this;
    }

    @Override
    public int length() {
      return length;
    }

    private int getInnerIndex(int index) {
      return index - skipped;
    }

    @Override
    public char charAt(int index) {
      moveTo(index);
      return innerSequence.charAt(getInnerIndex(index));
    }

    @Override
    public TextCharSequence subSequence(int start, int end) {
      return subText(start, end).sequence();
    }

    @Override
    public Text subText(int start, int end) {
      // TODO can be optimized for regions, which does not span multiple texts
      return new SubText(CompositeText.this, start, end);
    }

    @Override
    public TextLocation getLocation(int index) {
      moveTo(index);
      return innerSequence.getLocation(getInnerIndex(index));
    }

    private void moveTo(int index) {
      if (this.index == index) {
        return;
      }
      if (!(skipped <= index && index < skipped + texts[textIndex].length())) {
        if (index == length) {
          // Special case - end of input
          textIndex = texts.length - 1;
          skipped = length - texts[textIndex].length();
        } else if (index > this.index) {
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
        innerSequence = texts[textIndex].sequence();
      }
      this.index = index;
    }

    public TextLocation getCopyLocation(int index) {
      moveTo(index);
      return texts[textIndex] instanceof TransformedText
          ? ((TransformedText) texts[textIndex]).getTransformedText().sequence().getLocation(0)
          : null;
    }

    @Override
    public String toString() {
      // contract of CharSequence
      return getText().toString();
    }

  }

}
