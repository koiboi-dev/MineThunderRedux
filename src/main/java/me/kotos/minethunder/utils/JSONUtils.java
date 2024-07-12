package me.kotos.minethunder.utils;

import me.kotos.minethunder.damagemodel.datatypes.Vect;
import me.kotos.minethunder.enums.SeatType;
import org.bukkit.util.Vector;
import org.json.JSONArray;

public class JSONUtils {
    public static Vector getVectorFromJSON(JSONArray obj){
        return new Vector(
                obj.getFloat(0),
                obj.getFloat(1),
                obj.getFloat(2)
        );
    }
    public static Vect getVectFromJSON(JSONArray obj){
        return new Vect(
                obj.getFloat(0),
                obj.getFloat(1),
                obj.getFloat(2)
        );
    }

    public static SeatType[] getSeatTypesFromJSON(JSONArray obj) {
        SeatType[] out = new SeatType[obj.length()];
        int loops = 0;
        for (Object type : obj){
            if (type instanceof String str){
                out[loops] = SeatType.valueOf(str);
                loops++;
            }
        }
        return out;
    }

    public static int[] getIntegerArrayFromJSON(JSONArray obj){
        int[] out = new int[obj.length()];
        int loops = 0;
        for (Object type : obj){
            out[loops] = (int)type;
            loops++;
        }
        return out;
    }

    public static float[] getFloatArrayFromJSON(JSONArray obj){
        float[] out = new float[obj.length()];
        int loops = 0;
        for (Object type : obj){
            out[loops] = (float)type;
            loops++;
        }
        return out;
    }
}
