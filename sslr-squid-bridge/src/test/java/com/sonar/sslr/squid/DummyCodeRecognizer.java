/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import java.util.HashSet;
import java.util.Set;

import org.sonar.squid.recognizer.CodeRecognizer;
import org.sonar.squid.recognizer.ContainsDetector;
import org.sonar.squid.recognizer.Detector;
import org.sonar.squid.recognizer.LanguageFootprint;

public class DummyCodeRecognizer extends CodeRecognizer {

  public DummyCodeRecognizer() {

    super(0.9, new LanguageFootprint() {

      public Set<Detector> getDetectors() {
        return new HashSet<Detector>() {

          {
            add(new ContainsDetector(1.0, "CODE"));
          }
        };
      }
    });

  }

}
