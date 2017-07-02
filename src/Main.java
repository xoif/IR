public class Main {

    public static void main(String[] args) {
        System.out.println("Test");
        HelloLucene hl = new HelloLucene("idx_hello"); try {
            hl.index(); hl.searchAndDisplay("information"); hl.searchAndDisplay("lecture"); hl.searchAndDisplay("example");
        } catch (Exception e) { e.printStackTrace();
        }
    }

}




