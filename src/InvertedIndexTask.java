import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

public class InvertedIndexTask implements Runnable {
    private CompletionService<Document> completionService;
    private ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> invertedIndex;

    public InvertedIndexTask(CompletionService<Document> completionService, ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> invertedIndex) {
        this.completionService = completionService;
        this.invertedIndex = invertedIndex;
    }

    public void run() {
        try {
            while(!Thread.interrupted()) {
                try {
                    Document document = completionService.take().get();
                        updateInvertedIndex(document.getVoc(), invertedIndex, document.getFileName());
                } catch (InterruptedException e) {
                    break;
                }
            }

            while (true) {
                Future<Document> future = completionService.poll();
                if (future == null) {
                    break;
                    }

                    Document document = future.get();
                    updateInvertedIndex(document.getVoc(), invertedIndex, document.getFileName());
                }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateInvertedIndex(Map<String, Integer> voc, ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> invertedIndex, String fileName) {

        for (String word : voc.keySet()) {

            if(word.length() >= 3) {
                invertedIndex.computeIfAbsent(word, k -> new ConcurrentLinkedDeque<>()).add(fileName);
            }
        }
    }
}
