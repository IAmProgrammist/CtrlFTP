package rchat.info.ctrlftp.dbauth.dependencies;

import rchat.info.ctrlftp.core.annotations.Dependency;
import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyLevel;
import rchat.info.ctrlftp.dbauth.utils.OSValidator;

@Dependency(level = DependencyLevel.GLOBAL)
public class ServiceDependency extends AbstractDependency {
    public String getSystemName() {
        if (OSValidator.isWindows()) {
            return "Windows_NT";
        } else {
            return "UNIX Type: L8";
        }
    }
}
