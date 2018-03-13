package com.obroom.filenamesort.test;

import com.obroom.filenamesort.util.FileNameComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 简单的测试代码
 * Created by ob on 2018/3/13.
 */
public class WindowsSortTest {
    public static void main(String[] args) throws Exception {
        List<String> filenameList = new ArrayList<>();
        filenameList.add("hello.txt");
        filenameList.add("world.txt");

        Collections.sort(filenameList, new Comparator<String>() {
            private final Comparator<String> NATURAL_SORT = new FileNameComparator();
            @Override
            public int compare(String o1, String o2) {
                return NATURAL_SORT.compare(o1, o2);
            }
        });
        for (String filename : filenameList) {
            System.out.println(filename);
        }
    }
}
