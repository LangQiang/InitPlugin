package com.lq.plugin.init.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lq.plugin.init.InitClassInfo;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] aaa) {

        String json = "[" +
                "{\"moduleName\":\"b\",\"fullName\":\"bbb\", \"dependSet\":[\"d\",\"e\",\"c\"]}," +
                "{\"moduleName\":\"c\",\"fullName\":\"ccc\", \"dependSet\":[\"e\"]}," +
                "{\"moduleName\":\"d\",\"fullName\":\"ddd\", \"dependSet\":[\"f\"]}," +
                "{\"moduleName\":\"e\",\"fullName\":\"eee\", \"dependSet\":[\"g\"]}," +
                "{\"moduleName\":\"a\",\"fullName\":\"aaa\", \"dependSet\":[\"b\",\"c\",\"\"]}," +
                "{\"moduleName\":\"f\",\"fullName\":\"fff\", \"dependSet\":[]}" +
                "]";
        ArrayList<InitClassInfo> src = new Gson().fromJson(json, new TypeToken<List<InitClassInfo>>(){}.getType());
        ArrayList<InitClassInfo> sort = TopoSort.sort(src);
        for (InitClassInfo initClassInfo : sort) {
            Log.e(initClassInfo.fullName);
        }
    }
}
