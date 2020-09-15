package analyzer;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Main {

    public static ArrayList<FilePattern> getPatternList(File patternDB) {
        ArrayList<FilePattern> patternList = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(patternDB.toPath())) {
            String line = reader.readLine();
            while (line != null) {
                patternList.add(lineToPattern(line));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return patternList;
    }

    public static FilePattern lineToPattern(String line) {
        String[] lineSplit = line.split(";");
        return new FilePattern(Integer.parseInt(lineSplit[0]), lineSplit[1].replace("\"", ""), lineSplit[2].replace("\"", ""));
    }

    //Demo
    public static void main(String[] args) {
        //file directory
        File root = new File(args[0]);
        //test database
        File patternDB = new File(args[1]);


        ArrayList<FilePattern> patternList;
        patternList = getPatternList(patternDB);
        assert (patternList != null);
        assert (!patternList.isEmpty());
        patternList.sort(new FilePattern.SortByPriority());

        File[] fileList = root.listFiles();
        assert (fileList != null);

        ExecutorService cachedPoolExec = Executors.newCachedThreadPool();
        CompletionService<Pair<File, FilePattern>> executor = new ExecutorCompletionService<>(cachedPoolExec);
        int fileCount = 0;
        //Set strategy here.
        SearchStrategy strategy = SearchStrategy.RabinKarp();
        for (File file : fileList) {
            if (file.isFile()) {
                TypeChecker analyzer = new TypeChecker(file, patternList, strategy);
                executor.submit(analyzer);
                fileCount++;
            }
        }
        cachedPoolExec.shutdown();

        try {
            while (fileCount != 0) {
                Pair<File, FilePattern> result = executor.take().get();
                System.out.printf("%s: %s%n", result.getKey().getName(), result.getValue().getType());
                fileCount--;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
