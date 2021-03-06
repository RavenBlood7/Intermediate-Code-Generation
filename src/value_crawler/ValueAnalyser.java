package value_crawler;

import parser.InfoTable;
import parser.TableItem;
import parser.TreeNode;
import scope_crawler.ScopeAnalyser;
import sun.reflect.generics.tree.Tree;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by juan on 4/27/17.
 */
public class ValueAnalyser {
    InfoTable myTable;
    ArrayList<TreeNode> procStack = new ArrayList<TreeNode>();


    private class StringException extends Throwable
    {
        public String e;
        public StringException(String s)
        {
            e = "ERROR: " + s;
        }
    }

    private void convertAllNames(InfoTable table)
    {
        TableItem tempItem;
        ValueTable vt = new ValueTable();
        //place all distinct variables into vt;
        for (int i = 0; i < table.size(); i++)
        {
            tempItem = table.getItemAt(i);
            if (tempItem.tokenClass.equals("user-defined name"))
            {
                if (!vt.contains(tempItem.snippet, tempItem.type, tempItem.scope))
                {
                    vt.add(tempItem.snippet, tempItem.type, tempItem.scope);
                }
            }
        }

        //convert all names in tables
        for (int i = 0; i < table.size(); i++)
        {
            tempItem = table.getItemAt(i);
            if (tempItem.tokenClass.equals("user-defined name"))
            {
                table.getItemAt(i).newName = vt.getNewName(tempItem.snippet, tempItem.type, tempItem.scope);
            }
        }
    }

    public void doValueAnalysis(TreeNode node, InfoTable table) throws ScopeAnalyser.StringException
    {
        myTable = table;
        convertAllNames(myTable);

        try
        {
            System.out.println("\nStarting value analysis process: ");
            System.out.println("--------------------------------------------------- ");
            //convertAllNames(table);
            preprocessing();
            recursiveValue(node);
            postprocessing();
            System.out.println("\nSuccessfully completed value analysis process: ");
            System.out.println("\t writing symbol table to file... ");
            toFile();
            System.out.println("--------------------------------------------------- ");
        }
        catch (StringException e)
        {
            System.out.println("Value Analysis Error: " + e.e);
            System.out.println("Exiting value checker...\n");
            throw new ScopeAnalyser.StringException(e.e);
        }

    }

    private void recursiveValue(TreeNode node) throws StringException
    {

        // T never reach

        // Q
        // P
        // D
        // C
        // I
        // V
        // X
        // L
        // W
        // B
        // U
        if (       node.tokenClass.equals("Q")
                || node.tokenClass.equals("P")
                || node.tokenClass.equals("C")
                || node.tokenClass.equals("D")
                || node.tokenClass.equals("I")
                || node.tokenClass.equals("V")
                || node.tokenClass.equals("X")
                || node.tokenClass.equals("L")
                || node.tokenClass.equals("W")
                || node.tokenClass.equals("B")
                || node.tokenClass.equals("U"))
        {
            for (int i = 0; i < node.childrenSize(); i++)
            {
                recursiveValue(node.getChild(i));
            }
        }

        // A
        if (node.tokenClass.equals("A"))
        {
            recursiveValue(node.getChild(2));
            //right hand side is defined
            myTable.setDefined(myTable.getNewName(node.getChild(0).getChild(0).getChild(0).tokenNo));
        }

        // O
        if (node.tokenClass.equals("O"))
        {
            TreeNode tempNode = node.getChild(1).getChild(0).getChild(0);
            if (node.getChild(0).snippet.equals("input"))
            {
                myTable.setDefined(myTable.getNewName(tempNode.tokenNo));
            }
            else//is output
            {
                if (myTable.getDefined(myTable.getNewName(tempNode.tokenNo)) == 'u')
                {
                    throw new StringException("undefined variable in output: " + tempNode.snippet);
                }
            }
        }

        // R
        if (node.tokenClass.equals("R"))
        {
            recursiveValue(node.getChild(2));
            myTable.setDefined(node.getChild(1).newName);
        }

        // S
        if (node.tokenClass.equals("S"))
        {
            if (myTable.getDefined(myTable.getNewName(node.getChild(0).tokenNo)) == 'u')
                throw new StringException("undefined variable: " + node.getChild(0).snippet);
        }

        // N
        if (node.tokenClass.equals("N"))
        {
            if (myTable.getDefined(myTable.getNewName(node.getChild(0).tokenNo)) == 'u')
                throw new StringException("undefined variable: " + node.getChild(0).snippet);
        }

        // Z
        if (node.tokenClass.equals("Z"))
        {
            if (node.childrenSize() == 3) //while loop
            {
                for (int i = 0; i < node.childrenSize(); i++)
                {
                    recursiveValue(node.getChild(i));
                }
            }
            else //for loop
            {
                TreeNode tempName = node.getChild(1).getChild(0);
                myTable.setDefined(myTable.getNewName(tempName.tokenNo));

                tempName = node.getChild(4).getChild(0);
                if (myTable.getDefined(myTable.getNewName(tempName.tokenNo)) == 'u')
                    throw new StringException("undefined variable: " + tempName.snippet);
                tempName = node.getChild(6).getChild(0);
                if (myTable.getDefined(myTable.getNewName(tempName.tokenNo)) == 'u')
                    throw new StringException("undefined variable: " + tempName.snippet);
                tempName = node.getChild(7).getChild(0);
                if (myTable.getDefined(myTable.getNewName(tempName.tokenNo)) == 'u')
                    throw new StringException("undefined variable: " + tempName.snippet);
                tempName = node.getChild(10).getChild(0);
                if (myTable.getDefined(myTable.getNewName(tempName.tokenNo)) == 'u')
                    throw new StringException("undefined variable: " + tempName.snippet);
                recursiveValue(node.getChild(12));
            }

            // Y proc call checked at the end
            if (node.tokenClass.equals("Y"))
            {
                procStack.add(node);
            }
        }
    }

    void preprocessing()
    {
        for (int i = 0; i < myTable.size(); i++)
        {
            if (myTable.getItemAt(i).tokenClass.equals("short string")
                    ||myTable.getItemAt(i).tokenClass.equals("integer"))
            {
                myTable.getItemAt(i).defined = 'd';
            }
        }
    }

    void postprocessing() throws StringException
    {
        for (int i = 0; i < procStack.size(); i++)
        {
            if (myTable.getDefined(procStack.get(i).getChild(0).newName) == 'u')
            {
                throw new StringException("undefined procedure: " + procStack.get(i).getChild(0).snippet);
            }
        }
    }
    void toFile()
    {
        try
        {
            String file = "SymbolTableAfterValue";

            FileWriter fw = new FileWriter(file);
            fw.write(myTable.toString());
            fw.close();
        }
        catch (IOException e)
        {
            //e.printStackTrace();
            System.out.println("Error opening file: SymbolTable");
        }
    }
}

