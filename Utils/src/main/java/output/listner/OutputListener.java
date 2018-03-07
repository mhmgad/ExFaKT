package output.listner;

import output.writers.AbstractOutputChannel;
import output.writers.SerializableData;

import java.util.*;

public class OutputListener {



    Map<String,AbstractOutputChannel> outputWriters;

    public OutputListener() {
        this.outputWriters = new HashMap<>();
    }

    public OutputListener(Collection<AbstractOutputChannel> outputWriters) {
        this();
        registerAll(outputWriters);
    }

    private void registerAll(Collection<AbstractOutputChannel> outputWriters) {
        outputWriters.forEach(wr-> registerWriter(wr));
    }

    public void registerWriter(AbstractOutputChannel writer){
        outputWriters.put(writer.getName(),writer);
    }

    public void registerWriter(String name,AbstractOutputChannel writer){
        outputWriters.put(name,writer);
    }

    public void out(SerializableData outputObject){
        outputWriters.values().forEach(wr-> wr.write(outputObject));
    }

    public void out(Collection<SerializableData> outputObjects){
        outputWriters.values().forEach(wr-> wr.write(outputObjects));
    }
}
