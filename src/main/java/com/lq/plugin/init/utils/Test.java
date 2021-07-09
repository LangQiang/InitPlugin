package com.lq.plugin.init.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lq.plugin.init.InitClassInfo;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] aaa) {

        String json = "[" +
                "{\"fullName\":\"a\", \"dependSet\":[\"b\",\"c\",\"\"]}," +
                "{\"fullName\":\"b\", \"dependSet\":[\"d\",\"e\",\"c\"]}," +
                "{\"fullName\":\"c\", \"dependSet\":[\"e\"]}," +
                "{\"fullName\":\"d\", \"dependSet\":[\"f\"]}," +
                "{\"fullName\":\"e\", \"dependSet\":[\"g\"]}," +
                "{\"fullName\":\"f\", \"dependSet\":[\"\"]}" +
                "]";
        ArrayList<InitClassInfo> src = new Gson().fromJson(json, new TypeToken<List<InitClassInfo>>(){}.getType());
        ArrayList<InitClassInfo> sort = TopoSort.sort(src);
        for (InitClassInfo initClassInfo : sort) {
            Log.e(initClassInfo.fullName);
        }
    }
}
