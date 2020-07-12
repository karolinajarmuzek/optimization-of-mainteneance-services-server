package com.oms.serverapp.security;

import com.oms.serverapp.model.ServiceMan;
import com.oms.serverapp.repository.ServiceManRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceManDetails implements UserDetailsService {
    @Autowired
    ServiceManRepository serviceManRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        ServiceMan serviceMan = serviceManRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Serviceman Not Found with -> username." + username)
                );

        return ServiceManPrinciple.build(serviceMan);
    }
}
