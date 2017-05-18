package basic_optimizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BasicOptimizer
{
	public void optimize()
	{
	    preprocess();
		CommonSub cse = new CommonSub();
		CopyPropagation cp = new CopyPropagation();
		
		while (cse.eliminateCommonSubs() || cp.propagate()) {};
		
	}
	
	private void preprocess()
	{
		try
		{
			String line;
			FileReader fr = new FileReader("generated_basic.bas");
			BufferedReader br = new BufferedReader(fr);
			FileWriter fw = new FileWriter("working.bas");

			//for each line in infile
			while ((line = br.readLine()) != null)
			{
				//remove all tabs
				while (line.length() > 2 &&  line.charAt(0) == '\t')
					line = line.substring(1);
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
}