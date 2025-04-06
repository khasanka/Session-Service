package com.conference;

import com.conference.auth.ApiKeyAuthFilter;

import com.conference.config.AppConfiguration;
import com.conference.db.SessionDAO;
import com.conference.exception.GenericExceptionMapper;
import com.conference.api.SessionController;
import com.conference.service.SessionService;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.jdbi3.JdbiFactory;

import io.dropwizard.jetty.HttpConnectorFactory;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionServiceApplication extends Application<AppConfiguration> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionServiceApplication.class);


    public static void main(String[] args) throws Exception {
        new SessionServiceApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {

        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false) // false = don't fail if missing
                )
        );
    }

    @Override
    public void run(AppConfiguration config, Environment env) {
        LOGGER.info("Starting SessionServiceApplication...");
        LOGGER.info("Using API key: {}", config.getApiKey());

        env.jersey().register(new GenericExceptionMapper());
        LOGGER.info("Registered global exception mapper");

        ApiKeyAuthFilter authFilter = new ApiKeyAuthFilter(config.getApiKey());
        env.jersey().register(authFilter);
        LOGGER.info("Registered API Key Auth Filter");

        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(env, config.getDataSourceFactory(), "mysql");
        final SessionDAO dao = jdbi.onDemand(SessionDAO.class);
        final SessionService service = new SessionService(dao);
        LOGGER.info("Initialized JDBI + DAO");

        env.jersey().register(service);
        LOGGER.info("Registered SessionService");

        env.jersey().register(new SessionController(service));
        LOGGER.info("Registered SessionController");

        env.getObjectMapper().registerSubtypes(HttpConnectorFactory.class);

    }

}
