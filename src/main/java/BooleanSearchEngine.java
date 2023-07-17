import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private Map<String, List<PageEntry>> dataSearch = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        List<File> listOfPDFFiles = List.of(Objects.requireNonNull(pdfsDir.listFiles()));
        for (File pdf : listOfPDFFiles) {
            var doc = new PdfDocument(new PdfReader(pdf));
            for (int i = 1; i < doc.getNumberOfPages(); i++) {
                var text = PdfTextExtractor.getTextFromPage(doc.getPage(i + 1));
                var words = text.split("\\P{IsAlphabetic}+");

                Map<String, Integer> freqs = new HashMap<>(); // мапа, где ключом будет слово, а значением - частота
                for (var word : words) { // перебираем слова
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqs.put(word.toLowerCase(), freqs.getOrDefault(word, 0) + 1);
                }

                for (Map.Entry<String, Integer> freg : freqs.entrySet()) {
                    List<PageEntry> pageEntryList = new ArrayList<>();
                    if (dataSearch.containsKey(freg.getKey())) {
                        pageEntryList = dataSearch.get(freg.getKey());
                    }
                    PageEntry pageEntry = new PageEntry(pdf.getName(), i, freg.getValue());
                    pageEntryList.add(pageEntry);
                    Collections.sort(pageEntryList);
                    dataSearch.put(freg.getKey(), pageEntryList);
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        String wordToLowerCase = word.toLowerCase();
        if (dataSearch.containsKey(wordToLowerCase)) {
            return dataSearch.get(wordToLowerCase);
        }
        return Collections.emptyList();
    }
}