package dk.dagensoel.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.EntityManagerFactory;
import java.util.Properties;

public class HibernateConfig {

    private static EntityManagerFactory emf;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = createEntityManagerFactory();
        }
        return emf;
    }

    private static EntityManagerFactory createEntityManagerFactory() {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();

            props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
            props.put("hibernate.hbm2ddl.auto", "update");
            props.put("hibernate.show_sql", "true");
            props.put("hibernate.format_sql", "true");

            // ----------- Render.com DATABASE_URL handling --------------
            String dbUrl = System.getenv("DATABASE_URL");

            if (dbUrl != null && dbUrl.startsWith("postgres://")) {
                dbUrl = dbUrl.replace("postgres://", "");

                String user = dbUrl.substring(0, dbUrl.indexOf(':'));
                String pass = dbUrl.substring(dbUrl.indexOf(':') + 1, dbUrl.indexOf('@'));
                String hostPortDb = dbUrl.substring(dbUrl.indexOf('@') + 1);
                String host = hostPortDb.substring(0, hostPortDb.indexOf(':'));
                String port = hostPortDb.substring(hostPortDb.indexOf(':') + 1, hostPortDb.indexOf('/'));
                String db = hostPortDb.substring(hostPortDb.indexOf('/') + 1);

                props.put("hibernate.connection.url", "jdbc:postgresql://" + host + ":" + port + "/" + db);
                props.put("hibernate.connection.username", user);
                props.put("hibernate.connection.password", pass);

                System.out.println("Using Render DATABASE_URL");
            } else {
                props.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/dagensoel");
                props.put("hibernate.connection.username", "postgres");
                props.put("hibernate.connection.password", "postgres");

                System.out.println("Using local DB config");
            }

            configuration.setProperties(props);

            ServiceRegistry registry =
                    new StandardServiceRegistryBuilder().applySettings(props).build();

            SessionFactory sessionFactory = configuration.buildSessionFactory(registry);
            return sessionFactory.unwrap(EntityManagerFactory.class);

        } catch (Exception ex) {
            System.err.println("Hibernate initialization failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
}
