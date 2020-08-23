package com.oms.serverapp.security;

import com.oms.serverapp.model.ServiceTechnician;
import com.oms.serverapp.repository.ServiceTechnicianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceTechnicianDetails implements UserDetailsService {
    @Autowired
    ServiceTechnicianRepository serviceTechnicianRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        ServiceTechnician serviceTechnician = serviceTechnicianRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Service technician Not Found with -> username." + username)
                );

        return ServiceTechnicianPrinciple.build(serviceTechnician);
    }
}
