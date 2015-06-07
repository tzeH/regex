package de.cheppner.regex;

import java.util.ArrayList;
import java.util.List;

public class State {
	public static class Connection {
		public Connection(Atom atom, State state) {
			atom_ = atom;
			target_ = state;
		}

		public Atom getAtom() {
			return atom_;
		}

		public State getTarget() {
			return target_;
		}

		public boolean hasAtom() {
			return atom_ != null;
		}

		private Atom atom_;
		private State target_;
	}

	private List<Connection> targets_;
	private List<Connection> unlabledTargets_;
	private int nr_;

	public boolean mark1_;
	public boolean mark2_;
	public Object mark_;

	private static int numCreated_;

	public State() {
		targets_ = new ArrayList<>(2);
		unlabledTargets_ = new ArrayList<>(2);
		nr_ = numCreated_++;
	}

	public void addNext(Atom atom, State state) {
		targets_.add(new Connection(atom, state));
	}

	public void addNext(State state) {
		unlabledTargets_.add(new Connection(null, state));
	}

	public List<Connection> getTargets() {
		return targets_;
	}

	public List<Connection> getUnlabledTargets() {
		return unlabledTargets_;
	}

	@Override
	public String toString() {
		return "e"+nr_;
	}

}
