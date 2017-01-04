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
			System.err.println("required aruments:\n path to the grammar file,\n path to the lexicon file,\n startSymbol,\n input-sentence,\n number of parses to show\n");
		}
		try {
			String pathGrammar = args[0];
			String pathLexicon = args[1];
			NonTerminal startSymbol = new NonTerminal(args[2]);
			String input = args[3];
			int show = Integer.parseInt(args[4]);

			CYK cyk = new CYK(pathGrammar, pathLexicon);
			HashMap<NonTerminal, ArrayList<Tree<Symbol>>> parses = cyk.parse(input);
			System.out.println(cyk.prettyParse(parses, startSymbol, show));
			
			
			//OPTIONAL
			/*HashSet<Rule> atis = cyk.readInGrammarATIS("input/atis_grammar.txt");
			for(Rule rule : atis){
				System.out.println();
				System.out.println(rule.getLhs().getValue() + " -->");
				for(Symbol s : rule.getRhs()){
					System.out.print(" " + s.getValue() + " ");
				}
				
			}*/
			
			/*HashMap<Terminal, HashSet<NonTerminal>> result = cyk.readInLexcionATIS("input/atis_lexicon.txt");
			for(Terminal t : result.keySet()){
				for(NonTerminal nt : result.get(t)){
				
					System.out.print(t.getValue() + " ");
					System.out.println(nt.getValue());
					
				}
			}*/
			
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
