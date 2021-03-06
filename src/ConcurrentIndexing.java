import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConcurrentIndexing {
    public ConcurrentIndexing(){
    }

    public static void main(String[] args) {
        String findWord = "book";
        int numCores = Runtime.getRuntime().availableProcessors();
        //we define all the machines cores except one for work of the ThreadPoolExecutor
        ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(Math.max(numCores - 1, 1));
        ExecutorCompletionService<Document> completionService = new ExecutorCompletionService(executor);
        ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> invertedIndex = new ConcurrentHashMap<String, ConcurrentLinkedDeque<String>>();

        Date start, end;

        FilesSet filesSet = new FilesSet();
        filesSet.setFilepath("data\\aclImdb\\");

        ArrayList<File> files = filesSet.getFiles();
        //InvertedIndexTask objects processed in two independent Threads by the last machine core
        InvertedIndexTask invertedIndexTask = new InvertedIndexTask(completionService, invertedIndex);
        Thread thread1 = new Thread(invertedIndexTask);
        thread1.start();
        InvertedIndexTask invertedIndexTask2 = new InvertedIndexTask(completionService, invertedIndex);
        Thread thread2 = new Thread(invertedIndexTask2);
        thread2.start();

        start = new Date();
        //creation IndexingTask object for every file and
        // send it to the CompletionService using submit()
        for(File file : files) {
            IndexingTask task = new IndexingTask(file);
            completionService.submit(task);
            if(executor.getQueue().size()>1000){
                do{
                    try{
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                } while(executor.getQueue().size()>1000);
            }
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
        System.out.println("Number of documents where the word was found: " + ((ConcurrentLinkedDeque)invertedIndex.get(findWord)).size());
    }
}
