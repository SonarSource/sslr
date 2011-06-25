/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.impl.events.MatcherAdapter;
import com.sonar.sslr.impl.events.RuleMatcherAdapter;

public class MatcherTreePrinter {

	public static String print(IMatcher matcher) {
		return print(matcher, true, false);
	}

	public static String printWithAdapters(IMatcher matcher) {
		return print(matcher, true, true);
	}

	private static String print(IMatcher matcher, boolean expandRule, boolean showAdapters) {
		IMatcher[] children = matcher.getChildren();

		if (isAdapter(matcher) && !showAdapters) {
			/* Skip this adapter matcher */
			return print(children[0], expandRule, showAdapters);
		}

		StringBuilder result = new StringBuilder(matcher.toString());
		if (isRuleImpl(matcher) && expandRule) result.append(".is");

		if (hasChildren(children) && isNotRuleImplToCollapse(matcher, expandRule)) {
			if (result.length() >= 1 && result.charAt(result.length() - 1) == ')') {
				result.deleteCharAt(result.length() - 1);
				result.append(", ");
			}
			else result.append("(");

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

	private static boolean isNotRuleImplToCollapse(IMatcher matcher, boolean expandRule) {
		return !(isRuleImpl(matcher) && !expandRule);
	}

	private static boolean hasChildren(IMatcher[] children) {
		return children.length > 0;
	}

	private static boolean isRuleImpl(IMatcher matcher) {
		return !(matcher instanceof RuleMatcherAdapter) && matcher instanceof RuleMatcher;
	}

	private static boolean isAdapter(IMatcher matcher) {
		return (matcher instanceof MemoizerMatcher || matcher instanceof RuleMatcherAdapter || matcher instanceof MatcherAdapter);
	}
	
}
