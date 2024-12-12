package com.gatling.authentication;

import com.learning.project.authentication.dto.LoginRequest;
import com.learning.project.utils.JsonUtil;
import io.gatling.javaapi.core.ChainBuilder;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;

public class BasicLoginSimulation extends BaseLoginSimulation {
    @Override
    public ChainBuilder getToken(LoginRequest loginRequest) {
        return null;
    }
}
