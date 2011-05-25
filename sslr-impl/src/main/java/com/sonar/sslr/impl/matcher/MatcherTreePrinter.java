/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.impl.events.MatcherAdapter;
import com.sonar.sslr.impl.events.RuleImplAdapter;

public class MatcherTreePrinter {

	public static String print(Matcher matcher) {
		return print(matcher, true, false);
	}

	public static String printWithAdapters(Matcher matcher) {
		return print(matcher, true, true);
	}

	private static String print(Matcher matcher, boolean expandRule, boolean showAdapters) {
		Matcher[] children = matcher.getChildren();

		if (isAdapter(matcher) && !showAdapters) {
			/* Skip this adapter matcher */
			return print(children[0], expandRule, showAdapters);
		}

		StringBuilder result = new StringBuilder(matcher.toString());
		if (isRuleImpl(matcher) && expandRule) result.append(".is");

		if (hasChildren(children) && isNotRuleImplToCollapse(matcher, expandRule)) {
			result.append("(");

			/* Display the children */
			for (int i = 0; i < children.length; i++) {
				result.append(print(children[i], false, showAdapters));
				if (i < children.length - 1) {
					result.append(", ");
				}
			}

			result.append(")");
		}

		return result.toString();
	}

	private static boolean isNotRuleImplToCollapse(Matcher matcher, boolean expandRule) {
		return !(isRuleImpl(matcher) && !expandRule);
	}

	private static boolean hasChildren(Matcher[] children) {
		return children.length > 0;
	}

	private static boolean isRuleImpl(Matcher matcher) {
		return !(matcher instanceof RuleImplAdapter) && matcher instanceof RuleImpl;
	}

	private static boolean isAdapter(Matcher matcher) {
		return (matcher instanceof MemoizerMatcher || matcher instanceof RuleImplAdapter || matcher instanceof MatcherAdapter);
	}
	
}
