import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.next();
        int[] freq = new int[30];
        int ans = 0, tmp = 0;
        int l = 0;
        for(int r = 0; r < s.length(); r++) {
            freq[s.charAt(r)-'a']++;
            if(freq[s.charAt(r)-'a']==1) tmp++;
            while(freq[s.charAt(r)-'a'] > 1) {
                freq[s.charAt(l)-'a']--;
                if(freq[s.charAt(l)-'a']==0) tmp--;
                l++;
            }
            ans = Math.max(ans, tmp);
        }
        System.out.println(ans);
    }
}
