package de.cheppner.regex;

import java.util.LinkedList;
import java.util.List;

public class ActiveListMatcher implements Matcher {

	private final State begin_;
	private final State end_;
	private final CharSequence str_;

	public ActiveListMatcher(final State begin, final State end,
			final CharSequence str) {
		begin_ = begin;
		end_ = end;
		str_ = str;
	}

	@Override
	public boolean matches() {
		return matchesImpl();
	}

	private boolean matchesImpl() {
		final List<State> active = new LinkedList<>();
		markActive(begin_, active);

		return matchesImplActive(0, active);
	}

	private boolean matchesImplActive(final int charIndex,
			final List<State> active) {
		if (str_.length() == charIndex)
			return end_.mark1_;

		for (State s : active)
			s.mark1_ = false;

		final char nextChar = str_.charAt(charIndex);
		final List<State> nextActive = new LinkedList<>();

		for (State s : active) {
			for (State.Connection c : s.getTargets())
				if (c.getAtom().matches(nextChar))
					if (!c.getTarget().mark1_)
						markActive(c.getTarget(), nextActive);
		}

		return matchesImplActive(charIndex + 1, nextActive);
	}

	private void markActive(final State state, final List<State> active) {
		state.mark1_ = true;
		active.add(state);
		for (State.Connection c : state.getUnlabledTargets())
			if (!c.getTarget().mark1_)
				markActive(c.getTarget(), active);
	}
}
