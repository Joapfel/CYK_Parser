package de.ws1617.pccl.parser;

import de.ws1617.pccl.tree.*;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

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
import java.util.LinkedList;

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
	public CYK(String pathGrammar, String pathLexicon) throws IOException {
		super();
		this.grammar = new HashSet<>();
		this.lexicon = new HashMap<>();
		this.grammar = readInGrammar(pathGrammar);
		this.lexicon = readInLexcion(pathLexicon);
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
				Tree<Symbol> tree = new Tree<Symbol>(nt);
				tree.addChild(new Tree<Symbol>(word));
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

						if (chart[i][j] != null && chart[j][k] != null) {

							for (Rule rule : grammar) {

								// if chart[i][j] and chart[j][k] can be reduced
								// to
								// [i][k] with the
								// current rule

								for (NonTerminal ij : chart[i][j].keySet()) {

									// only go into the second for loop if ij
									// equals
									// the first rhs element of the rule
									if (rule.getRhs().get(0).equals(ij)) {

										for (NonTerminal jk : chart[j][k].keySet()) {

											// now compare jk to the second rhs
											// element of the rule
											if (rule.getRhs().get(1).equals(jk)) {

												// for all possible combinations
												// of
												// trees add them to the value
												HashMap<NonTerminal, ArrayList<Tree<Symbol>>> result = new HashMap<>();
												result.put(rule.getLhs(), new ArrayList<>());

												if (chart[i][k] == null) {
													chart[i][k] = result;
												}

												for (Tree<Symbol> treesIJ : chart[i][j].get(ij)) {

													for (Tree<Symbol> treesJK : chart[j][k].get(jk)) {

														// add a new tree
														// poining
														// to the
														// new child nodes
														Tree<Symbol> newTree = new Tree<Symbol>(rule.getLhs());
														newTree.addChild(treesIJ);
														newTree.addChild(treesJK);

														// add all the new trees
														// to
														// a array list
														// in order to use this
														// list
														// as the value in the
														// HashMap

														// it only works if you
														// access the chart
														// directly for some
														// reason..though it
														// seems to be the same
														// result.get(rule.getLhs()).add(newTree);
														chart[i][k].get(rule.getLhs()).add(newTree);

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

		}

		return chart[0][length];
	}

	public HashSet<Rule> readInGrammar(String path) throws IOException {
		HashSet<Rule> result = new HashSet<>();

		String line = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));

		while ((line = br.readLine()) != null) {
			if (line.trim().equals(""))
				continue;

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
		return result;
	}

	public HashMap<Terminal, HashSet<NonTerminal>> readInLexcion(String path) throws IOException {
		HashMap<Terminal, HashSet<NonTerminal>> result = new HashMap<>();

		String line = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));

		while ((line = br.readLine()) != null) {
			if (line.trim().equals(""))
				continue;

			String[] splt = line.split("-->");
			String lhs = splt[0].trim();
			String rhs = splt[1].trim();
			NonTerminal leftHandSide = new NonTerminal(lhs);
			Terminal rightHandSide = new Terminal(rhs);
			if (!result.containsKey(leftHandSide)) {
				result.put(rightHandSide, new HashSet<>());
				result.get(rightHandSide).add(leftHandSide);
			} else {
				result.get(rightHandSide).add(leftHandSide);
			}

		}

		return result;
	}

	public HashSet<Rule> readInGrammarATIS(String path) throws IOException {
		HashSet<Rule> result = new HashSet<>();

		String line = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));

		Rule rule = new Rule(null, null);
		// lhs is assigned after a empty line
		NonTerminal lhsSymb = null;
		// rhs is assigned after the first line of a block
		ArrayList<Symbol> rhsList = null;
		boolean firstLine = true;

		while ((line = br.readLine()) != null) {

			// operation on lhs
			if (line.trim().equals("") || firstLine) {

				// needed only for the first block
				firstLine = false;

				// if it was empty read the next line which is lhs
				if (line.equals("")) {
					line = br.readLine();
				}

				// it should be only one word so no need to split
				String lhs = line.trim();

				// the rule left hand side
				lhsSymb = new NonTerminal(lhs);

				// create the rule
				rule.setLhs(lhsSymb);

				// go to the next line
				line = br.readLine();

			}
			if (!line.trim().equals("")) {
				// iterate over rhs
				String[] rhsSplit = line.split("\\s+");
				rhsList = new ArrayList<>();
				for (int i = 0; i < rhsSplit.length; i++) {
					String symb = rhsSplit[i];
					Symbol s = new NonTerminal(symb);
					rhsList.add(s);
				}
				rule.setRhs(rhsList);

			}
			if (rule.getLhs() != null && rule.getRhs() != null) {
				result.add(rule);
			}

		}

		br.close();
		return result;
	}

	public HashMap<Terminal, HashSet<NonTerminal>> readInLexcionATIS(String path) throws IOException {
		HashMap<Terminal, HashSet<NonTerminal>> result = new HashMap<>();

		String line = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));

		while ((line = br.readLine()) != null) {
			if (line.trim().equals(""))
				continue;

			String[] splt = line.split("\\s+");
			String lhs = splt[1].trim();
			String rhs = splt[0].trim();
			NonTerminal leftHandSide = new NonTerminal(lhs);
			Terminal rightHandSide = new Terminal(rhs);
			if (!result.containsKey(leftHandSide)) {
				result.put(rightHandSide, new HashSet<>());
				result.get(rightHandSide).add(leftHandSide);
			} else {
				result.get(rightHandSide).add(leftHandSide);
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

	/**
	 * changes the parses into more readable parses
	 * 
	 * @param slot
	 * @param startSymbol
	 * @return
	 */
	public ArrayList<String> prettyParse(HashMap<NonTerminal, ArrayList<Tree<Symbol>>> slot, NonTerminal startSymbol) {

		ArrayList<String> parses = new ArrayList<>();

		for (Tree<Symbol> tree : slot.get(startSymbol)) {

			String result = tree.toString();
			result = result.replaceAll("null", "");
			result = result.replaceAll(",", "");
			result = result.replaceAll(" \\]", "]");
			result = result.trim();

			parses.add(result);

		}

		return parses;
	}

	/**
	 * takes a string of the form S [ADV [soon] S [NP [DET [the] N [man]]]]...
	 * and forms it in to its rules S -> ADV S ADV -> soon S -> NP NP -> DET N
	 * DET -> the N -> man
	 * 
	 * @param prettyParse
	 */
	public ArrayList<ArrayList<String>> showRules(String prettyParse) {

		Stack<ArrayList<String>> stack = new Stack<>();
		ArrayList<ArrayList<String>> result = new ArrayList<>();

		String[] elements = prettyParse.split("\\s+");
		for (int i = 0; i < elements.length; i++) {

			if (elements[i].startsWith("[")) {
				StringBuilder sb = new StringBuilder();
				sb.append(elements[i - 1]);
				sb.append(" --> ");
				sb.append(elements[i].substring(0, elements[i].length()));
				String rule = sb.toString().replaceAll("\\[", "");
				rule = rule.replaceAll("\\]", "");

				String[] tmp = rule.split("\\s+");
				ArrayList<String> list = new ArrayList<>();
				for (String s : tmp) {
					list.add(s);
				}
				// add the list for further processing
				stack.push(list);
				// add rule to result
				result.add(list);
			}
			if (elements[i].endsWith("]")) {
				String wordAtI = elements[i];

				while (wordAtI.endsWith("]") && stack.size() > 1) {

					String lhs = stack.pop().get(0);
					String rhs = stack.peek().get(2);
					// if not the same add lhs to rhs
					if (!lhs.equals(rhs)) {
						ArrayList<String> list = stack.pop();
						String tmp = list.get(2);
						tmp = tmp + " " + lhs;
						list.set(2, tmp);
						stack.push(list);

					}
					wordAtI = wordAtI.substring(0, wordAtI.length() - 1);
				}
			}

		}

		return result;
	}

}
