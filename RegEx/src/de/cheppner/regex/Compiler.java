package de.cheppner.regex;

import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import de.cheppner.regex.parser.RegExLexer;
import de.cheppner.regex.parser.RegExParser;
import de.cheppner.regex.parser.RegExParser.AtomContext;
import de.cheppner.regex.parser.RegExParser.CharacterContext;
import de.cheppner.regex.parser.RegExParser.ExpressionContext;
import de.cheppner.regex.parser.RegExParser.FactorContext;
import de.cheppner.regex.parser.RegExParser.InitContext;
import de.cheppner.regex.parser.RegExParser.TermContext;

public class Compiler {

	public static void main(String[] args) {
		// String regex = "(x+x+)+y";
		// String text = "xxxxy";
		// // email
		// String regex = "([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})";
//		String regex = "(.+)@(.+)\\.(.+)";
//		String text = "ch@cheppner.de";
		

		String regex = "(ab+|bbc)@cheppner\\.de";
		String text = "bbc@cheppner.de";

		Pattern pattern = Compiler.compile(regex);

		System.out.println(text + " matches: " + pattern.matches(text));
	}

	public static Pattern compile(final String regex) {
		return new Compiler(regex).compile();
	}

	private String regex_;

	public Compiler(String regex) {
		regex_ = regex;
	}

	private Pattern compile() {
		RegExLexer lexer = new RegExLexer(new ANTLRInputStream(regex_));
		RegExParser parser = new RegExParser(new CommonTokenStream(lexer));

		// parser.setTrace(true);
		InitContext tree = parser.init();
//		System.out.println(tree.toStringTree(parser));

		State begin = new State();
		State end = parse(begin, tree);
		return new Pattern(begin, end);
	}

	private State parse(final State input, final InitContext tree) {
		if (tree.expression() == null)
			return input;
		return parse(input, tree.expression());
	}

	private State parse(final State input, final ExpressionContext expression) {
		if (expression.expression() == null)
			return parse(input, expression.term());

		State termEnd = parse(input, expression.term());
		State expressionEnd = parse(input, expression.expression());

		expressionEnd.addNext(termEnd);

		return termEnd;
	}

	private State parse(final State input, final TermContext term) {
		State result = input;
		for (FactorContext factor : term.factor()) {
			result = parse(result, factor);
		}
		return result;
		
//		if (term.term() == null)
//			return parse(input, term.factor());
//
//		return parse(parse(input, term.factor()), term.term());
	}

	private State parse(final State input, final FactorContext factor) {

		State atomState = parse(input, factor.atom());

		if (factor.metacharacter() == null) {
			return atomState;
		}

		char character = regex_.charAt(factor.metacharacter().start
				.getStartIndex());

		switch (character) {
		case '?':
			input.addNext(atomState);
			return atomState;
		case '*':
			atomState.addNext(input);
			return input;
		case '+': // includes +?
			atomState.addNext(input);
			return atomState;
		default:
			throw new IllegalArgumentException("Unknown meta character '"
					+ character + "'");
		}
		// String metachar = factor.metacharacter().getText();
		// if (metachar.equals("?")) {
		// input.addNext(atomState);
		// return atomState;
		// }
		// if (metachar.equals("*")) {
		// atomState.addNext(input);
		// return input;
		// }
		// if (metachar.equals("+") || metachar.equals("+?")) {
		// atomState.addNext(input);
		// return atomState;
		// }
		// throw new IllegalArgumentException("Unknown meta character '"
		// + metachar + "'");
	}

	private State parse(final State input, final AtomContext atom) {
		if (atom.expression() != null)
			return parse(input, atom.expression());

		Atom atomMatcher;
		CharacterContext character = atom.character();
		if (character == null)
			atomMatcher = Atom.createAny();
		else {
			// Bei escapten Chars ist der letzte char der richtige und nicht "\"
			Token t = character.getStop();
			atomMatcher = Atom.createChar(regex_.charAt(t.getStartIndex()));
		}

		State atomState = new State();
		input.addNext(atomMatcher, atomState);

		return atomState;
	}
}
