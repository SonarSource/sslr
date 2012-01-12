/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.squid.checks;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.sonar.squid.api.CheckMessage;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.squid.SquidAstVisitor;

public class ViolationCounterCheck<GRAMMAR extends Grammar> extends SquidAstVisitor<GRAMMAR> {

  private final ViolationCounter violationCounter;
  private final String projectsDirCanonicalPath;

  public static class ViolationCounter {

    private final Map<String, Map<String, TreeSet<Integer>>> violationsByFileAndRule = new HashMap<String, Map<String, TreeSet<Integer>>>();

    public void increment(String fileRelativePath, String rule, int line) {
      if ( !violationsByFileAndRule.containsKey(fileRelativePath)) {
        violationsByFileAndRule.put(fileRelativePath, new HashMap<String, TreeSet<Integer>>());
      }
      Map<String, TreeSet<Integer>> violationsByRule = violationsByFileAndRule.get(fileRelativePath);

      if ( !violationsByRule.containsKey(rule)) {
        violationsByRule.put(rule, new TreeSet<Integer>());
      }
      TreeSet<Integer> violations = violationsByRule.get(rule);

      violations.add(line);
    }

    public void saveToFile(String destinationFilePath) {
      FileWriter writer = null;
      try {
        writer = new FileWriter(destinationFilePath);
        JSONValue.writeJSONString(violationsByFileAndRule, writer);
      } catch (IOException e) {
        throw new RuntimeException(e);
      } finally {
        IOUtils.closeQuietly(writer);
      }
    }

    public static ViolationCounter loadFromFile(String sourceFilePath) {
      ViolationCounter instance = new ViolationCounter();

      FileReader reader = null;
      try {
        reader = new FileReader(sourceFilePath);
        Map<String, Map<String, JSONArray>> violationsByFileAndRule = (Map<String, Map<String, JSONArray>>) JSONValue.parse(reader);
        for (String fileRelativePath : violationsByFileAndRule.keySet()) {
          for (String rule : violationsByFileAndRule.get(fileRelativePath).keySet()) {
            for (Object lineObject : violationsByFileAndRule.get(fileRelativePath).get(rule)) {
              int line = ((Long) lineObject).intValue();

              instance.increment(fileRelativePath, rule, line);
            }
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      } finally {
        IOUtils.closeQuietly(reader);
      }

      return instance;
    }

  }

  public static class ViolationDifferenceAnalyzer {

    private final ViolationCounter expected;
    private final ViolationCounter actual;

    public ViolationDifferenceAnalyzer(ViolationCounter expected, ViolationCounter actual) {
      this.expected = expected;
      this.actual = actual;
    }

    public void printReport() {
      printDifferencesByFile();
      System.out.println();
      System.out.println();
      printDifferencesByRule();
    }

    private void printDifferencesByFile() {
      System.out.println("Differences by file:");

      Set<String> handledFiles = new HashSet<String>();

      for (String file : expected.violationsByFileAndRule.keySet()) {
        handledFiles.add(file);

        boolean shouldPrintHeader = true;
        Set<String> handledRules = new HashSet<String>();
        for (String rule : expected.violationsByFileAndRule.get(file).keySet()) {
          handledRules.add(rule);

          shouldPrintHeader = printDifferencesByFileAndRule(shouldPrintHeader, file, rule);
        }
      }

      for (String file : actual.violationsByFileAndRule.keySet()) {
        if ( !handledFiles.contains(file)) {
          boolean shouldPrintHeader = true;
          for (String rule : actual.violationsByFileAndRule.get(file).keySet()) {
            shouldPrintHeader = printDifferencesByFileAndRule(shouldPrintHeader, file, rule);
          }
        }
      }

      System.out.println("End of differences by file.");
    }

    private static void printDifferencesByFileHeader(String file) {
      System.out.println("  File " + file + ":");
    }

    private boolean printDifferencesByFileAndRule(boolean shouldPrintHeader, String file, String rule) {

      TreeSet<Integer> linesExpected = getLines(expected, file, rule);
      TreeSet<Integer> linesActual = getLines(actual, file, rule);

      if ( !linesExpected.equals(linesActual)) {
        if (shouldPrintHeader) {
          printDifferencesByFileHeader(file);
        }

        System.out.println("    " + rule + ", (difference only) expected ("
            + StringUtils.join(setDifference(linesExpected, linesActual), ",") + "), actual ("
            + StringUtils.join(setDifference(linesActual, linesExpected), ",") + ").");

        return false;
      } else {
        return shouldPrintHeader;
      }

    }

    private static TreeSet<Integer> getLines(ViolationCounter counter, String file, String rule) {
      if ( !counter.violationsByFileAndRule.containsKey(file)
          || !counter.violationsByFileAndRule.get(file).containsKey(rule)) {
        return new TreeSet<Integer>();
      } else {
        return counter.violationsByFileAndRule.get(file).get(rule);
      }
    }

    private static TreeSet<Integer> setDifference(TreeSet<Integer> a, TreeSet<Integer> b) {
      TreeSet<Integer> aMinusB = new TreeSet<Integer>(a);
      aMinusB.removeAll(b);
      return aMinusB;
    }

    private void printDifferencesByRule() {
      System.out.println("Differences by rule:");

      for (String rule : getRules()) {
        int expectedViolations = getViolationsByRule(expected, rule);
        int actualViolations = getViolationsByRule(actual, rule);

        System.out.print("  " + rule + " expected: " + expectedViolations + ", actual: " + actualViolations + ": ");
        if (expectedViolations == actualViolations) {
          System.out.println("OK");
        } else {
          System.out.println("*** FAILURE ***");
        }
      }

      System.out.println("End of differences by rule.");
    }

    private Set<String> getRules() {
      Set<String> rules = new HashSet<String>();

      for (String file : expected.violationsByFileAndRule.keySet()) {
        rules.addAll(expected.violationsByFileAndRule.get(file).keySet());
      }

      for (String file : actual.violationsByFileAndRule.keySet()) {
        rules.addAll(actual.violationsByFileAndRule.get(file).keySet());
      }

      return rules;
    }

    private static int getViolationsByRule(ViolationCounter counter, String rule) {
      int violations = 0;

      for (String file : counter.violationsByFileAndRule.keySet()) {
        if (counter.violationsByFileAndRule.get(file).containsKey(rule)) {
          violations += counter.violationsByFileAndRule.get(file).get(rule).size();
        }
      }

      return violations;
    }

  }

  public ViolationCounterCheck(String projectsDir, ViolationCounter violationCounter) {
    try {
      this.projectsDirCanonicalPath = new File(projectsDir).getCanonicalPath();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    this.violationCounter = violationCounter;
  }

  @Override
  public void leaveFile(AstNode node) {
    Set<CheckMessage> violationsOnCurrentFile = new HashSet<CheckMessage>(getContext().peekSourceCode().getCheckMessages());
    for (CheckMessage violation : violationsOnCurrentFile) {
      violationCounter.increment(getRelativePath(getContext().getFile()), violation.getChecker().getKey(), violation.getLine() == null ? -1
          : violation.getLine());
    }
  }

  private String getRelativePath(File file) {
    if ( !file.exists()) {
      throw new IllegalArgumentException("The file located at \"" + file.getAbsolutePath() + "\" does not exist.");
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

}
