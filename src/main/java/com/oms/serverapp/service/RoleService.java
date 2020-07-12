package com.oms.serverapp.service;

import com.oms.serverapp.model.RoleName;
import com.oms.serverapp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void findByName(RoleName roleName) {
        roleRepository.findByName(roleName);
    }
}
