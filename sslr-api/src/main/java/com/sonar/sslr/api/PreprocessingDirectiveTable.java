/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

import java.util.ArrayList;
import java.util.List;

public class PreprocessingDirectiveTable {

  protected List<PreprocessingDirective> directives = new ArrayList<PreprocessingDirective>();

  public final void add(PreprocessingDirective directive) {
    directives.add(directive);
  }

  public final void addAllDirective(List<? extends PreprocessingDirective> directives) {
    this.directives.addAll(directives);
  }

  public final PreprocessingDirective getLast() {
    if (size() > 0) {
      return directives.get(size() - 1);
    }
    throw new IllegalStateException("The preprocessing directive table is empty.");
  }

  public int size() {
    return directives.size();
  }

  public <DIRECTIVE extends PreprocessingDirective> List<DIRECTIVE> getAll(Class<DIRECTIVE> directiveFilter) {
    List<DIRECTIVE> result = new ArrayList<DIRECTIVE>();
    for (PreprocessingDirective directive : directives) {
      if (directiveFilter.isInstance(directive)) {
        result.add((DIRECTIVE) directive);
      }
    }
    return result;
  }
  
  public <DIRECTIVE extends PreprocessingDirective> DIRECTIVE getFirst(Class<DIRECTIVE> directiveFilter) {
    for (PreprocessingDirective directive : directives) {
      if (directiveFilter.isInstance(directive)) {
        return (DIRECTIVE) directive;
      }
    }
    return null;
  }
  
  public <DIRECTIVE extends PreprocessingDirective> DIRECTIVE getLast(Class<DIRECTIVE> directiveFilter) {
    for(int i = directives.size() - 1 ; i >= 0 ; i--) {
      PreprocessingDirective directive = directives.get(i);
      if (directiveFilter.isInstance(directive)) {
        return (DIRECTIVE) directive;
      }
    }
    return null;
  }

}
