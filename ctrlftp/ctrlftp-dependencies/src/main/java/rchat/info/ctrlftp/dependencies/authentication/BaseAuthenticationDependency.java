package rchat.info.ctrlftp.dependencies.authentication;

import rchat.info.ctrlftp.core.annotations.Dependency;
import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyLevel;

/**
 * A dependency for authentication. Has a session level. You should specify UserInfo
 * type when overriding
 */
@Dependency(level = DependencyLevel.SESSION)
public abstract class BaseAuthenticationDependency<UserInfo> extends AbstractDependency {
    /**
     * Checks user authentication. Is something is wrong or missing, return object contains
     * cause of error
     * @return result of authentication
     */
    public abstract AuthenticationResult<UserInfo> authenticate();
}
