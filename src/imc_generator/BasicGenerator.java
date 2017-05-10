package imc_generator;

import parser.InfoTable;
import parser.TreeNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by juan on 5/10/17.
 */
public class BasicGenerator {

    private int ifCount = 0;
    private int whileCount = 0;
    private int forCount = 0;

    public void generateBasicCode(TreeNode root, InfoTable table)
    {
        try
        {
            System.out.println("\nStarting Intermediate (Basic) Code Generation process: ");
            System.out.println("--------------------------------------------------- ");

            String file = "generated_basic.bas";
            Scanner scan = new Scanner(System.in);
            FileWriter fw = new FileWriter(file);

            preprocessing(table);
            recursiveCodeToFile(root, table, fw, 0);

            System.out.println("--------------------------------------------------- ");
            System.out.println("\nSuccessfully completed Intermediate (Basic) Code Generation process: ");
            System.out.println("\t Writing code to file... ");
            System.out.println("--------------------------------------------------- ");

            scan.close();
            fw.close();
        }
        catch (IOException e)
        {
            System.out.println("Error opening file: gereated_basic.bas");
        }
    }

    private void preprocessing(InfoTable table)
    {
        table.setAllUndefined();
    }

    private void recursiveCodeToFile(TreeNode node, InfoTable table, FileWriter fw, int numTabs) throws IOException {
        // Q
        // D
        // C
        // I
        if (       node.tokenClass.equals("Q")
                || node.tokenClass.equals("D")
                || node.tokenClass.equals("C")
                || node.tokenClass.equals("I"))
        {
            for (int i = 0; i < node.childrenSize(); i++)
            {
                recursiveCodeToFile(node.getChild(i), table, fw, numTabs);
            }
        }

        // P
        if (node.tokenClass.equals("P"))
        {
            if (node.getParent().tokenClass.equals("Q"))
            {
                if (node.childrenSize() == 1)
                {
                    recursiveCodeToFile(node.getChild(0), table, fw, 0);
                    fw.write("End");
                }
                else
                {
                    recursiveCodeToFile(node.getChild(0), table, fw, 0);
                    fw.write("End\n\n");
                    recursiveCodeToFile(node.getChild(1), table, fw, 0);
                }
            }
            else    //parent is R
            {
                if (node.childrenSize() == 1)
                {
                    recursiveCodeToFile(node.getChild(0), table, fw, numTabs + 1);
                    fw.write(getSetTabs(numTabs) + "Return\n\n");
                }
                else
                {
                    recursiveCodeToFile(node.getChild(0), table, fw, numTabs + 1);
                    fw.write(getSetTabs(numTabs) + "Return\n");
                    recursiveCodeToFile(node.getChild(1), table, fw,numTabs + 1);
                    //fw.write("\n\n");
                }
            }
        }

        // R
        if (node.tokenClass.equals("R"))
        {
            String writeString = "\'proc def for " + node.getChild(1).newName + " aka " + node.getChild(1).snippet + "\n";
            writeString += node.getChild(1).newName + ":\n";    //set label
            fw.write(writeString);
            recursiveCodeToFile(node.getChild(2),table, fw, numTabs);
            //fw.write("Return\n\n"); //return to original statement
        }


        // I->halt
        if (node.tokenClass.equals("keyword") && node.snippet.equals("halt"))
        {
            fw.write(getSetTabs(numTabs) + "End\n");
            //fw.write("Return\n\n"); //return to original statement
        }

        // O
        if (node.tokenClass.equals("O"))
        {
            if (node.getChild(0).snippet.equals("input"))
            {
                //must first define the variable. first check if already defined
                fw.write(getSetTabs(numTabs) + "Input \"Input: \", "
                        + table.getNewName(node.getChild(1).getChild(0).getChild(0).tokenNo) + "\n");
                System.out.print(getSetTabs(numTabs) + "Input \"Input: \", "
                        + table.getNewName(node.getChild(1).getChild(0).getChild(0).tokenNo) + "\n");
            }
            else //is output
            {
                fw.write(getSetTabs(numTabs) + "Print "
                        + table.getNewName(node.getChild(1).getChild(0).getChild(0).tokenNo) + "\n");
                System.out.print(getSetTabs(numTabs)
                        + "Print " + table.getNewName(node.getChild(1).getChild(0).getChild(0).tokenNo) + "\n");
            }
        }

        // Y

        // A
        if (node.tokenClass.equals("A"))
        {
            String ret = "";
            String tempNewName = table.getNewName(node.getChild(0).getChild(0).getChild(0).tokenNo);
            char tempType = table.getType2(node.getChild(0).getChild(0).getChild(0).tokenNo);
            if (table.getDefined(tempNewName) == 'u')
            {
                if (tempType == 'n')
                    ret += getSetTabs(numTabs) + "Dim " + tempNewName + " As Integer\n";
                else
                    ret += getSetTabs(numTabs) + "Dim " + tempNewName + " As String\n";
                table.setDefined(tempNewName);
            }
            ret += getSetTabs(numTabs) + tempNewName + " = ";
            fw.write(ret);
            System.out.print(ret);
            recursiveCodeToFile(node.getChild(2), table, fw, numTabs);
        }

        // W if statement
        if (node.tokenClass.equals("W"))
        {
            fw.write(getSetTabs(numTabs) + "/\'if statement\'/\n");
            System.out.print(getSetTabs(numTabs) + "/\'if statement\'/\n");
            if (node.childrenSize() == 4) //without else
            {
                int currentIfCount = ifCount++;
                fw.write(getSetTabs(numTabs) + "On ");
                System.out.print(getSetTabs(numTabs) + "On ");
                recursiveCodeToFile(node.getChild(1), table, fw, numTabs);
                fw.write(" Goto endif" + currentIfCount + "\n");
                System.out.print("Goto endif" + currentIfCount + "\n");
                recursiveCodeToFile(node.getChild(3), table, fw, numTabs + 1);
                fw.write(getSetTabs(numTabs) + "endif" + currentIfCount + ":\n");
                System.out.print(getSetTabs(numTabs) + "endif" + currentIfCount + ":\n");
            }
            else //with else
            {
                int currentIfCount = ifCount++;
                fw.write(getSetTabs(numTabs) + "On ");
                System.out.print(getSetTabs(numTabs) + "On ");
                recursiveCodeToFile(node.getChild(1), table, fw, numTabs);
                fw.write(" Goto notCondition" + currentIfCount + "\n");
                System.out.print("Goto notCondition" + currentIfCount + "\n");
                recursiveCodeToFile(node.getChild(3), table, fw, numTabs + 1);
                fw.write(getSetTabs(numTabs) + "Goto endif" + currentIfCount + "\n");
                System.out.print(getSetTabs(numTabs) + "Goto endif" + currentIfCount + "\n");
                fw.write(getSetTabs(numTabs) + "notCondition" + currentIfCount + ":\n");
                System.out.print(getSetTabs(numTabs) + "notCondition" + currentIfCount + ":\n");
                recursiveCodeToFile(node.getChild(5), table, fw, numTabs + 1);
                fw.write(getSetTabs(numTabs) + "endif" + currentIfCount + ":\n");
                System.out.print(getSetTabs(numTabs) + "endif" + currentIfCount + ":\n");
            }
        }

        // Z
        // V
        // S
        // N
        // U
        if (node.tokenClass.equals("U"))
        {
            String ret = "";
            if (node.getChild(0).tokenClass.equals("S"))
            {
                ret += table.getNewName(node.getChild(0).getChild(0).tokenNo);
            }
            else if (node.getChild(0).tokenClass.equals("short string"))
            {
                ret += node.getChild(0).snippet;
            }
            else //is X
            {
                recursiveCodeToFile(node.getChild(0), table, fw, numTabs);
            }
            ret += "\n";
            fw.write(ret);
            System.out.print(ret);
        }

        // X
        if (node.tokenClass.equals("X"))
        {
            String ret = "";
            if (node.getChild(0).tokenClass.equals("N"))
            {
                ret += table.getNewName(node.getChild(0).getChild(0).tokenNo);
            }
            else if (node.getChild(0).tokenClass.equals("integer") || node.getChild(0).tokenClass.equals("number"))
            {
                ret += node.getChild(0).snippet;
            }
            else //is L
            {
                recursiveCodeToFile(node.getChild(0), table, fw, numTabs);
            }
            fw.write(ret);
            System.out.print(ret);
        }

        // L
        if (node.tokenClass.equals("L"))
        {
            fw.write("(");
            System.out.print("(");
            recursiveCodeToFile(node.getChild(1), table, fw, numTabs);
            if (node.getChild(0).snippet.equals("add"))
            {
                fw.write(" + ");
                System.out.print(" + ");
            }
            else if (node.getChild(0).snippet.equals("sub"))
            {
                fw.write(" - ");
                System.out.print(" - ");
            }
            else //is mult
            {
                fw.write(" * ");
                System.out.print(" * ");
            }
            recursiveCodeToFile(node.getChild(2), table, fw, numTabs);

            fw.write(")");
            System.out.print(")");
        }
    }

    private String getSetTabs(int numTabs)
    {
        String ret = "";
        for (int i = 0; i < numTabs; i++)
        {
            ret += "\t";
        }
        return ret;
    }
}