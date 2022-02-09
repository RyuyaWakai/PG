import java.util.*;
import java.util.Arrays;

public class Geister {

    // 起動
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int x, y;
        int tmp = 1;

        make_ghost self = new make_ghost();
        make_enemy enemy = new make_enemy();
        //self.set_pos();
        enemy.set_enemy();
        make_mass(self,enemy);
        System.out.println("Which position do you want to set '1'? Enter the vertical and horizontal positions.");
        for (int i = 0; i < 4; i++) {
            y = sc.nextInt();
            x = sc.nextInt();
            if (x == 1 | x == 6 | y < 5 | y > 6) {
                System.out.println("This position is out of range.");
                i--;
                continue;
            } else if (self.ghost_pos[y][x] == "1") {//修正
                System.out.println("This position is already full.");
                i--;
                continue;
            }
            self.ghost_pos_d[0][i] = y;
            self.ghost_pos_d[1][i] = x;
            if (i == 3) {
                break;
            }
            make_mass(self,enemy);
        }
        for (int i = 5; i < 7; i++) {
            for (int j = 2; j < 6; j++) {
                for (int k = 0; k < 4; k++) {
                    if (self.ghost_pos_d[0][k] == i & self.ghost_pos_d[1][k] == j) {
                        break;
                    }
                    if (k == 3) {
                        self.ghost_pos_d[0][k + tmp] = i;
                        self.ghost_pos_d[1][k + tmp] = j;
                        tmp++;
                    }
                }
            }
        }
        game_start(self, enemy);
    }

    static void make_mass(make_ghost self,make_enemy enemy) {
        System.out.println("----------------------");
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == 0) {
                    if (j == 0) {
                        System.out.print("  " + (j + 1));
                    } else if (j < 6) {
                        System.out.print(" " + (j + 1));
                        if (j == 5) {
                            System.out.println();
                        }
                    }
                } else if (i != 0) {
                    if (j == 0) {
                        System.out.print(i);
                    } else if (j < 7) {
                        System.out.print("|");
                        for (int k = 0; k < 8; k++) {
                            if (self.ghost_pos_d[0][k] == i & self.ghost_pos_d[1][k] == j) {
                                if (k < 4) {
                                    System.out.print("1");
                                    break;
                                } else {
                                    System.out.print("2");
                                    break;
                                }
                            }else if(enemy.enemy_pos_d[0][k] == i & enemy.enemy_pos_d[1][k] == j){
                                System.out.print("0");
                                break;
                            }
                            if (k == 7) {
                                System.out.print(" ");
                            }
                        }
                    } else if (j == 7) {
                        if (i == 1 | i == 2) {
                            System.out.print("|");
                            System.out.println("      Number of '" + i + "' you got : " + self.get_ghost[i - 1]);
                        } else {
                            System.out.println("|");
                        }
                    }
                }
            }
        }
    }

    static void game_start(make_ghost self, make_enemy enemy) {//修正
        Scanner sc = new Scanner(System.in);
        boolean game_end = false;
        int x, y;
        int tmp_x = 0;
        int tmp_y = 0;
        String dir;
        while (game_end == false) {
            make_mass(self,enemy);
            System.out.println("Which numbers do you want to move? Please enter position.");
            while (true) {
                y = sc.nextInt();
                x = sc.nextInt();
                if (self.ghost_pos[y][x].equals(" ") | self.ghost_pos[y][x].equals("0")) {
                    System.out.println("This position has no numbers you can move.");
                    continue;
                } else {
                    break;
                }
            }
            System.out.println("Which direction do you want to move? w:up s:down d:right a:left");
            while (true) {
                dir = sc.next();
                switch (dir) {
                    case "w":
                        tmp_x = 0;
                        tmp_y = -1;
                        break;
                    case "s":
                        tmp_x = 0;
                        tmp_y = 1;
                        break;
                    case "d":
                        tmp_x = 1;
                        tmp_y = 0;
                        break;
                    case "a":
                        tmp_x = -1;
                        tmp_y = 0;
                        break;
                }
                if (self.ghost_pos[y + tmp_y][x + tmp_x].equals("1")
                        | self.ghost_pos[y + tmp_y][x + tmp_x].equals("2")) {
                    System.out.println("This number can't move this direction.");
                    continue;
                }
                break;
            }
            switch (dir) {
                case "w":
                    self.ghost_pos[y - 1][x] = self.ghost_pos[y][x];
                    self.ghost_pos[y][x] = " ";
                    break;
                case "s":
                    self.ghost_pos[y + 1][x] = self.ghost_pos[y][x];
                    self.ghost_pos[y][x] = " ";
                    break;
                case "d":
                    self.ghost_pos[y][x + 1] = self.ghost_pos[y][x];
                    self.ghost_pos[y][x] = " ";
                    break;
                case "a":
                    self.ghost_pos[y][x - 1] = self.ghost_pos[y][x];
                    self.ghost_pos[y][x] = " ";
                    break;
            }
        }
    }

    static void enemy_move() {

    }

    static class make_ghost {
        int blue_ghost_num = 4;
        int red_ghost_num = 4;
        String[][] ghost_pos = new String[8][8];
        int[][] ghost_pos_d = new int[2][8]; // 0～3が"1"、1～7が"2"

        int[] get_ghost = { 0, 0, 0 };

      
    }

    static class make_enemy {
        int blue_enemy_num = 4;
        int red_enemy_num = 4;

        String[][] enemy_info = new String[8][8];
        int[][] enemy_pos_d = new int[2][8]; // 取ったときに1か2か判定
        void set_enemy() {
            int k = 0;
            for (int n = 1; n < 3; n++) {
                for (int m = 2; m < 6; m++) {
                    enemy_pos_d[0][k] = n;
                    enemy_pos_d[1][k] = m;
                    k++;
                }
            }

        }
    }

}
