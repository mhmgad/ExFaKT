package output.writers;

import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileOutputWriter extends AbstractOutputChannel {



    private  BufferedWriter fileWriter;

    public FileOutputWriter(String outputFilePath, OutputFormat mode)  {
        try {
            this.fileWriter = FileUtils.getBufferedUTF8Writer(outputFilePath+'.'+mode.getFileExtension());
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        this.mode=mode;

    }


    @Override
    public void write(SerializableData record) {
        try {
            String toWrite=formatString(record);
            if(toWrite!=null && !toWrite.isEmpty()) {
                fileWriter.write(toWrite);
                fileWriter.newLine();
            }
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
