package com.example.rces.service;

import java.io.IOException;

public interface TokenService {

    String getToken(String username, String password) throws IOException, InterruptedException;

}
