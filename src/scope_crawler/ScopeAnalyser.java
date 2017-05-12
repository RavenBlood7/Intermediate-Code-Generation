package scope_crawler;

import parser.InfoTable;
import parser.TreeNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ScopeAnalyser {
    SymbolTable stable = new SymbolTable();
    int newScope = 0;
    ArrayList<TreeNode> procCalls = new ArrayList<TreeNode>();

    public static class StringException extends Throwable
    {
        public String e;
        public StringException(String s)
        {
            e = "ERROR: " + s;
        }
    }

    public void doScopeAnalysis(TreeNode node, InfoTable table) throws StringException
    {
        System.out.println("\nStarting scope analysis process: ");
        System.out.println("--------------------------------------------------- ");
        try {
            recursiveScope(node, table, 0);
            postProcessing(table);
            System.out.println("\nSuccessfully completed scope analysis process: ");
            System.out.println("\t writing symbol table to file... ");
            toFile(table);
            System.out.println("--------------------------------------------------- ");

        }
        catch (StringException e)
        {
            System.out.println("Scope Analysis Error: " + e.e);
            System.out.println("Exiting scope checker...\n");
            throw e;
        }
    }

    private void postProcessing(InfoTable table)
    {
        for (int i = 0; i < procCalls.size(); i++)
        {
            for (int j = 0; j < table.size(); j++) {
                if (table.getItemAt(j).tokenNo != procCalls.get(i).tokenNo && table.getItemAt(j).type == 'p')
                {
                    if (table.getItemAt(j).snippet.equals(procCalls.get(i).snippet))
                    {
                        table.setScope(procCalls.get(i).tokenNo, table.getItemAt(j).scope);
                    }
                }

            }
        }
    }

    private void recursiveScope(TreeNode node, InfoTable table, int curScope) throws StringException
    {
        // Q
        // P
        // C
        // D
        // I
        // A
        // U
        // T
        // W
        // L
        // X
        // B
        // V
        if (       node.tokenClass.equals("Q")
                || node.tokenClass.equals("P")
                || node.tokenClass.equals("C")
                || node.tokenClass.equals("D")
                || node.tokenClass.equals("I")
                || node.tokenClass.equals("A")
                || node.tokenClass.equals("U")
                || node.tokenClass.equals("T")
                || node.tokenClass.equals("W")
                || node.tokenClass.equals("L")
                || node.tokenClass.equals("X")
                || node.tokenClass.equals("B")
                || node.tokenClass.equals("V"))
        {

            for (int i = 0; i < node.getChildren().size(); i++)
            {
                recursiveScope(node.getChild(i), table, curScope);
            }
        }
       /*
        if (node.tokenClass.equals("A"))
        {
            TreeNode tempLeft = node.getChild(0).getChild(0).getChild(0);
            if (!stable.lookup(tempLeft.snippet, table.getType2(tempLeft.tokenNo)))
                stable.bind(tempLeft.snippet, table.getType2(tempLeft.tokenNo));

            TreeNode tempRight = node.getChild(1).getChild(0);
            if (tempRight.tokenClass.equals("S"))
                tempRight = tempRight.getChild(0);
            if (!stable.lookup(tempRight.snippet, table.getType2(tempRight.tokenNo)))
                throw new StringException("assignment failed: undeclared variable: " + tempRight.snippet);
        }
        */

        // Z loops
        else if (node.tokenClass.equals("Z"))
        {
            if (node.getChildren().size() == 3)
            {
                for (int i = 0; i < node.getChildren().size(); i++)
                {
                    recursiveScope(node.getChild(i), table, curScope);
                }
            }
            else if (node.getChildren().size() == 13)
            {
                stable.enter();
                newScope++;
                stable.bind(node.getChild(1).getChild(0).snippet, table.getType2(node.getChild(1).getChild(0).tokenNo), newScope);
                table.setScope(node.getChild(1).getChild(0).tokenNo, newScope);
                stable.bind(node.getChild(4).getChild(0).snippet, table.getType2(node.getChild(4).getChild(0).tokenNo), newScope);
                table.setScope(node.getChild(4).getChild(0).tokenNo, newScope);
                if (!stable.lookup(node.getChild(6).getChild(0).snippet, table.getType2(node.getChild(6).getChild(0).tokenNo)))
                {
                    stable.bind(node.getChild(6).getChild(0).snippet, table.getType2(node.getChild(6).getChild(0).tokenNo), newScope);
                    table.setScope(node.getChild(6).getChild(0).tokenNo, newScope);
                }
                else
                {
                    table.setScope(node.getChild(6).getChild(0).tokenNo, curScope);
                }
                stable.bind(node.getChild(7).getChild(0).snippet, table.getType2(node.getChild(7).getChild(0).tokenNo), newScope);
                table.setScope(node.getChild(7).getChild(0).tokenNo, newScope);
                stable.bind(node.getChild(10).getChild(0).snippet, table.getType2(node.getChild(7).getChild(0).tokenNo), newScope);
                table.setScope(node.getChild(10).getChild(0).tokenNo, newScope);
                stable.exit();
               // newScope++;
                recursiveScope(node.getChild(12), table, curScope);
            }
        }

        // S
        // N
        else if (node.tokenClass.equals("N") || node.tokenClass.equals("S"))
        {
//            if (!stable.lookup(node.getChild(0).snippet, table.getType2(node.getChild(0).tokenNo)))
  //          {
    //            throw new StringException("undeclared variable: " + node.getChild(0).snippet);
      //      }
            if (!stable.lookup(node.getChild(0).snippet, table.getType2(node.getChild(0).tokenNo)))
            {
                stable.bind(node.getChild(0).snippet, table.getType2(node.getChild(0).tokenNo), curScope);
                table.setScope(node.getChild(0).tokenNo, curScope);
            }
            else
            {
                table.setScope(node.getChild(0).tokenNo, stable.findScope(node.getChild(0).snippet, table.getType2(node.getChild(0).tokenNo)));
            }
        }

        // R
        else if (node.tokenClass.equals("R"))
        {
            if (stable.lookup(node.getChild(1).snippet, table.getType2(node.getChild(1).tokenNo)))
            {
                throw new StringException("proc " + node.getChild(1).snippet + " already declared in this scope");
            }
            else
            {
                stable.bind(node.getChild(1).snippet, table.getType2(node.getChild(1).tokenNo), curScope);
                table.setScope(node.getChild(1).tokenNo, curScope);
                stable.enter();
                newScope++;
                recursiveScope(node.getChild(2), table, newScope);
                stable.exit();
            }
        }

        // O
        else if (node.tokenClass.equals("0"))
        {
                stable.bind(node.getChild(1).getChild(0).getChild(0).snippet, table.getType2(node.getChild(1).getChild(0).getChild(0).tokenNo), curScope);
                table.setScope(node.getChild(1).getChild(0).getChild(0).tokenNo, curScope);
        }

        // Y
        else if (node.tokenClass.equals("Y"))
        {
           // stable.bind(node.getChild(0).snippet, table.getType2(node.getChild(0).tokenNo));
           // table.setScope(node.getChild(0).tokenNo, curScope);
            procCalls.add(node.getChild(0));
          // if (!stable.lookup(node.getChild(0).snippet, table.getType2(node.getChild(0).tokenNo)))
          //  {
          //      throw new StringException("calling undefined proc: " + node.getChild(0).snippet);
          //  }
        }
        else
        {
            for (int i = 0; i < node.getChildren().size(); i++)
            {
                recursiveScope(node.getChild(i), table, curScope);
            }
        }

    }

    void toFile(InfoTable table)
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
    private int findIndex(TreeNode node, InfoTable table){
        //TableItem item = new TableItem(node.tokenNo, node.tokenClass, node.snippet);
        //return 1;//table.index(item);

        for (int i = 0; i < table.size(); i++) {
            if (node.tokenNo == table.getItemAt(i).tokenNo)
                return i;
        }
        return 0;
    }

}
