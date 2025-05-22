package rchat.info.ctrlftp.dbauth.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import rchat.info.ctrlftp.dbauth.entities.UserEntity;

public class UserEntityRepository {
    private final EntityManager database;

    public UserEntityRepository(EntityManager database) {
        this.database = database;
    }

    public UserEntity findUserEntityByLoginAndPassword(String login, String hashedPassword) {
        return database.createQuery("""
                        select user
                        from UserEntity user
                        where user.login = :login and
                        user.hashPassword = :password
                        """, UserEntity.class)
                .setParameter("login", login)
                .setParameter("password", hashedPassword)
                .getSingleResultOrNull();
    }

    public UserEntity findUserEntityByLogin(String login) {
        return database.createQuery("""
                        select user
                        from UserEntity user
                        where user.login = :login
                        """, UserEntity.class)
                .setParameter("login", login)
                .getSingleResultOrNull();
    }

    public void addUser(UserEntity newUser) {
        EntityTransaction entityTransaction = this.database.getTransaction();
        entityTransaction.begin();
        this.database.persist(newUser);
        entityTransaction.commit();
    }
}
