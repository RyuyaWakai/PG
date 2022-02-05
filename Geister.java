import java.util.*;
import java.util.Arrays;

public class Geister {

    // 起動
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int x, y;

        make_ghost self = new make_ghost();
        make_enemy enemy = new make_enemy();
        self.set_pos();
        enemy.set_enemy();
        make_mass(self.ghost_pos);
        for (int i = 0; i < 4; i++) {
            y = sc.nextInt();
            x = sc.nextInt();
            if(x == 0 | x == 5 | y<4){
                System.out.println("This position is out of range.");
                i--;
                continue;
            }else if(self.ghost_pos[y][x]=="1"){
                System.out.println("This position is already full.");
                i--;
                continue;
            }
            self.ghost_pos[y][x] = "1";
            make_mass(self.ghost_pos);
        }
        for(int i=4;i<6;i++){
            for(int j=1;j<5;j++){
                if(self.ghost_pos[i][j].equals(" ")){
                    self.ghost_pos[i][j] = "2";
                }
            }
        }
    }

    static void make_mass(String mass[][]) {
        System.out.println("----------------------");
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == 0) {
                    if (j == 0) {
                        System.out.print("  ");
                        System.out.print(j);
                    } else if (j < 6) {
                        System.out.print(" ");
                        System.out.print(j);
                        if (j == 5) {
                            System.out.println();
                        }
                    }
                } else if (i != 0) {
                    if (j == 0) {
                        System.out.print(i - 1);
                    } else if (j < 7) {
                        System.out.print("|");
                        System.out.print(mass[i - 1][j - 1]);
                    } else if (j == 7) {
                        System.out.println("|");
                    }
                }
            }
        }
    }

    static class make_ghost {
        int blue_ghost_num = 4;
        int red_ghost_num = 4;
        String[][] ghost_pos = new String[6][6];

        void set_pos() {
            for (int i = 0; i < 6; i++) {
                Arrays.fill(ghost_pos[i], " ");
                if(i==0 | i==1){
                    ghost_pos[i][1] = "0";
                    ghost_pos[i][2] = "0";
                    ghost_pos[i][3] = "0";
                    ghost_pos[i][4] = "0";
                }
            }
        }
    }

    static class make_enemy {
        int blue_enemy_num = 4;
        int red_enemy_num = 4;

        String[][] enemy_info = new String[6][6];

        void set_enemy() {
            int i = 4;
            int j = 4;
            for (int n = 0; n < 2; n++) {
                for (int m = 1; m < 5; m++) {
                    Random rand = new Random();
                    int r = rand.nextInt(2) + 1;
                    if((r==1 & i>0)  | j<=0){
                        enemy_info[n][m] = "1";
                        i--;
                    }else if((r==2 & j>0)| i<=0){
                        enemy_info[n][m] = "2";
                        j--;
                    }
                }
            }

        }
    }

    static class game_start{

    }
}
