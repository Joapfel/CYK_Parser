package de.ws1617.pccl.app;

import de.ws1617.pccl.parser.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.plaf.synth.SynthSeparatorUI;

import de.ws1617.pccl.grammar.Grammar;
import de.ws1617.pccl.grammar.GrammarUtils;
import de.ws1617.pccl.grammar.Lexicon;
import de.ws1617.pccl.grammar.NonTerminal;
import de.ws1617.pccl.grammar.Rule;
import de.ws1617.pccl.grammar.Symbol;
import de.ws1617.pccl.grammar.Terminal;
import de.ws1617.pccl.parser.CYK;
import de.ws1617.pccl.tree.Tree;

public class Main {

	public static void main(String[] args) {

		// TODO instantiate a CYK parser and parse a sentence
		if (args.length != 5) {
			System.err.println("required arguments:\n path to the grammar file,\n path to the lexicon file,\n startSymbol,\n input-sentence,\n number of parses to show\n");
		}
		try {
			String pathGrammar = args[0];
			String pathLexicon = args[1];
			NonTerminal startSymbol = new NonTerminal(args[2]);
			String input = args[3];
			int show = Integer.parseInt(args[4]);

			CYK cyk = new CYK(pathGrammar, pathLexicon);
			HashMap<NonTerminal, ArrayList<Tree<Symbol>>> parses = cyk.parse(input);
			
			//get the result in from of a syntax tree with brackets
			ArrayList<String> differentParses = cyk.prettyParse(parses, startSymbol);
			System.out.println("Total number of parses: " + differentParses.size());
			if(show > differentParses.size()){
				show = differentParses.size();
			}
			System.out.println("Number of parses shown: " + show);
			System.out.println();
			
			for(int i = 0; i < show; i++){
				
				//print parse
				System.out.println(differentParses.get(i));
				System.out.println();
				
				//print the parse in form of rules
				ArrayList<ArrayList<String>> result = cyk.showRules(differentParses.get(i));
				for (ArrayList<String> iter : result) {
					for (String s : iter) {
						System.out.print(s + " ");
					}
					System.out.println();
				}
				System.out.println();
				System.out.println("-------------------------------------------------------");
			}
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
