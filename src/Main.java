import java.util.Scanner;

/**
 * Created by sunweipeng on 2017/7/23.
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] a = new int[n];
        for(int i=0;i<n;i++) {
            a[i] = scanner.nextInt();
        }
        int[] b = new int[n];
        b[0]=1;
        for(int i=1;i<n;i++) {
            b[i]=1;
            for(int j=i-1;j>=0;j--){
                if(a[i]>a[j]) {
                    b[i]=b[j]+1;
                    if(b[j]+1>b[i])
                        b[i]=b[j]+1;
                }
            }
        }
        int max = 0;
        for(int i=0;i<n;i++){
            if(b[i]>max) {
                max = b[i];
            }
        }
        System.out.println(max);
    }
}
