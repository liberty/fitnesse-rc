package fitnesse.revisioncontrol;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class FileUtils {
  public static final String CONTENT_FILE_NAME = "content.txt";
  private static final String PROPERTIES_FILE_NAME = "properties.xml";

  public static File[] getPathsFromRoot(File file, boolean recursive) {
    List<File> foundFiles = new ArrayList<File>();

    if (file.isDirectory()) {
      getPathsInDirectory(file, foundFiles, recursive);
    }
    else
      foundFiles.add(file);

    return foundFiles.toArray(new File[foundFiles.size()]);
  }

  private static void getPathsInDirectory(File root, List<File> paths, boolean recursive) {
    for (File file : root.listFiles()) {
      if (file.getName().equals(".svn")) {
        continue;
      }

      if (file.isDirectory() && recursive)
        getPathsInDirectory(file, paths, recursive);
      else if (isContentOrPropertiesFile(file))
        paths.add(file);
    }
  }

  private static boolean isContentOrPropertiesFile(File file) {
    return file.getName().endsWith(CONTENT_FILE_NAME) || file.getName().endsWith(PROPERTIES_FILE_NAME);
  }
}
