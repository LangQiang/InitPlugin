package com.lq.plugin.init.utils;

import com.lq.plugin.init.InitClassInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TopoSort {

    public static ArrayList<InitClassInfo> sort(ArrayList<InitClassInfo> initClassInfos) {

        List<InitClassInfo> noDuplicateList = duplicateRemoval(initClassInfos);

        ArrayList<InitClassInfo> sortedList = new ArrayList<>();

        Log.e(noDuplicateList.size() + "");

        while (noDuplicateList.size() != 0) {
            List<String> keys = new ArrayList<>();
            InitClassInfo currentNode = noDuplicateList.get(0);
            if (currentNode.moduleName != null && !currentNode.moduleName.equals("")) {
                keys.add(currentNode.moduleName);
            }
            find0(currentNode, noDuplicateList, sortedList, keys);
        }

        Log.e(sortedList.toString());

        return sortedList;
    }

    private static void find0(InitClassInfo currentNode, List<InitClassInfo> noDuplicateList, ArrayList<InitClassInfo> sortedList, List<String> keys) {
        if (currentNode.dependSet == null || currentNode.dependSet.size() == 0) {
            removeDepend(noDuplicateList, currentNode.moduleName);
            if (noDuplicateList.remove(currentNode)) {
                sortedList.add(currentNode);
            }
            return;
        }

        InitClassInfo nextNode = null;
        String nextKey = null;
        for (String key : currentNode.dependSet) {
            if (key != null && !"".equals(key)) {
                InitClassInfo node = getNextNode(key, noDuplicateList);
                if (node != null) {
                    nextNode = node;
                    nextKey = key;
                    break;
                }
            }
        }

        if (nextNode == null) {
            removeDepend(noDuplicateList, currentNode.moduleName);
            if (noDuplicateList.remove(currentNode)) {
                sortedList.add(currentNode);
            }
            return;
        }

        int dupIndex = keys.indexOf(nextKey);
        if (dupIndex >= 0) {
            throw new RuntimeException("input err! circular dependencies：" + keys.subList(dupIndex, keys.size()));
        }
        keys.add(nextKey);
        find0(nextNode, noDuplicateList, sortedList, keys);
    }

    private static InitClassInfo getNextNode(String nextKey, List<InitClassInfo> noDuplicateList) {
        InitClassInfo initClassInfo = null;
        for (InitClassInfo classInfo : noDuplicateList) {
            if (classInfo.moduleName.equals(nextKey)) {
                initClassInfo = classInfo;
            }
        }
        return initClassInfo;
    }

    private static void removeDepend(List<InitClassInfo> noDuplicateList, String key) {
        for (InitClassInfo initClassInfo : noDuplicateList) {
            Iterator<String> iterator = initClassInfo.dependSet.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(key)) {
                    iterator.remove();
                }
            }
        }
    }

    private static List<InitClassInfo> duplicateRemoval(ArrayList<InitClassInfo> src) {
        Map<String, InitClassInfo> result = new HashMap<>();

        for (InitClassInfo initClassInfo : src) {
            result.put(initClassInfo.fullName, initClassInfo);
        }
        return new ArrayList<>(result.values());
    }
}
