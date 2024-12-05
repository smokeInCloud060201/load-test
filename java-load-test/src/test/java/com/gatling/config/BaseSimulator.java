package com.gatling.config;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.ArrayList;
import java.util.List;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jmesPath;
import static io.gatling.javaapi.core.CoreDsl.nothingFor;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public abstract class BaseSimulator extends Simulation {

    List<String> jwt = new ArrayList<>(1);

    HttpProtocolBuilder bookStore = http
            .baseUrl("https://abc.com/api")
            .acceptHeader("application/json").contentTypeHeader("application/json");

    HttpProtocolBuilder auth = http
            .baseUrl("https://identity-qa.spdigital-nonprod.auth0.com")
            .acceptHeader("application/json").contentTypeHeader("application/x-www-form-urlencoded");

    // HTTP CALLS
    ChainBuilder authenticate = exec(http("Authenticate")
            .post("/oauth/token")
            .formParam("grant_type", "password")
            .formParam("username", "mock.hos1@yopmail.com")
            .formParam("password", "Abcd1234")
            .formParam("audience", "https://profile.qa.up.spdigital.sg/")
            .formParam("scope", "openid profile email read:contract read:company")
            .formParam("client_id", "A9qjaQmOsC4OECo70cNx4xjl03BvaGGR")
            .check(jmesPath("token").saveAs("access_token"))
            .check(status().is(200)))
            .exec(session -> {
                jwt.add(session.getString("access_token"));
                System.out.println("JWT: " + jwt);
                return session;
            });

    ChainBuilder getAll = exec(
            http("Get Stop Work")
                    .post("/galvatron/v1/dashboard/stop-work")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer #{jwtToken}"))
            ;

    ScenarioBuilder scnAuth = scenario("Auth")
            .exec(authenticate);

    ScenarioBuilder scn = scenario("Book Store Test")
            .exec(s -> {
                System.out.println("JWT to use: " + jwt.get(0));
                s.set("jwtToken", jwt.get(0));
                return s;
            })
            .exec(getAll);

    {
        setUp(scnAuth.injectOpen(atOnceUsers(1)).protocols(auth));
    }
}
