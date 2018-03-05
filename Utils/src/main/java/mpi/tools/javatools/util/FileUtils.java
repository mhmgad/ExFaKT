package mpi.tools.javatools.util;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/** 
This class is part of the Java Tools (see http://mpii.de/yago-naga/javatools).
It is licensed under the Creative Commons Attribution License 
(see http://creativecommons.org/licenses/by/3.0) by 
the YAGO-NAGA team (see http://mpii.de/yago-naga)

Some utility methods for arrays
*/
public class FileUtils {
  /**
   * Creates a BufferedReader for UTF-8-encoded files
   * 
   * @param file  File in UTF-8 encoding
   * @return      BufferedReader for file
   * @throws FileNotFoundException
   */
  public static BufferedReader getBufferedUTF8Reader(File file) throws FileNotFoundException {
    return new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
  }
  
  /**
   * Creates a BufferedReader for UTF-8-encoded files
   * 
   * @param fileName  Path to file in UTF-8 encoding
   * @return      BufferedReader for file
   * @throws FileNotFoundException
   */
  public static BufferedReader getBufferedUTF8Reader(String fileName) throws FileNotFoundException {
    return new BufferedReader(new InputStreamReader(new FileInputStream(fileName), Charset.forName("UTF-8")));
  }
  
  /**
   * Creates a BufferedReader the UTF-8-encoded InputStream
   * 
   * @param inputStream  InputStream in UTF-8 encoding
   * @return      BufferedReader for inputStream
   */
  public static BufferedReader getBufferedUTF8Reader(InputStream inputStream) {
    return new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
  }

  /**
   * Creates a BufferedWriter for UTF-8-encoded files
   * 
   * @param file  File in UTF-8 encoding
   * @return      BufferedWriter for file
   * @throws FileNotFoundException
   */
  public static BufferedWriter getBufferedUTF8Writer(File file) throws FileNotFoundException {
    return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")));
  }
  
  /**
   * Creates a BufferedWriter for UTF-8-encoded files
   * 
   * @param fileName  Path to file in UTF-8 encoding
   * @return      BufferedWriter for file
   * @throws FileNotFoundException
   */
  public static BufferedWriter getBufferedUTF8Writer(String fileName) throws FileNotFoundException {
    return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), Charset.forName("UTF-8")));
  }

  /**
   * Returns the content of the (UTF-8 encoded) file as string. Linebreaks
   * are encoded as unix newlines (\n).
   *
   * @param file  File to get String content from.
   * @return      String content of file.
   * @throws IOException
   */
  public static String getFileContent(File file) throws IOException {
    return getFileContent(file, "UTF-8");
  }

  /**
   * Returns the content of the file as string. Linebreaks
   * are encoded as unix newlines (\n).
   * 
   * @param file  File to get String content from.
   * @param encoding  Character encoding of the file.
   * @return      String content of file.
   * @throws IOException 
   */
  public static String getFileContent(File file, String encoding) throws IOException {
    BufferedReader reader = getBufferedUTF8Reader(file);
    return getFileContent(reader);
  }

  /**
   * Returns the content of the file as string. Linebreaks
   * are encoded as unix newlines (\n).
   * @param reader File to get String content from.
   * @return  String content of file.
   * @throws IOException
   */
  public static String getFileContent(BufferedReader reader) throws IOException {
    StringBuilder sb = new StringBuilder();
    for (String line = reader.readLine();
        line != null;
        line = reader.readLine()) {
      sb.append(line);
      sb.append('\n');
    }
    reader.close();
    return sb.toString();
  }

  /**
   * Writes the content of the string to the (UTF-8 encoded) file.
   * 
   * @param file  File to write String content to.
   * @return      Content of file.
   * @throws IOException 
   */
  public static void writeFileContent(File file, String content) throws IOException {
    BufferedWriter writer = getBufferedUTF8Writer(file);
    writer.write(content);
    writer.flush();
    writer.close();
  }

  public static <T> void writeTObjectIntMapToFile(File file, TObjectIntHashMap<T> map) throws IOException {
    BufferedWriter writer = getBufferedUTF8Writer(file);
    for (TObjectIntIterator<T> itr = map.iterator(); itr.hasNext(); ) {
      itr.advance();
      writer.write(itr.key() + "\t" + itr.value());
      writer.newLine();
    }
    writer.flush();
    writer.close();
  }
    

  /**
   * Collects all non-directory files in the given input directory 
   * (recursively).
   * 
   * @param directory Input directory.
   * @return          All non-directory files, recursively.
   */
  public static Collection<File> getAllFiles(File directory) {
    Collection<File> files = new LinkedList<File>();
    getAllFilesRecursively(directory, files);
    return files;
  }
  
  /**
   * Helper for getAllSubdirectories(directory).
   */
  private static void getAllFilesRecursively(
      File directory, Collection<File> files) {
    
    for (File file : directory.listFiles()) {
      if (file.isDirectory()) {
        getAllFilesRecursively(file, files);
      } else {
        files.add(file);
      }
    }
  }

  /**
   * Returns the content of the file as list. Linebreaks
   * are encoded as unix newlines (\n).
   *author: Gad
   * @param fileName  File to get String content from.

   * @return      String content of file.
   *
   * @throws IOException
   */
  public static List<String> getFileContentasList(String fileName) throws IOException {
    return  getFileContentasList(new File(fileName));
  }


  /**
   * Returns the content of the file as list. Linebreaks
   * are encoded as unix newlines (\n).
   *author: Gad
   * @param file  File to get String content from.

   * @return      String content of file.
   *
   * @throws IOException
   */
  public static List<String> getFileContentasList(File file) throws IOException {
    ArrayList<String> lines=new ArrayList<>();
    BufferedReader reader = getBufferedUTF8Reader(file);
    for (String line = reader.readLine();
         line != null;
         line = reader.readLine()){
      lines.add(line);
    }


    reader.close();
    return lines;
  }


  /**
   * Returns file path stored in the java jar
   *author: Gad


   * @return      File path.
   *
   * @throws IOException
   */
  public static String fileToPath(String filename) throws UnsupportedEncodingException {
    URL url = FileUtils.class.getResource(filename);
    return URLDecoder.decode(url.getPath(), "UTF-8");
  }
  
  public static void main(String[] args) throws IOException {

  }
}
