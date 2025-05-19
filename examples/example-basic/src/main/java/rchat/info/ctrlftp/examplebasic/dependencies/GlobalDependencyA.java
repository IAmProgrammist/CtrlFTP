package rchat.info.ctrlftp.examplebasic.dependencies;

import rchat.info.ctrlftp.core.annotations.Dependency;
import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyLevel;

@Dependency(level = DependencyLevel.GLOBAL)
public class GlobalDependencyA extends AbstractDependency {
    GlobalDependencyB depB;
    GlobalDependencyC depC;

    public GlobalDependencyA(GlobalDependencyB depB, GlobalDependencyC depC) {
        this.depB = depB;
        this.depC = depC;
    }
}
