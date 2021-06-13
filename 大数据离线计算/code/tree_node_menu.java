import javax.swing.tree.DefaultMutableTreeNode;
import java.sql.SQLException;

public class tree_node_menu {
    public static DefaultMutableTreeNode sort;
    private DefaultMutableTreeNode db_node = new DefaultMutableTreeNode();
    private DefaultMutableTreeNode table_node = new DefaultMutableTreeNode();
    private DefaultMutableTreeNode column_node = new DefaultMutableTreeNode();

    tree_node_menu(){
        sort= new DefaultMutableTreeNode("当前连接目录");
    }

    //设置数据库菜单，表菜单，和个人表中的各列字段
    public void setTree() throws SQLException {
        getlists f = new getlists();
        Object[] db_list = f.get_db_list();

        for(int i=0;i<db_list.length;i++){
            db_node = new DefaultMutableTreeNode(db_list[i].toString());
            sort.add(db_node);
            Object[] tb_list = f.get_table_list(db_list[i].toString());
            //非本用户的库，只有获取表名的权限
            if(!db_list[i].toString().equals("user37_db")) {
                for (int j = 0; j < tb_list.length; j++) {
                    table_node = new DefaultMutableTreeNode(tb_list[j].toString());
                    db_node.add(table_node);
                }
            }
            else{
                for (int j=0;j<tb_list.length;j++){
                    table_node = new DefaultMutableTreeNode(tb_list[j].toString());
                    db_node.add(table_node);
                    Object[] column_list = f.get_column_list(tb_list[j].toString());
                    //可以获取本用户库中所有表的列字段
                    for(int m=0;m<column_list.length;m++){
                        column_node = new DefaultMutableTreeNode(column_list[m].toString());
                        table_node.add(column_node);
                    }
                }
            }
        }

    }
}