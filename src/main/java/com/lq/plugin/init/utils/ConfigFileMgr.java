package com.lq.plugin.init.utils;

import com.google.gson.Gson;
import com.lq.plugin.init.InitClassInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
        File file = new File("init.config");
        if (file.exists()) {
            file.delete();
        }
    }

    public ArrayList<InitClassInfo> readConfig() {
        File file = new File("init.config");
        if (!file.exists()) {
            return null;
        }

        BufferedReader bufferedReader = null;
        try {
            ArrayList<InitClassInfo> arrayList = new ArrayList<>();
            bufferedReader = new BufferedReader(new FileReader(file));
            String s = null;
            while ((s = bufferedReader.readLine()) != null) {
                String json = s.replace("\r", "").replace("\n", "");
                arrayList.add(new Gson().fromJson(json, InitClassInfo.class));
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
