package rchat.info.ctrlftp.examples3.features.authentication;

import rchat.info.ctrlftp.dependencies.authentication.AuthenticationResult;
import rchat.info.ctrlftp.dependencies.authentication.BaseAuthenticationDependency;

public class AuthenticationDependency extends BaseAuthenticationDependency<UserEntity> {
    @Override
    public AuthenticationResult<UserEntity> authenticate() {
        return null;
    }

    @Override
    public void logout() {

    }
}
