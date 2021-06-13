import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class new_search {
    private JPanel panel1 = new JPanel();
    private JPanel panel2 = new JPanel();
    private JSplitPane splitPane1 = new JSplitPane();
    private JSplitPane splitPane2 = new JSplitPane();
    private JSplitPane splitPane3 = new JSplitPane();
    private JSplitPane splitPane4 = new JSplitPane();
    private JScrollPane scrollPane1 = new JScrollPane();
    private JScrollPane scrollPane2 = new JScrollPane();
    private JTextArea textArea1 = new JTextArea();
    private JButton button1 = new JButton();
    private JButton button2 = new JButton();
    private JTable table1 = new JTable();
    private static String sql_sentence;
    private JLabel label1 = new JLabel();

    public JPanel new_panel(){

        panel1.setLayout(new BorderLayout());
        //======== splitPane1 ========
        {
            splitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
            splitPane1.setDividerSize(4);
            splitPane1.setEnabled(false);

            //======== splitPane2 ========
            {
                splitPane2.setDividerSize(0);
                splitPane2.setEnabled(false);
                splitPane2.setDividerLocation(1700);

                //======== splitPane4 ========
                {
                    splitPane4.setEnabled(false);
                    splitPane4.setDividerSize(0);
                    splitPane4.setDividerLocation(1550);

                    //---- button1 ----
                    button1.setText("运行");
                    button1.setOpaque(true);
                    button1.setBackground(Color.red);
                    button1.setPreferredSize(new Dimension(40,12));
                    button1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
                    button1.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            try {
                                label1.setText("拉 取 数 据 中 . . .");
                                button1MouseClicked(e);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        }
                    });
                    splitPane4.setRightComponent(button1);

                    //---- button2 ----
                    button2.setText("退出");
                    button2.setSize(40,12);
                    button2.setOpaque(true);
                    button2.setBackground(Color.MAGENTA);
                    button2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
                    button2.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            button2MouseClicked(e);
                        }
                    });
                    splitPane2.setRightComponent(button2);

                    //======== panel2 ========
                    {
                        panel2.setLayout(new BorderLayout());
                        label1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 20));
                        panel2.add(label1);
                    }
                    splitPane4.setLeftComponent(panel2);
                }
                splitPane2.setLeftComponent(splitPane4);
            }
            splitPane1.setTopComponent(splitPane2);

            //======== splitPane3 ========
            {
                splitPane3.setOrientation(JSplitPane.VERTICAL_SPLIT);
                splitPane3.setDividerLocation(700);
                splitPane3.setDividerSize(4);

                //======== scrollPane1 ========
                {
                    textArea1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 22));
                    scrollPane1.setViewportView(textArea1);
                }
                splitPane3.setTopComponent(scrollPane1);

                //======== scrollPane2 ========
                {
                    table1.setPreferredScrollableViewportSize(null);
                    table1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    scrollPane2.setViewportView(table1);
                }
                splitPane3.setBottomComponent(scrollPane2);
            }
            splitPane1.setBottomComponent(splitPane3);
        }
        panel1.add(splitPane1, BorderLayout.CENTER);
        return panel1;
    }

    //运行按钮的触发事件
    private void button1MouseClicked(MouseEvent e) throws SQLException {
        // TODO add your code here
        JOptionPane.showMessageDialog(null,"即 将 拉 取 数 据 ","",-1);
        sql_sentence = textArea1.getText();
        getlists f = new getlists();
        Object[] temp = f.get_column_name_list(sql_sentence);
        ResultSet resultSet = f.get_resultset(sql_sentence);

        DefaultTableModel table2 = new DefaultTableModel(null, temp);

        while (resultSet.next()) {
            List data_ = new ArrayList();
            for(int i = 1; i <= temp.length; i++){
                data_.add(resultSet.getString(i));
            }
            table2.addRow(data_.toArray());
        }

        table1.setModel(table2);
        label1.setText("已 成 功 获 取 到 数 据 !");
    }

    //退出按钮的触发事件
    private void button2MouseClicked(MouseEvent e) {
        // TODO add your code here
        int result = JOptionPane.showConfirmDialog(null,"是否退出?","",JOptionPane.YES_NO_OPTION);
        if(result==0)
            System.exit(0);//终止当前程序
    }

}
