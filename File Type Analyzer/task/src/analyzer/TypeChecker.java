package analyzer;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class TypeChecker implements Callable<Pair<File, FilePattern>> {
    private final File file;
    private final List<FilePattern> patternList;
    private final SearchStrategy searchStrategy;

    public TypeChecker(File file, List<FilePattern> patternList, SearchStrategy strategy) {
        this.patternList = patternList;
        this.file = file;
        this.searchStrategy = strategy;
    }

    private String getText() {
        StringBuilder textBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(this.file))) {
            String line = reader.readLine();
            while (line != null) {
                textBuilder.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textBuilder.toString();
    }


    @Override
    public Pair<File, FilePattern> call() {
        // Could be improved.
        // Don't read all text.
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<Boolean>> results = new LinkedList<>();
        final String fileContent = getText();
        for (FilePattern pattern : patternList) {
            results.add(executor.submit(() -> searchStrategy.search(fileContent, pattern.getPattern())));
        }
        executor.shutdown();

        for (int index = results.size() - 1; index >= 0; index--) {
            try {
                Future<Boolean> future = results.get(index);
                boolean typeMatches = future.get();
                if (typeMatches) {
                    return new Pair<>(file, patternList.get(index));
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return new Pair<>(this.file, FilePattern.UNKNOWN_TYPE);
    }
}
