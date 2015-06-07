package de.cheppner.regex;

public abstract class Atom {
	public static Atom createAny() {
		return new DotAtom();
	}

	public static Atom createChar(char c) {
		return new CharAtom(c);
	}

	public abstract boolean matches(char c);

	private static final class CharAtom extends Atom {

		private char c_;

		public CharAtom(char c) {
			c_ = c;
		}

		@Override
		public boolean matches(char c) {
			return c == c_;
		}

	}

	private static final class DotAtom extends Atom {
		public DotAtom() {

		}

		@Override
		public boolean matches(char c) {
			return c != '\n';
		}

	}
}
