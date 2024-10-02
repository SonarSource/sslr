/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.sslr.internal.toolkit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.Serializable;

public class ConfigurationPropertyPanel implements Serializable {

  private static final long serialVersionUID = 1L;
  private final JPanel panel;
  private final JTextField valueTextField;
  private final JLabel errorMessageLabel;

  public ConfigurationPropertyPanel(String name, String description) {
    panel = new JPanel(new GridBagLayout());

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.weightx = 1;
    constraints.gridx = 0;
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.insets = new Insets(3, 10, 3, 10);

    panel.setBorder(BorderFactory.createTitledBorder(name));

    JLabel descriptionLabel = new JLabel(description);
    panel.add(descriptionLabel, constraints);

    valueTextField = new JTextField();
    panel.add(valueTextField, constraints);

    errorMessageLabel = new JLabel();
    errorMessageLabel.setForeground(Color.RED);
    panel.add(errorMessageLabel, constraints);

    GridBagConstraints constraints2 = new GridBagConstraints();
    constraints2.gridx = 0;
    constraints2.weighty = 1;
    panel.add(Box.createGlue(), constraints2);
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
