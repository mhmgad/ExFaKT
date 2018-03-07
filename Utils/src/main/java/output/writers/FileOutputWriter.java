package output.writers;

import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileOutputWriter extends AbstractOutputChannel {



    private final BufferedWriter fileWriter;

    public FileOutputWriter(String outputFilePath, OutputFormat mode) throws FileNotFoundException {
        this.fileWriter = FileUtils.getBufferedUTF8Writer(outputFilePath);
        this.mode=mode;

    }


    @Override
    public void write(SerializableData record) {
        try {
            fileWriter.write(formatString(record));
            fileWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean close() {
        try {
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getName() {
        return "File-"+mode.toString();
    }
}
