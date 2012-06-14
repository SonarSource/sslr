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
package com.sonar.sslr.impl.analysis;

import java.util.HashMap;
import java.util.Map;

import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public final class Violation {

  private final Matcher affectedMatcher;
  private final RuleMatcher parentRule;
  private final ViolationConfidence confidence;
  private final Matcher[] relatedMatchers;
  private final Map<String, Object> propertyBag = new HashMap<String, Object>();

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
