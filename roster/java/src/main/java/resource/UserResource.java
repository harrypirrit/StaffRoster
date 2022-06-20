package resource;

import dao.SQLiteDAO;
import domain.ResponseType;
import domain.User;
import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;
import io.fusionauth.jwt.hmac.HMACVerifier;
import io.jooby.Jooby;
import io.jooby.StatusCode;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import service.EmailService;

public class UserResource extends Jooby {

    private String jwtPassphrase = "this is a staff events roster";

    public UserResource(SQLiteDAO dao) {
        path("/api", () -> {
            path("/user", () -> {
                /**
                 * post request for login. It returns a JWT token containing the
                 * userID and expiry time
                 */
                post("/login", ctx -> {
                    User user = ctx.body().to(User.class);
                    // Converts password to salted hash to compare with database
                    user.setPassword(user.getPasswordHash(user.getPassword()));
                    // Get user details with matching email and passwordhash
                    user = dao.getUserByEmailPassword(user);
                    // Creates JWT token using userID and set expiry 30 minutes from now
                    if (user != null) {
                        Signer signer = HMACSigner.newSHA256Signer(jwtPassphrase);
                        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
                        ZonedDateTime expiry = now.plusMinutes(30);
                        JWT jwt = new JWT().setIssuedAt(now)
                                .setUniqueId(user.getUserID().toString())
                                .setExpiration(expiry);

                        String encodedJWT = JWT.getEncoder().encode(jwt, signer);

                        return new ResponseType(encodedJWT, null);
                    } else {
                        return new ResponseType(null, "Your email or password is incorrect.");
                    }
                });

                /**
                 * Gets logged in user and role using the JWT tokens unique id
                 */
                get("", ctx -> {
                    try {
                        String encodedJWT = ctx.header("Authorization").to(String.class);
                        if (encodedJWT != null && !encodedJWT.equals("")) {
                            Verifier verifier = HMACVerifier.newVerifier(jwtPassphrase);
                            JWT jwt = JWT.getDecoder().decode(encodedJWT, verifier);
                            if (jwt.isExpired() && jwt.issuedAt.isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
                                return ctx.send(StatusCode.UNAUTHORIZED);
                            }
                            User user = dao.getUserByID(Integer.parseInt(jwt.uniqueId));
                            user.setRoleName(dao.getRoleNameByRoleID(user.getRoleID()));
                            return user;
                        } else {
                            return ctx.send(StatusCode.UNAUTHORIZED);
                        }
                    } catch (Exception ex) {
                        return ctx.send(StatusCode.UNAUTHORIZED);
                    }
                });

                /**
                 * Creates a new user without password
                 */
                post("/register", ctx -> {
                    User newUser = ctx.body().to(User.class);
                    newUser.setPassword(null);

                    // Check email being submitted is valid format.
                    String regex = "^(.+)@(.+)$";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(newUser.getEmail());
                    if (matcher.matches() == false)
                        { return new ResponseType(null, "Your email does not match the correct format. Please try again.") ;}

                    // Check authorisation
                    String encodedJWT = ctx.header("Authorization").to(String.class);
                    if (encodedJWT != null && !encodedJWT.equals("")) {
                        Verifier verifier = HMACVerifier.newVerifier(jwtPassphrase);
                        JWT jwt = JWT.getDecoder().decode(encodedJWT, verifier);
                        User user = dao.getUserByID(Integer.parseInt(jwt.uniqueId));
                        if (jwt.isExpired() && jwt.issuedAt.isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
                            return ctx.send(StatusCode.UNAUTHORIZED);
                        } else if (user == null || user.getRoleID() != 1) {
                            return ctx.send(StatusCode.UNAUTHORIZED);
                        }
                    } else {
                        return ctx.send(StatusCode.UNAUTHORIZED);
                    }

                    if (!newUser.exists()) {
                        try {
                            Signer signer = HMACSigner.newSHA256Signer(jwtPassphrase);
                            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
                            ZonedDateTime expiry = now.plusDays(2);
                            JWT jwt = new JWT().setIssuedAt(now)
                                    .setUniqueId(newUser.getEmail())
                                    .setExpiration(expiry);

                            String accessToken = JWT.getEncoder().encode(jwt, signer);
                            
                            EmailService emailService = new EmailService();
                            String emailBody = "Welcome " + newUser.getFirstName() + ","
                                    + "<br>"
                                    + "To set your password, please click the link below."
                                    + "<br>"
                                    + "<a href='" + ctx.header("origin").to(String.class) + "/login/set-password?token=" + accessToken + "'>Set password</a>";
                            String emailSubject = "Invitation to set your password.";
                            if (!emailService.SendEmail(newUser.getEmail(), emailSubject, emailBody)) {
                                return new ResponseType(null, "A problem has occurred and the user was not created.");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        dao.insertUser(newUser);
                        return new ResponseType(null,  null);
                    }

                    return new ResponseType(null, "A problem has occurred and the user was not created.");
                });

                /**
                 * Verifys token received via email
                 */
                get("/verifyPasswordToken/{token}", ctx -> {
                    String token = ctx.path("token").value();
                    Verifier verifier = HMACVerifier.newVerifier(jwtPassphrase);
                    JWT jwt = JWT.getDecoder().decode(token, verifier);
                    if (jwt.isExpired() && jwt.issuedAt.isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
                        return false;
                    }
                    User user = dao.getUserWithoutPassword(jwt.uniqueId);
                    if (user != null) {
                        return true;
                    } else {
                        return false;
                    }
                });

                /**
                 * Sets users password
                 */
                put("/setPassword/{token}", ctx -> {
                    String token = ctx.path("token").value();
                    String password = ctx.body().to(String.class);
                    if (password != null && !password.equals("")) {
                        Verifier verifier = HMACVerifier.newVerifier(jwtPassphrase);
                        JWT jwt = JWT.getDecoder().decode(token, verifier);
                        if (jwt.isExpired() && jwt.issuedAt.isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
                            return false;
                        }

                        User user = dao.getUserByEmail(jwt.uniqueId);
                        user.setPassword(password);
                        if (user.getPassword() != null) {
                            user.setPassword(User.getPasswordHash(user.getPassword()));
                            dao.setPassword(user);
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                });

                put("/update", ctx -> {
                    User newUser = ctx.body().to(User.class);
                    String encodedJWT = ctx.header("Authorization").to(String.class);
                    if (encodedJWT != null && !encodedJWT.equals("")) {
                        Verifier verifier = HMACVerifier.newVerifier(jwtPassphrase);
                        JWT jwt = JWT.getDecoder().decode(encodedJWT, verifier);
                        User user = dao.getUserByID(Integer.parseInt(jwt.uniqueId));
                        if (jwt.isExpired() && jwt.issuedAt.isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
                            return ctx.send(StatusCode.UNAUTHORIZED);
                        } else if (user == null || user.getRoleID() != 1) {
                            return ctx.send(StatusCode.UNAUTHORIZED);
                        }
                    } else {
                        return ctx.send(StatusCode.UNAUTHORIZED);
                    }
                    try {
                        dao.updateUser(newUser);
                        return true;
                    } catch (Exception ex) {
                        return ctx.send(StatusCode.NOT_FOUND);
                    }
                });

                delete("/{userID}", ctx -> {
                    Integer userID = Integer.parseInt(ctx.path("userID").value());
                    // Check authorisation
                    String encodedJWT = ctx.header("Authorization").to(String.class);
                    if (encodedJWT != null && !encodedJWT.equals("")) {
                        Verifier verifier = HMACVerifier.newVerifier(jwtPassphrase);
                        JWT jwt = JWT.getDecoder().decode(encodedJWT, verifier);
                        User user = dao.getUserByID(Integer.parseInt(jwt.uniqueId));
                        if (jwt.isExpired() && jwt.issuedAt.isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
                            return ctx.send(StatusCode.UNAUTHORIZED);
                        } else if (user == null || user.getRoleID() != 1) {
                            return ctx.send(StatusCode.UNAUTHORIZED);
                        }
                    } else {
                        return ctx.send(StatusCode.UNAUTHORIZED);
                    }

                    if (userID != null) {
                        User user = dao.getUserByID(userID);
                        if (user != null) {
                            dao.deleteInvitesByUserID(user.getUserID());
                            dao.deleteEventByUserID(user.getUserID());
                            dao.deleteUserByUserID(user.getUserID());
                            return ctx.send(StatusCode.OK);
                        } else {
                            return ctx.send(StatusCode.NOT_FOUND);
                        }
                    } else {
                        return ctx.send(StatusCode.NO_CONTENT);
                    }
                });
            });

            /**
             * Gets all users
             */
            get("/users", ctx -> {
                String encodedJWT = ctx.header("Authorization").to(String.class);
                if (encodedJWT != null && !encodedJWT.equals("")) {
                    Verifier verifier = HMACVerifier.newVerifier(jwtPassphrase);
                    JWT jwt = JWT.getDecoder().decode(encodedJWT, verifier);
                    User user = dao.getUserByID(Integer.parseInt(jwt.uniqueId));
                    if (jwt.isExpired() && jwt.issuedAt.isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
                        return ctx.send(StatusCode.UNAUTHORIZED);
                    }
                } else {
                    return ctx.send(StatusCode.UNAUTHORIZED);
                }
                return dao.getAllUsers();
            });

            /**
             * Gets all roles
             */
            get("/roles", ctx -> {
                String encodedJWT = ctx.header("Authorization").to(String.class);
                if (encodedJWT != null && !encodedJWT.equals("")) {
                    Verifier verifier = HMACVerifier.newVerifier(jwtPassphrase);
                    JWT jwt = JWT.getDecoder().decode(encodedJWT, verifier);
                    User user = dao.getUserByID(Integer.parseInt(jwt.uniqueId));
                    if (jwt.isExpired() && jwt.issuedAt.isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
                        return ctx.send(StatusCode.UNAUTHORIZED);
                    } else if (user == null || user.getRoleID() != 1) {
                        return ctx.send(StatusCode.UNAUTHORIZED);
                    }
                } else {
                    return ctx.send(StatusCode.UNAUTHORIZED);
                }
                return dao.getAllRoles();
            });
        });
    }
}
