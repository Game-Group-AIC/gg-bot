package aic.gas.sc.gg_bot.abstract_bot.utils;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;

/**
 * A simple class with generic serialize and deserialize method implementations
 *
 * @author pankaj
 */
public class SerializationUtil {

  /**
   * Get all files with extension in folder
   */
  public static Set<File> getAllFilesInFolder(String directoryName, String extension) {
    File directory = new File(directoryName);

    //get all the files from a directory
    File[] fList = directory.listFiles();
    if (fList == null) {
      return new HashSet<>();
    }
    return Arrays.stream(directory.listFiles()).filter(File::isFile)
        .filter(file -> Files.getFileExtension(file.getPath()).equals(extension))
        .collect(Collectors.toSet());
  }

  /**
   * Deserialize to <T> from given file
   */
  public static <T> T deserialize(String fileName) throws Exception {
    GZIPInputStream giz = new GZIPInputStream(new FileInputStream(fileName));
    byte[] bytes = IOUtils.toByteArray(giz);
    giz.close();
//        FileInputStream fis = new FileInputStream(fileName);
//        byte[] bytes = IOUtils.toByteArray(fis);
//        fis.close();
    return (T) SerializationUtils.deserialize(bytes);
  }

  /**
   * Deserialize to <T> from given file
   */
  public static <T> T deserialize(InputStream inputStream) throws Exception {
    GZIPInputStream giz = new GZIPInputStream(inputStream);
    byte[] bytes = IOUtils.toByteArray(giz);
    giz.close();
    return (T) SerializationUtils.deserialize(bytes);
  }

  /**
   * Serialize the given object and save it to file
   */
  public static <T extends Serializable> void serialize(T object, String fileName)
      throws Exception {
    GZIPOutputStream gz = new GZIPOutputStream(new FileOutputStream(fileName));
    ObjectOutputStream oos = new ObjectOutputStream(gz);
    oos.writeObject(object);
    oos.flush();
    oos.close();
  }

}
