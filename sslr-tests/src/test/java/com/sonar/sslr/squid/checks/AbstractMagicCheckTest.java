/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.google.common.collect.Sets;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import com.sonar.sslr.test.miniC.MiniCLexer;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static com.sonar.sslr.squid.metrics.ResourceParser.*;
import static com.sonar.sslr.test.squid.CheckMatchers.*;

public class AbstractMagicCheckTest {

  private static class Check extends AbstractMagicCheck<MiniCGrammar> {

    @Override
    public Set<AstNodeType> getPatterns() {
      return Collections.unmodifiableSet(Sets.newHashSet((AstNodeType) MiniCLexer.Literals.INTEGER));
    }

    @Override
    public Set<AstNodeType> getInclusions() {
      return Collections.unmodifiableSet(Sets.newHashSet((AstNodeType) getContext().getGrammar().whileStatement));
    }

    @Override
    public Set<AstNodeType> getExclusions() {
      return Collections.unmodifiableSet(Sets.newHashSet((AstNodeType) getContext().getGrammar().variableInitializer));
    }

    @Override
    public boolean isExcepted(AstNode candidate) {
      return "1337".equals(candidate.getTokenOriginalValue());
    }

    @Override
    public String getMessage() {
      return "Avoid magic stuff.";
    }

  }

  @Test
  public void detected() {
    setCurrentSourceFile(scanFile("/checks/magic.mc", new Check()));

    assertNumberOfViolations(2);

    assertViolation().atLine(5).withMessage("Avoid magic stuff.");
    assertViolation().atLine(9);
  }

}
