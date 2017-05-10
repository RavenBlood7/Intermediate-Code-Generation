package parser;
import java.util.ArrayList;

public class InfoTable
{
	private ArrayList<TableItem> symbols = new ArrayList<TableItem>();

	public int index(TableItem item){
		return symbols.indexOf(item);
	}

	public void insert(int ID, String tokenClass, String snippet){
		TableItem item = new TableItem(ID, tokenClass, snippet);
		symbols.add(item);
	}

	public char getType(int ID){
		try {
			TableItem item = symbols.get(ID);
			if (item == null) {
				return '\0';
			} else {
				return item.type;
			}
		} catch (IndexOutOfBoundsException e){
			System.out.println("Error: Ooops..failed to get the type\nReason: IndexOutOfBoundsException");
			return '\0';
		}
	}

	public void setType(int ID, char type){
		try {
			TableItem item = symbols.get(ID);
			item.type = type;
		} catch (IndexOutOfBoundsException e){
			System.out.println("Error: Ooops..failed to set type.\nReason: IndexOutOfBoundsException");
		}
	}

	public String getText(int ID){
		return "";
	}

//////////////////for scope
	public void setScope(int ID, int scope)
	{
		try {
			for (int i = 0; i < symbols.size(); i++) {
				if (symbols.get(i).tokenNo == ID)
					symbols.get(i).scope = scope;
			}

		} catch (IndexOutOfBoundsException e) {
			System.out.println("Error: Ooops..failed to set type.\nReason: IndexOutOfBoundsException");
		}
	}

	public int getScope(int ID)
	{
			try {
				for (int i = 0; i < symbols.size(); i++) {
					if (symbols.get(i).tokenNo == ID)
						return symbols.get(i).scope;
				}

			} catch (IndexOutOfBoundsException e) {
				System.out.println("Error: Ooops..failed to set type.\nReason: IndexOutOfBoundsException");
			}
			return -1;
	}

	public String toString()
    {
        String ret = "";
        for (int i = 0; i < symbols.size(); i++)
        {
            if (symbols.get(i).tokenClass.equals("user-defined name"))
                ret += symbols.get(i).toString() + "\n";
        }

        return ret;
    }

    public int size()
	{
		return symbols.size();

	}

	public TableItem getItemAt(int i)
	{
		return symbols.get(i);
	}

	public void setDefined(String varName) {
        try
        {
            TableItem item;
            for (int i = 0 ; i < symbols.size(); i++)
            {
                item = symbols.get(i);
                if (item.newName != null && item.newName.equals(varName))
                    item.defined = 'd';
            }
        } catch (IndexOutOfBoundsException e){
            System.out.println("Error: Ooops..failed to set type.\nReason: IndexOutOfBoundsException");
        }
	}

	public char getDefined(String varName) {
		try
        {
			TableItem item;
			for (int i = 0 ; i < symbols.size(); i++)
			{
				item = symbols.get(i);
				if (item.newName != null && item.newName.equals(varName))
					return item.defined;
			}
		} catch (IndexOutOfBoundsException e){
			System.out.println("Error: Ooops..failed to set type.\nReason: IndexOutOfBoundsException");
		}
        return 'u';
    }

    public TableItem get(int i) {
	    return getItemAt(i);
    }

    public char getType2(int tokenNo) {
        try {
            for (int i = 0; i < symbols.size(); i++) {
                if (symbols.get(i).tokenNo == tokenNo)
                    return symbols.get(i).type;
            }

        } catch (IndexOutOfBoundsException e) {
            System.out.println("Error: Ooops..failed to set type.\nReason: IndexOutOfBoundsException");
        }
        return '\0';
    }

    public void setType2(int tokenNo, char type) {
        try {
            for (int i = 0; i < symbols.size(); i++) {
                if (symbols.get(i).tokenNo == tokenNo)
                    symbols.get(i).type = type;
            }

        } catch (IndexOutOfBoundsException e) {
            System.out.println("Error: Ooops..failed to set type.\nReason: IndexOutOfBoundsException");
        }
    }

    public String getNewName(int tokenNo)
	{
		try {
			for (int i = 0; i < symbols.size(); i++) {
				if (symbols.get(i).tokenNo == tokenNo)
					return symbols.get(i).newName;
			}

		} catch (IndexOutOfBoundsException e) {
			System.out.println("Error: Ooops..failed to set type.\nReason: IndexOutOfBoundsException");
		}
		return "";
	}

    public char reverseTypeLookup(int tokenNo, String snippet)
    {
        int i;
        for (i = 0; i < symbols.size(); i++)
        {
            if (symbols.get(i).tokenNo == tokenNo)
                break;
        }

        for (i = i - 1; i >= 0 ; i--)
        {
            if (symbols.get(i).tokenClass.equals("user-defined name")
                    && symbols.get(i).snippet.equals(snippet)
                    && symbols.get(i).type != '\0')
                return symbols.get(i).type;
        }
        return 's';
    }

    public void setAllUndefined()
    {
        for (int i = 0; i < size(); i++)
        {
            symbols.get(i).defined = 'u';
        }
    }
}
