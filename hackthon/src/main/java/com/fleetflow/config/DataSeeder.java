package com.fleetflow.config;

import com.fleetflow.entity.Permission;
import com.fleetflow.entity.Role;
import com.fleetflow.enums.RoleType;
import com.fleetflow.repository.PermissionRepository;
import com.fleetflow.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Seeds roles and permissions on first run so you can register users without manual SQL.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final PermissionRepository permissionRepo;

    private static final String[] PERMISSION_NAMES = {
            "VEHICLE_CREATE", "VEHICLE_READ", "VEHICLE_UPDATE",
            "DRIVER_CREATE", "DRIVER_READ", "DRIVER_UPDATE",
            "TRIP_CREATE", "TRIP_READ", "TRIP_UPDATE",
            "MAINTENANCE_CREATE", "MAINTENANCE_READ", "MAINTENANCE_UPDATE",
            "FUEL_CREATE", "FUEL_READ"
    };

    @Override
    @Transactional
    public void run(String... args) {
        if (permissionRepo.count() > 0) return;

        for (String name : PERMISSION_NAMES) {
            permissionRepo.save(Permission.builder().name(name).build());
        }

        Set<Permission> all = permissionRepo.findAll().stream().collect(Collectors.toSet());

        for (RoleType roleType : RoleType.values()) {
            if (roleRepo.findByName(roleType).isEmpty()) {
                Role role = new Role();
                role.setName(roleType);
                role.setPermissions(all);
                roleRepo.save(role);
            }
        }
    }
}
