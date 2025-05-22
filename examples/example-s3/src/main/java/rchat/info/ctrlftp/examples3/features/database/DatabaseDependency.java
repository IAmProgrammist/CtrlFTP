package rchat.info.ctrlftp.examples3.features.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import rchat.info.ctrlftp.core.annotations.Dependency;
import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyLevel;

@Dependency(level = DependencyLevel.GLOBAL)
public class DatabaseDependency extends AbstractDependency {
    private static final EntityManagerFactory emf;
    private EntityManager entityManager;

    static {
        emf = Persistence.createEntityManagerFactory("ctrlftp-example-s3");
    }

    public DatabaseDependency() {
        this.entityManager = emf.createEntityManager();
    }

    public EntityManager getDatabase() {
        return this.entityManager;
    }
}
