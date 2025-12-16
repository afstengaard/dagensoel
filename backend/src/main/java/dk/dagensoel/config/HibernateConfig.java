package dk.dagensoel.config;

import dk.dagensoel.entities.AdminUser;
import dk.dagensoel.entities.Beer;
import dk.dagensoel.entities.Event;
import dk.dagensoel.entities.Vote;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import jakarta.persistence.EntityManagerFactory;
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

            // ----------- Render.com POSTGRES_ variables --------------
            String host = System.getenv("POSTGRES_HOST");
            String db   = System.getenv("POSTGRES_DATABASE");
            String user = System.getenv("POSTGRES_USER");
            String pass = System.getenv("POSTGRES_PASSWORD");

            if (host != null && db != null && user != null && pass != null) {
                String jdbcUrl = "jdbc:postgresql://" + host + ":5432/" + db;

                props.put("hibernate.connection.url", jdbcUrl);
                props.put("hibernate.connection.username", user);
                props.put("hibernate.connection.password", pass);

                System.out.println("Using Render POSTGRES_* env vars");
            } else {
                props.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/dagensoel");
                props.put("hibernate.connection.username", "postgres");
                props.put("hibernate.connection.password", "postgres");

                System.out.println("Using local DB config");
            }

            configuration.setProperties(props);

            configuration.addAnnotatedClass(Event.class);
            configuration.addAnnotatedClass(Beer.class);
            configuration.addAnnotatedClass(Vote.class);
            configuration.addAnnotatedClass(AdminUser.class);


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
