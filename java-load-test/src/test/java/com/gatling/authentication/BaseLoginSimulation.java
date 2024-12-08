package com.gatling.authentication;

import com.learning.project.authentication.dto.LoginRequest;
import io.gatling.javaapi.core.ChainBuilder;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseLoginSimulation {

    protected Map<String, String> tokenStore = new HashMap<>();

    public abstract ChainBuilder getToken(LoginRequest loginRequest);

}
