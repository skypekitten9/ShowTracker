
package showtracker.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

import showtracker.Helper;
import showtracker.User;

public class Profile extends JPanel {

    private ClientController cc;
    private User user;
    private Helper helper = new Helper();
    private ImageIcon image;
    private JPanel southPanel;

    private JTextField tfConfirmPassword;
    private JPasswordField password;

    public Profile(ClientController cc) {
        this.cc = cc;
        this.setLayout(new BorderLayout());
    }

    public void draw() {
        user = cc.getUser();
        add(profilePanel(), BorderLayout.NORTH);
        add(textFieldPanel(), BorderLayout.CENTER);
        changePanel();
        add(southPanel, BorderLayout.SOUTH);
    }

    public JPanel textFieldPanel() {
        JPanel panel = new JPanel();

        panel.setLayout(new GridLayout(2, 2, 6, 1));
        JLabel inputName = new JLabel(user.getUserName());

        JLabel namn = new JLabel("   Username:  ");
        JLabel mail = new JLabel("   Email:  ");

        panel.add(namn);
        panel.add(inputName);

        return panel;

    }

    private void changePanel() {
        southPanel = new JPanel(new BorderLayout());
        JPanel panel = new JPanel();

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

    public JPanel profilePanel() {
        image = getUserProfilePicture();
        JLabel imageLabel = new JLabel(image);

        JPanel topPanel = new JPanel();

        topPanel.setLayout(new GridLayout(1, 1, 1, 1));
        topPanel.add(imageLabel);

        return topPanel;
    }

    public ImageIcon getUserProfilePicture() {
        return user.getProfilePicture();
    }

    public JPanel changePasswordPanel() {

        JPanel panel = new JPanel();
        panel.setFont(new Font("Tahoma", Font.PLAIN, 20));
        panel.setLayout(new GridLayout(3, 2, 2, 2));

        password = new JPasswordField();
        tfConfirmPassword = new JTextField();

        JCheckBox check = new JCheckBox("Show password");

        JLabel label1 = new JLabel("Current password");
        JLabel label2 = new JLabel("New password");

        panel.add(label1);
        panel.add(tfConfirmPassword);

        panel.add(label2);
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

    public static void main(String[] args) throws Exception {
        ClientController cc = new ClientController();
        User user = new User("namn", null);
        cc.setUser(user);

        Profile profile = new Profile(cc);
        profile.draw();
        JFrame frame = new JFrame();
        frame.setTitle("Profile");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(profile);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
        frame.pack();
        frame.setSize(350, 500);

    }
}