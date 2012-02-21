/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.sdk;

import com.sonar.sslr.impl.Parser;
import org.sonar.colorizer.Tokenizer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@SuppressWarnings("serial")
public class SsdkGui extends javax.swing.JFrame {

  private final SourceCodeViewer sourceCodePane;
  private final JButton loadSourceButton = new JButton();
  private final transient AstViewer astViewPane;
  private final JFileChooser sourceFileChooser = new JFileChooser();

  public SsdkGui(Parser parser, List<Tokenizer> colorizerChannels) {
    sourceCodePane = new SourceCodeViewer(colorizerChannels);
    astViewPane = new AstViewer(parser);
    initLoadSourceButtonActions();
    initView();
    setDefaultCloseOperation(SsdkGui.EXIT_ON_CLOSE);
  }

  private void initLoadSourceButtonActions() {
    loadSourceButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        int returnVal = sourceFileChooser.showOpenDialog(SsdkGui.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

          File file = sourceFileChooser.getSelectedFile();
          try {
            sourceCodePane.loadAndColorizeFile(file);
            astViewPane.loadAndParse(file);
          } catch (Exception ex) {
            StringWriter errorMessage = new StringWriter();
            errorMessage.append("Unable to open source file '" + file.getAbsolutePath() + "'\n\n\n");
            ex.printStackTrace(new PrintWriter(errorMessage));
            sourceCodePane.setContentType("text/plain");
            sourceCodePane.setText(errorMessage.toString());
          }
        }
      }
    });

  }

  private void initView() {
    setLayout(new BorderLayout(2, 2));
    loadSourceButton.setText("Select source code file");

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(sourceCodePane), new JScrollPane(
        astViewPane.getJTree()));
    splitPane.setDividerLocation(500);
    add(splitPane, BorderLayout.CENTER);
    add(loadSourceButton, BorderLayout.PAGE_END);
  }
}
