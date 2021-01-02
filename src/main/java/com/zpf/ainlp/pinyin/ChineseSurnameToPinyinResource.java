package com.zpf.ainlp.pinyin;

import net.sourceforge.pinyin4j.multipinyin.Trie;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ChineseSurnameToPinyinResource {
    private Trie unicodeToSurnamePinyinTable;

    private void setUnicodeToSurnameHanyuPinyinTable(Trie unicodeToSurnamePinyinTable) {
        this.unicodeToSurnamePinyinTable = unicodeToSurnamePinyinTable;
    }

    Trie getUnicodeToSurnameHanyuPinyinTable() {
        return this.unicodeToSurnamePinyinTable;
    }

    private ChineseSurnameToPinyinResource() {
        this.unicodeToSurnamePinyinTable = null;
        this.initializeResource();
    }

    private void initializeResource() {
        try {
            String resourceName = "/pinyindb/unicode_to_surname.txt";
            String resourceMultiName = "/pinyindb/surname.txt";
            this.setUnicodeToSurnameHanyuPinyinTable(new Trie());
            this.getUnicodeToSurnameHanyuPinyinTable().load(ResourceHelper.getResourceInputStream(resourceName));
            this.getUnicodeToSurnameHanyuPinyinTable().loadMultiPinyin(ResourceHelper.getResourceInputStream(resourceMultiName));
            this.getUnicodeToSurnameHanyuPinyinTable().loadMultiPinyinExtend();
        } catch (FileNotFoundException var3) {
            var3.printStackTrace();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    Trie getHanyuPinyinTrie(char ch) {
        String codepointHexStr = Integer.toHexString(ch).toUpperCase();
        return this.getUnicodeToSurnameHanyuPinyinTable().get(codepointHexStr);
    }

    String[] getHanyuPinyinStringArray(char ch) {
        String pinyinRecord = this.getHanyuPinyinRecordFromChar(ch);
        return this.parsePinyinString(pinyinRecord);
    }

    String[] parsePinyinString(String pinyinRecord) {
        if (null != pinyinRecord) {
            int indexOfLeftBracket = pinyinRecord.indexOf("(");
            int indexOfRightBracket = pinyinRecord.lastIndexOf(")");
            String stripedString = pinyinRecord.substring(indexOfLeftBracket + "(".length(), indexOfRightBracket);
            return stripedString.split(",");
        } else {
            return null;
        }
    }

    private boolean isValidRecord(String record) {
        String noneStr = "(none0)";
        return null != record && !record.equals("(none0)") && record.startsWith("(") && record.endsWith(")");
    }

    private String getHanyuPinyinRecordFromChar(char ch) {
        String codepointHexStr = Integer.toHexString(ch).toUpperCase();
        Trie trie = this.getUnicodeToSurnameHanyuPinyinTable().get(codepointHexStr);
        String foundRecord = null;
        if (trie != null) {
            foundRecord = trie.getPinyin();
        }

        return this.isValidRecord(foundRecord) ? foundRecord : null;
    }

    static ChineseSurnameToPinyinResource getInstance() {
        return ChineseSurnameToPinyinResourceHolder.theInstance;
    }

    class Field {
        static final String LEFT_BRACKET = "(";
        static final String RIGHT_BRACKET = ")";
        static final String COMMA = ",";

        Field() {
        }
    }

    private static class ChineseSurnameToPinyinResourceHolder {
        static final ChineseSurnameToPinyinResource theInstance = new ChineseSurnameToPinyinResource();

        private ChineseSurnameToPinyinResourceHolder() {
        }
    }
}
