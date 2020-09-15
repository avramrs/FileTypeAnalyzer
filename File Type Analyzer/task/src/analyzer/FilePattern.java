package analyzer;

import java.util.Comparator;

public class FilePattern {
    public final static FilePattern UNKNOWN_TYPE = new FilePattern(-1, null, "Unknown file type");
    private final int priority;
    private final String pattern;
    private final String type;

    public FilePattern(int priority, String pattern, String type) {
        this.priority = priority;
        this.pattern = pattern;
        this.type = type;
    }

    public String getPattern() {
        return pattern;
    }

    public String getType() {
        return type;
    }

    public static class SortByPriority implements Comparator<FilePattern> {

        @Override
        public int compare(FilePattern filePattern, FilePattern t1) {
            return filePattern.priority - t1.priority;
        }
    }
}
