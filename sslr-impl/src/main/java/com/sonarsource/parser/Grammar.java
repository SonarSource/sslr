/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser;

import com.sonarsource.parser.matcher.Rule;

public interface Grammar {

  Rule getRootRule();

}
