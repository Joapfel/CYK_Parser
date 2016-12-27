package de.ws1617.pccl.app;

import de.ws1617.pccl.parser.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.ws1617.pccl.grammar.Grammar;
import de.ws1617.pccl.grammar.GrammarUtils;
import de.ws1617.pccl.grammar.Lexicon;
import de.ws1617.pccl.grammar.NonTerminal;
import de.ws1617.pccl.grammar.Symbol;
import de.ws1617.pccl.grammar.Terminal;
import de.ws1617.pccl.parser.CYK;
import de.ws1617.pccl.tree.Tree;

public class Main {

	public static void main(String[] args) {

		// TODO instantiate a CYK parser and parse a sentence
		if (args.length != 3) {
			System.err.println("required aruments: grammar, lexicon, input-sentence");
		}
		try {
			String path = args[0];
			Lexicon lexicon = GrammarUtils.readLexicon(args[1]);
			String input = args[2];

			CYK cyk = new CYK(path, lexicon);
			HashMap<NonTerminal, ArrayList<Tree<Symbol>>> parses = cyk.parse(input);
			cyk.prettyParse(parses);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
