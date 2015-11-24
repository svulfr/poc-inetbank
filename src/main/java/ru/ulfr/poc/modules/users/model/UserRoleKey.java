package ru.ulfr.poc.modules.users.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Joint key for {@link UserRole}
 */
@Embeddable
public class UserRoleKey implements Serializable {
    @Column(name = "username")
    private String userName;

    @Column(name = "role")
    private String role;

    public UserRoleKey() {
    }

    public UserRoleKey(String userName, String role) {
        this.userName = userName;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserRoleKey that = (UserRoleKey) o;

        return !(userName != null ? !userName.equals(that.userName) : that.userName != null) && !(role != null ? !role.equals(that.role) : that.role != null);

    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
