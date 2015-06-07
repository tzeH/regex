package de.cheppner.regex;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Pattern {
	private final State begin_;
	private final State end_;

	public Pattern(State begin, State end) {
		begin_ = begin;
		end_ = end;
	}

	public boolean matches(final CharSequence str) {
		final List<State> active = new LinkedList<>();
		markActive(begin_, active);

		return matchesImplActive(str, 0, active);
	}

	private boolean matchesImplActive(final CharSequence str,
			final int charIndex, final List<State> active) {
		if (str.length() == charIndex)
			return end_.mark1_;

		for (State s : active)
			s.mark1_ = false;

		final char nextChar = str.charAt(charIndex);
		final List<State> nextActive = new LinkedList<>();

		for (State s : active) {
			for (State.Connection c : s.getTargets())
				if (c.getAtom().matches(nextChar))
					if (!c.getTarget().mark1_)
						markActive(c.getTarget(), nextActive);
		}

		return matchesImplActive(str, charIndex + 1, nextActive);
	}

	private void markActive(final State state, final List<State> active) {
		state.mark1_ = true;
		active.add(state);
		for (State.Connection c : state.getUnlabledTargets())
			if (!c.getTarget().mark1_)
				markActive(c.getTarget(), active);
	}

	/* Implementierung mit Sets */

	public boolean matchesSets(String str) {
		Set<State> marks = new HashSet<>();
		marks.add(begin_);
		addUnlabledSets(begin_, marks);

		return matchesSets(str, marks);
	}

	private boolean matchesSets(String str, Set<State> marks) {
		if (str.isEmpty())
			return marks.contains(end_);

		char nextChar = str.charAt(0);
		Set<State> newMarks = new HashSet<>();

		for (State s : marks)
			for (State.Connection c : s.getTargets())
				if (c.getAtom().matches(nextChar))
					if (newMarks.add(c.getTarget()))
						addUnlabledSets(c.getTarget(), newMarks);

		return matchesSets(str.substring(1), newMarks);
	}

	private void addUnlabledSets(State state, Set<State> newMarks) {
		for (State.Connection c : state.getUnlabledTargets())
			if (newMarks.add(c.getTarget()))
				addUnlabledSets(c.getTarget(), newMarks);
	}

}
