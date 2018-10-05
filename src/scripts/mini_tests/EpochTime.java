package scripts.mini_tests;

import java.time.LocalDate;

public class EpochTime {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(LocalDate.now().toEpochDay());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
