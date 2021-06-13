import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.*;

public class mainWindow extends JFrame {
    public mainWindow() throws SQLException {
        initComponents();
    }

    public class MyCloseActionHandler implements ActionListener {

        public void actionPerformed(ActionEvent evt) {

            Component selected = tabbedPane1.getSelectedComponent();
            if (selected != null) {
                tabbedPane1.remove(selected);
            }

        }
    }

    private void button1MouseClicked(MouseEvent e) {
        // TODO add your code here
        new_search panelSet = new new_search();
        JPanel one_panel = panelSet.new_panel();
        tabbedPane1.addTab("新建查询"+num, one_panel);

        int index = tabbedPane1.indexOfTab("新建查询"+num);
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        JLabel new_op = new JLabel("新建查询"+num);
        JButton bc = new JButton("X");
        bc.setContentAreaFilled(false);//设置按钮透明
        bc.setBorder(null);//取消边框
        bc.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 18));
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;

        p.add(new_op, gc);

        gc.gridx++;
        gc.weightx = 0;
        p.add(bc, gc);

        tabbedPane1.setTabComponentAt(index, p);
        MyCloseActionHandler myCloseActionHandler = new MyCloseActionHandler();
        bc.addActionListener(myCloseActionHandler);
        num++;
    }



    private void initComponents() throws SQLException {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        splitPane1 = new JSplitPane();
        splitPane2 = new JSplitPane();
        splitPane3 = new JSplitPane();
        scrollPane1 = new JScrollPane();
        tree1 = new JTree();
        button1 = new JButton();
        panel1 = new JPanel();
        tabbedPane1 = new JTabbedPane();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== splitPane1 ========
        {
            splitPane1.setDividerSize(4);

            //======== scrollPane1 ========
            {

                //---- tree1 ----
                tree_node_menu tree_ = new tree_node_menu();
                tree_.setTree();
                tree1 = new JTree(tree_.sort);
                tree1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));
                scrollPane1.setViewportView(tree1);
            }
            splitPane1.setLeftComponent(scrollPane1);

            //======== splitPane2 ========
            {
                splitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
                splitPane2.setEnabled(false);
                splitPane2.setDividerSize(2);

                //======== splitPane3 ========
                {
                    splitPane3.setDividerSize(0);

                    //---- button1 ----
                    button1.setText("新建查询");
                    button1.setOpaque(true);
                    button1.setBackground(Color.lightGray);
                    button1.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 17));
                    button1.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            button1MouseClicked(e);
                        }
                    });
                    splitPane3.setLeftComponent(button1);

                    //======== panel1 ========
                    {
                        panel1.setLayout(new BorderLayout());
                    }
                    splitPane3.setRightComponent(panel1);
                }
                splitPane2.setTopComponent(splitPane3);
                splitPane2.setBottomComponent(tabbedPane1);
            }
            splitPane1.setRightComponent(splitPane2);
        }
        contentPane.add(splitPane1, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle bounds = new Rectangle(screenSize);
        setBounds(bounds);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JSplitPane splitPane1;
    private JSplitPane splitPane2;
    private JSplitPane splitPane3;
    private JScrollPane scrollPane1;
    private JTree tree1;    //使用树形存储数据库表结构
    private JButton button1;
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    //num 用于统计新建查询页面的个数
    private static int num=0;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
