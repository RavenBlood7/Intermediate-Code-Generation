package scope_crawler;

import java.util.Stack;

public class SymbolTable
{
	private Stack<STNode> stack = new Stack<STNode>();
	private class STNode
	{
		public String name;
		public char type;
		public int scope;
		
		STNode(String name, char type, int scope)
		{
			this.name = name;
			this.type = type;
			this.scope = scope;
		}
	}
	
	public boolean empty()
	{
		return stack.empty();
	}
	
	public void bind(String name, char type, int scope)
	{
		if (!lookup(name, type))stack.push(new STNode(name, type, scope));
	}
	
	public boolean lookup(String name, char type)
	{
		for (int i = 0; i < stack.size(); i++)
		{
			if (stack.elementAt(i).name.equals(name) && stack.elementAt(i).type == type)
				return true;
		}
		return false;
	}

	public int findScope(String name, char type)
	{
		for (int i = stack.size() - 1; i >= 0 ; i--)
		{
			if (stack.elementAt(i).name.equals(name) && stack.elementAt(i).type == type)
				return stack.elementAt(i).scope;
		}
		return 0;
	}
	
	public void enter()
	{
		stack.push(new STNode("mark", '#', -1));
	}
	
	public void exit()
	{
		while (!stack.empty() && stack.pop().name != "mark") {};
	}
}