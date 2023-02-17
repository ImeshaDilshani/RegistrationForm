import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class RegistrationForm extends JDialog{
    private JTextField tFname;
    private JTextField tFemail;
    private JTextField tFphonenumber;
    private JTextField tFaddress;
    private JTextField tFpassword ;
    private JButton registerButton;
    private JButton cancelButton;
    private JPanel registrationPanel;
    private JPasswordField pFpassword;
    private JPasswordField pFconfirmpw;

    public RegistrationForm(JFrame parent){
        super(parent);
        setTitle("Create a new account");
        setContentPane(registrationPanel);
        setMinimumSize(new Dimension(450,474));
        setModal(true);
        setLocationRelativeTo(parent);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        //register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        //cancel button
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);

    }

    private void registerUser() {
        String name = tFname.getText();
        String email = tFemail.getText();
        String phone = tFphonenumber.getText();
        String address = tFaddress.getText();
        String password = pFpassword.getText();
        String confirmPassword = String.valueOf(pFconfirmpw.getPassword());

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()  || password.isEmpty()){
            JOptionPane.showMessageDialog(this,
                    "Please enter all fields",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)){
            JOptionPane.showMessageDialog(this,
                    "Confirm Password does not match",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        user = addUserTODatabase(name,email,phone,password, password);

        if (user != null){
            dispose();
        }
        else
        {
            JOptionPane.showMessageDialog(this,
                    "Fail to register new user",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public User user;
    private User addUserTODatabase(String name, String email, String phone, String address, String password){
        User user = null;

        final String DB_URL = "jdbc:mysql://localhost/mystore?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {

            Connection conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);
            // connected to database successfully.....

            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO users (name, email,phone, address, password)" + "VALUES (?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,email);
            preparedStatement.setString(3,phone);
            preparedStatement.setString(4,address);
            preparedStatement.setString(5,password);

            //Insert row into the table
            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0){
                user = new User();
                user.name = name;
                user.email = email;
                user.phone = phone;
                user.address = address;
                user.password = password;
            }

            //close the connection
            stmt.close();
            conn.close();

        }catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }



    public static void main(String[] args) {
        RegistrationForm myForm = new RegistrationForm(null);
        User user = myForm.user;
        if (user != null){
            System.out.println("Successful registration of : " + user.name);
        }else {
            System.out.println("Registration canceled");
        }
    }
}
