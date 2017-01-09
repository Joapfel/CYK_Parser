The CYK-Parser takes 5 arguments:

1. args[0] the path to the grammar file
2. args[1] the path to the lexicon file
3. args[2] the startsymbol
4. args[3] the input string
5. args[4] the number of parses one wants to see printed to the console
           - if args[4] > the total amount of parses 
           - then total amount of parses is printed to the console 
	   - without a warning that the user typed in a greater number than expected

-----------------------------------------------------------------------------------------------------------------------------------------------
The output is of the form:

Total number of parses: 10
Number of parses shown: 1

(This is the actual parse in a more readable way: the "prettyParse" method is responsible for the transformation)

S [ADV [soon] S [NP [DET [the] N [man]] VP [VP [V [drinks] NP [NP [DET [an] N [espresso]] PP [P [on] NP [DET [the] N [table]]]]] ADV [later]]]]


(Those are the corresponding rules which were applied to get the parse above: the "showRules" method is responsible for extracting the rules out of 
the parse above)

S --> ADV S 
ADV --> soon 
S --> NP VP 
NP --> DET N 
DET --> the 
N --> man 
VP --> VP ADV 
VP --> V NP 
V --> drinks 
NP --> NP PP 
NP --> DET N 
DET --> an 
N --> espresso 
PP --> P NP 
P --> on 
NP --> DET N 
DET --> the 
N --> table 
ADV --> later
