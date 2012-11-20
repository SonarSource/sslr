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
package org.sonar.sslr.internal.toolkit;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.GridLayout;

public class ConfigurationPropertyPanel {

  private final JPanel panel;
  private final JLabel descriptionLabel;
  private final JTextField valueTextField;
  private final JLabel errorMessageLabel;

  public ConfigurationPropertyPanel(String name, String description) {
    panel = new JPanel(new GridLayout(3, 1));
    panel.setBorder(BorderFactory.createTitledBorder(name));

    descriptionLabel = new JLabel(description);
    panel.add(descriptionLabel);

    valueTextField = new JTextField();
    panel.add(valueTextField);

    errorMessageLabel = new JLabel();
    errorMessageLabel.setForeground(Color.RED);
    panel.add(errorMessageLabel);
  }

  public JPanel getPanel() {
    return panel;
  }

  public JLabel getErrorMessageLabel() {
    return errorMessageLabel;
  }

  public JTextField getValueTextField() {
    return valueTextField;
  }

}
