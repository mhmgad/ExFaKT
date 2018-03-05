package extras.data.serialization;

import mpi.tools.javatools.util.FileUtils;

import java.io.FileNotFoundException;

public class JSONSerializer extends AbstractSerializer{




    public JSONSerializer(String outputFilePath) throws FileNotFoundException {
        FileUtils.getBufferedUTF8Writer(outputFilePath);

    }


    @Override
    public void write(SerializableData record) {
            record.toJSON();
    }

    @Override
    public boolean close() {
        return false;
    }
}
