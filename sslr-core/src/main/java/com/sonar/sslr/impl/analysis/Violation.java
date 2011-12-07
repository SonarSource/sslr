/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import java.util.HashMap;

import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class Violation {

  private final Matcher affectedMatcher;
  private final RuleMatcher parentRule;
  private final ViolationConfidence confidence;
  private final Matcher[] relatedMatchers;
  private final HashMap<String, Object> propertyBag = new HashMap<String, Object>();

  public Violation(Matcher affectedMatcher) {
    this(affectedMatcher, null);
  }

  public Violation(Matcher affectedMatcher, RuleMatcher parentRule) {
    this(affectedMatcher, parentRule, ViolationConfidence.LOW, new Matcher[] {});
  }

  public Violation(Matcher affectedMatcher, RuleMatcher parentRule, ViolationConfidence confidence) {
    this(affectedMatcher, parentRule, confidence, new Matcher[] {});
  }

  public Violation(Matcher affectedMatcher, RuleMatcher parentRule, ViolationConfidence confidence, Matcher... relatedMatchers) {
    this.affectedMatcher = affectedMatcher;
    this.parentRule = parentRule;
    this.confidence = confidence;
    this.relatedMatchers = relatedMatchers;
  }

  public Violation addOrReplaceProperty(String propertyName, Object value) {
    propertyBag.put(propertyName, value);
    return this;
  }

  public Object getProperty(String propertyName) {
    return propertyBag.get(propertyName);
  }

  /**
   * @return the affectedMatcher
   */
  public Matcher getAffectedMatcher() {
    return affectedMatcher;
  }

  /**
   * @return the parentRule
   */
  public RuleMatcher getParentRule() {
    return parentRule;
  }

  /**
   * @return the confidence
   */
  public ViolationConfidence getConfidence() {
    return confidence;
  }

  /**
   * @return the relatedMatchers
   */
  public Matcher[] getRelatedMatchers() {
    return relatedMatchers;
  }

  /**
   * @return the related matcher at index n (or null if no such matcher exists)
   */
  public Matcher getRelatedMatcher(int n) {
    try {
      return relatedMatchers[n];
    } catch (ArrayIndexOutOfBoundsException e) {
      return null;
    }
  }

}
