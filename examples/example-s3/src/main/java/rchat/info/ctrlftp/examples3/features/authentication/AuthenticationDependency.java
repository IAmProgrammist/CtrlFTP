package rchat.info.ctrlftp.examples3.features.authentication;

import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.core.responses.ResponseTypes;
import rchat.info.ctrlftp.dependencies.authentication.AuthenticationResult;
import rchat.info.ctrlftp.dependencies.authentication.BaseAuthenticationDependency;
import rchat.info.ctrlftp.examples3.features.database.DatabaseDependency;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthenticationDependency extends BaseAuthenticationDependency<UserEntity> {
    private String login = null;
    private String hashedPassword = null;
    private UserEntity found = null;
    private final MessageDigest messageDigest;
    private final UserEntityRepository userEntityRepository;

    public AuthenticationDependency(DatabaseDependency database) {
        this.userEntityRepository = new UserEntityRepository(database.getDatabase());
        try {
            this.messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        messageDigest.update(password.getBytes());

        this.hashedPassword = new String(messageDigest.digest());
    }

    @Override
    public AuthenticationResult<UserEntity> authenticate() {
        if (found != null) {
            return new AuthenticationResult<>(
                    true,
                    new Response(ResponseTypes.AUTH_SUCCESS, String.format("You logged in successfully as '%s' (%d)",
                            found.getLogin(), found.getId())),
                    found);
        }

        if (login == null) {
            return new AuthenticationResult<>(false,
                    new Response(ResponseTypes.NEED_ACCOUNT_TO_LOGIN, "Please, provide login"),
                    null);
        }

        if (hashedPassword == null) {
            return new AuthenticationResult<>(false,
                    new Response(ResponseTypes.USERNAME_OK_NEED_PASS, "Please, provide password"),
                    null);
        }

        UserEntity foundUser = userEntityRepository.findUserEntityByLoginAndPassword(this.login, this.hashedPassword);
        if (foundUser != null) {
            this.found = foundUser;
            this.login = null;
            this.hashedPassword = null;
            return new AuthenticationResult<>(
                    true,
                    new Response(ResponseTypes.AUTH_SUCCESS, String.format("You logged in successfully as '%s' (%d)",
                            found.getLogin(), found.getId())),
                    foundUser);
        }

        if (userEntityRepository.findUserEntityByLogin(this.login) != null) {
            return new AuthenticationResult<>(true,
                    new Response(ResponseTypes.NOT_LOGGED_IN, "Invalid password"),
                    null);
        }

        UserEntity newUser = new UserEntity();
        newUser.setLogin(this.login);
        newUser.setHashPassword(this.hashedPassword);
        userEntityRepository.addUser(newUser);

        this.found = newUser;
        this.login = null;
        this.hashedPassword = null;

        return new AuthenticationResult<>(
                true,
                new Response(ResponseTypes.AUTH_SUCCESS, String.format("You created account '%s' (%d)",
                        found.getLogin(), found.getId())),
                this.found);
    }

    @Override
    public void logout() {
        this.found = null;
        this.login = null;
        this.hashedPassword = null;
    }
}
