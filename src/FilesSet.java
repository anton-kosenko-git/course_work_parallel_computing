import java.io.File;
import java.util.ArrayList;

public class FilesSet {

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    private String filepath = "data\\aclImdb\\";
    private int variant = 5;

    private static ArrayList<File> files = new ArrayList<>();
    private static ArrayList<File> folders = new ArrayList<>();

    private static void readFiles(File[] folders, int n, int variant) {
        for (int FileBeginWith = n / 50 * (variant - 1); FileBeginWith < n / 50 * variant; FileBeginWith++) {
            for (File file : folders) {
                if (file.getName().startsWith(String.valueOf(FileBeginWith))) {
                    files.add(file.getAbsoluteFile());
                }
            }
        }
    }

    //read files
    public ArrayList<File> getFiles() {

        //Folders with rating
        folders.add(new File(filepath + "train\\neg"));
        folders.add(new File(filepath + "test\\pos"));
        folders.add(new File(filepath + "train\\neg"));
        folders.add(new File(filepath + "train\\pos"));
        //Folder contains files w/o rating
        folders.add(new File(filepath + "train\\unsup"));

        //read files from all folders
        for (int processingFolder = 0; processingFolder < folders.size() - 1; processingFolder++) {
            readFiles(folders.get(processingFolder).listFiles(), 12500, variant);
        }
        readFiles(folders.get(4).listFiles(), 50000, variant);

        return files;
    }

}