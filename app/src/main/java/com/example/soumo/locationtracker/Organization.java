package com.example.soumo.locationtracker;

public class Organization {
    private String organizationName;
    private String orgId;

    public Organization(){


    }

    public Organization(String organizationName, String orgId){
        this.organizationName = organizationName;
        this.orgId = orgId;

    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
    @Override
    public String toString() {
        return organizationName;
    }
}
