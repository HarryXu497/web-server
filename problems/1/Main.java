import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        
        long A = input.nextLong();
        long B = input.nextLong();
        
        System.out.println(A + B);
        
        input.close();
    }
}
