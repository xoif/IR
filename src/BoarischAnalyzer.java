import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.de.GermanLightStemFilter;
import org.apache.lucene.analysis.de.GermanNormalizationFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * Created by Domi on 02.07.17.
 */
public class BoarischAnalyzer extends Analyzer {

    public final static String DEFAULT_STOPWORD_FILE = "./data/boarisch-stopwords.txt";

    public static final CharArraySet getDefaultStopSet(){
      return DefaultSetHolder.DEFAULT_SET;
    }
    private static class DefaultSetHolder {
    private static final CharArraySet DEFAULT_SET;
    static {
      try {
        DEFAULT_SET = WordlistLoader.getSnowballWordSet(IOUtils.getDecodingReader(SnowballFilter.class, 
            DEFAULT_STOPWORD_FILE, StandardCharsets.UTF_8));
      } catch (IOException ex) {
          throw new RuntimeException("Unable to load default stopword set");
        }
      }
    }
 
    /**
     * Contains words that should be indexed but not stemmed.
     */
    private final CharArraySet exclusionSet;

    public BoarischAnalyzer() {
      this(DefaultSetHolder.DEFAULT_SET);
    }
    
    /**
     * Builds an analyzer with the given stop words
     */
    public BoarischAnalyzer(CharArraySet stopwords, CharArraySet stemExclusionSet) {
      super(stopwords);
      exclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionSet));
    }

    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new StandardTokenizer();
        TokenStream result = new StandardFilter(source);
        result = new LowerCaseFilter(result);
        //result = new StopFilter(result, stopwords);
        result = new SetKeywordMarkerFilter(result, exclusionSet);
        result = new GermanNormalizationFilter(result);
        result = new GermanLightStemFilter(result);
        return new TokenStreamComponents(source, result);
    }
}
