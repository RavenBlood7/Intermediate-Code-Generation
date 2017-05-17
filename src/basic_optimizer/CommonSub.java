package basic_optimizer;

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
		boolean changed = false;
		//open file
		
		//for each line
			//if line is an Assignment (n0 = ...)
				//if ... already exists in list
					//replace ... with accompanying variable on line but not in list
					//write line
					//update n0
				//else
					//add n0 and ... to list
		
		//close file
		return changed;
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
				i = 0;
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
	
	private String update(String varibale, String expression)
	{
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i).variable.equals(variable))
			{
				list.get(i).expression = expression;
			}
		}		
	}
}