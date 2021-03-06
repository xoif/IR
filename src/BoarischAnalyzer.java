import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.de.GermanLightStemFilter;
import org.apache.lucene.analysis.de.GermanNormalizationFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by Domi on 02.07.17.
 */
public class BoarischAnalyzer extends Analyzer {

    public final static String DEFAULT_STOPWORD_FILE = "./data/boarisch-stopwords.txt";
 
    /**
     * Contains words that should be indexed but not stemmed.
     */
    private CharArraySet stopSet = null;


    /**
     * Builds an analyzer with the given stop words
     */
    public BoarischAnalyzer() {
        try {
            stopSet = WordlistLoader.getSnowballWordSet(new BufferedReader(new FileReader(DEFAULT_STOPWORD_FILE)));
                    //WordlistLoader.getSnowballWordSet(IOUtils.getDecodingReader(SnowballFilter.class, DEFAULT_STOPWORD_FILE, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new StandardTokenizer();
        TokenStream result = new StandardFilter(source);
        result = new LowerCaseFilter(result);
        if (stopSet != null) {
            result = new StopFilter(result, stopSet);
        }
        result = new GermanNormalizationFilter(result);
        result = new GermanLightStemFilter(result);
        result = new DoubleVowelFilter(result);
        return new TokenStreamComponents(source, result);
    }
}
