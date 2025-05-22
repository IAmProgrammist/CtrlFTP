package rchat.info.ctrlftp.examples3.features.authentication;

import jakarta.persistence.EntityManager;

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
                .getSingleResult();
    }

    public UserEntity findUserEntityByLogin(String login) {
        return database.createQuery("""
                        select user
                        from UserEntity user
                        where user.login = :login and
                        """, UserEntity.class)
                .setParameter("login", login)
                .getSingleResult();
    }

    public void addUser(UserEntity newUser) {
        this.database.persist(newUser);
    }
}
