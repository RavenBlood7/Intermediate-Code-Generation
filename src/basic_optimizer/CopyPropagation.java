package basic_optimizer;

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
		boolean changed = false;
		//open file
		
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
		return changed;
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
				i = 0;
			}
		}
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
	
	private String update(String varibale, String secondVar)
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