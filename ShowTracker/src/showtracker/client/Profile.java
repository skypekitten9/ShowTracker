
package showtracker.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

import showtracker.Helper;
import showtracker.User;

public class Profile extends JPanel {

    private ClientController cc;
    private User user;
    private Helper helper = new Helper();

    private ImageIcon profileImage = new ImageIcon("images/profile.png");

    private JTextField tfConfirmPassword;
    private JPasswordField password;

    private JPanel southPanel;

    public Profile(ClientController cc) {
        this.cc = cc;
        this.setLayout(new BorderLayout());
    }

    public void draw() {
        user = cc.getUser();
        add(textFieldPanel(), BorderLayout.CENTER);
        changePanel();
        add(southPanel, BorderLayout.SOUTH);
    }


    public JPanel textFieldPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(null);
        Image image = profileImage.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel lblProfile = new JLabel(new ImageIcon(image));
        JLabel namn = new JLabel("   Username:  " + user.getUserName());
        lblProfile.setBounds(115, 10, 120, 120);
        namn.setBounds(90, 150, 130, 70);

        panel.add(lblProfile);
        panel.add(namn);

        return panel;

    }

    private void changePanel() {
        southPanel = new JPanel(new BorderLayout());
        JPanel panel = new JPanel();
//        panel.setBackground(Color.WHITE);
        JButton btnChangePass = new JButton("Change Password?");
        panel.add(btnChangePass);

        southPanel.add(panel, BorderLayout.SOUTH);


        btnChangePass.addActionListener(new ActionListener() {
            @SuppressWarnings("static-access")
            public void actionPerformed(ActionEvent e) {

                int res = JOptionPane.showConfirmDialog(null, changePasswordPanel(), "Change password!",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                while (!(helper.checkPasswordValidity(password.getText())) && res == JOptionPane.OK_OPTION) {

                    if (!helper.checkPasswordValidity(password.getText()))
                        JOptionPane.showMessageDialog(null,
                                "Your password must contain at least 8 charachters, \n one capital letter,"
                                        + " one small letter and one digit!",
                                "Weak password", JOptionPane.WARNING_MESSAGE);


                    res = JOptionPane.showConfirmDialog(null, changePasswordPanel(), "Change password!",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                }

                if (res == JOptionPane.OK_OPTION) {
                    String reply = cc.updatePassword(user.getUserName(), tfConfirmPassword.getText(), new String(password.getText()));
                    if (reply.equals("Password changed"))
                        JOptionPane.showMessageDialog(null, reply, "Request approved", JOptionPane.INFORMATION_MESSAGE);
                    else {
                        JOptionPane.showMessageDialog(null, reply, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

    }

    public JPanel changePasswordPanel() {

        JPanel panel = new JPanel();
        panel.setFont(new Font("Tahoma", Font.PLAIN, 20));
        panel.setLayout(new GridLayout(3, 2, 2, 2));

        password = new JPasswordField();
        tfConfirmPassword = new JTextField();

        JCheckBox check = new JCheckBox("Show password");

        JLabel lblCurrentPassWord = new JLabel("Current password");
        JLabel lblNewPassWord = new JLabel("New password");

        panel.add(lblCurrentPassWord);
        panel.add(tfConfirmPassword);

        panel.add(lblNewPassWord);
        panel.add(password);

        panel.add(check);

        check.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (check.isSelected()) {
                    password.setEchoChar((char) 0);
                } else {
                    password.setEchoChar('*');
                }
            }
        });

        return panel;
    }


}