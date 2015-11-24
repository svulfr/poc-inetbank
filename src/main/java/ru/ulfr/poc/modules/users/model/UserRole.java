package ru.ulfr.poc.modules.users.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Model class describing user role. User for account role management
 */
@Entity
@Table(name = "users_roles")
@SuppressWarnings("unused")
public class UserRole implements Serializable {
    @Id
    private UserRoleKey id;


    public UserRole() {
    }

    public UserRole(String userName, String role) {
        this.id = new UserRoleKey(userName, role);
    }

    public UserRoleKey getId() {
        return id;
    }

    public void setId(UserRoleKey id) {
        this.id = id;
    }
}
