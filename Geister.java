import java.util.*;
import java.util.Arrays;

public class Geister {

    // ***数値と方向の対応付け(コンソール上から見て)***//

    // ***0 : 上***//
    // ***1 : 下***//
    // ***2 : 右***//
    // ***3 : 左***//

    static int flag = 0;
    static int[][] Available_blue_table = { { 1, 1, 2, 3 },
            { 1, 2, 3, 3 },
            { 2, 3, 4, 5 },
            { 3, 4, 6, 7 } };
    static boolean game_end = false;

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
        enemy.set_keeper();
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

    // 盤面描画
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

    static void move_player(make_ghost self, make_enemy enemy, mass_data mass) {
        Scanner sc = new Scanner(System.in);
        int x, y, k;
        int tmp_x = 0;
        int tmp_y = 0;
        String dir;

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

        // 自分の駒の移動
        for (int i = 0; i < 8; i++) {
            if (enemy.enemy_pos_d[0][i] == y + tmp_y & enemy.enemy_pos_d[1][i] == x + tmp_x) {
                enemy.enemy_pos_d[0][i] = -1;
                enemy.enemy_pos_d[1][i] = -1;
                if (i < 4) {
                    enemy.blue_enemy_num--;
                } else {
                    enemy.red_enemy_num--;
                }
                break;
            }
        }
        mass.mass[y + tmp_y][x + tmp_x] = mass.mass[y][x];
        mass.mass[y][x] = " ";
        self.ghost_pos_d[0][k] = self.ghost_pos_d[0][k] + tmp_y;
        self.ghost_pos_d[1][k] = self.ghost_pos_d[1][k] + tmp_x;

    }

    static void game_start(make_ghost self, make_enemy enemy, mass_data mass) {// 修正
        while (game_end == false) {
            make_mass(self, mass);
            move_player(self, enemy, mass);
            enemy_algorithm(self, enemy, mass);
        }
    }

    // 終了処理
    static void game_end(make_ghost self, make_enemy enemy) {
    }

    // double型の配列を受け取って最大値のインデックスを返す関数
    static int search_max(double array[]) {
        double max = array[0];
        int index = 0;
        for (int i = 0; i < array.length; i++) {
            if (max < array[i]) {
                max = array[i];
                index = i;
            }
        }
        return index;
    }

    // 敵の駒を動かす関数
    static void move_enemy(int index, int dir, make_ghost self, make_enemy enemy, mass_data mass) {
        int dir_y = 0;
        int dir_x = 0;
        switch (dir) {
            case 0:
                dir_y = -1;
                dir_x = 0;
                break;
            case 1:
                dir_y = 1;
                dir_x = 0;
                break;
            case 2:
                dir_y = 0;
                dir_x = 1;
                break;
            case 3:
                dir_y = 0;
                dir_x = -1;
                break;
        }
        for (int i = 0; i < 8; i++) {
            if (enemy.enemy_pos_d[0][index] + dir_y == self.ghost_pos_d[0][i] & enemy.enemy_pos_d[1][index]
                    + dir_x == self.ghost_pos_d[1][i]) {
                self.ghost_pos_d[0][i] = -1;
                self.ghost_pos_d[1][i] = -1;
            }
        }
        if (mass.mass[enemy.enemy_pos_d[0][index] + dir_y][enemy.enemy_pos_d[1][index]
                + dir_x].equals("1")) {
            self.blue_ghost_num--;
        } else if (mass.mass[enemy.enemy_pos_d[0][index] + dir_y][enemy.enemy_pos_d[1][index]
                + dir_x].equals("2")) {
            self.red_ghost_num--;
        }
        mass.mass[enemy.enemy_pos_d[0][index] + dir_y][enemy.enemy_pos_d[1][index]
                + dir_x] = mass.mass[enemy.enemy_pos_d[0][index]][enemy.enemy_pos_d[1][index]];
        mass.mass[enemy.enemy_pos_d[0][index]][enemy.enemy_pos_d[1][index]] = " ";
        enemy.enemy_pos_d[0][index] = enemy.enemy_pos_d[0][index] + dir_y;
        enemy.enemy_pos_d[1][index] = enemy.enemy_pos_d[1][index] + dir_x;
    }

    // 敵のアルゴリズム
    static void enemy_algorithm(make_ghost self, make_enemy enemy, mass_data mass) {
        enemy.update_value(self); // 位置による青らしさの更新
        enemy.contact_enemy_update(mass, enemy); // 敵の接敵数を更新
        self.contact_ghost_update(mass, self); // プレイヤーの接敵数を更新
        update_value_byPlayer(self, enemy); // プレイヤーの移動による青らしさの更新
        protect_blue(mass, self, enemy);
        catch_blue(mass, self, enemy);
        adjusting_keeper(mass, self, enemy);
        advance_warface(mass, self, enemy);
        flag = 0;
    }

