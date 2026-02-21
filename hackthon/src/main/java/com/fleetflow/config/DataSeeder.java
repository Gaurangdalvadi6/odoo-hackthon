package com.fleetflow.config;

import com.fleetflow.entity.Permission;
import com.fleetflow.entity.Role;
import com.fleetflow.enums.RoleType;
import com.fleetflow.repository.PermissionRepository;
import com.fleetflow.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Seeds permissions and assigns them per role so Manager, Dispatcher, Analyst, and Safety have distinct access.
 */
@Component
@Order(1)
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final PermissionRepository permissionRepo;

    private static final String[] ALL_PERMISSION_NAMES = {
            "VEHICLE_CREATE", "VEHICLE_READ", "VEHICLE_UPDATE",
            "DRIVER_CREATE", "DRIVER_READ", "DRIVER_UPDATE",
            "TRIP_CREATE", "TRIP_READ", "TRIP_UPDATE",
            "MAINTENANCE_CREATE", "MAINTENANCE_READ", "MAINTENANCE_UPDATE",
            "FUEL_CREATE", "FUEL_READ",
            "DASHBOARD_READ", "ANALYTICS_READ", "EXPORT"
    };

    /** Manager: full access (fleet, assets, scheduling, maintenance, reports). */
    private static final String[] MANAGER_PERMISSIONS = ALL_PERMISSION_NAMES;

    /** Dispatcher: create trips, assign drivers; read vehicles and drivers only. */
    private static final String[] DISPATCHER_PERMISSIONS = {
            "VEHICLE_READ", "DRIVER_READ",
            "TRIP_CREATE", "TRIP_READ", "TRIP_UPDATE",
            "DASHBOARD_READ"
    };

    /** Analyst: read data and run reports/exports (audit fuel, ROI, costs). */
    private static final String[] ANALYST_PERMISSIONS = {
            "VEHICLE_READ", "DRIVER_READ", "TRIP_READ",
            "MAINTENANCE_READ", "FUEL_READ",
            "DASHBOARD_READ", "ANALYTICS_READ", "EXPORT"
    };

    /** Safety: driver compliance, license, safety scores; update driver status. */
    private static final String[] SAFETY_PERMISSIONS = {
            "VEHICLE_READ", "DRIVER_READ", "DRIVER_UPDATE", "TRIP_READ",
            "MAINTENANCE_READ", "FUEL_READ",
            "DASHBOARD_READ"
    };

    @Override
    @Transactional
    public void run(String... args) {
        for (String name : ALL_PERMISSION_NAMES) {
            if (permissionRepo.findByName(name).isEmpty()) {
                permissionRepo.save(Permission.builder().name(name).build());
            }
        }

        java.util.Map<String, Permission> byName = permissionRepo.findAll().stream()
                .collect(Collectors.toMap(Permission::getName, p -> p));

        for (RoleType roleType : RoleType.values()) {
            Role role = roleRepo.findByName(roleType)
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setName(roleType);
                        return roleRepo.save(r);
                    });

            String[] names = switch (roleType) {
                case ROLE_MANAGER -> MANAGER_PERMISSIONS;
                case ROLE_DISPATCHER -> DISPATCHER_PERMISSIONS;
                case ROLE_ANALYST -> ANALYST_PERMISSIONS;
                case ROLE_SAFETY -> SAFETY_PERMISSIONS;
            };

            Set<Permission> perms = new HashSet<>();
            for (String n : names) {
                if (byName.containsKey(n)) perms.add(byName.get(n));
            }
            role.setPermissions(perms);
            roleRepo.save(role);
        }
    }
}
