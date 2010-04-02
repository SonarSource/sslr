/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr;

import com.sonarsource.sslr.matcher.RuleImpl;

public interface Grammar {

  RuleImpl getRootRule();

}
