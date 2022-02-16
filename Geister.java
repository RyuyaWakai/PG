import java.util.*;
import java.util.Arrays;

public class Geister {

    public int win_method_flag = 0;

    // 起動
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int x, y;
        int tmp = 0;

        make_ghost self = new make_ghost();
        make_enemy enemy = new make_enemy();
        mass_data mass = new mass_data();
        // self.set_pos();
        mass.set_mass();
        enemy.set_enemy(mass);
        make_mass(self, mass);
        System.out.println("Which position do you want to set '1'? Enter the vertical and horizontal positions.");
        for (int i = 0; i < 4; i++) {
            y = sc.nextInt();
            x = sc.nextInt();
            if (x == 1 | x == 6 | y < 5 | y > 6) {
                System.out.println("This position is out of range.");
                i--;
                continue;
            } else if (mass.mass[y][x] == "1") {// 修正
                System.out.println("This position is already full.");
                i--;
                continue;
            }
            self.ghost_pos_d[0][i] = y;
            self.ghost_pos_d[1][i] = x;
            self.pre_ghost_pos_d[0][i] = y;
            self.pre_ghost_pos_d[1][i] = x;
            mass.mass[y][x] = "1";
            if (i == 3) {
                break;
            }
            make_mass(self, mass);
        }
        tmp = 4;
        for (int i = 5; i < 7; i++) {
            for (int j = 2; j < 6; j++) {
                if (mass.mass[i][j].equals("1") != true) {
                    mass.mass[i][j] = "2";
                    self.ghost_pos_d[0][tmp] = i;
                    self.ghost_pos_d[1][tmp] = j;
                    self.pre_ghost_pos_d[0][tmp] = i;
                    self.pre_ghost_pos_d[1][tmp] = j;
                    tmp++;
                }
            }
        }
        game_start(self, enemy, mass);
    }

    static void make_mass(make_ghost self, mass_data mass) {
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
                        System.out.print(mass.mass[i][j]);
                    } else if (j == 7) {
                        if (i == 1 | i == 2) {
                            System.out.print("|");
                            System.out.println("      Number of '" + i + "' you got : " + self.get_enemy[i - 1]);
                        } else {
                            System.out.println("|");
                        }
                    }
                }
            }
        }
    }

    static void game_start(make_ghost self, make_enemy enemy, mass_data mass) {// 修正
        Scanner sc = new Scanner(System.in);
        boolean game_end = false;
        int x, y, k;
        int tmp_x = 0;
        int tmp_y = 0;
        String dir;
        while (game_end == false) {
            make_mass(self, mass);

            for (int a = 0; a < 8; a++) {
                System.out.print(self.ghost_con_num[a] + " ");
            }
            System.out.println();

            for (k = 0; k < 8; k++) {
                self.ghost_pos_d[0][k] = self.ghost_pos_d[0][k];
                self.ghost_pos_d[1][k] = self.ghost_pos_d[1][k];
            }
            System.out.println("Which numbers do you want to move? Please enter position.");
            while (true) {
                y = sc.nextInt();
                x = sc.nextInt();
                if (mass.mass[y][x].equals(" ") | mass.mass[y][x].equals("0") | y < 1 | y > 6 | x < 1 | x > 6) {
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
                if (mass.mass[y + tmp_y][x + tmp_x].equals("1")
                        | mass.mass[y + tmp_y][x + tmp_x].equals("2")) {
                    System.out.println("This number can't move this direction.");
                    continue;
                }
                break;
            }

            // kの指定
            for (k = 0; k < 8; k++) {
                if (self.ghost_pos_d[0][k] == y & self.ghost_pos_d[1][k] == x) {
                    break;
                }
            }

            switch (dir) {
                case "w":
                    mass.mass[y - 1][x] = mass.mass[y][x];
                    mass.mass[y][x] = " ";
                    self.ghost_pos_d[0][k] = self.ghost_pos_d[0][k] - 1;
                    break;
                case "s":
                    mass.mass[y + 1][x] = mass.mass[y][x];
                    mass.mass[y][x] = " ";
                    self.ghost_pos_d[0][k] = self.ghost_pos_d[0][k] + 1;
                    break;
                case "d":
                    mass.mass[y][x + 1] = mass.mass[y][x];
                    mass.mass[y][x] = " ";
                    self.ghost_pos_d[1][k] = self.ghost_pos_d[1][k] + 1;
                    break;
                case "a":
                    mass.mass[y][x - 1] = mass.mass[y][x];
                    mass.mass[y][x] = " ";
                    self.ghost_pos_d[1][k] = self.ghost_pos_d[1][k] - 1;
                    break;
            }
            enemy_move(self, enemy, mass);
        }
    }

    static void enemy_move(make_ghost self, make_enemy enemy, mass_data mass) {
        // 青らしさの更新
        enemy.update_value(self);
        enemy.contact_enemy_update(mass, enemy);
        self.contact_ghost_update(mass, self);
        for (int i = 0; i < 8; i++) {
            if (self.pre_ghost_pos_d[0][i] - self.ghost_pos_d[0][i] == 1) {
                if (self.ghost_con_num[i] - self.pre_ghost_con_num[i] > 0) {
                    if (self.ghost_pos_d[0][i] < 3 & (self.ghost_pos_d[1][i] == 1 | self.ghost_pos_d[1][i] == 6)) {
                        enemy.value_blue[i] += 2.5;
                    } else {
                        enemy.value_blue[i] -= 1.5;
                    }
                } else if (self.ghost_con_num[i] - self.pre_ghost_con_num[i] == 0) {
                    enemy.value_blue[i] += 4.0;
                }
            } else if (self.pre_ghost_pos_d[1][i] - self.ghost_pos_d[1][i] == 1
                    | self.pre_ghost_pos_d[1][i] - self.ghost_pos_d[1][i] == -1) {
                if (self.ghost_con_num[i] - self.pre_ghost_con_num[i] > 0) {
                    enemy.value_blue[i] -= 1.0;
                } else {
                    enemy.value_blue[i] += 1.5;
                }
            } else if (self.pre_ghost_pos_d[0][i] - self.ghost_pos_d[0][i] == -1) {
                if (self.ghost_con_num[i] - self.pre_ghost_con_num[i] == 0) {
                    enemy.value_blue[i] += 1.5;
                }
            } else if (self.ghost_con_num[i] == 0 & self.pre_ghost_con_num[i] == 0) {
                if (self.ghost_pos_d[0][i] < 3 & (self.ghost_pos_d[1][i] == 1 | self.ghost_pos_d[1][i] == 6)) {
                    enemy.value_blue[i] += 10.0;
                }
            } else if (self.pre_ghost_con_num[i] >= 1) {
                if (self.pre_ghost_pos_d[0][i] - self.ghost_pos_d[0][i] == 0
                        & self.pre_ghost_pos_d[1][i] - self.ghost_pos_d[1][i] == 0) {
                    enemy.value_blue[i] -= 1.2;
                }
            }
        }
    }

    void protect_blue(mass_data mass, make_ghost self, make_enemy enemy) {
        int i;
        if (enemy.blue_enemy_num == 1) {
            for (i = 0; i < 4; i++) {
                if (enemy.enemy_pos_d[0][i] != (-1)) {
                    break;
                }
            }
            for (int j = 0; j < 4; j++) {
                if (j < 2) {
                    if (mass.mass[enemy.enemy_pos_d[0][i] + (int) Math.pow(-1, j)][enemy.enemy_pos_d[1][i]]
                            .equals(" ")) {
                        //青駒を動かす処理を書く
                    }
                } else {
                    if (mass.mass[enemy.enemy_pos_d[0][i]][enemy.enemy_pos_d[1][i] + (int) Math.pow(-1, j)]
                            .equals(" ")) {
                        //青駒を動かす処理を書く
                    }
                }
            }
        }
    }

    int contact_number() {
        return 0;
    }

    static class mass_data {
        String[][] mass = new String[8][8];

        void set_mass() {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (i == 0 | i == 7 | j == 0 | j == 7) {
                        mass[i][j] = "1";
                    } else {
                        mass[i][j] = " ";
                    }
                }
            }
        }
    }

    static class make_ghost {
        int blue_ghost_num = 4;
        int red_ghost_num = 4;
        String[][] ghost_pos = new String[8][8];
        int[][] ghost_pos_d = new int[2][8]; // 0～3が"1"、1～7が"2"
        int[][] pre_ghost_pos_d = new int[2][8]; // 移動処理の1つ前の座標

        int[] ghost_con_num = new int[8];
        int[] pre_ghost_con_num = new int[8];
        int[] get_enemy = { 0, 0, 0 };

        void contact_ghost_update(mass_data mass, make_ghost self) {
            for (int i = 0; i < 8; i++) {
                self.pre_ghost_con_num[i] = self.ghost_con_num[i];
                self.ghost_con_num[i] = 0;
                for (int j = 0; j < 4; j++) {
                    if (j < 2) {
                        if (mass.mass[self.ghost_pos_d[0][i] + (int) Math.pow(-1, j)][self.ghost_pos_d[1][i]]
                                .equals("0")) {
                            self.ghost_con_num[i]++;
                        }
                    } else {
                        if (mass.mass[self.ghost_pos_d[0][i]][self.ghost_pos_d[1][i] + (int) Math.pow(-1, j)]
                                .equals("0")) {
                            self.ghost_con_num[i]++;
                        }
                    }
                }
            }
        }

    }

    static class make_enemy {
        int blue_enemy_num = 4;
        int red_enemy_num = 4;

        String[][] enemy_info = new String[8][8];
        int[][] enemy_pos_d = new int[2][8];

        int[] get_ghost = { 0, 0, 0 };
        double[] value_blue = new double[8];

        int[] enemy_con_num = new int[8];
        int[] pre_enemy_con_num = new int[8];

        void set_enemy(mass_data mass) {
            int k = 0;
            int l = 4;
            Random random = new Random();
            int rand = random.nextInt(100);
            for (int n = 1; n < 3; n++) {
                for (int m = 2; m < 6; m++) {
                    mass.mass[n][m] = "0";
                    if (rand < 25) {
                        if ((n == 1) & (m != 5) | (m == 5) & (n == 2)) {
                            enemy_pos_d[0][l] = n;
                            enemy_pos_d[1][l] = m;
                            l++;
                        } else {
                            enemy_pos_d[0][k] = n;
                            enemy_pos_d[1][k] = m;
                            k++;
                        }
                    } else if (rand >= 25 & rand < 50) {
                        if ((n == 1) & (m != 5) | (m == 5) & (n == 2)) {
                            enemy_pos_d[0][k] = n;
                            enemy_pos_d[1][k] = m;
                            k++;
                        } else {
                            enemy_pos_d[0][l] = n;
                            enemy_pos_d[1][l] = m;
                            l++;
                        }
                    } else if (rand >= 51 & rand < 75) {
                        if ((n == 1 & m % 2 == 0) | (n == 2 & m % 2 == 1)) {
                            enemy_pos_d[0][k] = n;
                            enemy_pos_d[1][k] = m;
                            k++;
                        } else if ((n == 1 & m % 2 == 1) | (n == 2 & m % 2 == 0)) {
                            enemy_pos_d[0][l] = n;
                            enemy_pos_d[1][l] = m;
                            l++;
                        }
                    } else if (rand >= 76 & rand < 100) {
                        if (n == 1) {
                            if (m < 4) {
                                enemy_pos_d[0][k] = n;
                                enemy_pos_d[1][k] = m;
                                k++;
                            } else {
                                enemy_pos_d[0][l] = n;
                                enemy_pos_d[1][l] = m;
                                l++;
                            }
                        } else {
                            if (m < 4) {
                                enemy_pos_d[0][l] = n;
                                enemy_pos_d[1][l] = m;
                                l++;
                            } else {
                                enemy_pos_d[0][k] = n;
                                enemy_pos_d[1][k] = m;
                                k++;
                            }
                        }
                    }
                }
            }
        }

        void update_value(make_ghost self) {
            for (int i = 0; i < 8; i++) {
                if (self.ghost_pos_d[0][i] > 2 & (self.ghost_pos_d[1][i] == 3) | (self.ghost_pos_d[1][i] == 4)) {
                    value_blue[i] += 0.0 - value_blue[i];
                } else if (self.ghost_pos_d[0][i] > 2 & (self.ghost_pos_d[1][i] == 2) | (self.ghost_pos_d[1][i] == 5)) {
                    value_blue[i] += 0.1 - value_blue[i];
                } else if (self.ghost_pos_d[0][i] > 2 & (self.ghost_pos_d[1][i] == 1) | (self.ghost_pos_d[1][i] == 6)) {
                    value_blue[i] += 0.4 - value_blue[i];
                } else if (self.ghost_pos_d[0][i] == 2) {
                    value_blue[i] += 1.0 - value_blue[i];
                } else if (self.ghost_pos_d[0][i] == 1 & (self.ghost_pos_d[1][i] == 3)
                        | (self.ghost_pos_d[1][i] == 4)) {
                    value_blue[i] += 5.0 - value_blue[i];
                } else if (self.ghost_pos_d[0][i] == 1 & (self.ghost_pos_d[1][i] == 2)
                        | (self.ghost_pos_d[1][i] == 5)) {
                    value_blue[i] = 5.1 - value_blue[i];
                } else if (self.ghost_pos_d[0][i] == 1 & (self.ghost_pos_d[1][i] == 1)
                        | (self.ghost_pos_d[1][i] == 6)) {
                    value_blue[i] = 5.4 - value_blue[i];
                }
            }
        }

        void contact_enemy_update(mass_data mass, make_enemy enemy) {
            for (int i = 0; i < 8; i++) {
                enemy.pre_enemy_con_num[i] = enemy.enemy_con_num[i];
                enemy.enemy_con_num[i] = 0;
                for (int j = 0; j < 4; j++) {
                    if (j < 2) {
                        if (mass.mass[enemy.enemy_pos_d[0][i] + (int) Math.pow(-1, j)][enemy.enemy_pos_d[1][i]]
                                .equals("1")
                                | mass.mass[enemy.enemy_pos_d[0][i] + (int) Math.pow(-1, j)][enemy.enemy_pos_d[1][i]]
                                        .equals("2")) {
                            enemy.enemy_con_num[i]++;
                        }
                    } else {
                        if (mass.mass[enemy.enemy_pos_d[0][i]][enemy.enemy_pos_d[1][i] + (int) Math.pow(-1, j)]
                                .equals("1")
                                | mass.mass[enemy.enemy_pos_d[0][i]][enemy.enemy_pos_d[1][i] + (int) Math.pow(-1, j)]
                                        .equals("2")) {
                            enemy.enemy_con_num[i]++;
                        }
                    }
                }
            }
        }

    }
}
