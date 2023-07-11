package com.mediprice.hospitals.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SpHelper {
    public static class Role{
        String role=null;
        String id=null;
        Role(String role,String id){
            this.role=role;
            this.id=id;
        }
        public String getRole(){
            return role;
        }
        public String getId(){
            return  id;
        }

        @Override
        public String toString() {
            return "Role{" +
                    "role='" + role + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }
    public static void saveRoll(Context c,String id,String role){
        SharedPreferences prefs = c.getSharedPreferences(
                "covidhospital", Context.MODE_PRIVATE);
        prefs.edit().putString("id",id).putString("role",role).apply();
        Log.d("SAVEPREF","Saving role");
    }
    public static Role getRoll(Context c){
        SharedPreferences prefs = c.getSharedPreferences(
                "covidhospital", Context.MODE_PRIVATE);
        Log.d("SAVEPREF","Getting  role");
        return new Role(prefs.getString("role",null),prefs.getString("id",null));
    }
    public static void clearRole(Context c){
        SharedPreferences prefs = c.getSharedPreferences(
                "covidhospital", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

}
