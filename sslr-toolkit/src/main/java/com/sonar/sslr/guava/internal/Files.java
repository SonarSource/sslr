/*
 * Copyright (C) 2009-2012 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.guava.internal;

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
public class Files {

  public static String toString(File file, Charset charset) {
    try {
      InputStream in = new FileInputStream(file);
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

      return new String(b, charset);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
