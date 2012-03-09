/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.squid.checks;

import com.google.common.collect.TreeMultiset;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.squid.SquidAstVisitor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.sonar.squid.api.CheckMessage;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ViolationCounterCheck<GRAMMAR extends Grammar> extends SquidAstVisitor<GRAMMAR> {

  private final ViolationCounter violationCounter;
  private final String projectsDirCanonicalPath;

  public static class ViolationCounter {

    private final Map<String, Map<String, TreeMultiset<Integer>>> violationsByFileAndRule;

    public ViolationCounter() {
      this.violationsByFileAndRule = new HashMap<String, Map<String, TreeMultiset<Integer>>>();
    }

    private ViolationCounter(Map<String, Map<String, TreeMultiset<Integer>>> violationsByFileAndRule) {
      this.violationsByFileAndRule = violationsByFileAndRule;
    }

    public void increment(String fileRelativePath, String rule, int line) {
      if (!violationsByFileAndRule.containsKey(fileRelativePath)) {
        violationsByFileAndRule.put(fileRelativePath, new HashMap<String, TreeMultiset<Integer>>());
      }
      Map<String, TreeMultiset<Integer>> violationsByRule = violationsByFileAndRule.get(fileRelativePath);

      if (!violationsByRule.containsKey(rule)) {
        violationsByRule.put(rule, TreeMultiset.<Integer> create());
      }
      TreeMultiset<Integer> violations = violationsByRule.get(rule);

      violations.add(line);
    }

    public void saveToFile(String destinationFilePath) {
      FileOutputStream fos = null;
      ObjectOutputStream oos = null;
      try {
        fos = new FileOutputStream(destinationFilePath);
        oos = new ObjectOutputStream(fos);
        oos.writeObject(this.violationsByFileAndRule); // NOSONAR findbugs.DMI_NONSERIALIZABLE_OBJECT_WRITTEN
      } catch (Exception e) {
        throw new RuntimeException(e);
      } finally {
        IOUtils.closeQuietly(fos);
        IOUtils.closeQuietly(oos);
      }
    }

    public static ViolationCounter loadFromFile(File sourceFile) {
      if (!sourceFile.exists() || sourceFile.length() == 0) {
        return new ViolationCounter(new HashMap<String, Map<String, TreeMultiset<Integer>>>());
      } else {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
          fis = new FileInputStream(sourceFile);
          ois = new ObjectInputStream(fis);
          return new ViolationCounter((Map<String, Map<String, TreeMultiset<Integer>>>) ois.readObject());
        } catch (Exception e) {
          throw new RuntimeException(e);
        } finally {
          IOUtils.closeQuietly(fis);
          IOUtils.closeQuietly(ois);
        }
      }
    }
  }

  public static class ViolationDifferenceAnalyzer {

    private final ViolationCounter expected;
    private final ViolationCounter actual;
    private boolean hasDifferences = false;

    public ViolationDifferenceAnalyzer(ViolationCounter expected, ViolationCounter actual) {
      this.expected = expected;
      this.actual = actual;
    }

    public void printReport() {
      System.out.println(); // NOSONAR println() is ok here, KIIS principle
      System.out.println(); // NOSONAR println() is ok here, KIIS principle
      System.out.println("********************************"); // NOSONAR println() is ok here, KIIS principle
      System.out.println("* Violation differences report *"); // NOSONAR println() is ok here, KIIS principle
      System.out.println("********************************"); // NOSONAR println() is ok here, KIIS principle
      System.out.println(); // NOSONAR println() is ok here, KIIS principle
      System.out.println(); // NOSONAR println() is ok here, KIIS principle
      printDifferencesByFile();
      System.out.println(); // NOSONAR println() is ok here, KIIS principle
      System.out.println(); // NOSONAR println() is ok here, KIIS principle
      printDifferencesByRule();
      System.out.println(); // NOSONAR println() is ok here, KIIS principle
      System.out.println(); // NOSONAR println() is ok here, KIIS principle
      System.out.println("*****************"); // NOSONAR println() is ok here, KIIS principle
      System.out.println("* End of report *"); // NOSONAR println() is ok here, KIIS principle
      System.out.println("*****************"); // NOSONAR println() is ok here, KIIS principle
      System.out.println(); // NOSONAR println() is ok here, KIIS principle
      System.out.println(); // NOSONAR println() is ok here, KIIS principle
    }

    private void printDifferencesByFile() {
      System.out.println("Differences by file:"); // NOSONAR println() is ok here, KIIS principle

      Set<String[]> handledFilesRules = new HashSet<String[]>();

      for (String file : expected.violationsByFileAndRule.keySet()) {
        boolean shouldPrintHeader = true;
        for (String rule : expected.violationsByFileAndRule.get(file).keySet()) {
          handledFilesRules.add(new String[] {file, rule});
          shouldPrintHeader = printDifferencesByFileAndRule(shouldPrintHeader, file, rule);
        }
      }

      for (String file : actual.violationsByFileAndRule.keySet()) {
        boolean shouldPrintHeader = true;
        for (String rule : actual.violationsByFileAndRule.get(file).keySet()) {
          if (handledFilesRules.contains(new String[] {file, rule})) {
            continue;
          }
          shouldPrintHeader = printDifferencesByFileAndRule(shouldPrintHeader, file, rule);
        }
      }

      System.out.println("End of differences by file."); // NOSONAR println() is ok here, KIIS principle
    }

    private static void printDifferencesByFileHeader(String file) {
      System.out.println("  File " + file + ":"); // NOSONAR println() is ok here, KIIS principle
    }

    private boolean printDifferencesByFileAndRule(boolean shouldPrintHeader, String file, String rule) {

      TreeMultiset<Integer> linesExpected = getLines(expected, file, rule);
      TreeMultiset<Integer> linesActual = getLines(actual, file, rule);

      if (!linesExpected.equals(linesActual)) {
        hasDifferences = true;

        if (shouldPrintHeader) {
          printDifferencesByFileHeader(file);
        }

        System.out.println("    " + rule + ", (difference only) expected (" // NOSONAR println() is ok here, KIIS principle
          + StringUtils.join(setDifference(linesExpected, linesActual), ",") + "), actual ("
          + StringUtils.join(setDifference(linesActual, linesExpected), ",") + ").");

        return false;
      } else {
        return shouldPrintHeader;
      }

    }

    private static TreeMultiset<Integer> getLines(ViolationCounter counter, String file, String rule) {
      if (!counter.violationsByFileAndRule.containsKey(file)
        || !counter.violationsByFileAndRule.get(file).containsKey(rule)) {
        return TreeMultiset.create();
      } else {
        return counter.violationsByFileAndRule.get(file).get(rule);
      }
    }

    private static TreeMultiset<Integer> setDifference(TreeMultiset<Integer> a, TreeMultiset<Integer> b) {
      TreeMultiset<Integer> aMinusB = TreeMultiset.create(a);
      aMinusB.removeAll(b);
      return aMinusB;
    }

    private void printDifferencesByRule() {
      System.out.println("Differences by rule:"); // NOSONAR println() is ok here, KIIS principle

      for (String rule : getRules()) {
        int expectedViolations = getViolationsByRule(expected, rule);
        int actualViolations = getViolationsByRule(actual, rule);

        System.out.print("  " + rule + " expected: " + expectedViolations + ", actual: " + actualViolations + ": "); // NOSONAR print() is
                                                                                                                     // ok here, KIIS
                                                                                                                     // principle
        if (expectedViolations == actualViolations) {
          System.out.println("OK"); // NOSONAR println() is ok here, KIIS principle
        } else {
          System.out.println("*** FAILURE ***"); // NOSONAR println() is ok here, KIIS principle
        }
      }

      System.out.println("End of differences by rule."); // NOSONAR println() is ok here, KIIS principle
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

    public boolean hasDifferences() {
      return hasDifferences;
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
      violationCounter.increment(getRelativePath(getContext().getFile()), violation.getChecker().getClass().getSimpleName(),
          violation.getLine() == null ? -1
              : violation.getLine());
    }
  }

  private String getRelativePath(File file) {
    if (!file.exists()) {
      throw new IllegalArgumentException("The file located at \"" + file.getAbsolutePath() + "\" does not exist.");
    }

    String canonicalPath;
    try {
      canonicalPath = file.getCanonicalPath();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    if (!canonicalPath.startsWith(projectsDirCanonicalPath)) {
      throw new IllegalArgumentException("The file located at \"" + canonicalPath + "\" is not within projectsDir (\""
        + projectsDirCanonicalPath + "\").");
    }

    return canonicalPath.substring(projectsDirCanonicalPath.length()).replace('\\', '/');
  }

}
