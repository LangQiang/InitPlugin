package com.lq.plugin.init.utils;

import com.google.gson.Gson;
import com.lq.plugin.init.DeepLinkClassInfo;
import com.lq.plugin.init.InitClassInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigFileMgr {

    ArrayList<String> classes;

    private ConfigFileMgr() {
        classes = new ArrayList<>();
    }

    public static ConfigFileMgr getInstance() {
        return Inner.INSTANCE;
    }

    private static class Inner {
        private static final ConfigFileMgr INSTANCE = new ConfigFileMgr();
    }

    public void deleteConfig() {
        deleteConfig("init.config");
        deleteConfig("deeplink.config");
    }

    private void deleteConfig(String fileName) {
        File cacheFile = new File("./.idea", fileName + ".cache");
        File file = new File("./.idea", fileName);
        if (file.exists()) {
            if (cacheFile.exists()) {
                cacheFile.delete();
            }
            file.renameTo(cacheFile);
        }
    }

    public ArrayList<DeepLinkClassInfo> readDeepLinkConfig() {
        List<String> strings = readConfig("deeplink.config");
        if (strings == null) {
            return null;
        }

        ArrayList<DeepLinkClassInfo> arrayList = new ArrayList<>();

        for (String s : strings) {
            arrayList.add(new Gson().fromJson(s, DeepLinkClassInfo.class));
        }
        return arrayList;
    }

    public ArrayList<InitClassInfo> readInitConfig() {
        List<String> strings = readConfig("init.config");
        if (strings == null) {
            return null;
        }

        ArrayList<InitClassInfo> arrayList = new ArrayList<>();

        for (String s : strings) {
            arrayList.add(new Gson().fromJson(s, InitClassInfo.class));
        }
        return arrayList;
    }

    private List<String> readConfig(String fileName) {
        File file = new File("./.idea", fileName);
        if (!file.exists()) {
            file = new File("./.idea", fileName + ".cache");
            if (!file.exists()) {
                return null;
            }
        }

        BufferedReader bufferedReader = null;
        try {
            ArrayList<String> arrayList = new ArrayList<>();
            bufferedReader = new BufferedReader(new FileReader(file));
            String s = null;
            while ((s = bufferedReader.readLine()) != null) {
                String json = s.replace("\r", "").replace("\n", "");
                arrayList.add(json);
            }
            return arrayList;
        } catch (FileNotFoundException fnfe) {
            Log.e(fnfe.getMessage());
        } catch (IOException e) {
            Log.e(e.getMessage());
        }finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ioe) {
                Log.e(ioe.getMessage());
            }
        }
        return null;
    }
}
