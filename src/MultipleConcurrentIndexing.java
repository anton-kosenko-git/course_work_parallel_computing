import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MultipleConcurrentIndexing {
    public MultipleConcurrentIndexing(){
    }

    public static void main(String[] args) {
        int V = 5;
        int numCores = Runtime.getRuntime().availableProcessors();
        //we define all the machines cores except one for work of the ThreadPoolExecutor
        ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(Math.max(numCores - 1, 1));
        ExecutorCompletionService<List<Document>> completionService = new ExecutorCompletionService(executor);
        ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> invertedIndex = new ConcurrentHashMap<String, ConcurrentLinkedDeque<String>>();
        boolean NUMBER_OF_DOCUMENTS = true;

        Date start, end;

        File source = new File("data");
        File[] files = source.listFiles();

        //InvertedIndexTask objects processed in two independent Threads by the last machine core
        MultipleInvertedIndexTask invertedIndexTask = new MultipleInvertedIndexTask(completionService, invertedIndex);
        Thread thread1 = new Thread(invertedIndexTask);
        thread1.start();
        MultipleInvertedIndexTask invertedIndexTask2 = new MultipleInvertedIndexTask(completionService, invertedIndex);
        Thread thread2 = new Thread(invertedIndexTask2);
        thread2.start();

        start = new Date();
        List<File> taskFiles = new ArrayList<>();
        File[] indexedFiles = files;
        int endIndex = files.length/50*V;

        //!!!think about refactoring: implementation of directory traversal
        // possible solution - Files.walkFileTree()
        for(int index = files.length/50*(V-1); index < endIndex; ++index) {
            File file = indexedFiles[index];
            //creation IndexingTask object for every file and
            // send it to the CompletionService using submit()
            taskFiles.add(file);
            if (taskFiles.size() == 50) {
                MultipleIndexingTask task = new MultipleIndexingTask(taskFiles);
                completionService.submit(task);
                taskFiles = new ArrayList<>();
            }

            if (executor.getQueue().size() > 10) {
                    do {
                        try {
                            TimeUnit.MILLISECONDS.sleep(50L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while (executor.getQueue().size() > 10);
                }
            }
            if (taskFiles.size() > 0) {
                MultipleIndexingTask task = new MultipleIndexingTask(taskFiles);
                completionService.submit(task);
            }

        executor.shutdown();
        try{
            executor.awaitTermination(1, TimeUnit.DAYS);
            thread1.interrupt();
            thread2.interrupt();
            thread1.join();
            thread2.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        end = new Date();
        System.out.println("Execution Time: " + (end.getTime() - start.getTime()));
        System.out.println("invertedIndex: " + invertedIndex.size());
        System.out.println(((ConcurrentLinkedDeque)invertedIndex.get("book")));
    }
}