    // プレイヤーの移動による青らしさの更新
    static void update_value_byPlayer(make_ghost self, make_enemy enemy) {
        // プレイヤーの移動による青らしさの更新

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

    // 青駒を守らせるように動かす関数
    static void protect_blue(mass_data mass, make_ghost self, make_enemy enemy) {
        if (flag == 0) {
            if (enemy.blue_enemy_num == 1) {
                int i, j;
                for (i = 0; i < 4; i++) {
                    if (enemy.enemy_pos_d[0][i] != (-1)) {
                        break;
                    }
                }
                int min = 4;
                int dir = -1;
                for (j = 0; j < 4; j++) {
                    if (j < 2) {
                        if (mass.mass[enemy.enemy_pos_d[0][i] + (int) Math.pow(-1, j)][enemy.enemy_pos_d[1][i]]
                                .equals(" ")) {
                            if (min > min_contact_number(enemy.enemy_pos_d[0][i] + (int) Math.pow(-1, j),
                                    enemy.enemy_pos_d[1][i], enemy, mass)) {
                                min = min_contact_number(enemy.enemy_pos_d[0][i] + (int) Math.pow(-1, j),
                                        enemy.enemy_pos_d[1][i], enemy, mass);
                                dir = j;
                            }
                        }
                    } else {
                        if (mass.mass[enemy.enemy_pos_d[0][i]][enemy.enemy_pos_d[1][i] + (int) Math.pow(-1, j)]
                                .equals(" ")) {
                            if (min > min_contact_number(enemy.enemy_pos_d[0][i] + (int) Math.pow(-1, j),
                                    enemy.enemy_pos_d[1][i] + (int) Math.pow(-1, j), enemy, mass)) {
                                min = min_contact_number(enemy.enemy_pos_d[0][i] + (int) Math.pow(-1, j),
                                        enemy.enemy_pos_d[1][i] + (int) Math.pow(-1, j), enemy, mass);
                                dir = j;
                            }
                        }
                    }
                }
                if (min != 4 & dir != -1) {
                    move_enemy(i, dir, self, enemy, mass);
                    flag = 1;
                }
            }
        }
    }

    // (y,x)の周りに敵が何体いるかを返す関数
    static int min_contact_number(int y, int x, make_enemy enemy, mass_data mass) {
        int count = 0;
        for (int i = 0; i < 4; i++) {
            if (i < 2) {
                if (mass.mass[y + (int) Math.pow(-1, i)][x]
                        .equals("1")
                        | mass.mass[y + (int) Math.pow(-1, i)][x]
                                .equals("2")) {
                    count++;
                }
            } else {
                if (mass.mass[y][x + (int) Math.pow(-1, i)]
                        .equals("1")
                        | mass.mass[y][x + (int) Math.pow(-1, i)]
                                .equals("2")) {
                    count++;
                }
            }
        }
        return count;
    }

    // 周りに青らしい駒があれば取る関数
    static void catch_blue(mass_data mass, make_ghost self, make_enemy enemy) {
        if (flag == 0) {
            int[] dir_max = new int[8];
            double[] blue_max = new double[8];
            double max = -100.0;
            int k;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 4; j++) {
                    if (j < 2) {
                        if (max < return_value_blue(enemy.enemy_pos_d[0][i] + (int) Math.pow(-1, j),
                                enemy.enemy_pos_d[1][i], mass, self, enemy)) {
                            max = return_value_blue(enemy.enemy_pos_d[0][i] + (int) Math.pow(-1, j),
                                    enemy.enemy_pos_d[1][i], mass, self, enemy);
                            dir_max[i] = j;
                        }
                    } else {
                        if (max < return_value_blue(enemy.enemy_pos_d[0][i],
                                enemy.enemy_pos_d[1][i] + (int) Math.pow(-1, j), mass, self, enemy)) {
                            max = return_value_blue(enemy.enemy_pos_d[0][i],
                                    enemy.enemy_pos_d[1][i] + (int) Math.pow(-1, j), mass, self, enemy);
                            dir_max[i] = j;
                        }
                    }
                }
                blue_max[i] = max;
                if (i != 7) {
                    max = -100.0;
                }
            }
            int index = search_max(enemy.value_blue); // enemy[index]が青駒を取れるという意味
            double[] sort_value_blue = enemy.value_blue;
            Arrays.sort(sort_value_blue);
            for (k = Available_blue_table[enemy.blue_enemy_num - 1][enemy.red_enemy_num - 1]; k < 8; k++) {
                if (sort_value_blue[k] == max) {
                    break;
                }
            }
            if (k < 8) {
                // enemy[index]をdir_max[index]の方向に動かす
                move_enemy(index, dir_max[index], self, enemy, mass);
                flag = 1;
            }
        }
    }

    // 指定した座標に１か２があれば、その駒の青らしさを返す関数
    static double return_value_blue(int y, int x, mass_data mass, make_ghost self, make_enemy enemy) {
        int i;
        if (y != -1 & x != -1) {
            if (mass.mass[y][x].equals("1") | mass.mass[y][x].equals("2")) {
                for (i = 0; i < 8; i++) {
                    if (self.ghost_pos_d[0][i] == y & self.ghost_pos_d[1][i] == x) {
                        break;
                    }
                }
                if (i != 8) {
                    return enemy.value_blue[i];
                }
            }
        }
        return -100.0;
    }

    // キーパーの位置を調整する関数
    static void adjusting_keeper(mass_data mass, make_ghost self, make_enemy enemy) {
        if (flag == 0) {
            for (int i = 1; i < 3; i++) {
                if (enemy.enemy_pos_d[0][enemy.keeper_index[i - 1]] > 1) {
                    // 後方に動かす(上)
                    move_enemy(enemy.keeper_index[i - 1], 0, self, enemy, mass);
                    flag = 1;
                } else if (enemy.enemy_pos_d[1][enemy.keeper_index[i - 1]] < 3 * i - 1) {
                    // 右に動かす
                    move_enemy(enemy.keeper_index[i - 1], 2, self, enemy, mass);
                    flag = 1;
                } else if (enemy.enemy_pos_d[1][enemy.keeper_index[i - 1]] > 3 * i - 1) {
                    // 左に動かす
                    move_enemy(enemy.keeper_index[i - 1], 3, self, enemy, mass);
                    flag = 1;
                }
            }
        }
    }

    // 戦型の前進をする関数
    static void advance_warface(mass_data mass, make_ghost self, make_enemy enemy) {
        if (flag == 0) {
            int[] index = new int[8];
            int tmp = 0;
            Random random = new Random();
            int horizontal_rand = random.nextInt(6);
            // 前方(下)が" "の駒を調べる
            for (int i = 0; i < 8; i++) {
                if (enemy.enemy_pos_d[0][i] != -1 & enemy.enemy_pos_d[1][i] != -1) {
                    if (mass.mass[enemy.enemy_pos_d[0][i] + 1][enemy.enemy_pos_d[1][i]].equals(" ")) {
                        if (i != enemy.keeper_index[0] | i != enemy.keeper_index[1]) {
                            index[tmp] = i; // それらを配列に記録してく
                            tmp++;
                        }
                    }
                }
            }
            int index_rand;
            if (tmp == 1) {
                index_rand = 0;
            } else {
                index_rand = random.nextInt(tmp);
            }
            if (horizontal_rand == 0) { // 選んだ駒に対して、横にも移動できる場合は1/6の確率で横に移動する
                if (mass.mass[enemy.enemy_pos_d[0][index[index_rand]]][enemy.enemy_pos_d[1][index[index_rand]] - 1]
                        .equals(" ")) {
                    move_enemy(index[index_rand], 3, self, enemy, mass);
                    flag = 1;
                } else if (mass.mass[enemy.enemy_pos_d[0][index[index_rand]]][enemy.enemy_pos_d[1][index[index_rand]]
                        + 1].equals(" ")) {
                    move_enemy(index[index_rand], 2, self, enemy, mass);
                    flag = 1;
                }
            } else { // 5/6の確率で前方に移動する
                move_enemy(index[index_rand], 1, self, enemy, mass);
                flag = 1;
            }
        }
    }

    // 盤面作成
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

    // プレイヤー情報作成
    static class make_ghost {
        int blue_ghost_num = 4;
        int red_ghost_num = 4;
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

        int[][] enemy_pos_d = new int[2][8];
        int[] keeper_index = new int[2]; // (1,2)が0で、(1,5)が1

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

        void set_keeper() {
            for (int i = 0; i < 8; i++) {
                if (enemy_pos_d[0][i] == 1) {
                    if (enemy_pos_d[1][i] == 2) {
                        keeper_index[0] = i;
                    } else if (enemy_pos_d[1][i] == 5) {
                        keeper_index[1] = i;
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
                if (enemy.enemy_pos_d[0][i] != -1 & enemy.enemy_pos_d[1][i] != -1) {
                    enemy.pre_enemy_con_num[i] = enemy.enemy_con_num[i];
                    enemy.enemy_con_num[i] = 0;
                    for (int j = 0; j < 4; j++) {
                        if (j < 2) {
                            if (mass.mass[enemy.enemy_pos_d[0][i] + (int) Math.pow(-1, j)][enemy.enemy_pos_d[1][i]]
                                    .equals("1")
                                    | mass.mass[enemy.enemy_pos_d[0][i]
                                            + (int) Math.pow(-1, j)][enemy.enemy_pos_d[1][i]]
                                                    .equals("2")) {
                                enemy.enemy_con_num[i]++;
                            }
                        } else {
                            if (mass.mass[enemy.enemy_pos_d[0][i]][enemy.enemy_pos_d[1][i] + (int) Math.pow(-1, j)]
                                    .equals("1")
                                    | mass.mass[enemy.enemy_pos_d[0][i]][enemy.enemy_pos_d[1][i]
                                            + (int) Math.pow(-1, j)]
                                                    .equals("2")) {
                                enemy.enemy_con_num[i]++;
                            }
                        }
                    }
                }
            }
        }

    }
}
