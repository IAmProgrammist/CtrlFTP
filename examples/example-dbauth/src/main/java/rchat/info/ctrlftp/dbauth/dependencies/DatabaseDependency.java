package rchat.info.ctrlftp.dbauth.dependencies;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import rchat.info.ctrlftp.core.annotations.Dependency;
import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyLevel;

import java.util.HashMap;
import java.util.Map;

@Dependency(level = DependencyLevel.GLOBAL)
public class DatabaseDependency extends AbstractDependency {
    private static final EntityManagerFactory emf;
    private EntityManager entityManager;

    static {
        Map<String, String> env = System.getenv();
        Map<String, Object> configOverrides = new HashMap<String, Object>();
        for (String envName : env.keySet()) {
            if (envName.contains("DB_URL")) {
                configOverrides.put("jakarta.persistence.jdbc.url", env.get(envName));
            } else if (envName.contains("DB_USER")) {
                configOverrides.put("jakarta.persistence.jdbc.user", env.get(envName));
            } else if (envName.contains("DB_PASSWORD")) {
                configOverrides.put("jakarta.persistence.jdbc.password", env.get(envName));
            }
        }

        emf = Persistence.createEntityManagerFactory("ctrlftp-example-s3", configOverrides);
    }

    public DatabaseDependency() {
        this.entityManager = emf.createEntityManager();
    }

    public EntityManager getDatabase() {
        return this.entityManager;
    }
}
