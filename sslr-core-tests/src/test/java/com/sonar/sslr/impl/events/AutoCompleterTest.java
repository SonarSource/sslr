/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.*;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;

public class AutoCompleterTest {
  
  @Test
  public void testCaseEmpty() {
    AutoCompleter auto = new AutoCompleter();
    
    auto.autoComplete(
      opt(and("hello"))
    );
    
    assertMatches(auto, new String[][]{ {} }, new String[][]{});
  }
  
  @Test
  public void testCase1() {
    AutoCompleter auto = new AutoCompleter();
    
    auto.autoComplete(
      and("hello")  
    );
    
    assertMatches(auto, new String[][]{ { "hello" } }, new String[][]{});
  }
  
  @Test
  public void testCase2() {
    AutoCompleter auto = new AutoCompleter();
    
    auto.autoComplete(
        and(
            "hello",
            opt("buddy"),
            or(
                "Olivier",
                "Freddy"
            )
        )
    );
    
    assertMatches(auto, new String[][]{ { "hello", "Olivier" }, { "hello", "Freddy" }, { "hello", "buddy", "Olivier" }, { "hello", "buddy", "Freddy" } }, new String[][]{});
  }
  
  @Test
  public void testCase3() {
    AutoCompleter auto = new AutoCompleter();
    
    auto.autoComplete(
        and(
            one2n("hello"),
            "world"
        )
    );
    
    assertMatches(auto, new String[][]{ { "hello", "world" }, { "hello", "hello", "world" }, { "hello", "hello", "hello", "world" }, { "hello", "hello", "hello", "hello", "world" } }, new String[][]{ { "hello", "hello", "hello", "hello", "hello" } });
  }
  
  @Test
  public void testCase4() {
    AutoCompleter auto = new AutoCompleter();
    
    auto.autoComplete(
        and(
            "hi",
            o2n("folks")
        )
    );
    
    assertMatches(auto, new String[][]{ { "hi" } }, new String[][]{});
  }
  
  @Test
  public void testCase5() {
    AutoCompleter auto = new AutoCompleter();
    
    auto.autoComplete(
        and(
            "hi",
            opt("folks")
        )
    );
    
    assertMatches(auto, new String[][]{ { "hi" } }, new String[][]{});
  }
  
  @Test
  public void testCase6() {
    AutoCompleter auto = new AutoCompleter();
    
    auto.autoComplete(
        and(
            o2n("hello"),
            "hello"
        )
    );
    
    assertMatches(auto, new String[][]{}, new String[][]{ { "hello", "hello", "hello", "hello", "hello" } });
  }
  
  @Test
  public void testCase7() {
    AutoCompleter auto = new AutoCompleter();
    
    auto.autoComplete(
        or(
            "fail",
            and("fail", "ure")
        )
    );
    
    assertMatches(auto, new String[][]{ { "fail" } }, new String[][]{});
  }
  
  @Test
  public void testPredicateCase1() {
    AutoCompleter auto = new AutoCompleter();
    
    auto.autoComplete(
        and(not("hello"), "world")
    );
    
    assertMatches(auto, new String[][]{ { "world" } }, new String[][]{});
  }
  
  @Test
  public void testPredicateCase2() {
    AutoCompleter auto = new AutoCompleter();
    
    auto.autoComplete(
        and(not(and("hello", "world")), "foo")
    );
    
    assertMatches(auto, new String[][]{ { "foo" } }, new String[][]{});
  }
  
  @Test
  public void testCaseTokenValueWithDouleQuotes() {
    AutoCompleter auto = new AutoCompleter();
    
    auto.autoComplete(
        and("fail\"")
    );
    
    assertMatches(auto, new String[][]{ { "fail\"" } }, new String[][]{});
  }
  
  private enum MyTokenType implements TokenType {
    ADD("+"), SUB("-");
    
    private String value;
    
    private MyTokenType(String value) {
      this.value = value;
    }
    
    public String getName() {
      return name();
    }

    public String getValue() {
      return value;
    }

    public boolean hasToBeSkippedFromAst(AstNode node) {
      return false;
    }
    
  }
  
  @Test
  public void testCaseTokenType() {
    AutoCompleter auto = new AutoCompleter();
    
    auto.autoComplete(
        and(
            or(
                MyTokenType.ADD,
                MyTokenType.SUB
            ),
            "number"
        )
    );
    
    assertMatches(auto, new String[][]{ { "+", "number" }, { "-", "number" } }, new String[][]{});
  }
  
  private void assertMatches(AutoCompleter auto, String[][] fullMatches, String[][] partialMatches) {
    assertThat(auto.getFullMatches().size(), is(fullMatches.length));
    assertThat(auto.getPartialMatches().size(), is(partialMatches.length));
    
    /* Compare the full matches */
    for (List<Token> list: auto.getFullMatches()) {
      boolean found = false;
      for (String[] fullMatch: fullMatches) {
        if (fullMatch.length == list.size()) {
          /* Compare token by token */
          int i;
          for (i = 0; i < fullMatch.length; i++) {
            if (!fullMatch[i].equals(list.get(i).getValue())) {
              break;
            }
          }
          
          if (i == fullMatch.length) {
            found = true;
            break;
          }
        }
      }
      
      if (!found) {
        StringBuilder errorMessage = new StringBuilder(System.getProperty("line.separator"));
        errorMessage.append("Expected a full match corresponding to:");
        errorMessage.append(System.getProperty("line.separator"));
        errorMessage.append('\t');
        
        for (Token token: list) {
          errorMessage.append(token.getValue());
          errorMessage.append(" ");
        }
        
        throw new AssertionError(errorMessage.toString());
      }
    }
    
    /* Compare the partial matches */
    for (List<Token> list: auto.getPartialMatches()) {
      boolean found = false;
      for (String[] partialMatch: partialMatches) {
        if (partialMatch.length == list.size()) {
          /* Compare token by token */
          int i;
          for (i = 0; i < partialMatch.length; i++) {
            if (!partialMatch[i].equals(list.get(i).getValue())) {
              break;
            }
          }
          
          if (i == partialMatch.length) {
            found = true;
            break;
          }
        }
      }
      
      if (!found) {
        StringBuilder errorMessage = new StringBuilder(System.getProperty("line.separator"));
        errorMessage.append("Expected a partial match corresponding to:");
        errorMessage.append(System.getProperty("line.separator"));
        errorMessage.append('\t');
        
        for (Token token: list) {
          errorMessage.append(token.getValue());
          errorMessage.append(" ");
        }
        
        throw new AssertionError(errorMessage.toString());
      }
    }
  }
  
}
