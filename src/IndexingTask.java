import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

public class IndexingTask implements Callable<Document> {
    private File file;

    public IndexingTask(File file) {
        this.file = file;
    }

    public Document call() throws Exception {
        DocumentParser parser = new DocumentParser();

        //!!!think about refactoring: implementation of directory traversal
        Map<String, Integer> voc = parser.parse(this.file.getAbsolutePath());
        Document document = new Document();
        document.setFileName(this.file.getName());
        document.setVoc(voc);

        return document;
    }
}
