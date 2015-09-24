package com.huan.jerp.address;

import java.util.Comparator;

/**
 * Created by Administrator on 2015/8/17.
 */
public class PinyinComparator implements Comparator<com.huan.jerp.address.SortModel> {
    public int compare(com.huan.jerp.address.SortModel o1, com.huan.jerp.address.SortModel o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }
}
