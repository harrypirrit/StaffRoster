package domain;

import static java.lang.Math.random;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import roster.App;

/**
 *
 * @author harrypirrit
 */
public class User {

    private Integer userID;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private Integer roleID;
    private String roleName;
    private boolean isAccepted;

    public User(Integer userID, String firstName, String lastName, String email, String password, String phoneNumber, Integer roleID) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.roleID = roleID;
    }

    public User() {
        super();
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return App.dao.getRoleNameByRoleID(roleID);
    }

    public Integer getRoleID() {
        return roleID;
    }

    public void setRoleID(Integer roleID) {
        this.roleID = roleID;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * This method takes in the password and returns a salted hash
     *
     * @param passwordToHash
     * @return
     */
    public static String getPasswordHash(String passwordToHash) {
        if (passwordToHash != null && passwordToHash != "") {
            try {
                byte[] salt = passwordToHash.getBytes();

                KeySpec spec = new PBEKeySpec(passwordToHash.toCharArray(), salt, 65536, 128);
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                byte[] hashedPassword = factory.generateSecret(spec).getEncoded();
                Base64.Encoder encoder = Base64.getEncoder();
                String toReturn = encoder.encodeToString(hashedPassword);
                return toReturn;
            } catch (Exception ex) {
                //ex.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    /**
     * Checks if user exists with email
     * @return 
     */
    public boolean exists() {
        User user = App.dao.getUserByEmail(email);
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
    
}
