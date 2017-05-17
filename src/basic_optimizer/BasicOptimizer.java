package basic_optimizer;

public class BasicOptimizer
{
	public void optimize()
	{
		CommonSub cse = new CommonSub();
		CopyPropagation cp = new CopyPropagation();
		
		while (cse.eliminateCommonSubs() || cp.propagate()) {};
		
	}
	
	private void preprocess()
	{
		//open in file
		//open out file
		
		//for each line in infile
			//remove all tabs
			//write to out file
		
		//close in file
		//close out file
	}
}