public class Main {

    final static String pathToWarc = "data/ClueWeb09_English_Sample_File.warc";
    final static String indexRecord = "idx_hello";

    public static void main(String[] args) {
        HelloLucene hl = new HelloLucene(indexRecord, pathToWarc);
        try {
            hl.index();
            hl.searchAndDisplay("Geburtsdog");
            hl.searchAndDisplay("Audi");
            hl.searchAndDisplay("Fingerhakeln");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




