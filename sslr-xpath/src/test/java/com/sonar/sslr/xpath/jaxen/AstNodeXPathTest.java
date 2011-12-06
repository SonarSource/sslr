package com.sonar.sslr.xpath.jaxen;

import org.jaxen.JaxenException;
import org.jaxen.saxpath.helpers.XPathReaderFactory;
import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.xpath.miniC.checks.AbstractCheck;

import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class AstNodeXPathTest extends AbstractCheck {

  @Test
  public void simpleTest() throws JaxenException {
    System.setProperty(XPathReaderFactory.DRIVER_PROPERTY, "");
    AstNodeXPath xpath = new AstNodeXPath("//declaration");
    AstNode fileNode = parse("/checks/basicQueries.mc");

    Object result = xpath.selectNodes(fileNode);
    assertThat(result, not(nullValue()));
  }

}
