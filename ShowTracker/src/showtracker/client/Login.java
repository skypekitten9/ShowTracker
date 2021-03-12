package showtracker.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;

import showtracker.Helper;
import showtracker.User;

import static showtracker.Helper.*;

/**
 * @author
 */
public class Login extends JPanel {

    private ClientController cc;
    private JButton btLogIn = new JButton("Log In!");
    private JButton btSignUp = new JButton("Sign up!");

    private JTextField tfUsernameSignUp;
    private JPasswordField pfPasswordSignUp;

    private JTextField tfUsername = new JTextField();
    private JPasswordField pfPassword = new JPasswordField();

    private JPanel pnlLog = new JPanel();
    private JPanel pnlSign = new JPanel();

    public Login(ClientController cc) {
        this.cc = cc;
        setLayout(null);
        pnlLog.setLayout(null);
        pnlSign.setLayout(null);

        ImageIcon ii = new ImageIcon("images/show_transparant.png");
        Image image = ii.getImage().getScaledInstance(190, 150, Image.SCALE_SMOOTH);
        JLabel lbLogo = new JLabel(new ImageIcon(image));
        add(pnlLog);
        add(pnlSign);
        pnlLog.setBounds(38,190,260,130);
        pnlLog.setBorder(BorderFactory.createTitledBorder("Log in"));

        pnlSign.setBounds(38,328,260,60);
        pnlSign.setBorder(BorderFactory.createTitledBorder("New here?"));

        btLogIn.addActionListener(e -> checkUserLogin());
        btSignUp.addActionListener(e -> signUp());

        lbLogo.setBounds(80, 25, 180, 150);
        tfUsername.setBounds(35, 30, 200, 23);
        pfPassword.setBounds(35, 60, 200, 23);
        btLogIn.setBounds(75, 90, 120, 23);
        btSignUp.setBounds(75, 22, 120, 23);

        add(lbLogo);
        pnlLog.add(tfUsername);
        pnlLog.add(pfPassword);
        pnlLog.add(btLogIn);
        pnlSign.add(btSignUp);

        draw();
    }

    public void draw() {
        redrawInnerPanels();
        tfUsername.setText("Username");
        pfPassword.setText("Password123");
        tfUsername.selectAll();
        pfPassword.selectAll();
    }

    public void redrawInnerPanels(){
        tfUsername.updateUI();
        pfPassword.updateUI();
        btLogIn.updateUI();
        btSignUp.updateUI();
        pnlLog.updateUI();
        pnlSign.updateUI();

    }

    private void signUp() {
        
        int res = JOptionPane.showConfirmDialog(null, createAccount(), "Sign Up!", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        while (!(checkPasswordValidity(new String(pfPasswordSignUp.getPassword()))
                && checkUsernameValidity(tfUsernameSignUp.getText()) && cc.isUsernameAvailable(tfUsernameSignUp.getText()))&& res == JOptionPane.OK_OPTION ) {

            if (!checkUsernameValidity(tfUsernameSignUp.getText()) &&  res == JOptionPane.OK_OPTION)
                JOptionPane.showMessageDialog(null, "Username not valid!", "No Username", JOptionPane.WARNING_MESSAGE);

            if (!checkPasswordValidity(new String(pfPasswordSignUp.getPassword()))&& res == JOptionPane.OK_OPTION)
                JOptionPane.showMessageDialog(null, "Your password must contain at least 8 charachters, "
                        + "\none capital letter, one small letter and one digit!", "No Password!", JOptionPane.WARNING_MESSAGE);

            if(!cc.isUsernameAvailable(tfUsernameSignUp.getText()))
                JOptionPane.showMessageDialog(null, "Someone is already using this username!", "Username taken!", JOptionPane.WARNING_MESSAGE);

            res = JOptionPane.showConfirmDialog(null, createAccount(), "Sign Up!", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            
            while (res == JOptionPane.CANCEL_OPTION){
            break;}
        }

        if (res == JOptionPane.OK_OPTION){
            cc.signUp(tfUsernameSignUp.getText(), new String(pfPasswordSignUp.getPassword()));}
        else if(res == JOptionPane.CANCEL_OPTION){
            JOptionPane.getRootFrame().dispose();

        }
    }

    public JPanel createAccount() {
        JPanel panel = new JPanel(new GridLayout(7, 1));
        JLabel usernameLabel = new JLabel("Username : ");
        JLabel userPasswordLabel = new JLabel("Password : ");

        tfUsernameSignUp = new JTextField(20);
        pfPasswordSignUp = new JPasswordField(20);
        JCheckBox check = new JCheckBox("Show password");

        JLabel lblPwdRestrictions = new JLabel("Password restrictions:" + "\n" + "At least 8 characters, 1 capital letter, 1 small letter and 1 digit.");

        panel.add(usernameLabel);
        panel.add(tfUsernameSignUp);
        panel.add(userPasswordLabel);
        panel.add(pfPasswordSignUp);
        panel.add(lblPwdRestrictions);
        panel.add(check);


        check.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (check.isSelected()) {
                    pfPasswordSignUp.setEchoChar((char) 0);
                } else {
                    pfPasswordSignUp.setEchoChar('*');
                }
            }
        });

        return panel;

    }

    public void checkUserLogin() {
        String username = tfUsername.getText();
        String password = new String(pfPassword.getPassword());
        System.out.println(username + ", " + password);
        User user = cc.logIn(username, password);
        if (user != null) {
            cc.finalizeUser(user);
        } else {
            Helper.errorMessage("Login failed!");
            Border compound = null;
            Border redline = BorderFactory.createLineBorder(Color.red);
            compound = BorderFactory.createCompoundBorder(redline, compound);
            btSignUp.setBorder(BorderFactory.createCompoundBorder(redline, compound));
            revalidate();
        }
    }
}