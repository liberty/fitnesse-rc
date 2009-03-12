package fitnesse.revisioncontrol.svn.client;

import java.io.File;

public class PathUtils {
  public static String rationalizePath(String filePath) {
    return rationalizePath(new File(filePath));
  }

  public static String rationalizePath(File file) {
    String filePath = file.getAbsolutePath();
    return filePath.replaceAll("\\\\", "/");
  }

  public static String buildFullPath(File file, String rootPath) {
    String path = rationalizePath(file);
    if (path.startsWith(rootPath)) {
      path = path.substring(rootPath.length() + 1);
    }

    path = path.replaceAll("\\\\", ".");
    path = path.replaceAll("/", ".");
    return path;
  }
}
