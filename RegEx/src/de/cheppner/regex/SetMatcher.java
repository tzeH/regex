package de.cheppner.regex;

import java.util.HashSet;
import java.util.Set;

public class SetMatcher implements Matcher {

	private final State begin_;
	private final State end_;
	private final CharSequence str_;

	public SetMatcher(final State begin, final State end, final CharSequence str) {
		begin_ = begin;
		end_ = end;
		str_ = str;
	}

	@Override
	public boolean matches() {
		return matches(str_);
	}

	private boolean matches(final CharSequence str) {
		Set<State> marks = new HashSet<>();
		marks.add(begin_);
		addUnlabledSets(begin_, marks);

		return matchesSets(str, marks);
	}

	private boolean matchesSets(final CharSequence str, Set<State> marks) {
		if (str.length() == 0)
			return marks.contains(end_);

		char nextChar = str.charAt(0);
		Set<State> newMarks = new HashSet<>();

		for (State s : marks)
			for (State.Connection c : s.getTargets())
				if (c.getAtom().matches(nextChar))
					if (newMarks.add(c.getTarget()))
						addUnlabledSets(c.getTarget(), newMarks);

		return matchesSets(str.subSequence(1, str.length()), newMarks);
	}

	private void addUnlabledSets(State state, Set<State> newMarks) {
		for (State.Connection c : state.getUnlabledTargets())
			if (newMarks.add(c.getTarget()))
				addUnlabledSets(c.getTarget(), newMarks);
	}
}
