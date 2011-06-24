/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;

public interface IMatcher {
	IMatcher[] getChildren();
	boolean isMatching(ParsingState parsingState);
	int matchToIndex(ParsingState parsingState);
	AstNode match(ParsingState parsingState);
}
