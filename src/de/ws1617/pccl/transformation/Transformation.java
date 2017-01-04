package de.ws1617.pccl.transformation;

import de.ws1617.pccl.grammar.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

/**
 * -----------------------------------------------------MUSTERLÖSUNG----------------------------------------------------------
 * @author tobi
 *
 */
public class Transformation {

	private Grammar grammar;

	private Lexicon lexicon;


	public Transformation(Grammar g, Lexicon l){
		this.grammar = g;

		this.lexicon = l;
	}
	
	/**
	 * removes unreachable Rules
	 * @param start start symbol of the Grammar
	 */
	public void removeUnreachableRules(NonTerminal start){

		Grammar newGrammar = new Grammar();
		Lexicon newLexicon = new Lexicon();

		NonTerminal startSymbol = start;

		HashSet<NonTerminal> visited = new HashSet<>();
		Stack<NonTerminal> agenda = new Stack<>();

		agenda.push(startSymbol);
		
		//Check which NonTerminals can be reached (derived) by the start symbol
		while(!agenda.isEmpty()){

			NonTerminal n = agenda.pop();
			
			//If the current NonTerminal has already been processed, ignore it
			if(!visited.contains(n)){

				visited.add(n);

				for(ArrayList<Symbol> rightHandSide : this.grammar.getRuleForLHS(n)){

					for(Symbol s : rightHandSide){

						if(s instanceof NonTerminal){
							agenda.push((NonTerminal) s);
						}
					}
				}
			}
		}
		
		//Add those rules to the new Grammar, that have a derivable Nonterminal on the left hand side
		for(NonTerminal left : this.grammar.getNonTerminals()){

			if(visited.contains(left)){

				for(ArrayList<Symbol> rightHandSide : this.grammar.getRuleForLHS(left)){


					newGrammar.addRule(left, rightHandSide);
				}
			}
		}
		//Add those rules to the new Lexicon, that have a derivable Nonterminal on the left hand side
		for(NonTerminal left : this.lexicon.getNonTerminals()){

			if(visited.contains(left)){

				for(ArrayList<Terminal> rightHandSide : this.lexicon.getRules(left)){
					newLexicon.addRule(left, rightHandSide);
				}
			}
		}
		
		//Overwrite the Grammar and the Lexicon 
		this.grammar = newGrammar;
		this.lexicon = newLexicon;
	}
 	
