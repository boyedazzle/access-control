package com.babbangona.accesscontrol;

public class VersionControlClass {

    private String app_name;
    private String appid_staffid;
    private String app_id;
    private String staff_name;
    private String staff_id;
    private String supervisor_id;
    private String lead_id;
    private String access_control_version;
    private String user_version;


    public VersionControlClass(String appid_staffid, String app_name, String app_id, String staff_name, String staff_id, String supervisor_id, String lead_id, String access_control_version, String user_version) {

        this.appid_staffid = appid_staffid;
        this.app_name = app_name;
        this.app_id = app_id;
        this.staff_name = staff_name;
        this.staff_id = staff_id;
        this.supervisor_id = supervisor_id;
        this.lead_id = lead_id;
        this.access_control_version = access_control_version;
        this.user_version = user_version;

    }

    public String getAppid_staffid() {
        return appid_staffid;
    }

    public String getApp_name() {
        return app_name;
    }

    public String getApp_id() {
        return app_id;
    }

    public String getStaff_name() {
        return staff_name;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public String getSupervisor_id() {
        return supervisor_id;
    }

    public String getLead_id() {
        return lead_id;
    }

    public String getAccess_control_version() {
        return access_control_version;
    }

    public String getUser_version() {
        return user_version;
    }


}
