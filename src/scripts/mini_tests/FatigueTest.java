package scripts.mini_tests;

public class FatigueTest {

    public static void main(String[] args) {
        FatigueTest f = new FatigueTest();
        System.out.println(f.fatigueFunction(1800000));
        System.out.println(f.fatigueFunction(3600000));
        System.out.println(f.fatigueFunction(4800000));
    }

    public double fatigueFunction(long millis) {
        return 2-Math.exp(-millis/3600000D);
    }
}
