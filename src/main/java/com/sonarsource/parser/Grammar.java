/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser;

import com.sonarsource.parser.matcher.Rule;

public interface Grammar {

  Rule getRootRule();

}
