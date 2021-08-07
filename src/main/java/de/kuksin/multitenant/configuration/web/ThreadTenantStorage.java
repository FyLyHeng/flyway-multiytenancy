package de.kuksin.multitenant.configuration.web;

public class ThreadTenantStorage {

    private static ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setTenantId(String tenantId) {
        currentTenant.set(tenantId);
    }

    public static String getTenantId() {
        System.out.println("currentTenant.get()___"+currentTenant.get());
        return currentTenant.get();
    }

    public static void clear(){
        currentTenant.remove();
    }
}
