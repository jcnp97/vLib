package asia.virtualmc.vLib.storage.mysql.utilities;

public class MySQLUtils {

    /**
     * Returns a safe identifier with quotes, useful for MySQL table names.
     *
     * @param string    the string to clean and add quotations with.
     */
    public static String toSafeIdentifer(String string) {
        if (!string.matches("[A-Za-z0-9_]+"))
            throw new IllegalArgumentException("Invalid identifier: " + string);
        return "`" + string + "`";
    }
}