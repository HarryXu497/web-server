import java.util.*;
public class Main {
    static final int mod = 1000000007, MN = 1004;
    static long N, M, f = 1;
    static long[][] dp = new long[MN][MN];
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        N = sc.nextLong();
        M = sc.nextLong();
        if(M == 0 || N > M) { System.out.print(0); return; }
        for(int i=1; i<=N; i++) f = (f * i) % mod;
        dp[0][0] = 1; dp[1][1] = 1;
        for(int i=1; i<=M; i++)
            for(int j=1; j<=N; j++)
                dp[i][j] = (dp[i-1][j-1] + (dp[i-1][j] * j) % mod) % mod;
        System.out.print((dp[(int)M][(int)N] * f) % mod);
    }
}