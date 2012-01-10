/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.squid.checks;

import java.io.*;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.sonar.squid.api.CheckMessage;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.squid.SquidAstVisitor;

public class ViolationCounterCheck<GRAMMAR extends Grammar> extends SquidAstVisitor<GRAMMAR> {

  private final Map<String, Map<String, TreeSet<Integer>>> expectedViolationsByFileAndRule;
  private final PrintStream out;
  private final PrintStream dump;
  private final String projectsDirCanonicalPath;
  private final Map<String, TreeSet<Integer>> violationsOnCurrentFile = new HashMap<String, TreeSet<Integer>>();
  private final Map<String, Integer> violationByRule = new HashMap<String, Integer>();
  private boolean hasFailed = false;

  public ViolationCounterCheck(File expectedViolationsFile, String projectsDir, PrintStream out, PrintStream dump) {
    try {
      this.projectsDirCanonicalPath = new File(projectsDir).getCanonicalPath();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    if (out == null) {
      throw new NullPointerException("out cannot be null.");
    }
    this.out = out;

    if (dump == null) {
      throw new NullPointerException("dump cannot be null.");
    }
    this.dump = dump;

    if (expectedViolationsFile == null) {
      throw new NullPointerException("expectedViolationsFile cannot be null.");
    }
    this.expectedViolationsByFileAndRule = new HashMap<String, Map<String, TreeSet<Integer>>>();
    loadExpectedViolations(expectedViolationsFile);
  }

  public boolean hasFailed() {
    return hasFailed;
  }

  @Override
  public void leaveFile(AstNode node) {
    violationsOnCurrentFile.clear();
    Set<CheckMessage> violations = new HashSet<CheckMessage>(getContext().peekSourceCode().getCheckMessages());
    for (CheckMessage violation : violations) {
      countOnCurrentFile(violation);
      countByRule(violation);
    }
    getContext().peekSourceCode().getCheckMessages().removeAll(violations);
    processViolationsOnFile(getRelativePath(getContext().peekSourceCode().getKey()));
  }

  private String getRelativePath(String absolutePath) {
    File file = new File(absolutePath);
    if ( !file.exists()) {
      throw new IllegalArgumentException("The file located at \"" + absolutePath + "\" does not exist.");
    }

    String canonicalPath;
    try {
      canonicalPath = file.getCanonicalPath();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    if ( !canonicalPath.startsWith(projectsDirCanonicalPath)) {
      throw new IllegalArgumentException("The file located at \"" + canonicalPath + "\" is not within projectsDir (\""
          + projectsDirCanonicalPath + "\").");
    }

    return canonicalPath.substring(projectsDirCanonicalPath.length());
  }

  private void countByRule(CheckMessage violation) {
    String rule = violation.getChecker().getClass().getSimpleName();
    if (violationByRule.containsKey(rule)) {
      violationByRule.put(rule, violationByRule.get(rule) + 1);
    } else {
      violationByRule.put(rule, 1);
    }
  }

  private void countOnCurrentFile(CheckMessage violation) {
    String rule = violation.getChecker().getClass().getSimpleName();
    if ( !violationsOnCurrentFile.containsKey(rule)) {
      violationsOnCurrentFile.put(rule, new TreeSet<Integer>());
    }
    violationsOnCurrentFile.get(rule).add(violation.getLine() == null ? -1 : violation.getLine());
  }

  private void processViolationsOnFile(String relativePath) {
    dumpViolationsOnFile(relativePath);
    compareViolationsOnFile(relativePath);
  }

  private void dumpViolationsOnFile(String relativePath) {
    for (String rule : violationsOnCurrentFile.keySet()) {
      dump.print(rule + " ");

      TreeSet<Integer> linesTreeSet = violationsOnCurrentFile.get(rule);
      dump.print(StringUtils.join(linesTreeSet, ","));
      dump.print(' ');

      dump.println(relativePath);
    }
  }

  private void loadExpectedViolations(File expectedViolationsFile) {
    if ( !expectedViolationsFile.exists()) {
      throw new IllegalArgumentException("The exptected violations file located under \"" + expectedViolationsFile.getAbsolutePath()
          + "\" does not exist.");
    }

    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(expectedViolationsFile))));

