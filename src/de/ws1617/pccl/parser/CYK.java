package de.ws1617.pccl.parser;

import de.ws1617.pccl.tree.*;
import java.util.List;

import javax.naming.ldap.LdapName;
import javax.swing.plaf.synth.SynthSeparatorUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import de.ws1617.pccl.grammar.Grammar;
import de.ws1617.pccl.grammar.Lexicon;
import de.ws1617.pccl.grammar.NonTerminal;
import de.ws1617.pccl.grammar.Rule;
import de.ws1617.pccl.grammar.Symbol;
import de.ws1617.pccl.grammar.Terminal;

public class CYK {

	private HashMap<NonTerminal, ArrayList<Tree<Symbol>>>[][] chart;
	private HashSet<Rule> grammar;
	private HashMap<Terminal, HashSet<NonTerminal>> lexicon;

	/**
	 * Constructor
	 * 
	 * @param chart
	 * @throws IOException
	 */
	public CYK(String path, Lexicon lex) throws IOException {
		super();
		this.grammar = new HashSet<>();
		this.lexicon = new HashMap<>();
		this.grammar = readInGrammar(path);
		this.lexicon = readInLexcion(lex);
	}

	public void initialize(int length) {
		this.chart = new HashMap[length + 1][length + 1];
	}

	public HashMap<NonTerminal, ArrayList<Tree<Symbol>>> parse(String inputString) {

		int length = inputString.split("\\s+").length;
		initialize(length);
		
		// assign the input to terminals
		String[] in = inputString.split("\\s+");
		Terminal input[] = new Terminal[length];
		for (int i = 0; i < in.length; i++) {
			input[i] = new Terminal(in[i]);
		}

		// for all endpositions of constituents
		for (int k = 1; k <= length; k++) {
			Terminal word = input[k - 1];
			HashSet<NonTerminal> setOfNt = this.lexicon.get(word);

			for (NonTerminal nt : setOfNt) {

				// for storing in the two-dim-array
				HashMap<NonTerminal, ArrayList<Tree<Symbol>>> tmp = new HashMap<>();
				// in the value in the hashmap
				Tree<Symbol> tree = new Tree<Symbol>(word);
				// value in the hashmap
				ArrayList<Tree<Symbol>> listOfTrees = new ArrayList<>();

				listOfTrees.add(tree);
				tmp.put(nt, listOfTrees);
				chart[k - 1][k] = tmp;

				// for all startpositions of constituents
				for (int i = k - 2; i >= 0; i--) {

					// for all splitpoints
					for (int j = i + 1; j <= k - 1; j++) {

						// for all rules
						for (Rule rule : grammar) {

							// if chart[i][j] and chart[j][k] can be reduced to
							// [i][k] with the
							// current rule
							for (NonTerminal ij : chart[i][j].keySet()) {
								
								// only go into the second for loop if ij equals
								// the first rhs element of the rule
								if (rule.getRhs().get(0).equals(ij)) {

									for (NonTerminal jk : chart[j][k].keySet()) {

										// now compare jk to the second rhs
										// element of the rule
										if (rule.getRhs().get(1).equals(jk)) {

											// for all possible combinations of
											// trees
											for (Tree<Symbol> treesIJ : chart[i][j].get(ij)) {
												
												for (Tree<Symbol> treesJK : chart[j][k].get(jk)) {
													

													// add a new tree pointing
													// to the
													// new child nodes
													Tree<Symbol> newTree = new Tree<Symbol>(rule.getLhs());
													ArrayList<Tree<Symbol>> children = new ArrayList<>();
													children.add(0, new Tree<Symbol>(treesIJ.getData()));
													children.add(1, new Tree<Symbol>(treesJK.getData()));
													/*
													 * newTree.addChild(treesIJ)
													 * ;
													 * newTree.addChild(treesJK)
													 * ;
													 */
													newTree.setChildren(children);    

													// add all the new trees to
													// a array list
													// in order to use this list
													// as the value in the
													// HashMap
													ArrayList<Tree<Symbol>> value = new ArrayList<>();
													value.add(newTree);

													// fill the chart at [i][k]
													HashMap<NonTerminal, ArrayList<Tree<Symbol>>> result = new HashMap<>();
													result.put(rule.getLhs(), value);
													chart[i][k] = result;

												}

											}

										}
									}
								}

							}

						}
					}
				}
			}

		}

		return chart[0][length];
	}

	public HashSet<Rule> readInGrammar(String path) throws IOException {
		HashSet<Rule> result = new HashSet<>();

		String line = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));

		while ((line = br.readLine()) != null) {
			if (!line.trim().equals("")) {
				String[] splt = line.split("-->");
				String lhs = splt[0].trim();
				// holds two nonterminals because chomsky normal form
				String rhs = splt[1].trim();

				// the rule left hand side
				NonTerminal lhsSymb = new NonTerminal(lhs);

				// iterate over rhs
				String[] rhsSplit = rhs.split("\\s+");
				ArrayList<Symbol> rhsList = new ArrayList<>();
				for (String symbol : rhsSplit) {
					rhsList.add(new NonTerminal(symbol));
				}
				result.add(new Rule(lhsSymb, rhsList));
			}
		}
		return result;
	}

	public HashMap<Terminal, HashSet<NonTerminal>> readInLexcion(Lexicon lex) {
		HashMap<Terminal, HashSet<NonTerminal>> result = new HashMap<>();

		for (NonTerminal nt : lex.getNonTerminals()) {

			for (ArrayList<Terminal> lhs : lex.getRules(nt)) {

				HashSet<NonTerminal> tmp = new HashSet<>();
				tmp.add(nt);
				result.put(lhs.get(0), tmp);

			}

		}
		return result;
	}

	public HashMap<NonTerminal, ArrayList<Tree<Symbol>>>[][] getChart() {
		return chart;
	}

	public HashSet<Rule> getGrammar() {
		return grammar;
	}

	public void setGrammar(HashSet<Rule> grammar) {
		this.grammar = grammar;
	}

	public HashMap<Terminal, HashSet<NonTerminal>> getLexicon() {
		return lexicon;
	}

	public void setLexicon(HashMap<Terminal, HashSet<NonTerminal>> lexicon) {
		this.lexicon = lexicon;
	}

	public void setChart(HashMap<NonTerminal, ArrayList<Tree<Symbol>>>[][] chart) {
		this.chart = chart;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(chart);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CYK other = (CYK) obj;
		if (!Arrays.deepEquals(chart, other.chart))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CYK [chart=" + Arrays.toString(chart) + "]";
	}

	public String prettyParse(HashMap<NonTerminal, ArrayList<Tree<Symbol>>> slot) {

		StringBuilder sb = new StringBuilder();
		for (NonTerminal nt : slot.keySet()) {
			sb.append(nt.getValue() + " --> to \n");
			for (Tree<Symbol> tree : slot.get(sb)) {
				sb.append(tree.getData().getValue() + "\n\n");
			}

		}

		return sb.toString();
	}

}
