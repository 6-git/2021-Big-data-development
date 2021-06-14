import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.*;


/*
测试句子
select * from anjian
select * from t_rk_jbxx limit 10
*/

public class login extends JFrame {
    public login() {
        initComponents();
    }

    private void button1MouseClicked(MouseEvent e) throws SQLException {
        // TODO add your code here
        String admin=textField1.getText();
        char[] password=passwordField1.getPassword();
        String str=String.valueOf(password);
        String url = textField3.getText();

        //if(admin.equals("username")&&str.equals("password")&&url.equals("jdbc:hive2://"))
        if(admin.equals("1")&&str.equals("1")&&url.equals("1"))
        {
            JOptionPane.showMessageDialog(this,"是否连接服务器？");
            mainWindow mw=new mainWindow();//跳转到主界面
            dispose();//销毁登录界面
        }
        else {
            count++;
            if(count<5){
                JOptionPane.showMessageDialog(this,"输入错误！还剩下"+(5-count)+"次机会！");
            }
            else if(count==5){
                JOptionPane.showMessageDialog(this,"5次输入错误！连接失败！");
                dispose();
            }
        }
    }


    private void button2MouseClicked(MouseEvent e) {
        // TODO add your code here
        System.exit(0);//终止当前程序
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        //设置label并居中
        label1 = new JLabel("UserName",JLabel.CENTER);
        label2 = new JLabel("Password",JLabel.CENTER);
        label3 = new JLabel("Url",JLabel.CENTER);
        textField3 = new JTextField();
        button1 = new JButton();
        button2 = new JButton();
        passwordField1 = new JPasswordField();
        textField1 = new JTextField();

        //======== this ========
        setResizable(false);
        setMinimumSize(new Dimension(800, 400));
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- label1 ----
        label1.setOpaque(true);
        label1.setBackground(Color.lightGray);
        label1.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));
        contentPane.add(label1);
        label1.setBounds(20, 20, 100, 60);;

        //---- label2 ----
        label2.setOpaque(true);
        label2.setBackground(Color.lightGray);
        label2.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));
        contentPane.add(label2);
        label2.setBounds(20, 100, 100, 60);

        //---- label3 ----
        label3.setOpaque(true);
        label3.setBackground(Color.lightGray);
        label3.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));
        contentPane.add(label3);
        label3.setBounds(20,180,100,60);

        //---- textField1 ----
        textField1.setOpaque(true);
        textField1.setBackground(Color.lightGray);
        textField1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 17));
        textField1.setHorizontalAlignment(JTextField.CENTER);
        contentPane.add(textField1);
        textField1.setBounds(130, 20, 600, 60);

        //---- passwordField1 ----
        passwordField1.setOpaque(true);
        passwordField1.setBackground(Color.lightGray);
        passwordField1.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 17));
        passwordField1.setHorizontalAlignment(JTextField.CENTER);
        contentPane.add(passwordField1);
        passwordField1.setBounds(130, 100, 600, 60);


        //---- textField3 ----
        textField3.setOpaque(true);
        textField3.setBackground(Color.lightGray);
        textField3.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 17));
        textField3.setHorizontalAlignment(JTextField.CENTER);
        contentPane.add(textField3);
        textField3.setBounds(130, 180, 600, 60);

        //---- button1 ----
        button1.setText("Login");
        button1.setOpaque(true);
        button1.setBackground(Color.lightGray);
        button1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    button1MouseClicked(e);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        contentPane.add(button1);
        button1.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));
        button1.setBounds(200, 250, 100, 50);

        //---- button2 ----
        button2.setText("Exit");
        button2.setOpaque(true);
        button2.setBackground(Color.lightGray);
        button2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                button2MouseClicked(e);
            }
        });
        contentPane.add(button2);
        button2.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));
        button2.setBounds(450, 250, 100, 50);



        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        pack();
        setLocationRelativeTo(getOwner());
        //设置窗口可见
        setVisible(true);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    //count用于统计密码错误次数
    private int count=0;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JButton button1;
    private JButton button2;
    private JPasswordField passwordField1;
    private JTextField textField1;
    private JTextField textField3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
    public static void main(String[]args){
        login l = new login();
    }
}