      String line;
      while ((line = br.readLine()) != null) {
        String[] parts = line.split(" ", 3);
        if (parts.length != 3) {
          throw new IllegalArgumentException("The line \"" + line + "\" does not have 3 parts.");
        }
        String rule = parts[0];
        String relativePath = parts[2];

        String[] lineNumbers = parts[1].split(",", -1);
        TreeSet<Integer> lineNumbersTreeSet = new TreeSet<Integer>();
        for (String lineNumber : lineNumbers) {
          if (lineNumber.length() == 0) {
            lineNumbersTreeSet.add( -1);
          } else {
            lineNumbersTreeSet.add(Integer.parseInt(lineNumber));
          }
        }

        if ( !expectedViolationsByFileAndRule.containsKey(relativePath)) {
          expectedViolationsByFileAndRule.put(relativePath, new HashMap<String, TreeSet<Integer>>());
        }

        if (expectedViolationsByFileAndRule.get(relativePath).containsKey(rule)) {
          throw new IllegalArgumentException("There are (at least) two records for the file \"" + relativePath + "\" for rule \"" + rule
              + "\".");
        }

        expectedViolationsByFileAndRule.get(relativePath).put(rule, lineNumbersTreeSet);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (br != null) {
        IOUtils.closeQuietly(br);
      }
    }
  }

  private void compareViolationsOnFile(String relativePath) {
    boolean flagPrintedFileName = false;

    if (expectedViolationsByFileAndRule.containsKey(relativePath)) {
      for (String rule : expectedViolationsByFileAndRule.get(relativePath).keySet()) {
        TreeSet<Integer> expectedViolations = getExpectedViolationLines(relativePath, rule);
        TreeSet<Integer> actualViolations = getActualViolationLines(rule);
        flagPrintedFileName = printDifferences(flagPrintedFileName, relativePath, rule, expectedViolations, actualViolations);

        violationsOnCurrentFile.remove(rule);
      }
    }

    for (String rule : violationsOnCurrentFile.keySet()) {
      TreeSet<Integer> expectedViolations = getExpectedViolationLines(relativePath, rule);
      TreeSet<Integer> actualViolations = getActualViolationLines(rule);
      flagPrintedFileName = printDifferences(flagPrintedFileName, relativePath, rule, expectedViolations, actualViolations);

      violationsOnCurrentFile.remove(rule);
    }
  }

  private boolean printDifferences(boolean flagPrintedFileName, String relativePath, String rule, TreeSet<Integer> expectedViolations,
      TreeSet<Integer> actualViolations) {
    if ( !expectedViolations.equals(actualViolations)) {
      this.hasFailed = true;

      if ( !flagPrintedFileName) {
        out.println("Differences for the file \"" + relativePath + "\":");
        flagPrintedFileName = true;
      }

      out.println(" - Rule " + rule + ", expected (" + expectedViolations.size() + ") \""
          + StringUtils.join(expectedViolations, ", ") + "\" got (" + actualViolations.size() + ") \""
          + StringUtils.join(actualViolations, ", ") + "\"");
    }

    return flagPrintedFileName;
  }

  private TreeSet<Integer> getExpectedViolationLines(String relativePath, String rule) {
    if ( !expectedViolationsByFileAndRule.containsKey(relativePath)
        || !expectedViolationsByFileAndRule.get(relativePath).containsKey(rule)) {
      return new TreeSet<Integer>();
    } else {
      return expectedViolationsByFileAndRule.get(relativePath).get(rule);
    }
  }

  private TreeSet<Integer> getActualViolationLines(String rule) {
    if ( !violationsOnCurrentFile.containsKey(rule)) {
      return new TreeSet<Integer>();
    } else {
      return violationsOnCurrentFile.get(rule);
    }
  }

}
