package fitnesse.revisioncontrol.svn.client;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class ConstantEnumUtil {
  public static <T> Set<T> getEnumsWhichConstantNameStartsWith(Class<T> enumClass, String prefix) {
    HashSet<T> types = new HashSet<T>();
    for (Field field : enumClass.getFields()) {
      if (Modifier.isStatic(field.getModifiers()) &&
          field.getType().equals(enumClass) &&
          field.getName().startsWith(prefix)) {
        try {
          types.add((T) field.get(null));
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    return types;
  }
}