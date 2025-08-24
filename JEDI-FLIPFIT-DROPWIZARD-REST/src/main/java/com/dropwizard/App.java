package com.dropwizard;

import com.flipfit.resources.GymFlipFitAdminRestController;
import com.flipfit.resources.GymFlipFitCustomerRestController;
import com.flipfit.resources.GymFlipFitGymOwnerRestController;
import com.flipfit.resources.GymFlipFitUserRestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.flipkart.rest.*;

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
        // Register all requested REST controllers (Resources)
        e.jersey().register(new GymFlipFitAdminRestController());
        e.jersey().register(new GymFlipFitCustomerRestController());
        e.jersey().register(new GymFlipFitGymOwnerRestController());
        e.jersey().register(new GymFlipFitUserRestController());
//        environment.jersey().register(new GymFlipFitPaymentRestController());
    }
    public static void main(String[] args) throws Exception {
        new App().run(args);
    }
}
