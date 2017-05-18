package basic_optimizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CopyPropagation
{
	ArrayList<VarExp> list = new ArrayList<VarExp>();
	
	private class VarExp //variable secondVar
	{		
		public String variable;
		public String secondVar;
	}
	public boolean propagate()
	{
		CommonSub cs = new CommonSub();
		cs.preprocessing();
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

				//if line is an assignment between one variable and another v1 = v2
				if (isDirectAssignment(line))
				{
					var = cs.getVarFromLine(line);
					var2 = cs.getExprFromLine(line);
					//see if it needs replacement
					if (lookupFirstVar(var2) != "zzz")
					{
						line = line.replaceAll(var2, lookupFirstVar(var2));
						var2 = lookupFirstVar(var2);
					}
					//if v1 does not exist
					if (lookupFirstVar(var) == "zzz")
					{
						addToList(var, var2);
					}
					else
					{
						update(var, var2);
					}
					purgeFromList(var);
				}
				//if line is assignment between v1 and anything else or input(v1)
				else if (line.contains("Input"))
				{
					var = cs.getVarFromInputLine(line);
					purgeFromList(var);
					removeFromList(var);
				}
				else if (isAssignment(line))
				{
					var = cs.getVarFromLine(line);
					expr = cs.getExprFromLine(line);
					for (int i = 0; i < list.size(); i++)
					{
						//run through string and look for v1
						//replace with corrosponding v2

						expr = expr.replaceAll(list.get(i).variable, list.get(i).secondVar);
					}
					line = var + " = " + expr;
					purgeFromList(var);
					removeFromList(var);
				}
				else

				{
					//for each v1 in list
					for (int i = 0; i < list.size(); i++)
					{
						//run through string and look for v1
						//replace with corrosponding v2
						line = line.replace(list.get(i).variable, list.get(i).secondVar);
					}
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
		//for each line
			//if line is an assignment between one variable and another
				//if v1 does not exist
					//add it
				//else
					//update it
			//if line is assignment between v1 and anything else or input(v1)
				//remove v1 from list
			//else
				//for each v1 in list
					//run through string and look for v1
					//replace with corrosponding v2
			//write line
		
		//close file
		//cs.postProcessing();
		return changed;
	}

	private void purgeFromList(String var)
	{
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i).secondVar.contains(var))
			{
				list.remove(i);
				i = -1;
			}
		}
	}

	private boolean isAssignment(String line)
	{
		return  (line.contains("=") && !line.contains("for"));
	}

	private boolean isDirectAssignment(String line)
	{
		if (isAssignment(line))
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

	private void addToList(String variable, String secondVar)
	{
		VarExp temp = new VarExp();
		temp.variable = variable;
		temp.secondVar = secondVar;
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
	
	private String lookupFirstVar(String firstVar)
	{
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i).variable.equals(firstVar))
			{
				return list.get(i).secondVar;
			}
		}		
		return "zzz";
	}

	private String lookupSecondVar(String secondVar)
	{
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i).secondVar.equals(secondVar))
			{
				return list.get(i).variable;
			}
		}
		return "zzz";
	}
	
	private void update(String variable, String secondVar)
	{
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i).variable.equals(variable))
			{
				list.get(i).secondVar = secondVar;
			}
		}
	}
}