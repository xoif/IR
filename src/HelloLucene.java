import edu.cmu.lemurproject.WarcHTMLResponseRecord;
import edu.cmu.lemurproject.WarcRecord;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.apache.lucene.search.*;
import org.apache.lucene.queryparser.classic.QueryParser;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.nio.file.*;



@SuppressWarnings("ALL")
public class HelloLucene {
    Analyzer analyzer = new StandardAnalyzer();
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

        // Lucene initialisieren
        IndexWriterConfig iwconf = new IndexWriterConfig(analyzer);
        Path path = FileSystems.getDefault().getPath(indexPath);
        Directory store = new SimpleFSDirectory(path);
        IndexWriter iw = new IndexWriter(store, iwconf);

        // Warc Datei laden
        FileInputStream fileInputStream = new FileInputStream(pathToWarc);
        DataInputStream inStream = new DataInputStream(fileInputStream);

        // Stream verarbeiten
        WarcRecord thisWarcRecord;
        while ((thisWarcRecord=WarcRecord.readNextWarcRecord(inStream))!=null) {
            // response record gefunden

            if (thisWarcRecord.getHeaderRecordType().equals("response")) {
                // WarcHTML record erzeugen
                WarcHTMLResponseRecord htmlRecord=new WarcHTMLResponseRecord(thisWarcRecord);

                // daten aus dem WarcRecord lesen
                String targetTrecID = htmlRecord.getTargetTrecID();
                String targetURI = htmlRecord.getTargetURI();
                String content = thisWarcRecord.getContentUTF8();

                // Dokument erzeugen
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


    public void index() {
        try {
            addDocsFromWarc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchAndDisplay(String searchText) throws Exception{
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
        for(ScoreDoc hit : hits.scoreDocs){
            Document doc = is.doc(hit.doc);
            System.out.println(doc.get(fieldNameTrecID) + " - " + doc.get(fieldNameTargetURI));
        }
        System.out.println("");
        //is.close();
    }

}