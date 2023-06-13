import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int[][] ans;
        int N = input.nextInt();
        ans = new int[N+1][N+1];
        for(int i=1; i<=N; i++) {
            for(int j=1; j<=N; j++) {
                ans[N-j+1][N-i+1] = input.nextInt();
            }
        }
        for(int i=1; i<=N; i++) {
            for(int j=1; j<=N; j++) {
                System.out.print(ans[i][j] + (j == N ? "\n" : " "));
            }
        }
    }
}
