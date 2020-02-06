package com.babbangona.accesscontrol;

public class ApplicationClass {



        private String app_name;
        private String package_name;
        private String status;
        private String staff_id;
        private String staff_name;
        private String staff_role;
        private String staff_program;
        private String hub;
        private String download_version;
        private String new_version;
        private String what_new;
        private String app_version;




    public ApplicationClass(String app_name, String package_name, String status, String staff_id, String staff_name, String download_version, String new_version, String what_new, String staff_role, String hub, String app_version, String staff_program) {

            this.app_name = app_name;
            this.package_name = package_name;
            this.status = status;
            this.staff_id = staff_id;
            this.staff_name = staff_name;
            this.download_version = download_version;
            this.new_version = new_version;
            this.what_new = what_new;
            this.staff_role = staff_role;
            this.hub = hub;
            this.app_version = app_version;
            this.staff_program = staff_program;
        }

        public String getAppName() {
            return app_name;
        }
        public String getPackageName() {
            return package_name;
        }
        public String getStatus() {
            return status;
        }
        public String getStaffId(){ return staff_id;}
        public String getStaffName(){return  staff_name;}
        public String getDownload_version(){return  download_version;}
        public String getNew_version(){return  new_version;}
        public String getWhat_new(){return  what_new;}
        public String getStaff_role(){return staff_role;}
        public String getStaff_hub(){return hub;}
        public String getApp_version(){return app_version;}
        public String getStaff_program() { return staff_program; }



}
