public class Main {

    final static String pathToWarc = "./ClueWeb09_English_Sample_File.warc";
    final static String indexRecord = "idx_hello";

    public static void main(String[] args) {
        System.out.println("Test");
        HelloLucene hl = new HelloLucene(indexRecord);
        try {
            hl.index();
            hl.searchAndDisplay("information");
            hl.searchAndDisplay("lecture");
            hl.searchAndDisplay("example");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}




