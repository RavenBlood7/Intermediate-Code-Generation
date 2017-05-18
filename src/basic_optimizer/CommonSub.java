package basic_optimizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CommonSub
{
	ArrayList<VarExp> list = new ArrayList<VarExp>();
	
	private class VarExp //variable expression
	{		
		public String variable;
		public String expression;
	}
	
	public boolean eliminateCommonSubs()
	{
		preprocessing();
		boolean changed = false;
		try
		{
			//open file
			String line;
			FileReader fr = new FileReader("working.bas");
			BufferedReader br = new BufferedReader(fr);
			FileWriter fw = new FileWriter("optimized_basic.bas");

			//for each line
			String expr = "";
			String var = "";
			String var2 = "";
			while ((line = br.readLine()) != null)
			{
				if (line.indexOf("Input") != -1) //is input
				{
					var = getVarFromInputLine(line);
					update(var);
					removeFromList(var);
				}
				//if line is an Assignment (n0 = ...)
				if (isAssignment(line))
				{
					expr = getExprFromLine(line);
					var = getVarFromLine(line);
					//if ... already exists in list
					if (!(var2 = lookupExpression(expr)).equals("zzz"))
					{
						//replace ... with accompanying variable on line but not in list
						line = line.substring(0, line.indexOf('=') + 2) + var2;
						changed = true;
						//update n0
						//update(var, expr);
						addToList(var, expr);
						//if (expr.indexOf(var) != -1) // the variable appears in its own expression
						//{
						//	removeExpr(expr);
						//}
						removeFromList(var);
					}
					else
					{
						//add n0 and ... to list
						removeFromList(var);
						addToList(var, expr);
					}
					//removeFromList(var);
					update(var);
				}
				else if (isDirectAssignment(line))
				{
					var = getVarFromLine(line);
					removeFromList(var);
					update(var);
				}
				//write line
				if (!line.equals(var + " = " + var))
					fw.write(line + '\n');
			}

			//close file
			br.close();
			fr.close();
			fw.close();
		}
		catch (IOException e)
		{
			System.out.println("Error: could not open file generated_basic.bas or working.bas");
		}
		postProcessing();
		return changed;
	}

	private boolean isDirectAssignment(String line)
	{
		if (line.contains("=") && !line.contains("for"))
		{
			CommonSub cs = new CommonSub();
			String v1 = cs.getVarFromLine(line);
			String v2 = cs.getExprFromLine(line);

			if ((v1.charAt(0) == 'n' || v1.charAt(0) == 's')
					&& (v2.charAt(0) == 'n' || v2.charAt(0) == 's')
					&& v2.length() < 4)
				return true;
		}
		return false;
	}

	public void postProcessing()
	{
		try
		{
			String line;
			FileReader fr = new FileReader("optimized_basic.bas");
			BufferedReader br = new BufferedReader(fr);
			FileWriter fw = new FileWriter("working.bas");

			//for each line in infile
			while ((line = br.readLine()) != null)
			{
				fw.write(line + '\n');
			}

			br.close();
			fr.close();
			fw.close();
		}
		catch (IOException e)
		{
			System.out.println("Error: could not open file generated_basic.bas");
		}
	}

	public String getVarFromInputLine(String line)
	{
		return line.substring(line.indexOf(',') + 2);
	}

	private void removeExpr(String expression)
	{
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i).expression.equals(expression))
			{
				list.remove(i);
				i = -1;
			}
		}
	}

	public String getVarFromLine(String line)
	{
		return line.substring(0, line.indexOf('=') - 1); //-1 for space
	}

	public String getExprFromLine(String line)
	{
		return line.substring(line.indexOf('=') + 2); //+ 1 for space
	}

	public boolean isAssignment(String line)
	{
		//if (line.length() == 7 || line.length() == 8 || line.length() == 9) //is of the form n0 = n1 or not an assignment at all
		//	return false;
		if (line.indexOf('=') != -1)//if it has an assignment in it
		{
			char x = line.charAt(line.indexOf('=') + 2);
			if (( x == 'n' || x == 's') && (line.length() == 7 || line.length() == 8 || line.length() == 9))
				return false;

		}
		else return false;
		if (line.charAt(3) == '=' || line.charAt(4) == '=') //has an = sign
		{
			return true;
		}
		return false;
	}

	public void preprocessing()
	{
		try
		{
			String line;
			FileReader fr = new FileReader("working.bas");
			BufferedReader br = new BufferedReader(fr);
			FileWriter fw = new FileWriter("optimized_basic.bas");

			//for each line in infile
			while ((line = br.readLine()) != null)
			{
				fw.write(line + '\n');
			}

			br.close();
			fr.close();
			fw.close();
		}
		catch (IOException e)
		{
			System.out.println("Error: could not open file generated_basic.bas");
		}
	}

	private void addToList(String variable, String expression)
	{
		VarExp temp = new VarExp();
		temp.variable = variable;
		temp.expression = expression;
		list.add(temp);
	}	
	
	private void removeFromList(String variable)
	{
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i).variable.equals(variable))
			{
				list.remove(i);
				i = -1;
			}
		}
	}
	
	private String lookupExpression(String expression)
	{
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i).expression.equals(expression))
			{
				return list.get(i).variable;
			}
		}		
		return "zzz";
	}
	
	private void update(String variable)
	{
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i).expression.indexOf(variable) != -1)
			{
				list.remove(i);
				i = -1;
			}
		}		
	}
}