package dk.dagensoel.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.EntityManagerFactory;
import java.util.Properties;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
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

            // Hibernate settings
            Properties props = new Properties();
            props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            props.put("hibernate.connection.driver_class", "org.postgresql.Driver");

            // Get database credentials from environment variables (Render.com)
            String host = System.getenv("POSTGRES_HOST");
            String user = System.getenv("POSTGRES_USER");
            String pass = System.getenv("POSTGRES_PASSWORD");
            String db   = System.getenv("POSTGRES_DATABASE");

            // Fallback for local development
            if (host == null || host.isEmpty()) {
                host = "localhost";
                user = "postgres";
                pass = "postgres";
                db = "dagensoel";
            }

            String url = "jdbc:postgresql://" + host + ":5432/" + db;
            props.put("hibernate.connection.url", url);
            props.put("hibernate.connection.username", user);
            props.put("hibernate.connection.password", pass);

            props.put("hibernate.hbm2ddl.auto", "update");
            props.put("hibernate.show_sql", "true");
            props.put("hibernate.format_sql", "true");

            configuration.setProperties(props);

            // TODO: Add entity classes
            // configuration.addAnnotatedClass(Beer.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            return sessionFactory.unwrap(EntityManagerFactory.class);

        } catch (Exception ex) {
            System.err.println("Hibernate initialization failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

}
