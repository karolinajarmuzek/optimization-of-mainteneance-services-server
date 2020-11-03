package com.oms.serverapp.service;

import com.oms.serverapp.model.Role;
import com.oms.serverapp.model.RoleName;
import com.oms.serverapp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void findByName(RoleName roleName) {
        roleRepository.findByName(roleName);
    }

    public Set<Role> idsToRoles(Set<Long> rolesIds) {
        Set<Role> roles = new HashSet<>();
        if (rolesIds != null) {
            for (Long roleId : rolesIds) {
                Role role = roleRepository.findById(roleId).orElse(null);
                if (role != null) roles.add(role);
            }
        }
        return roles;
    }
}
