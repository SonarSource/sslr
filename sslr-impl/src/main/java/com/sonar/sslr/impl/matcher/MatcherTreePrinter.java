/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.matcher;

import java.util.HashSet;

import com.sonar.sslr.impl.events.MatcherAdapter;
import com.sonar.sslr.impl.events.RuleImplAdapter;

public class MatcherTreePrinter {
	
	public static String print(Matcher matcher) {
		return print(matcher, 0, false);
	}
	
	public static String printWithAdapters(Matcher matcher) {
		return print(matcher, 0, true);
	}
	
	private static String print(Matcher matcher, int level, boolean showAdapters) {
		HashSet<String> implicitAnd = new HashSet<String>(){
			{
				OptMatcher.class.getCanonicalName();
				NextMatcher.class.getCanonicalName();
				ZeroToNMatcher.class.getCanonicalName();
				OneToNMatcher.class.getCanonicalName();
			}
		};
		
		Matcher[] children = matcher.getChildren();
		
		if (!showAdapters && (matcher instanceof MemoizerMatcher || matcher instanceof RuleImplAdapter || matcher instanceof MatcherAdapter)) {
			/* Skip this adapter matcher */
			return print(children[0], level, showAdapters);
		} else {
			StringBuilder result = new StringBuilder(matcher.toString());
			if (!(matcher instanceof RuleImplAdapter) && matcher instanceof RuleImpl && level == 0) result.append(".is");
			
			if (children.length > 0 && (matcher instanceof RuleImplAdapter || !(matcher instanceof RuleImpl) || level == 0)) {
				result.append("(");

				if (children.length == 1 && children[0] instanceof AndMatcher && children[0].getChildren().length == 1 && implicitAnd.contains(matcher.getClass().getCanonicalName())) {
					/* Remove the implicitly added and matcher (which contains only a single child) */
					result.append(print(matcher.children[0].children[0], level + 1, showAdapters));
				}
				else {
					/* Display the children */
			    for (int i = 0; i < children.length; i++) {
			    	result.append(print(children[i], level + 1, showAdapters));
			      if (i < children.length - 1) {
			      	result.append(", ");
			      }
			    }
		    }
				
				result.append(")");
			}
	    
	    return result.toString();
		}
	}
	
}
