package com.robertx22.library_of_exile.registry;

import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListStringData {

    private List<Part> list = new ArrayList<>();

    static int MAX = 10000;

    public List<String> getList() {
        return list.stream()
                .map(x -> x.getString())
                .collect(Collectors.toList());
    }

    public static class Part {

        Part(String string) {
            if (string.length() > MAX) {
                this.s = Splitter.fixedLength(MAX)
                        .splitToList(string);
            } else {
                this.s.add(string);
            }
        }

        public Part() {

        }

        public String getString() {
            String string = "";
            for (String x : s) {
                string += x;
            }
            return string;

        }

        public List<String> s = new ArrayList<>();
    }

    public ListStringData() {
    }

    public ListStringData(List<String> list) {
        this.list = list.stream()
                .map(x -> new Part(x))
                .collect(Collectors.toList());
    }
}
