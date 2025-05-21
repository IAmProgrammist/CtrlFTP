package rchat.info.ctrlftp.examplebasic.features.service;

import rchat.info.ctrlftp.core.annotations.Dependency;
import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyLevel;

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
