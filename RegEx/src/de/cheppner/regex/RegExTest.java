package de.cheppner.regex;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RegExTest {

	@Test
	public void EmptyPatternMatchesEmptyString() {
		assertTrue(Compiler.compile("").matches(""));
	}

	@Test
	public void SingleCharMatchesSingleChar() {
		assertTrue(Compiler.compile("a").matches("a"));
		assertFalse(Compiler.compile("a").matches("b"));
	}

	@Test
	public void PointMatchesAnyChar() {
		assertTrue(Compiler.compile(".").matches("a"));
	}

	@Test
	public void QuestionMatchesZeroOrOne() {
		assertTrue(Compiler.compile("a?").matches(""));
		assertTrue(Compiler.compile("a?").matches("a"));
		assertFalse(Compiler.compile("a?").matches("aa"));
	}

	@Test
	public void StarMatchesZeroOrMany() {
		assertTrue(Compiler.compile("a*").matches(""));
		assertTrue(Compiler.compile("a*").matches("a"));
		assertTrue(Compiler.compile("a*").matches("aa"));
	}

	@Test
	public void PlusMatchesOneOrMany() {
		assertFalse(Compiler.compile("a+").matches(""));
		assertTrue(Compiler.compile("a+").matches("a"));
		assertTrue(Compiler.compile("a+").matches("aa"));
	}

	@Test
	public void PlusQuestionMatchesOneOrMany() {
		assertFalse(Compiler.compile("a+?").matches(""));
		assertTrue(Compiler.compile("a+?").matches("a"));
		assertTrue(Compiler.compile("a+?").matches("aa"));
	}

	@Test
	public void AlternativeMatchesEveryAlternative() {
		assertTrue(Compiler.compile("a|b|c").matches("a"));
		assertTrue(Compiler.compile("a|b|c").matches("b"));
		assertTrue(Compiler.compile("a|b|c").matches("c"));
	}

	@Test
	public void QuestionAndStarDoCombine() {
		assertTrue(Compiler.compile("(a?)*").matches(""));
		assertTrue(Compiler.compile("(a?)*").matches("a"));
		assertTrue(Compiler.compile("(a?)*").matches("aa"));
	}

	@Test
	public void BackslashEscapesSpecialChars() {
		assertTrue(Compiler.compile("\\*").matches("*"));
		assertTrue(Compiler.compile("\\+").matches("+"));
		assertTrue(Compiler.compile("\\?").matches("?"));
	}

}
