package com.example.rces.configuration;

import com.example.rces.models.BaseRevisionEntity;
import com.example.rces.models.Employee;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class EnversRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        BaseRevisionEntity revision = (BaseRevisionEntity) revisionEntity;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Employee currentUser = (Employee) authentication.getPrincipal();
            revision.setChangedBy(currentUser);
        } else {
            revision.setChangedBy(null);
        }
    }

}
