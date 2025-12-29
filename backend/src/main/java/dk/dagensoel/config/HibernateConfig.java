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

            String url  = System.getProperty("DB_URL");
            String user = System.getProperty("DB_USER");
            String pass = System.getProperty("DB_PASS");

            if (url != null && user != null && pass != null) {
                props.put("hibernate.connection.url", url);
                props.put("hibernate.connection.username", user);
                props.put("hibernate.connection.password", pass);
                System.out.println("Using DB from system properties");
            } else {
                String host = System.getenv("POSTGRES_HOST");
                String db   = System.getenv("POSTGRES_DATABASE");
                String envUser = System.getenv("POSTGRES_USER");
                String envPass = System.getenv("POSTGRES_PASSWORD");

                if (host != null && db != null && envUser != null && envPass != null) {
                    props.put("hibernate.connection.url",
                            "jdbc:postgresql://" + host + ":5432/" + db);
                    props.put("hibernate.connection.username", envUser);
                    props.put("hibernate.connection.password", envPass);
                    System.out.println("Using Render POSTGRES_* env vars");
                } else {
                    props.put("hibernate.connection.url",
                            "jdbc:postgresql://localhost:5432/dagensoel");
                    props.put("hibernate.connection.username", "postgres");
                    props.put("hibernate.connection.password", "postgres");
                    System.out.println("Using local DB config");
                }
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