 	/**
	 * Removes unit rules 
	 */
	public void removeUnitRules(){

		Grammar newGrammar = new Grammar();
		Lexicon newLexicon = new Lexicon();

		//Loop over right hand sides to identify unary rules
		for(NonTerminal nonTerminal : this.grammar.getNonTerminals()){

			for(ArrayList<Symbol> rightHandSide : this.grammar.getRuleForLHS(nonTerminal)){

				if(rightHandSide.size() == 1){
					
					// agenda to keep track of multiple unit rules; for example, rules like
					// A -> B
					// A -> C
					// A -> D
					// would result in a ArrayList containing <B, C, D> 
					Stack<ArrayList<NonTerminal>> agenda = new Stack<>();

					//This left hand side will definetely expand to a singular NonTerminal
					ArrayList<NonTerminal> initial = new ArrayList<>();
					initial.add(nonTerminal);
					agenda.push(initial);
					
					// visited-Stack containing all NonTerminals to which right hand sides will be attached to
					Stack<NonTerminal> visited = new Stack<>();
					
					while(!agenda.isEmpty()){
						
						//Retrieve always the first NonTerminal 
						//In case, there is only one element
						NonTerminal n = agenda.peek().get(0);
						
						//If visited does not contain the top element, add it 
						if(!visited.contains(n)){
							
							visited.push(n);

							ArrayList<NonTerminal> tmp = new ArrayList<>();
							
							agenda.push(tmp);
							
							//Go over all grammar rules for the top NonTerminal
							for(ArrayList<Symbol> rhs : this.grammar.getRuleForLHS(n)){

								//In case the right hand side is not unary
								if(rhs.size() > 1){

									//All NonTerminals seen so far expand to this right hand side
									for(NonTerminal l : visited){
										
										newGrammar.addRule(l, rhs);
									}

								}

								//Else, the right hand side of the new unary Rule is added to the agenda
								else{
									
									NonTerminal rhsNT = (NonTerminal) rhs.get(0);
									
									if(!visited.contains(rhsNT)){
									
										agenda.peek().add(rhsNT);
									}
								}
							}
							
							//All NonTerminals seen so far can also expand to lexical items
							for(ArrayList<Terminal> rhs : this.lexicon.getRules(n)){

								for(NonTerminal l : visited){
									
									newLexicon.addRule(l, rhs);
								}
							}
							
							//If there were no subsequent unit rules
							//pop the empty ArrayList from the stack 
							if(agenda.peek().isEmpty()){
								agenda.pop();
							}
						}
						//If the top NonTerminal is already contained in the visited stack
						else{
							
							//Remove the element at index 0, when there are 2 or more elements in the ArrayList
							if(agenda.peek().size() > 1){
								agenda.peek().remove(0);
							}
							
							//Else (if there is only one element) pop it
							else{
								agenda.pop();
							}
							
							//In either case, pop the top element of the visited stack
							//There are no right hand sides left to which this NonTerminal could expand
							visited.pop();
						}
					}
				}

				//If there is normal rule, it gets added to the new grammar 
				else{
					newGrammar.addRule(nonTerminal, rightHandSide);
				}
			}
		}
		
		//Add also all other Lexical Rules
		for(NonTerminal left : this.lexicon.getNonTerminals()){

			for(ArrayList<Terminal> rhs : this.lexicon.getRules(left)){
				newLexicon.addRule(left, rhs);
			}
		}
		
		//Overwrite the Grammar and the Lexicon
		this.grammar = newGrammar;
		this.lexicon = newLexicon;
	} 

	/**
	 * Sorts Grammar Rules by the length of their right hand sides
	 * @return  HashMap<NonTerminal, ArrayList<HashSet<ArrayList<Symbol>>>> containing the sorted rules
	 */
	public HashMap<NonTerminal, ArrayList<HashSet<ArrayList<Symbol>>>> sortRulesByRightHandSide(){

		HashMap<NonTerminal,ArrayList<HashSet<ArrayList<Symbol>>>> sortedRules = new HashMap<>();

		//Determine the max size of a rightHandSide for a NonTerminal n 
		for(NonTerminal n : this.grammar.getNonTerminals()){

			HashSet<ArrayList<Symbol>> rightHandSides = this.grammar.getRuleForLHS(n);
			
			int largestRightHandSide = 0;
			for(ArrayList<Symbol> rightHandSide : rightHandSides){
				
				int rightHandSize = rightHandSide.size();
				
				if(largestRightHandSide > rightHandSize){
					largestRightHandSide = rightHandSize;
				}
				
			}
			
			//Create the ArrayList of that size
			ArrayList<HashSet<ArrayList<Symbol>>> sortedRightHandSides = new ArrayList<>(largestRightHandSide + 1);

			//Fill the Array with the rules 
			for(ArrayList<Symbol> rightHandSide : rightHandSides){

				int rightHandSideSize = rightHandSide.size();

				if(sortedRightHandSides.get(rightHandSideSize) == null){

					sortedRightHandSides.add(rightHandSideSize, new HashSet<ArrayList<Symbol>>());

					sortedRightHandSides.get(rightHandSideSize).add(rightHandSide);
				}
				else{
					sortedRightHandSides.get(rightHandSideSize).add(rightHandSide);
				}
			}

			//Store the sorted rightHandSides under the NonTerminal in question
			sortedRules.put(n, sortedRightHandSides);
		}

		return sortedRules;
	}
	
	/**
	 * Returns the Grammar object
	 * @return current Grammar object
	 */
	public Grammar getGrammar(){
		return this.grammar;
	}

	/**
	 * Returns the Lexicon Object
	 * @return current Lexicon Object
	 */
	public Lexicon getLexicon(){
		return this.lexicon;
	}
}	