package rchat.info.ctrlftp.examplebasic.features.authentication;

import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.core.responses.ResponseTypes;
import rchat.info.ctrlftp.dependencies.authentication.AuthenticationResult;
import rchat.info.ctrlftp.dependencies.authentication.BaseAuthenticationDependency;

public class BasicAuthenticationDependency extends BaseAuthenticationDependency<UserInfo> {
    private UserInfo userInfo;
    private boolean passwordSetup = false;

    public BasicAuthenticationDependency() {
        this.userInfo = new UserInfo();
    }

    public void setLogin(String login) {
        this.userInfo.setLogin(login);
    }

    public void setPassword(String password) {
        passwordSetup = true;
    }

    @Override
    public AuthenticationResult<UserInfo> authenticate() {
        if (userInfo.getLogin() == null) {
            return new AuthenticationResult<>(false,
                    new Response(ResponseTypes.NOT_LOGGED_IN, "Specify login"),
                    null);
        }

        if (!passwordSetup) {
            return new AuthenticationResult<>(false,
                    new Response(ResponseTypes.USERNAME_OK_NEED_PASS, "Specify password"),
                    null);
        }

        return new AuthenticationResult<>(true,
                new Response(ResponseTypes.AUTH_SUCCESS, "Authentication succeded"),
                this.userInfo);
    }

    @Override
    public void logout() {
        this.userInfo.setLogin(null);
        this.passwordSetup = false;
    }
}
