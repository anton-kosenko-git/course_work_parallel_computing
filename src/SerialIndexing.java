import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SerialIndexing {
    public static void main(String[] args) {
        String findWord = "book";
        Date start, end;

        FilesSet filesSet = new FilesSet();
        filesSet.setFilepath("data\\aclImdb\\");

        ArrayList<File> files = filesSet.getFiles();
        Map<String, List<String>> invertedIndex = new HashMap<String, List<String>>();

        start = new Date();
        for (File file : files){
            DocumentParser parser = new DocumentParser();

            if(file.getName().endsWith(".txt")) {
                Map<String, Integer> voc = parser.parse(file.getAbsolutePath());
                updateInvertedIndex(voc, invertedIndex, file.getName());
            }
        }
        end = new Date();

        System.out.println("Execution Time: " + (end.getTime() - start.getTime()));
        System.out.println("invertedIndex: " + invertedIndex.size());
        System.out.println("Number of documents where the word was found: " + (invertedIndex.get(findWord)).size());
    }

    private static void updateInvertedIndex(Map<String, Integer> voc, Map<String, List<String>> invertedIndex, String fileName) {
        for (String word : voc.keySet()) {
            if(word.length() >= 3) {
                invertedIndex.computeIfAbsent(word, k -> new ArrayList<>()).add(fileName);
            }
        }
    }
}
