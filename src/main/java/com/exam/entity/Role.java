//package com.exam.entity;
//
//import jakarta.persistence.*;
//import java.util.HashSet;
//import java.util.Set;
//
//@Entity
//public class Role {
//    @Id
//    private Long roleId;
//    private String roleName;
//
//    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "role")
//    private Set<UserRole> userRoles = new HashSet<>();
//
//    public Set<UserRole> getUserRoles() {
//        return userRoles;
//    }
//
//    public void setUserRoles(Set<UserRole> userRoles) {
//        this.userRoles = userRoles;
//    }
//
//    public Role(Long roleId, String roleName) {
//        this.roleId = roleId;
//        this.roleName = roleName;
//    }
//    public Role() {
//    }
//    public Long getRoleId() {
//        return roleId;
//    }
//    public void setRoleId(Long roleId) {
//        this.roleId = roleId;
//    }
//    public String getRoleName() {
//        return roleName;
//    }
//    public void setRoleName(String roleName) {
//        this.roleName = roleName;
//    }
//
//}
