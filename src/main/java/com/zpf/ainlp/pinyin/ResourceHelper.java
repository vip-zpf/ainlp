package com.zpf.ainlp.pinyin;

import java.io.BufferedInputStream;

public class ResourceHelper {
    ResourceHelper() {
    }

    static BufferedInputStream getResourceInputStream(String resourceName) {
        return new BufferedInputStream(ResourceHelper.class.getResourceAsStream(resourceName));
    }
}
