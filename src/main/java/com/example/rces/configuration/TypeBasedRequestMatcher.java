package com.example.rces.configuration;

import com.example.rces.service.impl.WebSecurityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeBasedRequestMatcher implements RequestMatcher {

    private final Pattern pattern = Pattern.compile("/view/(\\d+)");
    private final WebSecurityService webSecurityService;

    String type;

    public TypeBasedRequestMatcher(WebSecurityService webSecurityService) {
        this.webSecurityService = webSecurityService;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        Matcher matcher = pattern.matcher(request.getRequestURI());
        if (matcher.matches()) {
            String requestNumber = matcher.group(1);
            if (requestNumber != null) {
                type = String.valueOf(webSecurityService.findByRequestNumber(Integer.valueOf(requestNumber)).getTypeRequest());
            }

            return switch (type) {
                case "otk" -> request.isUserInRole("OTK") ||
                        request.isUserInRole("ADMIN") ||
                        request.isUserInRole("MASTER");
                case "technologist" -> request.isUserInRole("TECHNOLOGIST") ||
                        request.isUserInRole("ADMIN") ||
                        request.isUserInRole("MASTER");
                case "constructor" -> request.isUserInRole("CONSTRUCTOR") ||
                        request.isUserInRole("ADMIN") ||
                        request.isUserInRole("MASTER");
                default -> false;
            };
        }
        return false;
    }

}
