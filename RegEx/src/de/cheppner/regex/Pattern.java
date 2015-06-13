package de.cheppner.regex;

public class Pattern {
	private final State begin_;
	private final State end_;

	public Pattern(State begin, State end) {
		begin_ = begin;
		end_ = end;
	}

	public Matcher matcher(final CharSequence str) {
		return new ActiveListMatcher(begin_, end_, str);
	}

	public Matcher setMatcher(final CharSequence str) {
		return new SetMatcher(begin_, end_, str);
	}

	public boolean matches(final CharSequence str) {
		return matcher(str).matches();
	}

}
