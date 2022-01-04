package com.lq.plugin.init.utils;

import com.google.gson.Gson;
import com.lq.plugin.init.DeepLinkClassInfo;
import com.lq.plugin.init.InitClassInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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

    public void addConfig() {
        addConfig("init.config");
        addConfig("deeplink.config");
    }

    private void addConfig(String fileName) {
        File cacheFile = new File("./.idea", fileName + ".cache");
        File file = new File("./.idea", fileName);
        if (!file.exists()) {
            return;
        }
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            bufferedWriter = new BufferedWriter(new FileWriter(cacheFile, true));
            String s = null;
            while ((s = bufferedReader.readLine()) != null) {
                bufferedWriter.write(s);
                bufferedWriter.newLine();
            }
        } catch (FileNotFoundException fnfe) {
            Log.e(fnfe.getMessage());
        } catch (IOException e) {
            Log.e(e.getMessage());
        }finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException ioe) {
                Log.e(ioe.getMessage());
            }
            file.delete();
        }
    }

    public void deleteConfig() {
        Log.e("delete config");
        deleteConfig("init.config");
        deleteConfig("deeplink.config");
    }

    private void deleteConfig(String fileName) {
        File cacheFile = new File("./.idea", fileName + ".cache");
        File file = new File("./.idea", fileName);
        cacheFile.delete();
        file.delete();
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
        List<String> list = new ArrayList<>();
        readFile(list, fileName + ".cache");
        readFile(list, fileName);
        return list;
    }

    private void readFile(List<String> list, String fileName) {
        File file = new File("./.idea", fileName);
        if (!file.exists()) {
            return;
        }

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String s = null;
            while ((s = bufferedReader.readLine()) != null) {
                String json = s.replace("\r", "").replace("\n", "");
                if (!list.contains(json)) {
                    list.add(json);
                }
            }
            return;
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
    }
}
