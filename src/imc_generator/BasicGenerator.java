package imc_generator;

import parser.InfoTable;
import parser.TreeNode;
import scope_crawler.ScopeAnalyser;

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
                    fw.write("End\n");
                    System.out.print("End\n");
                }
                else
                {
                    recursiveCodeToFile(node.getChild(0), table, fw, 0);
                    fw.write("End\n\n");
                    System.out.print("End\n\n");
                    recursiveCodeToFile(node.getChild(1), table, fw, 0);
                }
            }
            else    //parent is R
            {
                if (node.childrenSize() == 1)
                {
                    recursiveCodeToFile(node.getChild(0), table, fw, numTabs + 1);
                    fw.write(getSetTabs(numTabs) + "Return\n\n");
                    System.out.print(getSetTabs(numTabs) + "Return\n\n");
                }
                else
                {
                    recursiveCodeToFile(node.getChild(0), table, fw, numTabs + 1);
                    fw.write(getSetTabs(numTabs) + "Return\n");
                    System.out.print(getSetTabs(numTabs) + "Return\n");
                    recursiveCodeToFile(node.getChild(1), table, fw,numTabs + 1);
                    //fw.write("\n\n");
                }
            }
        }

        // R
        if (node.tokenClass.equals("R"))
        {
            String writeString = getSetTabs(numTabs) + "/\'proc def for " + table.getNewName(node.getChild(1).tokenNo) + " aka " + node.getChild(1).snippet + "\'/\n";
            writeString += getSetTabs(numTabs) + table.getNewName(node.getChild(1).tokenNo) + ":\n";    //set label
            fw.write(writeString);
            System.out.print(writeString);
            recursiveCodeToFile(node.getChild(2),table, fw, numTabs);
            //fw.write("Return\n\n"); //return to original statement
        }


        // I->halt
        if (node.tokenClass.equals("keyword") && node.snippet.equals("halt"))
        {
            System.out.print(getSetTabs(numTabs) + "End\n");
            fw.write(getSetTabs(numTabs) + "End\n");
            //fw.write("Return\n\n"); //return to original statement
        }

        // O
        if (node.tokenClass.equals("O"))
        {
            if (node.getChild(0).snippet.equals("input"))
            {
            //Removed because input can only be an integer
            /*    String tempNewName = table.getNewName(node.getChild(1).getChild(0).getChild(0).tokenNo);
                char tempType = table.getType2(node.getChild(1).getChild(0).getChild(0).tokenNo);
                if (table.getDefined(tempNewName) == 'u')
                {
                    if (tempType == 'n')
                    {
                        fw.write(getSetTabs(numTabs) + "Dim " + tempNewName + " As Integer\n");
                        System.out.print(getSetTabs(numTabs) + "Dim " + tempNewName + " As Integer\n");
                    }
                    else
                    {
                        fw.write(getSetTabs(numTabs) + "Dim " + tempNewName + " As String\n");
                        System.out.print(getSetTabs(numTabs) + "Dim " + tempNewName + " As String\n");
                    }
                    table.setDefined(tempNewName);
                }
                */
                //must first define the variable. first check if already defined
                fw.write(getSetTabs(numTabs) + "Input \"Input: \", "
                        + table.getNewName(node.getChild(1).getChild(0).getChild(0).tokenNo) + "\n");
                System.out.print(getSetTabs(numTabs) + "Input \"Input: \", "
                        + table.getNewName(node.getChild(1).getChild(0).getChild(0).tokenNo) + "\n");
                table.setDefined(table.getNewName(node.getChild(1).getChild(0).getChild(0).tokenNo));
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
        if (node.tokenClass.equals("Y"))
        {
            fw.write(getSetTabs(numTabs) + "Gosub " + table.getNewName(node.getChild(0).tokenNo) + "\n");
            System.out.print(getSetTabs(numTabs) + "Gosub " + table.getNewName(node.getChild(0).tokenNo) + "\n");
        }

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
                fw.write(getSetTabs(numTabs) + "On 1 - (");
                System.out.print(getSetTabs(numTabs) + "On 1 - (");
                recursiveCodeToFile(node.getChild(1), table, fw, numTabs);
                fw.write(") Goto endif" + currentIfCount + "\n");
                System.out.print(") Goto endif" + currentIfCount + "\n");
                recursiveCodeToFile(node.getChild(3), table, fw, numTabs + 1);
                fw.write(getSetTabs(numTabs) + "endif" + currentIfCount + ":\n");
                System.out.print(getSetTabs(numTabs) + "endif" + currentIfCount + ":\n");
            }
            else //with else
            {
                int currentIfCount = ifCount++;
                fw.write(getSetTabs(numTabs) + "On 1 - (");
                System.out.print(getSetTabs(numTabs) + "On 1 - (");
                recursiveCodeToFile(node.getChild(1), table, fw, numTabs);
                fw.write(") Goto notCondition" + currentIfCount + "\n");
                System.out.print(") Goto notCondition" + currentIfCount + "\n");
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

        // B
        if (node.tokenClass.equals("B"))
        {
            if (node.childrenSize() == 2) //not B
            {
                fw.write("1 - ");
                System.out.print("1 - ");
                recursiveCodeToFile(node.getChild(1), table, fw, numTabs);
                //fw.write(")");
                //System.out.print(")");
            }
            else if (node.getChild(0).snippet.equals("eq")) //eq
            {
                TreeNode tempName = node.getChild(1).getChild(0).getChild(0);
                fw.write("(1 - ((" + table.getNewName(tempName.tokenNo) + " = ");
                System.out.print("(1 - ((" + table.getNewName(tempName.tokenNo) + " = ");
                tempName = node.getChild(2).getChild(0).getChild(0);
                fw.write(table.getNewName(tempName.tokenNo) + ") + 1))");
                System.out.print(table.getNewName(tempName.tokenNo) + ") + 1))");
            }
            else if (node.getChild(0).tokenClass.equals("keyword")) // and or
            {
                fw.write("(");
                System.out.print("(");
                recursiveCodeToFile(node.getChild(1), table, fw, numTabs);
                if (node.getChild(0).snippet.equals("and"))
                {
                    fw.write(" AND ");
                    System.out.print(" AND ");
                }
                else
                {
                    fw.write(" OR ");
                    System.out.print(" OR ");
                }
                recursiveCodeToFile(node.getChild(2), table, fw, numTabs);
                fw.write(")");
                System.out.print(")");
            }
            else //< >
            {
                TreeNode tempName = node.getChild(0).getChild(0);
                if (node.getChild(1).snippet.equals("<"))
                {
                    fw.write("(1 - ((" + table.getNewName(tempName.tokenNo) + " < ");
                    System.out.print("(1 - ((" + table.getNewName(tempName.tokenNo) + " < ");
                }
                else
                {
                    fw.write("(1 - ((" + table.getNewName(tempName.tokenNo) + " > ");
                    System.out.print("(1 - ((" + table.getNewName(tempName.tokenNo) + " > ");
                }
                tempName = node.getChild(2).getChild(0);
                fw.write(table.getNewName(tempName.tokenNo) + ") + 1))");
                System.out.print(table.getNewName(tempName.tokenNo) + ") + 1))");
            }
        }

        // Z
        if (node.tokenClass.equals("Z"))
        {
            fw.write(getSetTabs(numTabs) + "/\'loop\'/\n");
            System.out.print(getSetTabs(numTabs) + "/\'loop\'/\n");
            if (node.childrenSize() == 3) //while loop
            {
                int currentWhileCount = whileCount++;
                fw.write(getSetTabs(numTabs) + "while" + currentWhileCount+ ":\n");
                System.out.print(getSetTabs(numTabs) + "while" + currentWhileCount + ":\n");
                fw.write("On (1 - (");
                System.out.print("On (1 - (");

                recursiveCodeToFile(node.getChild(1), table, fw, numTabs);

                fw.write(getSetTabs(numTabs) + ")) Goto endwhile" + currentWhileCount + "\n");
                System.out.print(getSetTabs(numTabs) + ")) Goto endwhile" + currentWhileCount + "\n");

                recursiveCodeToFile(node.getChild(2), table, fw, numTabs + 1);

                fw.write(getSetTabs(numTabs) + "Goto while" + currentWhileCount + "\n");
                System.out.print(getSetTabs(numTabs) + "Goto while" + currentWhileCount + "\n");
                fw.write(getSetTabs(numTabs) + "endwhile" + currentWhileCount + ":\n");
                System.out.print(getSetTabs(numTabs) + "endwhile" + currentWhileCount + ":\n");

            }
            else //for loop
            {
                int currentForCount = forCount++;
                TreeNode tempName = node. getChild(1).getChild(0);
                System.out.print(getSetTabs(numTabs) + "Dim "
                            + table.getNewName(tempName.tokenNo) + " As Integer\n"
                        + getSetTabs(numTabs) + table.getNewName(tempName.tokenNo)
                            + " = " + node.getChild(3).snippet + "\n"
                        + getSetTabs(numTabs) + "for" + currentForCount + ":\n"
                        + getSetTabs(numTabs) + "On (");
                fw.write(getSetTabs(numTabs) + "Dim "
                        + table.getNewName(tempName.tokenNo) + " As Integer\n"
                        + getSetTabs(numTabs) + table.getNewName(tempName.tokenNo)
                        + " = " + node.getChild(3).snippet + "\n"
                        + getSetTabs(numTabs) + "for" + currentForCount + ":\n"
                        + getSetTabs(numTabs) + "On (");


                tempName = node.getChild(4).getChild(0);
                System.out.print(table.getNewName(tempName.tokenNo) + " < ");
                fw.write(table.getNewName(tempName.tokenNo) + " < " );
                tempName = node.getChild(6).getChild(0);
                System.out.print(table.getNewName(tempName.tokenNo) + ") + 1 Goto endfor" + currentForCount + "\n");
                fw.write(table.getNewName(tempName.tokenNo) + ") + 1 Goto endfor" + currentForCount + "\n");

                recursiveCodeToFile(node.getChild(12), table, fw, numTabs + 1);

                tempName = node.getChild(7).getChild(0);
                System.out.print(getSetTabs(numTabs) + table.getNewName(tempName.tokenNo) + " = "
                        + table.getNewName(tempName.tokenNo) + " + " + node.getChild(11).snippet + "\n"
                    + getSetTabs(numTabs) + "Goto for" + currentForCount + "\n"
                    + getSetTabs(numTabs) + "endfor" + currentForCount + ":\n");
                fw.write(getSetTabs(numTabs) + table.getNewName(tempName.tokenNo) + " = "
                        + table.getNewName(tempName.tokenNo) + " + " + node.getChild(11).snippet + "\n"
                        + getSetTabs(numTabs) + "Goto for" + currentForCount + "\n"
                        + getSetTabs(numTabs) + "endfor" + currentForCount + ":\n");

            }
        }
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
