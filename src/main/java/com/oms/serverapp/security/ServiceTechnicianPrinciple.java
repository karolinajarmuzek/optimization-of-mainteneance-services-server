package com.oms.serverapp.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oms.serverapp.model.ServiceTechnician;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ServiceTechnicianPrinciple implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String firstName;

    private String lastName;

    private String username;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public ServiceTechnicianPrinciple(Long id, String firstName, String lastName,
                                      String username, String password,
                                      Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public static ServiceTechnicianPrinciple build(ServiceTechnician serviceTechnician) {
        List<GrantedAuthority> authorities = serviceTechnician.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getName().name())
        ).collect(Collectors.toList());

        return new ServiceTechnicianPrinciple(
                serviceTechnician.getId(),
                serviceTechnician.getFirstName(),
                serviceTechnician.getLastName(),
                serviceTechnician.getUsername(),
                serviceTechnician.getPassword(),
                authorities
        );
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceTechnicianPrinciple user = (ServiceTechnicianPrinciple) o;
        return Objects.equals(id, user.id);
    }
}
