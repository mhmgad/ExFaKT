package extras.data.serialization;

import extras.data.serialization.SerializableData;

import java.util.Collection;

public abstract class AbstractSerializer {




    public void write(Collection<SerializableData> collection){
        collection.forEach(item->this.write(item));
    }



    abstract public void write(SerializableData record);

    abstract public boolean close();



}
