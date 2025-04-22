package rchat.info.ctrlftp.core.dependencies;

import rchat.info.ctrlftp.core.Server;
import rchat.info.ctrlftp.core.annotations.Dependency;

import java.util.ArrayList;
import java.util.List;

public class DependencyManager {
    private final DependencyLevel level;
    private DependencyManager parentResolver = null;
    private List<AbstractDependency> dependencies;
    private final String command;

    /**
     * This constructor may be used for creating global level dependency resolver.
     * A parent is not specified for this level
     *
     * @param context a server context
     */
    public DependencyManager(Server context) {
        this.level = DependencyLevel.GLOBAL;
        this.dependencies = new ArrayList<>();
        this.command = null;

        linkDependencies(context);
    }

    /**
     * This constructor is used for creating any intermediate-leveled dependency resolver.
     * Lookup {@link DependencyManager#DependencyManager(Server)}  DependencyResolver} to
     * create global-leveled dependency resolver and
     * {@link DependencyManager#DependencyManager(Server, String, DependencyManager)}  DependencyResolver} for
     * creating command-leveled dependency resolver
     *
     * @param context a server context
     * @param level a level
     * @param parentResolver a parent-level resolver
     */
    DependencyManager(Server context, DependencyLevel level, DependencyManager parentResolver) {
        this.level = level;
        this.dependencies = new ArrayList<>();
        this.parentResolver = parentResolver;
        this.command = null;
        if (areParentAndChildLevelsNotCompatible()) {
            throw new RuntimeException(
                    "A parent dependency resolver shouldn't have upper level. " +
                            "Parent level: " + this.parentResolver.level.name() + " (" + this.parentResolver.level.ordinal() + ") " +
                            "current level: " + this.level.name() + " (" + this.level.ordinal() + ")");
        }

        linkDependencies(context);
    }

    /**
     * A dependency resolver could be created for a command scope. The difference for this resolver
     * is that command string should be passed and processed by dependencies.
     *
     * @param context a server context
     * @param command a raw command coming from client
     * @param parentResolver a parent-level resolver
     */
    DependencyManager(Server context, String command, DependencyManager parentResolver) {
        this.level = DependencyLevel.COMMAND;
        this.dependencies = new ArrayList<>();
        this.parentResolver = parentResolver;
        this.command = command;
        if (areParentAndChildLevelsNotCompatible()) {
            throw new RuntimeException(
                    "A parent dependency resolver shouldn't have upper level. " +
                            "Parent level: " + this.parentResolver.level.name() + " (" + this.parentResolver.level.ordinal() + ") " +
                            "current level: " + this.level.name() + " (" + this.level.ordinal() + ")");
        }

        linkDependencies(context);
    }

    /**
     * Checks if parent and current resolvers levels are compatible
     * @return true if they are not compatible. Amogus tun dun dun dun dun dun dun
     * dududun. PUM BUM. Tun-dun-dun-dun-dun-dun-DUN! DU-du-du-DU-du-du-DUN!
     */
    private boolean areParentAndChildLevelsNotCompatible() {
        return this.parentResolver.level.ordinal() >= this.level.ordinal();
    }

    /**
     * Finds dependency classes with level that equals to dependency resolver level
     * @param context a server context
     * @return a list of suitable classes
     */
    private List<Class<? extends AbstractDependency>> getDependenciesForCurrentResolver(Server context) {
        return context.getDependencyClasses().stream()
                .filter(aClass -> {
                    var annotation = aClass.getAnnotation(Dependency.class);
                    if (annotation == null) return false;

                    return annotation.level() == this.level;
                })
                .toList();
    }

    /**
     * Initializes dependencies and executes dependency injection for dependency
     * @param context
     */
    private void linkDependencies(Server context) {
        this.dependencies.clear();

        var depsWithResolverLevel = getDependenciesForCurrentResolver(context);
        while (!depsWithResolverLevel.isEmpty()) {

        }
    }
}
