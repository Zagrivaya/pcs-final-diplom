import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    //???
    private Map<String, List<PageEntry>> dataSearch = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        // прочтите тут все pdf и сохраните нужные данные,
        // тк во время поиска сервер не должен уже читать файлы
        for (var pdf : pdfsDir.listFiles()) {
            var doc = new PdfDocument(new PdfReader(pdf));
            int pageCount = doc.getNumberOfPages();
            for (int i = 1; i <= pageCount; i++) {
                var page = doc.getPage(i);
                var text = PdfTextExtractor.getTextFromPage(page).toLowerCase();
                var words = text.split("\\P{IsAlphabetic}+");

                Map<String, Integer> freqs = new HashMap<>(); // мапа, где ключом будет слово, а значением - частота
                for (var word : words) { // перебираем слова
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                }

                for (String word : freqs.keySet()) {
                    PageEntry pageEntry = new PageEntry(pdf.getName(), i, freqs.get(word));
                    if (dataSearch.containsKey(word)) {
                        dataSearch.get(word).add(pageEntry);
                    } else {
                        dataSearch.put(word, new ArrayList<>());
                        dataSearch.get(word).add(pageEntry);
                    }
                }
            }
        }
    }

    @Override
    public List<PageEntry> search (String word){
                // тут реализуйте поиск по слову
        if (dataSearch.containsKey(word)) {
            return dataSearch.get(word);
        }
        return Collections.emptyList();
    }
}
