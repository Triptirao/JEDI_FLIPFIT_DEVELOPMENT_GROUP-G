package com.dropwizard;

import com.flipfit.resources.GymFlipFitAdminRestController;
import com.flipfit.resources.GymFlipFitCustomerRestController;
import com.flipfit.resources.GymFlipFitGymOwnerRestController;
import com.flipfit.resources.GymFlipFitUserRestController;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class App extends Application<Configuration>{

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    @Override
    public void initialize(Bootstrap<Configuration> b) {
    }

    @Override
    public void run(Configuration c, Environment e) throws Exception {
        LOGGER.info("Registering REST resources");

        // Register the CORS filter to allow requests from your UI
        final FilterRegistration.Dynamic cors = e.servlets().addFilter("CORS", CrossOriginFilter.class);
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        // Register all requested REST controllers
        e.jersey().register(new GymFlipFitAdminRestController());
        e.jersey().register(new GymFlipFitCustomerRestController());
        e.jersey().register(new GymFlipFitGymOwnerRestController());
        e.jersey().register(new GymFlipFitUserRestController());
    }

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

}
