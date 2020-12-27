package com.elt.bank.Setup;

import com.elt.bank.modal.Privilege;
import com.elt.bank.modal.Role;
import com.elt.bank.modal.User;
import com.elt.bank.repo.PrivilegeRepo;
import com.elt.bank.repo.RoleRepo;
import com.elt.bank.repo.UserRepo;
import com.elt.bank.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SetupPrivileges implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger log = LoggerFactory.getLogger(SetupPrivileges.class);

    boolean alreadySetup = false;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private RoleRepo roleRepository;

    @Autowired
    private PrivilegeRepo privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public SetupPrivileges getSetupPrivileges() {
        return new SetupPrivileges();
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;



        List<Privilege> adminPrivileges = createAdminPrivileges();
        List<Privilege> empPrivileges = createEmpPrivileges();

        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_EMP", empPrivileges);

        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        User user = new User();
        user.setFirstName("Vijay");
        user.setLastName("Sharma");
        user.setUserName("admin");
        user.setPassword(passwordEncoder.encode("test"));
        user.setEmail("vjymits@gmail.com");
        user.setRoles(Arrays.asList(adminRole));
        user.setType(Constants.ADMIN_USER_TYPE);
        userRepository.save(user);
        log.info("Admin setup Done...");
        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {

        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege();
            privilege.setName(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(
            String name, List<Privilege> privileges) {

        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role();
            role.setName(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }

    private List<Privilege> createAdminPrivileges() {
        List<Privilege> prvs = new ArrayList<>();
        prvs.add(createPrivilegeIfNotFound(Constants.ADD_EMP_PRIVILEGE));
        prvs.add(createPrivilegeIfNotFound(Constants.DEL_EMP_PRIVILEGE));
        return prvs;
    }

    private List<Privilege> createEmpPrivileges() {
        List<Privilege> prvs = new ArrayList<>();
        prvs.add(createPrivilegeIfNotFound(Constants.ADD_CUST));
        prvs.add(createPrivilegeIfNotFound(Constants.DEL_CUST));
        prvs.add(createPrivilegeIfNotFound(Constants.READ_CUST));
        prvs.add(createPrivilegeIfNotFound(Constants.UPDATE_CUST));

        prvs.add(createPrivilegeIfNotFound(Constants.ADD_ACC));
        prvs.add(createPrivilegeIfNotFound(Constants.DEL_ACC));
        prvs.add(createPrivilegeIfNotFound(Constants.READ_ACC));
        prvs.add(createPrivilegeIfNotFound(Constants.UPDATE_ACC));

        prvs.add(createPrivilegeIfNotFound(Constants.ADD_TRN));
        prvs.add(createPrivilegeIfNotFound(Constants.READ_TRN));
        return prvs;
    }


}
