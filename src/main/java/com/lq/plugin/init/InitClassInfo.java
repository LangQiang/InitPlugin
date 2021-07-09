package com.lq.plugin.init;

import java.util.HashSet;
import java.util.Set;

public class InitClassInfo {

    public String fullName;

    public String moduleName;

    public Set<String> dependSet = new HashSet<>();

    @Override
    public String toString() {
        return moduleName == null ? "" : moduleName;
    }
}
