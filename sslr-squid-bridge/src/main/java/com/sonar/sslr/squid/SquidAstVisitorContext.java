/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import java.io.File;

import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceProject;

import com.sonar.sslr.api.Comments;
import com.sonar.sslr.api.Grammar;

public abstract class SquidAstVisitorContext<GRAMMAR extends Grammar> {

  public abstract Comments getComments();

  public abstract File getFile();

  public abstract GRAMMAR getGrammar();

  public abstract void addSourceCode(SourceCode child);

  public abstract void popSourceCode();

  public abstract SourceCode peekSourceCode();

  public abstract SourceProject getProject();
}
