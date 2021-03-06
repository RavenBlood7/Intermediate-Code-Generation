package type_crawler;

import parser.*;
import scope_crawler.ScopeAnalyser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeChecker {
	private char [] validTypes = {
		'\0', // signals error state i.e unbounded 0
		'w', // well-typed 1
		'p', // procedure 2
		'n', // numbers 3
		'o', // output 4
		's', // strings 5
		'b', // booleans 6
		'h', // halt 7
        'z'  // 'z'---default
	};

	public void doTypeChecking(TreeNode node, InfoTable table) throws ScopeAnalyser.StringException
	{

		System.out.println("\nStarting Type Checking process: ");
		System.out.println("--------------------------------------------------- ");
		initializeTable(node, table);
		visitAST(node, table);
		toFile(table);
		checkTree(table);
		System.out.println("\nCompleted Type Checking process: ");
		System.out.println("\t writing symbol table to file... ");
		toFile(table);
		System.out.println("--------------------------------------------------- ");
	}

	private void checkTree(InfoTable table)
	{
		if(table != null){
			int i = 0;
			while(i < table.size()){
				if(table.get(i).type == '\0'){
					System.out.println("Type Error Occurred at:");
					System.out.println("Node ["+table.get(i).tokenNo+"]");
					System.out.println("|\tClass: "+table.get(i).tokenClass);
					System.out.println("|\tSnippet: "+table.get(i).snippet);
					System.exit(0);
				}
				i++;
			}
		}
	}

	/**
	 * This function will recursively walk through the Abstract Syntaxt Tree (AST)
	 * and create table entries for each node within the AST
	 * @param node
	 * @param table
	 */
	private void initializeTable(TreeNode node, InfoTable table) {
		if(node == null){ return; }
		table.insert(node.tokenNo, node.tokenClass, node.snippet);
		if(node.childrenSize() > 0){
			int size = node.childrenSize();
			for(int i = 0; i < size; i++){
				initializeTable(node.getChild(i), table);
			}
		}
	}

	/**
	 * This function will recursively walk through the Abstract Syntaxt Tree (AST)
	 * and based on the Grammar rules defined for the SPL language,
	 * it will assign types to each node in the AST if successful, or report an error
	 * @param node
	 * @param table
	 */
	private void visitAST(TreeNode node, InfoTable table) throws ScopeAnalyser.StringException
	{
		//TableItem node = new TableItem(node1.tokenNo, node1.tokenClass, node1.snippet);

		// trivial case
		if(node == null){ return; }

		// Number Syntactic Category, Symbol: b
		if(node.tokenClass.equals("integer") || node.tokenClass.equals("number")){
			if(node.type == '\0'){
				node.type = validTypes[3];
				table.setType(findIndex(node, table), validTypes[3]);
			}
		}

		// user-defined name Syntactic Category, Symbol: u
		if(node.tokenClass.equals("user-defined name")){
			if(node.getParent().tokenClass.equals("N")){
				node.type = validTypes[3]; // number
				table.setType(findIndex(node, table), validTypes[3]);
			} else if(node.getParent().tokenClass.equals("S")){
				node.type = validTypes[5]; // string
				table.setType(findIndex(node, table), validTypes[5]);
			} else {
				reportError(node, "", "");
			}
		}

		// short string Syntactic Category, Symbol: s
		if(node.tokenClass.equals("short string")){
			if(node.type == '\0'){
				node.type = validTypes[5];
				table.setType(findIndex(node, table), validTypes[5]);
			}
		}

		// Boolean Syntactic Category, Symbol: e, n, a, o
		Pattern p = Pattern.compile("eq|not|and|or");
		Matcher m = p.matcher(node.tokenClass);
		if(m.find()){
			if(node.type == '\0'){
				node.type = validTypes[6]; // booleans
				table.setType(findIndex(node, table), validTypes[6]);
			}
		}

		// NVAR Syntactic Category, Symbol: N
		if(node.tokenClass.equals("N")){
			if(node.type == '\0'){
				visitAST(node.getChild(0), table);
				if (node.getChild(0).type == validTypes[3]){
					node.type = validTypes[3]; // number
					table.setType(findIndex(node, table), validTypes[3]);
				} else {
					reportError(node, "number", getTypeOf(node.type));
				}
			}
		}

		// SVAR Syntactic Category, Symbol: S
		if(node.tokenClass.equals("S")){
			if(node.type == '\0'){
				visitAST(node.getChild(0), table);
				if(node.getChild(0).type == validTypes[5]){
					node.type = validTypes[5]; //string
					table.setType(findIndex(node, table), validTypes[5]);
				} else {
					reportError(node, "String", getTypeOf(node.getChild(0).type));
				}
			}
		}

		// Variable Syntactic Category, Symbol: V
		if(node.tokenClass.equals("V")){
			if(node.type == '\0'){
				visitAST(node.getChild(0), table);
				if(node.getChild(0).type == validTypes[3] || node.getChild(0).type == validTypes[5]){
					node.type = validTypes[4];
					table.setType(findIndex(node, table), validTypes[4]); // output
				} else {
					reportError(node, "Output", getTypeOf(node.getChild(0).type));
				}
			}
		}

		// Calculation Syntactic Category Symbol: L
		if(node.tokenClass.equals("L")){
			if(node.type == '\0'){
				visitAST(node.getChild(1), table);
				visitAST(node.getChild(2), table);
				if(node.getChild(1).type == validTypes[3] && node.getChild(2).type == validTypes[3]){
					node.getChild(0).type = validTypes[8]; // default
					table.setType(findIndex(node.getChild(0), table), validTypes[8]);//default
					node.type = validTypes[3]; // number
					table.setType(findIndex(node, table), validTypes[3]);
				} else {
					reportError(node, "Number", getTypeOf(node.getChild(1).type));
				}
			}
		}

		// Boolean Syntactic Category, Symbol: B
		if(node.tokenClass.equals("B")){
			if(node.type == '\0'){
				if(node.childrenSize() == 2){ // not
					//visitAST(node.getChild(0), table);
					visitAST(node.getChild(1), table);
					if(node.getChild(1).type == validTypes[6]){
						node.getChild(0).type = validTypes[8]; // default
						table.setType(findIndex(node.getChild(0), table), validTypes[8]);
						node.type = validTypes[6]; // boolean
						table.setType(findIndex(node, table), validTypes[6]);
					} else {
						reportError(node, "Boolean", getTypeOf(node.getChild(1).type));
					}
				} else if (node.childrenSize() == 3) { // eq, and, or, <, >
                    if (node.getChild(0).tokenClass.equals("keyword"))  // eq, and, or
                    {
                        if (node.getChild(0).snippet.equals("eq")) {    //eq
                                //get type of first V
                                TreeNode tempName = node.getChild(1).getChild(0).getChild(0);
                                char tempType1 = table.reverseTypeLookup(tempName.tokenNo, tempName.snippet);

                                node.getChild(1).type = tempType1;
                                table.setType(findIndex(node.getChild(1), table), tempType1);
                                if (tempType1 == 'n')
                                    node.getChild(1).getChild(0).tokenClass = "N";
                                else
                                    node.getChild(1).getChild(0).tokenClass = "S";
                                node.getChild(1).getChild(0).type = tempType1;
                                table.setType(findIndex(node.getChild(1).getChild(0), table), tempType1);
                                tempName.type = tempType1;
                                table.setType(findIndex(tempName, table), tempType1);

                                //get type of second V
                                tempName = node.getChild(2).getChild(0).getChild(0);
                                char tempType2 = table.reverseTypeLookup(tempName.tokenNo, tempName.snippet);

                                node.getChild(2).type = tempType2;
                                table.setType(findIndex(node.getChild(2), table), tempType2);
                                if (tempType1 == 'n')
                                    node.getChild(2).getChild(0).tokenClass = "N";
                                else
                                    node.getChild(2).getChild(0).tokenClass = "S";
                                node.getChild(2).getChild(0).type = tempType2;
                                table.setType(findIndex(node.getChild(2).getChild(0), table), tempType2);
                                tempName.type = tempType2;
                                table.setType(findIndex(tempName, table), tempType2);


                                node.getChild(0).type = validTypes[8]; // default
                                table.setType(findIndex(node.getChild(0), table), validTypes[8]);

                                if (tempType1 == tempType2)
                                {
                                    node.type = validTypes[6]; // boolean
                                    table.setType(findIndex(node, table), validTypes[6]);
                                }
                        } else { //and/or
                            visitAST(node.getChild(1), table);
                            visitAST(node.getChild(2), table);
                            if (node.getChild(1).type == 'b' && node.getChild(2).type == 'b') {
                                node.getChild(0).type = validTypes[8]; // default
                                table.setType(findIndex(node.getChild(0), table), validTypes[8]);
                                node.type = validTypes[6]; // boolean
                                table.setType(findIndex(node, table), validTypes[6]);
                            } else {
                                reportError(node, "Boolean", getTypeOf(node.getChild(1).type));
                            }
                        }
                    } else // < , >
                    {
                        visitAST(node.getChild(0), table);
                        visitAST(node.getChild(2), table);
                        if (node.getChild(0).type == 'n' && node.getChild(2).type == 'n') {
                            node.getChild(1).type = validTypes[8]; // default
                            table.setType(findIndex(node.getChild(1), table), validTypes[8]);
                            node.type = validTypes[6]; // boolean
                            table.setType(findIndex(node, table), validTypes[6]);
                        }
                    }
                }

				/*	visitAST(node.getChild(2), table);
					if(node.getChild(1).type == validTypes[4] && node.getChild(2).type == validTypes[4]){ // VAR
						node.type = validTypes[6]; // boolean
						table.setType(findIndex(node, table), validTypes[6]);
					} else if(node.getChild(0).type == validTypes[3] && node.getChild(2).type == validTypes[3]){ // NVAR
						node.type = validTypes[6]; // boolean
						table.setType(findIndex(node, table), validTypes[6]);
					} else if(node.getChild(0).type == validTypes[6] && node.getChild(2).type == validTypes[6]){ // Boolean
						node.type = validTypes[6]; // boolean
						table.setType(findIndex(node, table), validTypes[6]);
					} else {
						if(node.getChild(0).type != validTypes[4] && node.getChild(0).type != validTypes[3] && node.getChild(0).type != validTypes[6])
							reportError(node, "Boolean", getTypeOf(node.getChild(0).type));
						else if (node.getChild(1).type != validTypes[4] && node.getChild(1).type != validTypes[3] && node.getChild(1).type != validTypes[6])
							reportError(node, "Boolean", getTypeOf(node.getChild(1).type));
						else
							reportError(node, "", "");
					}

				} else {
					System.out.println("Boolean Expression Error: Too many arguments");
				}*/
			}
		}

		// Conditional Branch Syntactic Category, Symbol: W
		if(node.tokenClass.equals("W")){
			if(node.type == '\0'){
				visitAST(node.getChild(1), table); // bool
				visitAST(node.getChild(3), table); // code
				if(node.childrenSize() == 4) {
			   		if(node.getChild(1).type == validTypes[6] && node.getChild(3).type == validTypes[1]){
				   		node.getChild(0).type = validTypes[8]; // default
				   		table.setType(findIndex(node.getChild(0), table), validTypes[8]);
				   		node.getChild(2).type = validTypes[8]; // default
				   		table.setType(findIndex(node.getChild(2), table), validTypes[8]);
				   		node.type = validTypes[1]; // well-typed
				   		table.setType(findIndex(node, table), validTypes[1]);
			   		} else {
			   			if(node.getChild(1).type != validTypes[6])
			   				reportError(node, "Well-Typed", getTypeOf(node.getChild(1).type));
			   			else if (node.getChild(3).type != validTypes[1])
							reportError(node, "Well-Typed", getTypeOf(node.getChild(3).type));
			   			else
							reportError(node, "", "");
			   		}
				} else if(node.childrenSize() == 6){
					visitAST(node.getChild(5), table);
					if(node.getChild(1).type == validTypes[6] &&
						node.getChild(3).type == validTypes[1] &&
						node.getChild(5).type == validTypes[1]){
                        node.getChild(0).type = validTypes[8]; // default
                        table.setType(findIndex(node.getChild(0), table), validTypes[8]);
                        node.getChild(2).type = validTypes[8]; // default
                        table.setType(findIndex(node.getChild(2), table), validTypes[8]);
                        node.getChild(4).type = validTypes[8]; // default
                        table.setType(findIndex(node.getChild(4), table), validTypes[8]);
						node.type = validTypes[1]; // well-typed
						table.setType(findIndex(node, table), validTypes[1]);
					} else {
						if(node.getChild(1).type != validTypes[6])
							reportError(node, "Well-Typed", getTypeOf(node.getChild(1).type));
						else if (node.getChild(3).type != validTypes[1])
							reportError(node, "Well-Typed", getTypeOf(node.getChild(3).type));
						else if (node.getChild(5).type != validTypes[1])
							reportError(node, "Well-Typed", getTypeOf(node.getChild(5).type));
						else
							reportError(node, "", "");
					}
				} else {
					System.out.println("Conditional Branch Expression Error: Invalid number of arguments");
				}
			}
		}

		// Intermediate Syntactic Category, Symbol: T, U
		//not counting for T
		if(node.tokenClass.equals("U")){
			if(node.type == '\0'){
				visitAST(node.getChild(0), table);
				if (node.getChild(0).type == 'n') {
					node.type = validTypes[3]; // number
					table.setType(findIndex(node, table), validTypes[3]);
				}
				else if (node.getChild(0).type == 's')
				{
					node.type = 's'; // string
					table.setType(findIndex(node, table), 's');
				}
				/*if(node.getChild(0).type == validTypes[5])
				{ // SVAR
					node.type = validTypes[1]; // well-typed
					table.setType(findIndex(node, table), validTypes[1]);
				}
				else if(node.getChild(0).type == validTypes[3])
				{ // NVAR
					node.type = validTypes[1]; // well-typed
					table.setType(findIndex(node, table), validTypes[1]);
				}
				else
				{
					reportError(node, "Well-Typed", getTypeOf(node.getChild(0).type));
				}*/
			}
		}

		// Expression, Symbol: X
		if(node.tokenClass.equals("X")){
			if(node.type == '\0') {
                visitAST(node.getChild(0), table);
                if (node.getChild(0).type == 'n') {
                    node.type = validTypes[3]; // number
                    table.setType(findIndex(node, table), validTypes[3]);
                }else {
                    reportError(node, "Number", getTypeOf(node.getChild(0).type));
                }
            }
		}

		// Assign Syntactic Category, Symbol: A
		if(node.tokenClass.equals("A"))
		{
			if(node.type == '\0')
			{
				node.getChild(1).type = validTypes[8];//DEFAULT  for '='
				table.setType(findIndex(node.getChild(1), table), validTypes[8]);
				if (node.getChild(2).getChild(0).tokenClass.equals("S"))
				{
					TreeNode varName = 	node.getChild(2).getChild(0).getChild(0);
					char tempType = table.reverseTypeLookup(varName.tokenNo, varName.snippet);
					varName.type = tempType;
					table.setType2(varName.tokenNo, tempType);

					node.getChild(2).type = tempType;
					table.setType2(node.getChild(2).tokenNo, tempType);

					node.getChild(2).getChild(0).type = tempType;
					table.setType2(node.getChild(2).getChild(0).tokenNo, tempType);
				}
				else
				{
					visitAST(node.getChild(2), table);
				}
				if (node.getChild(2).type == 'n')
				{
					node.getChild(0).type = 'n';//update T
					table.setType(findIndex(node.getChild(0), table), 'n');
					node.getChild(0).getChild(0).type = 'n';//update S/N make it N
					node.getChild(0).getChild(0).tokenClass = "N";//update S/N make it N
					table.setType(findIndex(node.getChild(0).getChild(0), table), 'n');
					node.getChild(0).getChild(0).getChild(0).type = 'n';//update u
					table.setType(findIndex(node.getChild(0).getChild(0).getChild(0), table), 'n');

					node.type = 'w';//update u
					table.setType(findIndex(node, table), 'w');
				}
				else if (node.getChild(2).type == 's')
				{
					node.getChild(0).type = 's';//update T
					table.setType(findIndex(node.getChild(0), table), 's');
					node.getChild(0).getChild(0).type = 's';//update S/N make it N
					node.getChild(0).getChild(0).tokenClass = "S";//update S/N make it N
					table.setType(findIndex(node.getChild(0).getChild(0), table), 's');
					node.getChild(0).getChild(0).getChild(0).type = 's';//update u
					table.setType(findIndex(node.getChild(0).getChild(0).getChild(0), table), 's');

					node.type = 'w';//update u
					table.setType(findIndex(node, table), 'w');
				}
				/*
				if (node.getChild(2).getChild(0).tokenClass.equals("X"))
				{
				    if (node.getChild(2).getChild(0).type == 'n')
                    {
                        node.type = validTypes[1];
                        table.setType(findIndex(node, table), validTypes[1]);
                    }
                }
                else if(node.getChild(0).getChild(0).type == validTypes[5] && node.getChild(2).getChild(0).type == validTypes[5])
				{
					node.type = validTypes[1];
					table.setType(findIndex(node, table), validTypes[1]);
				}
				else if(node.getChild(0).getChild(0).type == validTypes[3] && node.getChild(0).getChild(2).type == validTypes[3])
				{
					node.type = validTypes[1];
					table.setType(findIndex(node, table), validTypes[1]);
				}
				else
				{
					reportError(node, "Well-Typed", getTypeOf(node.getChild(0).type));
				}
				*/
			}
		}

		// Conditional Loop Syntactic Category, Symbol: Z
		if(node.tokenClass.equals("Z")){
			if(node.type == '\0'){
				if(node.childrenSize() == 3){
					visitAST(node.getChild(1), table);
					visitAST(node.getChild(2), table);
					if(node.getChild(1).type == validTypes[6] && node.getChild(2).type == validTypes[1]){
						node.getChild(0).type = validTypes[8]; // default
				   		table.setType(findIndex(node.getChild(0), table), validTypes[8]);
						node.type = validTypes[1]; // well-typed
				   		table.setType(findIndex(node, table), validTypes[1]);
					} else {
						if(node.getChild(1).type != validTypes[6])
							reportError(node, "Well-Typed", getTypeOf(node.getChild(1).type));
						else if(node.getChild(2).type != validTypes[1])
							reportError(node, "Well-Typed", getTypeOf(node.getChild(2).type));
						else
							reportError(node, "", "");
					}
				} else if (node.childrenSize() == 13){ // for loop
					Boolean isOk = true;
					visitAST(node.getChild(1), table);
					visitAST(node.getChild(3), table);
					if(node.getChild(1).type != validTypes[3] ||
					   node.getChild(3).type != validTypes[3]){
						isOk = false;
						if(node.getChild(1).type == validTypes[3])
							reportError(node, "Well-Typed", getTypeOf(node.getChild(3).type));
						else
							reportError(node, "Well-Typed", getTypeOf(node.getChild(1).type));
					}
					if(isOk){
						visitAST(node.getChild(4), table);
						visitAST(node.getChild(6), table);
						if(node.getChild(4).type != validTypes[3] ||
						   node.getChild(6).type != validTypes[3]){
							isOk = false;
							if(node.getChild(4).type == validTypes[3])
								reportError(node, "Well-Typed", getTypeOf(node.getChild(6).type));
							else
								reportError(node, "Well-Typed", getTypeOf(node.getChild(4).type));
						}
						if(isOk){
							visitAST(node.getChild(7), table);
							visitAST(node.getChild(10), table);
							visitAST(node.getChild(11), table);
							if(node.getChild(7).type != validTypes[3] ||
							  node.getChild(10).type != validTypes[3] ||
							  node.getChild(11).type != validTypes[3]){
								isOk = false;
								if(node.getChild(7).type == validTypes[3]) {
									if (node.getChild(10).type == validTypes[3])
										reportError(node, "Well-Typed", getTypeOf(node.getChild(11).type));
									else
										reportError(node, "Well-Typed", getTypeOf(node.getChild(10).type));
								} else if(node.getChild(10).type == validTypes[3]) {
									if (node.getChild(7).type == validTypes[3])
										reportError(node, "Well-Typed", getTypeOf(node.getChild(11).type));
									else
										reportError(node, "Well-Typed", getTypeOf(node.getChild(7).type));
								} else if(node.getChild(11).type == validTypes[3]) {
									if (node.getChild(7).type == validTypes[3])
										reportError(node, "Well-Typed", getTypeOf(node.getChild(10).type));
									else
										reportError(node, "Well-Typed", getTypeOf(node.getChild(7).type));
								} else {
									reportError(node, "", "");
								}
							}
							if(isOk){
								visitAST(node.getChild(12), table);
								if(node.getChild(12).type != validTypes[1]){
									isOk = false;
									reportError(node, "Well-Typed", getTypeOf(node.getChild(12).type));
								}
							}
						}
					}

					if(isOk){
						node.getChild(0).type = validTypes[8]; // default
				   		table.setType(findIndex(node.getChild(0), table), validTypes[8]);
						node.getChild(2).type = validTypes[8]; // default
				   		table.setType(findIndex(node.getChild(2), table), validTypes[8]);
						node.getChild(5).type = validTypes[8]; // default
				   		table.setType(findIndex(node.getChild(5), table), validTypes[8]);
						node.getChild(8).type = validTypes[8]; // default
				   		table.setType(findIndex(node.getChild(8), table), validTypes[8]);
						node.getChild(9).type = validTypes[8]; // default
				   		table.setType(findIndex(node.getChild(9), table), validTypes[8]);
						node.type = validTypes[1]; // well-typed
				   		table.setType(findIndex(node, table), validTypes[1]);
					} else {
						reportError(node, "", "");
					}
				} else {
					System.out.println("Conditional Loop Expression Error: Invalid number of arguments");
				}
			}
		}

		if(node.tokenClass.equals("Q")){
			if(node.type == '\0'){
				visitAST(node.getChild(0), table);
				if(node.getChild(0).type == validTypes[1]){
					node.type = validTypes[1]; // well-typed
					table.setType(findIndex(node, table), validTypes[1]);
				} else {
					reportError(node, "Well-Typed", getTypeOf(node.getChild(0).type));
				}
			}
			//this means tree is done, so check if all have type symbols
		}

		// P Syntactic Category : PROG
		if(node.tokenClass.equals("P")){
			if (node.childrenSize() == 1){
				visitAST(node.getChild(0), table);
				if(node.type == '\0' && node.getChild(0).type == 'w'){
					node.type = validTypes[1];
					table.setType(findIndex(node, table), validTypes[1]);
				} else {
					reportError(node, "Well-Typed", getTypeOf(node.getChild(0).type));
				}
			}
			else if (node.childrenSize() == 2){
				visitAST(node.getChild(0), table);
				visitAST(node.getChild(1), table);
				if (node.getChild(0).type == 'w' && node.getChild(1).type == 'w'){
					node.type = validTypes[1];
					table.setType(findIndex(node, table), validTypes[1]);
				} else {
					reportError(node, "Well-Typed", getTypeOf(node.getChild(0).type));
				}
			}
			//this means tree is done, so check if all have type symbols
		}

		// D Syntactic Category : PROC_DEFS
		if(node.tokenClass.equals("D")){
			if (node.childrenSize() == 1){
				visitAST(node.getChild(0), table);
				if(node.type == '\0' && node.getChild(0).type == 'w'){
					node.type = validTypes[1];
					table.setType(findIndex(node, table), validTypes[1]);
				} else {
					reportError(node, "Well-Typed", getTypeOf(node.getChild(0).type));
				}
			}
			else if (node.childrenSize() == 2){
				visitAST(node.getChild(0), table);
				visitAST(node.getChild(1), table);
				if (node.type == '\0' && node.getChild(0).type == 'w' && node.getChild(1).type == 'w'){
					node.type = validTypes[1];
					table.setType(findIndex(node, table), validTypes[1]);
				} else {
					reportError(node, "Well-Typed", getTypeOf(node.getChild(0).type));
				}
			}
		}

		// R Syntactic Category : PROC
		if(node.tokenClass.equals("R")){
			visitAST(node.getChild(2), table);
			if(node.type == '\0' && node.getChild(1).type == '\0'
				&& node.getChild(2).type == 'w'){
				node.getChild(1).type = validTypes[2];
				table.setType(findIndex(node.getChild(1), table), validTypes[2]);
				node.getChild(0).type = validTypes[8];
				table.setType(findIndex(node.getChild(0), table), validTypes[8]);
				node.type = validTypes[1];
				table.setType(findIndex(node, table), validTypes[1]);
			}
		}

		// C Syntactic Category : CODE
		if(node.tokenClass.equals("C")){
			if (node.childrenSize() == 1){
				visitAST(node.getChild(0), table);
				if(node.type == '\0' && node.getChild(0).type == 'w'){
					node.type = validTypes[1];
					table.setType(findIndex(node, table), validTypes[1]);
				} else {
					reportError(node, "Well-Typed", getTypeOf(node.getChild(0).type));
				}
			}
			else if (node.childrenSize() == 2) {
				visitAST(node.getChild(0), table);
				visitAST(node.getChild(1), table);
				if (node.type == '\0' && node.getChild(0).type == 'w' && node.getChild(1).type == 'w'){
					node.type = validTypes[1];
					table.setType(findIndex(node, table), validTypes[1]);
				} else {
					reportError(node, "Well-Typed", getTypeOf(node.getChild(0).type));
				}
			}
		}

		// I Syntactic Category : INSTR
		if(node.tokenClass.equals("I")){
			if (node.childrenSize() >0){
				if (node.getChild(0).tokenClass.equals("keyword")){
					if (node.type == '\0'){
						node.getChild(0).type = validTypes[7]; // for halt
						table.setType(findIndex(node.getChild(0), table), validTypes[7]);
						node.type = validTypes[1];
						table.setType(findIndex(node, table), validTypes[1]);
					}
				}
				else  { //for O A W Z Y
					visitAST(node.getChild(0), table);
					if (node.type == '\0' && node.getChild(0).type == 'w'){
						node.type = validTypes[1];
						table.setType(findIndex(node, table), validTypes[1]);
					} else {
						reportError(node, "Well-Typed", getTypeOf(node.getChild(0).type));
					}
				}
			}
		}

		// O Syntactic Category : IO
		if(node.tokenClass.equals("O")){
			if (node.childrenSize() >0){
				if (node.type == '\0')
				{
                    node.getChild(0).type = validTypes[8];
                    table.setType(findIndex(node.getChild(0), table), validTypes[8]);

				    if (node.getChild(0).snippet.equals("input"))
                    {
                        node.getChild(1).type = 'n';
                        table.setType(findIndex(node.getChild(1), table), 'n');
                        node.getChild(1).getChild(0).type = 'n';
                        table.setType(findIndex(node.getChild(1).getChild(0), table), 'n');
                        node.getChild(1).getChild(0).tokenClass = "N";
                        node.getChild(1).getChild(0).getChild(0).type = 'n';
                        table.setType(findIndex(node.getChild(1).getChild(0).getChild(0), table), 'n');

                        node.type = validTypes[1];
                        table.setType(findIndex(node, table), validTypes[1]);
                    }
                    else //output
                    {
                        TreeNode tempName = node.getChild(1).getChild(0).getChild(0);
                        char tempType = table.reverseTypeLookup(tempName.tokenNo, tempName.snippet);
                        node.getChild(1).type = tempType;
                        table.setType(findIndex(node.getChild(1), table), tempType);
                        node.getChild(1).getChild(0).type = tempType;
                        table.setType(findIndex(node.getChild(1).getChild(0), table), tempType);
                        if (tempType == 'n')
                            node.getChild(1).getChild(0).tokenClass = "N";
                        else
                            node.getChild(1).getChild(0).tokenClass = "S";
                        node.getChild(1).getChild(0).getChild(0).type = tempType;
                        table.setType(findIndex(node.getChild(1).getChild(0).getChild(0), table), tempType);

                        node.type = validTypes[1];
                        table.setType(findIndex(node, table), validTypes[1]);
                    }
                    //visitAST(node.getChild(1), table);
					//node.getChild(1).type = validTypes[3];
					//table.setType(findIndex(node.getChild(1), table), validTypes[3]);
					//node.type = validTypes[1];
					//table.setType(findIndex(node, table), validTypes[1]);
				}
			}
		}

		// Y Syntactic Category : CALL
		if(node.tokenClass.equals("Y")){
			if (node.childrenSize() > 0){
				if (node.type == '\0'){
					node.getChild(0).type = validTypes[2];
					table.setType(findIndex(node.getChild(0), table), validTypes[2]);
					node.type = validTypes[1];
					table.setType(findIndex(node, table), validTypes[1]);
				}
			}
		}
	}

	private String getTypeOf(char type) {
		if(type == 'w'){
			return "Well-Typed";
		} else if (type == 'p'){
			return "Procedure";
		} else if (type == 'n'){
			return "Number";
		} else if(type == 'o'){
			return "Output";
		} else if (type == 's'){
			return "String";
		} else if (type == 'b'){
			return "Boolean";
		} else if (type == 'h'){
			return "Halt";
		} else {
			return "Unidentified Type";
		}
	}

	private void reportError(TreeNode node, String guess, String actual) throws ScopeAnalyser.StringException
	{
		System.out.println("Type Error Occurred at:");
		System.out.println("Node ["+node.tokenNo+"]");
		System.out.println("|\tClass: "+node.tokenClass);
		System.out.println("|\tSnippet: "+node.snippet);
		if(!guess.equals("") && !actual.equals("")) {
			System.out.println("Expected a type of " + guess);
			System.out.println("Received a type of " + actual);
		}
		System.exit(0);
		throw new ScopeAnalyser.StringException("");
	}

	private int findIndex(TreeNode node, InfoTable table){
		//TableItem item = new TableItem(node.tokenNo, node.tokenClass, node.snippet);
		//return 1;//table.index(item);

        for (int i = 0; i < table.size(); i++) {
            if (node.tokenNo == table.getItemAt(i).tokenNo)
                return i;
        }
        return 0;
    }
	public void toFile(InfoTable table)
	{
		try
		{
			String file = "SymbolTable";

			FileWriter fw = new FileWriter(file);
			fw.write(table.toString());
			fw.close();
		}
		catch (IOException e)
		{
			//e.printStackTrace();
			System.out.println("Error opening file: SymbolTable");
		}
	}
}
