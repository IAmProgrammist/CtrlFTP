package rchat.info.ctrlftp.dependencies.authentication;

import rchat.info.ctrlftp.core.responses.Response;

/**
 * A class that describes authentication result (user info, cause of error and isAuthenticated flag)
 */
public record AuthenticationResult<UserInfo>(boolean isAuthenticated, Response cause, UserInfo authInfo) {
}
