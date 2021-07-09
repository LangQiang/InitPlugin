package com.lq.plugin.init.utils;

import com.lq.plugin.init.InitClassInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TopoSort {

    public static ArrayList<InitClassInfo> sort(ArrayList<InitClassInfo> initClassInfos) {
        Map<String, InitClassInfo> noDuplicateList = duplicateRemoval(initClassInfos);
        List<String> nodes = new ArrayList<>(noDuplicateList.keySet());

        ArrayList<InitClassInfo> sortedList = new ArrayList<>();

        while (nodes.size() != 0) {
            List<String> keys = new ArrayList<>();
            String key = nodes.get(0);
            keys.add(key);
            find0(key, noDuplicateList, sortedList, nodes, keys);
        }

        Log.e(sortedList.toString());

        return sortedList;
    }

    private static void find0(String key, Map<String, InitClassInfo> noDuplicateList, ArrayList<InitClassInfo> sortedList, List<String> nodes, List<String> keys) {
        InitClassInfo initClassInfo = noDuplicateList.get(key);
        if (initClassInfo == null || initClassInfo.dependSet == null || initClassInfo.dependSet.size() == 0) {
            removeDepend(noDuplicateList, key);
            if (nodes.remove(key)) {
                sortedList.add(initClassInfo);
            }
        } else {
            String nextKey = initClassInfo.dependSet.iterator().next();
            int dupIndex = keys.indexOf(nextKey);
            if (dupIndex >= 0) {
                throw new RuntimeException("input err! 存在环：" + keys.subList(dupIndex, keys.size()));
            }
            keys.add(nextKey);
            find0(nextKey, noDuplicateList, sortedList, nodes, keys);
        }
    }

    private static void removeDepend(Map<String, InitClassInfo> noDuplicateList, String key) {
        for (Map.Entry<String, InitClassInfo> stringInitClassInfoEntry : noDuplicateList.entrySet()) {
            InitClassInfo value = stringInitClassInfoEntry.getValue();
            Iterator<String> iterator = value.dependSet.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(key)) {
                    iterator.remove();
                }
            }
        }
    }

    private static Map<String, InitClassInfo> duplicateRemoval(ArrayList<InitClassInfo> src) {
        Map<String, InitClassInfo> result = new HashMap<>();

        for (InitClassInfo initClassInfo : src) {
            result.put(initClassInfo.fullName, initClassInfo);
        }
        return result;
    }
}
