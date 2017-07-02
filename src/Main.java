public class Main {

    final static String pathToWarc = "data/ClueWeb09_English_Sample_File.warc";
    final static String indexRecord = "idx_hello";

    public static void main(String[] args) {
        System.out.println("Test");
        HelloLucene hl = new HelloLucene(indexRecord, pathToWarc);
        try {
            hl.index();
            hl.searchAndDisplay("Choose from a variety of website designs to find the perfect");
            hl.searchAndDisplay("Jandus");
            hl.searchAndDisplay("Javascript");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}




