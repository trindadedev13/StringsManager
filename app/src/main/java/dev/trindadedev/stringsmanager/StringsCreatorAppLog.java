package dev.trindadedev.stringsmanager;

import java.util.ArrayList;
import java.util.List;

public class StringsCreatorAppLog{
    
    List<String> logs;
    
    public StringsCreatorAppLog() {
        logs = new ArrayList<>();
    }
    
    public String getLogs(){
        return logs.toString();
    }
    
    public void add(String logVal){
        logs.add(logVal);
    }
}
