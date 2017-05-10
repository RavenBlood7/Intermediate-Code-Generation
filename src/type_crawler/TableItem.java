package type_crawler;

import parser.*;
public class TableItem extends TreeNode {
    public char type;
    public int scope;
    public char defined;
    public TableItem(int number, String tokenClass, String snippet){
        super(number, tokenClass, snippet);
        type = '\0';
        scope = -1;
        defined = 'u';
    }

    public TableItem getChild(int i)
    {
        return (TableItem) children.get(i);
    }

    public void setChild(TreeNode node){
        children.add(node);
    }

    @Override
    public String toString() {
        return tokenNo + "\t" + tokenClass + "\t" + snippet + "\t" + type + "\t" + scope + "\t" + defined + "\t" + newName;
    }
}
