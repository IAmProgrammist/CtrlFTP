package rchat.info.ctrlftp.core.dependencies;

import rchat.info.ctrlftp.core.Server;
import rchat.info.ctrlftp.core.annotations.Dependency;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
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
    public DependencyManager(Server context, DependencyLevel level, DependencyManager parentResolver) {
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
    public DependencyManager(Server context, String command, DependencyManager parentResolver) {
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
     * Searches for dependencies in dependency manager and paren dependency managers
     * @param dependencyClass a class to search for
     * @return dependency with fitting class
     */
    protected AbstractDependency getDependency(Class<?> dependencyClass) {
        for (var dependency : this.dependencies) {
            if (dependency.getClass().equals(dependencyClass)) {
                return dependency;
            }
        }

        return parentResolver != null ? parentResolver.getDependency(dependencyClass) : null;
    }

    /**
     * Finds objects for parameters
     * @param parameterList a list of parameters
     * @return a list of objects: Strings and AbstractDependencies to be injected
     */
    public List<Object> getDependenciesForParameters(Parameter[] parameterList) {
        return getDependenciesForParameters(parameterList, 0);
    }

    /**
     * Finds objects for parameters
     * @param parameterList a list of parameters
     * @param depth a recursive depth
     * @return a list of objects: Strings and AbstractDependencies to be injected
     */
    private List<Object> getDependenciesForParameters(Parameter[] parameterList, int depth) {
        var constructorArgs = new ArrayList<Object>();

        for (var parameter : parameterList) {
            if (parameter.getType().equals(String.class)) {
                if (level == DependencyLevel.COMMAND) {
                    constructorArgs.add(command);
                } else {
                    throw new RuntimeException("A dependency " + parameter.getClass() + " with not command-level " +
                            level + " can't have a String as a parameter");
                }
            } else {
                var dependencyClass = parameter.getType();

                if (AbstractDependency.class.isAssignableFrom(dependencyClass)) {
                    var dependencyClassAnnotation = dependencyClass.getAnnotation(Dependency.class);

                    if (dependencyClassAnnotation == null) {
                        throw new RuntimeException("A @Dependency annotation is missing on " + dependencyClass);
                    } else if (dependencyClassAnnotation.level().ordinal() > level.ordinal()) {
                        throw new RuntimeException("You can't require upper-level dependencies from lower-level dependencies");
                    } else {
                        var dependencyObject = getDependency(dependencyClass);

                        if (dependencyObject == null && dependencyClassAnnotation.level().ordinal() == level.ordinal()) {
                            this.dependencies.add((AbstractDependency) constructDependency(dependencyClass, depth + 1));

                            dependencyObject = getDependency(dependencyClass);

                            if (dependencyObject == null) {
                                throw new RuntimeException("Couldn't inject dependency");
                            }
                        }

                        constructorArgs.add(dependencyObject);
                    }
                } else {
                    throw new RuntimeException("A dependency should be inherited from a base class AbstractDependency and " +
                            "contain @Dependency annotation");
                }
            }
        }

        return constructorArgs;
    }

    /**
     * Constructs dependencies and searches for sub-dependencies
     * @param classToInject a dependency class to inject
     * @param depth a recursive depth
     * @return a constructed object
     */
    private Object constructDependency(Class<?> classToInject, int depth) {
        if (depth >= 512) {
            throw new RuntimeException("Injection depth exceeded limits (512 iterations), " +
                    "probably you have recursive dependencies");
        }

        if (!AbstractDependency.class.isAssignableFrom(classToInject)) {
            throw new RuntimeException("A dependency should be inherited from AbstractDependency " + classToInject);
        }

        if (classToInject.getConstructors().length != 1) {
            throw new RuntimeException("A dependency should contain single constructor which parameters should " +
                    "be inherited from AbstractDependency or be a String object for DependencyLevel command");
        }

        var constructor = classToInject.getConstructors()[0];

        try {
            return constructor.newInstance(getDependenciesForParameters(constructor.getParameters(), depth).toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes dependencies and executes dependency injection for dependency
     * @param context a server context
     */
    private void linkDependencies(Server context) {
        this.dependencies.clear();

        for (var dependency : getDependenciesForCurrentResolver(context)) {
            if (getDependency(dependency) == null) {
                this.dependencies.add((AbstractDependency) constructDependency(dependency, 0));
            }
        }
    }
}
