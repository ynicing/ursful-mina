package com.ursful.framework.tool.checkbox;
import javax.swing.tree.DefaultMutableTreeNode;

public class CheckBoxTreeNode extends DefaultMutableTreeNode  
{  
    protected boolean isSelected;  
      
    public CheckBoxTreeNode()  
    {  
        this(null);  
    }  
      
    public CheckBoxTreeNode(Object userObject)  
    {  
        this(userObject, true, false);  
    }  
      
    public CheckBoxTreeNode(Object userObject, boolean allowsChildren, boolean isSelected)  
    {  
        super(userObject, allowsChildren);  
        this.isSelected = isSelected;  
    }  
  
    public boolean isSelected()  
    {  
        return isSelected;  
    }  
      
    public void setSelected(boolean _isSelected)  
    {  
        this.isSelected = _isSelected;  
          
        if(_isSelected)  
        {  
            // ���ѡ�У��������е��ӽ�㶼ѡ��  
            if(children != null)  
            {  
                for(Object obj : children)  
                {  
                    CheckBoxTreeNode node = (CheckBoxTreeNode)obj;
                    if(_isSelected != node.isSelected())  
                        node.setSelected(_isSelected);  
                }  
            }  
            // ���ϼ�飬�����������ӽ�㶼��ѡ�У���ô�������Ҳѡ��  
            CheckBoxTreeNode pNode = (CheckBoxTreeNode)parent;
            // ��ʼ���pNode�������ӽڵ��Ƿ񶼱�ѡ��  
            if(pNode != null)  
            {  
                int index = 0;  
                for(; index < pNode.children.size(); ++ index)  
                {  
                    CheckBoxTreeNode pChildNode = (CheckBoxTreeNode)pNode.children.get(index);
                    if(!pChildNode.isSelected())  
                        break;  
                }  
                /*  
                 * ����pNode�����ӽ�㶼�Ѿ�ѡ�У���ѡ�и���㣬 
                 * �÷�����һ���ݹ鷽��������ڴ˲���Ҫ���е����Ϊ 
                 * ��ѡ�и����󣬸���㱾������ϼ��ġ� 
                 */  
                if(index == pNode.children.size())  
                {  
                    if(pNode.isSelected() != _isSelected)  
                        pNode.setSelected(_isSelected);  
                }  
            }  
        }  
        else   
        {  
            /* 
             * �����ȡ���㵼���ӽ��ȡ����ô��ʱ���е��ӽ�㶼Ӧ����ѡ���ϵģ� 
             * ��������ӽ��ȡ���¸����ȡ��Ȼ�󸸽��ȡ������Ҫȡ���ӽ�㣬�� 
             * ����ʱ���ǲ���Ҫȡ���ӽ��ġ� 
             */  
            if(children != null)  
            {  
                int index = 0;  
                for(; index < children.size(); ++ index)  
                {  
                    CheckBoxTreeNode childNode = (CheckBoxTreeNode)children.get(index);
                    if(!childNode.isSelected())  
                        break;  
                }  
                // ��������ȡ���ʱ��  
                if(index == children.size())  
                {  
                    for(int i = 0; i < children.size(); ++ i)  
                    {  
                        CheckBoxTreeNode node = (CheckBoxTreeNode)children.get(i);
                        if(node.isSelected() != _isSelected)  
                            node.setSelected(_isSelected);  
                    }  
                }  
            }  
              
            // ����ȡ��ֻҪ����һ���ӽڵ㲻��ѡ�ϵģ���ô���ڵ�Ͳ�Ӧ�ñ�ѡ�ϡ�  
            CheckBoxTreeNode pNode = (CheckBoxTreeNode)parent;
            if(pNode != null && pNode.isSelected() != _isSelected)  
                pNode.setSelected(_isSelected);  
        }  
    }  
}  