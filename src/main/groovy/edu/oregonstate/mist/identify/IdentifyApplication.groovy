package edu.oregonstate.mist.identify

import edu.oregonstate.mist.api.Application
import edu.oregonstate.mist.api.Configuration
import edu.oregonstate.mist.identify.db.IdentifyDAO
import edu.oregonstate.mist.identify.health.IdentifyHealthCheck
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.setup.Environment
import org.skife.jdbi.v2.DBI

/**
 * Main application class.
 */
class IdentifyApplication extends Application<IdentifyConfiguration> {
    /**
     * Parses command-line arguments and runs the application.
     *
     * @param configuration
     * @param environment
     */
    @Override
    public void run(IdentifyConfiguration configuration, Environment environment) {
        this.setup(configuration, environment)

        DBIFactory factory = new DBIFactory()
        DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "jdbi")
        IdentifyDAO identifyDAO = jdbi.onDemand(IdentifyDAO.class)
        environment.jersey().register(new IdentifyResource(identifyDAO))

        IdentifyHealthCheck healthCheck = new IdentifyHealthCheck(identifyDAO)
        environment.healthChecks().register("identifyHealthCheck", healthCheck)
    }

    /**
     * Instantiates the application class with command-line arguments.
     *
     * @param arguments
     * @throws Exception
     */
    public static void main(String[] arguments) throws Exception {
        new IdentifyApplication().run(arguments)
    }
}
