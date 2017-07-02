import edu.cmu.lemurproject.WarcHTMLResponseRecord;
import edu.cmu.lemurproject.WarcRecord;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import javax.security.auth.login.FailedLoginException;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;


@SuppressWarnings("ALL")
public class HelloLucene {
    Analyzer analyzer = new BoarischAnalyzer();
    String indexPath = "";
    String pathToWarc;
    String fieldName = "title";
    String fieldNameTrecID = "trecid";
    String fieldNameTargetURI = "target_uri";
    String fieldNameContent = "content";

    public HelloLucene(String indexPath, String pathToWarc) {
        this.indexPath = indexPath;
        this.pathToWarc = pathToWarc;
    }

    public void addDocs(String[] dataArr) throws Exception {
        IndexWriterConfig iwconf = new IndexWriterConfig(analyzer);
        Path path = FileSystems.getDefault().getPath(indexPath);
        Directory store = new SimpleFSDirectory(path);
        IndexWriter iw = new IndexWriter(store, iwconf);
        for (String data : dataArr) {
            Document doc = new Document();
            doc.add(new TextField(fieldName, data, Field.Store.YES));
            iw.addDocument(doc);
        }
        iw.close();
    }

    public void addDocsFromWarc() throws Exception {

        System.out.println("Indexing: " + pathToWarc);

        IndexWriterConfig iwconf = new IndexWriterConfig(analyzer);
        Path path = FileSystems.getDefault().getPath(indexPath);
        Directory store = new SimpleFSDirectory(path);
        IndexWriter iw = new IndexWriter(store, iwconf);

        FileInputStream fileInputStream = new FileInputStream(pathToWarc);
        DataInputStream inStream = new DataInputStream(fileInputStream);

        WarcRecord thisWarcRecord;
        while ((thisWarcRecord = WarcRecord.readNextWarcRecord(inStream)) != null) {

            if (thisWarcRecord.getHeaderRecordType().equals("response")) {

                WarcHTMLResponseRecord htmlRecord = new WarcHTMLResponseRecord(thisWarcRecord);

                String targetTrecID = htmlRecord.getTargetTrecID();
                String targetURI = htmlRecord.getTargetURI();
                String content = thisWarcRecord.getContentUTF8();

                Document doc = new Document();
                doc.add(new TextField(fieldNameTrecID, targetTrecID, Field.Store.YES));
                doc.add(new TextField(fieldNameTargetURI, targetURI, Field.Store.YES));
                doc.add(new TextField(fieldNameContent, content, Field.Store.YES));
                iw.addDocument(doc);
            }
        }

        inStream.close();
        iw.close();

        System.out.println("... done");
        System.out.println("");
    }

    public void addDocsFromWiki() throws IOException, ClassNotFoundException {
        Wiki wiki = null;
        File f = new File("wiki.dat");
        if (f.exists()) // we already have a copy on disk
        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
            wiki = (Wiki) in.readObject();
        } else {
            try {
                wiki = new Wiki("bar.wikipedia.org"); // create a new wiki connection to en.wikipedia.org
                wiki.setThrottle(5000); // set the edit throttle to 0.2 Hz
                wiki.login("IR-Bot2017", "ir"); // log in as user ExampleBot, with the specified password
            } catch (FailedLoginException ex) {
                ex.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (wiki != null) {
            String[] pageNames = wiki.getCategoryMembers("Sport", true);
            String[] pages = wiki.getPageText(pageNames);

            IndexWriterConfig iwconf = new IndexWriterConfig(analyzer);
            Path path = FileSystems.getDefault().getPath(indexPath);
            Directory store = new SimpleFSDirectory(path);
            IndexWriter iw = new IndexWriter(store, iwconf);

            if (pageNames.length == pages.length) {
                for (int i = 0; i < pages.length; i++)
                {
                    Document doc = new Document();
                    doc.add(new TextField(fieldNameTrecID, ""+i, Field.Store.YES));
                    doc.add(new TextField(fieldNameTargetURI, "https://bar.wikipedia.org/wiki/" + pageNames[i], Field.Store.YES));
                    doc.add(new TextField(fieldNameContent, pages[i], Field.Store.YES));
                    iw.addDocument(doc);
                }
            }

            iw.close();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
            out.writeObject(wiki); // if we want the session to persist
            out.close();
            wiki.logout();
        }

    }

    public void index() {
        try {
            addDocsFromWiki();
       //     addDocsFromWarc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchAndDisplay(String searchText) throws Exception {
        System.out.println("Query: " + searchText);
        //query
        QueryParser qParser = new QueryParser(fieldNameContent, analyzer);
        Query q = qParser.parse(searchText);
        //search
        Path path = FileSystems.getDefault().getPath(indexPath);
        DirectoryReader reader = DirectoryReader.open(new SimpleFSDirectory(path));
        IndexSearcher is = new IndexSearcher(reader);
        int topHits = 10;
        TopDocs hits = is.search(q, topHits);
        //display
        for (ScoreDoc hit : hits.scoreDocs) {
            Document doc = is.doc(hit.doc);
            System.out.println(doc.get(fieldNameTrecID) + " - " + doc.get(fieldNameTargetURI));
        }
        System.out.println("");
        //is.close();
    }

}