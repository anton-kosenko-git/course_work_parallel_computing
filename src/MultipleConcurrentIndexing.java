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
        int NUMBER_PER_TASK = 10;
        String findWord = "book";
        int numCores = Runtime.getRuntime().availableProcessors();
        //we define all the machines cores except one for work of the ThreadPoolExecutor
        ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(Math.max(numCores - 1, 1));
        ExecutorCompletionService<List<Document>> completionService = new ExecutorCompletionService(executor);
        ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> invertedIndex = new ConcurrentHashMap<String, ConcurrentLinkedDeque<String>>();

        Date start, end;

        FilesSet filesSet = new FilesSet();
        filesSet.setFilepath("data\\aclImdb\\");

        ArrayList<File> files = filesSet.getFiles();

        //InvertedIndexTask objects processed in two independent Threads by the last machine core
        MultipleInvertedIndexTask invertedIndexTask = new MultipleInvertedIndexTask(completionService, invertedIndex);
        Thread thread1 = new Thread(invertedIndexTask);
        thread1.start();
        MultipleInvertedIndexTask invertedIndexTask2 = new MultipleInvertedIndexTask(completionService, invertedIndex);
        Thread thread2 = new Thread(invertedIndexTask2);
        thread2.start();

        start = new Date();
        List<File> taskFiles = new ArrayList<>();
        for(File file : files) {
            //creation IndexingTask object for every file and
            // send it to the CompletionService using submit()
            taskFiles.add(file);
            if (taskFiles.size() > NUMBER_PER_TASK) {
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
        System.out.println("Number of documents per task: " + NUMBER_PER_TASK);
        System.out.println("Execution Time: " + (end.getTime() - start.getTime()));
        System.out.println("invertedIndex: " + invertedIndex.size());
        System.out.println("Number of documents where the word was found: " + ((ConcurrentLinkedDeque)invertedIndex.get(findWord)).size());
    }
}
