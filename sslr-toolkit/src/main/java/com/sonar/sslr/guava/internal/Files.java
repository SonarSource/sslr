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
package com.sonar.sslr.guava.internal;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Partial duplication of Guava's Files class
 *
 * Used as a workaround till we can actually depend on Guava (SSLR-183)
 */
public final class Files {

  private Files() {
  }

  public static String toString(File file, Charset charset) {
    InputStream in = null;
    try {
      in = new FileInputStream(file);
      byte[] b = new byte[(int) file.length()];
      int len = b.length;
      int total = 0;

      while (total < len) {
        int result = in.read(b, total, len - total);
        if (result == -1) {
          break;
        }
        total += result;
      }

      return new String(b, charset.name());
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

}
