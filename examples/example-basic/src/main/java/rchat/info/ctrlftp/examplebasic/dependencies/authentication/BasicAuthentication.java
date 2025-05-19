package rchat.info.ctrlftp.examplebasic.dependencies.authentication;

import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.core.responses.ResponseTypes;
import rchat.info.ctrlftp.dependencies.authentication.AuthenticationResult;
import rchat.info.ctrlftp.dependencies.authentication.BaseAuthenticationDependency;

public class BasicAuthentication extends BaseAuthenticationDependency<UserInfo> {
    private UserInfo userInfo;
    private boolean passwordSetup = false;

    public BasicAuthentication() {
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
}
