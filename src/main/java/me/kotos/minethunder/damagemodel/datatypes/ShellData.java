package me.kotos.minethunder.damagemodel.datatypes;

import org.json.JSONObject;

public record ShellData(String name, String fullName, int tntPower, float launchSpeed, float barrelDevationMult, float penetration, boolean producesShrapnel) {
    public static ShellData fromJson(JSONObject object){
        return new ShellData(
            object.getString("name"),
                object.getString("fullName"),
                object.getInt("tntPower"),
                object.getFloat("launchSpeed"),
                object.getFloat("barrelDevationMult"),
                object.getInt("penetration"),
                false
        );
    }
}
