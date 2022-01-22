import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DocumentParser {
    //we use Pattern to compile a regular expression, specified as a string into an instance of class
    private static final Pattern PATTERN = Pattern.compile("\\P{IsAlphabetic}+");

    public DocumentParser(){
    }

    public Map<String, Integer> parse(String route) {
        Map<String, Integer> ret = new HashMap();
        Path file = Paths.get(route);

        try {
            List<String> lines = Files.readAllLines(file);
            Iterator var6 = lines.iterator();

            while(var6.hasNext()) {
                String line = (String)var6.next();
                this.parseLine(line, ret);
            }
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        return ret;
    }

    private void parseLine(String line, Map<String, Integer> ret) {
        String[] var6;
        int var5 = (var6 = PATTERN.split(line)).length;

        for(int var4 = 0; var4 < var5; ++var4) {
            String word = var6[var4];
            if (!word.isEmpty()) {
                //Normalizer with NFKD (Normalization Form Compatibility Decomposition)
                // helps in normalization and decomposition of characters
                ret.merge(Normalizer.normalize(word, Form.NFKD).toLowerCase(), 1, (a, b) ->{
                    return a + b;
                });
            }
        }
    }
}
