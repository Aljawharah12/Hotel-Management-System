package com.myhotel.app;

public record Session(Role role, int id, String name, String subtitle) {
    public enum Role {
        GUEST,
        EMPLOYEE
    }
}
