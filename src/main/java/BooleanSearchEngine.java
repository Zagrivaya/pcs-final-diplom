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
            for (int i = 0; i < doc.getNumberOfPages(); i++) {
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
                int count;
                for (var word : freqs.keySet()) {
                    String wordToLowerCase = word.toLowerCase();
                    if (freqs.get(wordToLowerCase) != null) {
                        count = freqs.get(wordToLowerCase);
                        dataSearch.computeIfAbsent(wordToLowerCase, k -> new ArrayList<>()).add(new PageEntry(pdf.getName(), i + 1, count));
                    }
                }
                freqs.clear();
            }
        }
    }

    @Override
    public List<PageEntry> search (String word){
        List<PageEntry> result = new ArrayList<>();
        String wordToLowerCase = word.toLowerCase();
        if (dataSearch.get(wordToLowerCase) != null) {
            for (PageEntry pageEntry : dataSearch.get(wordToLowerCase)) {
                result.add(pageEntry);
            }
        }
        Collections.sort(result);
        return result;
    }
}
