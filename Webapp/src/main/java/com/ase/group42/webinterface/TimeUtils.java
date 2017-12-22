package com.ase.group42.webinterface;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class TimeUtils {
    public static int getWeekId() {
        final long start = 1509922801000L;
        Date current = new Date();
        long weekId = (current.getTime() - start) / 604800000L;

        return (int) weekId;
    }

    public static boolean checkDate (Student s) throws IOException, ParseException {
        Date current = new Date();
        int weekId = getWeekId();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File gFile = new File(classLoader.getResource("groups.json").getFile());
        JSONArray groups = (JSONArray) new JSONParser().parse(new FileReader(gFile));
        long startStudent = Long.parseLong(((Map<String, String>)groups.get(s.group - 1)).get("start"));

        long startThisWeek = startStudent + 604800000L * weekId;
        return startThisWeek <= current.getTime() && (startThisWeek + 7200000) >= current.getTime();

    }
}
