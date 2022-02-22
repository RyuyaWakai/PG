import java.util.*;
import java.util.Arrays;

public class Geister {

    // ***数値と方向の対応付け(コンソール上から見て)***//

    // ***0 : 下***//
    // ***1 : 上***//
    // ***2 : 右***//
    // ***3 : 左***//

    static String[] piece = { "0", "1", "2" }; // 盤面上の駒。[0]がエネミーの駒、[1]がプレイヤーの青駒、[2]がプレイヤーの赤駒
    static int victory_or_defeat = 0; // 勝敗フラグ。 0は試合続行、1はプレイヤーの勝ち、-1はエネミーの勝ち
    static int flag = 0; // エネミーの行動の実行フラグ。0ならば駒を移動でき、移動後は次のターンまで1にしておく

    static int[][] Available_blue_table = { { 1, 1, 2, 3 }, // 残りのエネミーの駒数に対して何番目に青らしい駒まで取れるかを示す表 [青駒の数][赤駒の数]
            { 1, 2, 3, 3 },
            { 2, 3, 4, 5 },
            { 3, 4, 6, 7 } };

    // 起動
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int x, y;
        int tmp = 0;

        make_ghost self = new make_ghost();
        make_ghost enemy = new make_ghost();
        mass_data mass = new mass_data();

        mass.set_mass();
        enemy.set(mass);
        enemy.set_keeper();
        make_mass(self, mass, enemy);
        System.out.println(
                "Which position do you want to set '" + piece[1] + "'? Enter the vertical and horizontal positions.");
        for (int i = 0; i < 4; i++) {
            y = sc.nextInt();
            x = sc.nextInt();
            if (x == 1 | x == 6 | y < 5 | y > 6) {
                System.out.println("This position is out of range.");
                i--;
                continue;
            } else if (mass.mass[y][x].equals(piece[1])) {
                System.out.println("This position is already full.");
                i--;
                continue;
            }
            self.pos_d[0][i] = y;
            self.pos_d[1][i] = x;
            self.pre_pos_d[0][i] = y;
            self.pre_pos_d[1][i] = x;
            mass.mass[y][x] = piece[1];
            if (i == 3) {
                break;
            }
            make_mass(self, mass, enemy);

        }
        tmp = 4;
        for (int i = 5; i < 7; i++) {
            for (int j = 2; j < 6; j++) {
                if (mass.mass[i][j].equals(piece[1]) != true) {
                    mass.mass[i][j] = piece[2];
                    self.pos_d[0][tmp] = i;
                    self.pos_d[1][tmp] = j;
                    self.pre_pos_d[0][tmp] = i;
                    self.pre_pos_d[1][tmp] = j;
                    tmp++;
                }
            }
        }
        game_start(self, enemy, mass);

    }

    // 盤面描画
    static void make_mass(make_ghost self, mass_data mass, make_ghost enemy) {
        System.out.println("---------------------------------------");
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
                            System.out.println("      Number of '" + piece[i] + "' you got : " + self.get[i]);
                        } else if (i == 4 | i == 5) {
                            System.out.print("|");
                            System.out.println(
                                    "      Number of '" + piece[i - 3] + "' taken by enemy : " + enemy.get[i - 3]);
                        } else {
                            System.out.println("|");
                        }
                    }
                }
            }
        }
    }

    // ゲームのスタート、および対戦結果を行う関数
    static void game_start(make_ghost self, make_ghost enemy, mass_data mass) {
        Random rand = new Random();
        int turn = rand.nextInt(2);
        String[] First = { "You are first.", "Enemy is first." };

        System.out.println("---------------------------------------");
        System.out.println("            " + First[turn]);

        if (turn == 0) {
            while (victory_or_defeat == 0) {
                make_mass(self, mass, enemy);
                move_player(self, enemy, mass);
                if (check_exit(0, self) == 1) {
                    victory_or_defeat = 1;
                    break;
                }
                enemy_algorithm(self, enemy, mass);
                if (check_exit(1, enemy) == 1) {
                    victory_or_defeat = -1;
                    break;
                }
                check_number_pieces(self, enemy);
            }
        } else {
            while (victory_or_defeat == 0) {
                enemy_algorithm(self, enemy, mass);
                if (check_exit(1, enemy) == 1) {
                    victory_or_defeat = -1;
                    break;
                }
                make_mass(self, mass, enemy);
                move_player(self, enemy, mass);
                if (check_exit(0, self) == 1) {
                    victory_or_defeat = 1;
                    break;
                }
                check_number_pieces(self, enemy);
            }
        }

        if (victory_or_defeat == -1) {
            System.out.println("***************************************");
            System.out.println("              You Lose...");
            System.out.println("***************************************");
        } else {
            System.out.println("***************************************");
            System.out.println("              You Win!!!");
            System.out.println("***************************************");
        }

    }

    // プレイヤーを操作する関数
    static void move_player(make_ghost self, make_ghost enemy, mass_data mass) {
        Scanner sc = new Scanner(System.in);
        int x, y, k;
        int tmp_x = 0;
        int tmp_y = 0;
        String dir;

        System.out.println("Which pieces do you want to move? Please enter position.");
        while (true) {
            y = sc.nextInt();
            x = sc.nextInt();
            if (mass.mass[y][x].equals(" ") | mass.mass[y][x].equals(piece[0]) | y < 1 | y > 6 | x < 1 | x > 6
                    | min_contact_number(y, x, self, mass) == 4) {
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

            // kの指定
            for (k = 0; k < 8; k++) {
                if (self.pos_d[0][k] == y & self.pos_d[1][k] == x) {
                    break;
                }
            }

            // もしkが4未満で、なおかつ(y,x) = (1,1) or (1,6)のときは、「動けません」を出さない
            if ((y != 1 & x != 1) | (y != 1 & x != 6)) {
                if (k >= 4) {
                    if (mass.mass[y + tmp_y][x + tmp_x].equals(piece[1])
                            | mass.mass[y + tmp_y][x + tmp_x].equals(piece[2])) {
                        System.out.println("This number can't move this direction.");
                        continue;
                    }
                }
            }
            break;
        }

        // 移動した先に敵の駒があればその駒を取る。取った敵駒の座標を(-1,-1)にする。
        for (int i = 0; i < 8; i++) {
            if (enemy.pos_d[0][i] == y + tmp_y & enemy.pos_d[1][i] == x + tmp_x) {
                enemy.pos_d[0][i] = -1;
                enemy.pos_d[1][i] = -1;
                if (i < 4) {
                    enemy.blue_num--;
                    self.get[1]++;
                } else {
                    enemy.red_num--;
                    self.get[2]++;
                }
                break;
            }
        }

        // 自分の駒の移動
        mass.mass[y + tmp_y][x + tmp_x] = mass.mass[y][x];
        mass.mass[y][x] = " ";
        self.pos_d[0][k] = self.pos_d[0][k] + tmp_y;
        self.pos_d[1][k] = self.pos_d[1][k] + tmp_x;

    }

    // 残りの駒数をチェックする
    static void check_number_pieces(make_ghost self, make_ghost enemy) {
        if (self.blue_num == 0 | self.get[2] == 4 | enemy.get[1] == 4) {
            victory_or_defeat = -1;
        } else if (enemy.blue_num == 0 | self.get[1] == 4 | enemy.get[2] == 4) {
            victory_or_defeat = 1;
        }
    }

    // 出口から出たかどうかチェックする
    static int check_exit(int isEnemy, make_ghost tmp) {
        for (int i = 0; i < 4; i++) {
            if (tmp.pos_d[0][i] != -1 & tmp.pos_d[1][i] != -1) {
                if ((tmp.pos_d[0][i] == 7 * isEnemy & (tmp.pos_d[1][i] == 1 | tmp.pos_d[1][i] == 6))
                        | (tmp.pos_d[0][i] == (int) Math.pow(6, isEnemy)
                                & (tmp.pos_d[1][i] == 0 | tmp.pos_d[1][i] == 7))) {
                    return 1; // 出口から出た
                }
            }
        }
        return 0; // 出口から出ていない
    }

    // エネミーのアルゴリズム
    static void enemy_algorithm(make_ghost self, make_ghost enemy, mass_data mass) {
        self.contact_update(1, mass); // プレイヤーの接敵数を更新
        enemy.contact_update(0, mass); // 敵の接敵数を更新

        enemy.update_value_byPosition(); // 位置による青らしさの更新
        enemy.update_value_byMoving(self); // プレイヤーの移動による青らしさの更新
        enemy.update_value_Total(); // 総合的な青らしさの更新

        go_exit(mass, enemy); // 出口から出れるのであれば出る
        protect_blue(mass, self, enemy); // 残りの青駒が1個の時、青を守るように動く
        catch_blue(mass, self, enemy); // 周りに青駒と思われる駒があれば取る
        adjusting_keeper(mass, self, enemy); // キーパーが初期位置から動いていたら修正する
        head_exit(mass, self, enemy); // 青駒が6段目まで辿り着いていれば、その駒を動かす
        advance_warface(mass, self, enemy); // キーパー以外の駒で戦型の前進を行う
        flag = 0;
    }

    // (6,1)もしくは(6,6)に青駒があれば出口を出てゲーム終了する関数
    static void go_exit(mass_data mass, make_ghost enemy) {
        if (flag == 0) {
            for (int i = 0; i < 4; i++) {
                if (enemy.pos_d[0][i] != -1 & enemy.pos_d[1][i] != -1) {
                    if (enemy.pos_d[0][i] == 6 & (enemy.pos_d[1][i] == 1 | enemy.pos_d[1][i] == 6)) {
                        enemy.pos_d[0][i] += 1;
                        flag = 1;
                    }
                }
            }
        }
    }

    // 青駒を守らせるように動かす関数
    static void protect_blue(mass_data mass, make_ghost self, make_ghost enemy) {
        if (flag == 0) {
            if (enemy.blue_num == 1) {
                int i, j;
                for (i = 0; i < 4; i++) {
                    if (enemy.pos_d[0][i] != (-1)) {
                        break;
                    }
                }
                int min = 4;
                int dir = -1;
                for (j = 0; j < 4; j++) {
                    if (j < 2) {
                        if ((enemy.pos_d[0][i] != 6 | j != 0) & (enemy.pos_d[0][i] != 1 | j != 1)) {
                            if (mass.mass[enemy.pos_d[0][i] + (int) Math.pow(-1, j)][enemy.pos_d[1][i]]
                                    .equals(" ")) {
                                if (min > min_contact_number(enemy.pos_d[0][i] + (int) Math.pow(-1, j),
                                        enemy.pos_d[1][i], enemy, mass)) {
                                    min = min_contact_number(enemy.pos_d[0][i] + (int) Math.pow(-1, j),
                                            enemy.pos_d[1][i], enemy, mass);
                                    dir = j;
                                }
                            }
                        }
                    } else {
                        if ((enemy.pos_d[1][i] != 6 | j != 2) & (enemy.pos_d[1][i] != 1 | j != 3)) {
                            if (mass.mass[enemy.pos_d[0][i]][enemy.pos_d[1][i] + (int) Math.pow(-1, j)]
                                    .equals(" ")) {
                                if (min > min_contact_number(enemy.pos_d[0][i],
                                        enemy.pos_d[1][i] + (int) Math.pow(-1, j), enemy, mass)) {
                                    min = min_contact_number(enemy.pos_d[0][i],
                                            enemy.pos_d[1][i] + (int) Math.pow(-1, j), enemy, mass);
                                    dir = j;
                                }
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

    // 周りに青らしい駒があれば取る関数
    static void catch_blue(mass_data mass, make_ghost self, make_ghost enemy) {
        if (flag == 0) {
            int[] dir_max = new int[8];
            double[] blue_max = new double[8];
            double max = -100.0;
            int k = -1;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 4; j++) {
                    if (j < 2) {
                        if (max < return_value_blue(enemy.pos_d[0][i] + (int) Math.pow(-1, j),
                                enemy.pos_d[1][i], mass, self, enemy)) {
                            max = return_value_blue(enemy.pos_d[0][i] + (int) Math.pow(-1, j),
                                    enemy.pos_d[1][i], mass, self, enemy);
                            dir_max[i] = j;
                        }
                    } else {
                        if (max < return_value_blue(enemy.pos_d[0][i],
                                enemy.pos_d[1][i] + (int) Math.pow(-1, j), mass, self, enemy)) {
                            max = return_value_blue(enemy.pos_d[0][i],
                                    enemy.pos_d[1][i] + (int) Math.pow(-1, j), mass, self, enemy);
                            dir_max[i] = j;
                        }
                    }
                }
                blue_max[i] = max;
                if (i != 7) {
                    max = -100.0;
                }
            }
            int index = search_max(blue_max); // enemy[index]が青駒を取れるという意味
            double[] sort_value_blue = new double[8];
            for (int i = 0; i < 8; i++) {
                sort_value_blue[i] = enemy.value_blue[i];
            }
            Arrays.sort(sort_value_blue);
            if (enemy.blue_num >= 1 & enemy.red_num >= 1) {
                for (k = 8 - Available_blue_table[enemy.blue_num - 1][enemy.red_num - 1]; k < 8; k++) {
                    if (sort_value_blue[k] == blue_max[index]) {
                        break;
                    }
                }
            }
            if (k < 8 & k >= 0) {
                // enemy[index]をdir_max[index]の方向に動かす
                move_enemy(index, dir_max[index], self, enemy, mass);
                flag = 1;
            }
        }
    }

    // キーパーの位置を調整する関数
    static void adjusting_keeper(mass_data mass, make_ghost self, make_ghost enemy) {
        if (flag == 0 & enemy.blue_num + enemy.red_num > 3) {
            for (int i = 1; i < 3; i++) {
                if (enemy.pos_d[0][enemy.keeper_index[i - 1]] != -1 & enemy.pos_d[1][enemy.keeper_index[i - 1]] != -1) {
                    if (enemy.pos_d[0][enemy.keeper_index[i - 1]] > 1) {
                        // 後方に動かす(上)
                        move_enemy(enemy.keeper_index[i - 1], 1, self, enemy, mass);
                        flag = 1;
                    } else if (enemy.pos_d[1][enemy.keeper_index[i - 1]] < 3 * i - 1) {
                        // 右に動かす
                        move_enemy(enemy.keeper_index[i - 1], 2, self, enemy, mass);
                        flag = 1;
                    } else if (enemy.pos_d[1][enemy.keeper_index[i - 1]] > 3 * i - 1) {
                        // 左に動かす
                        move_enemy(enemy.keeper_index[i - 1], 3, self, enemy, mass);
                        flag = 1;
                    }
                }
            }
        }
    }

    // 6段目までたどり着いた駒が青駒ならば近い出口まで進む関数
    static void head_exit(mass_data mass, make_ghost self, make_ghost enemy) {
        if (flag == 0) {
            int i;
            for (i = 0; i < 4; i++) {
                if (enemy.pos_d[0][i] != -1 & enemy.pos_d[1][i] != -1) {
                    if (enemy.pos_d[0][i] == 6) {
                        if (enemy.pos_d[1][i] <= 3) {
                            move_enemy(i, 3, self, enemy, mass);
                            flag = 1;
                        } else {
                            move_enemy(i, 2, self, enemy, mass);
                            flag = 1;
                        }
                    }
                }
            }
        }
    }

    // 戦型の前進をする関数
    static void advance_warface(mass_data mass, make_ghost self, make_ghost enemy) {
        if (flag == 0) {
            int[] Available_index = new int[8];
            int tmp = 0;
            Random random = new Random();
            int horizontal_rand = random.nextInt(6);
            // 前方(下)が" "の駒を調べる
            for (int i = 0; i < 8; i++) {
                if (enemy.pos_d[0][i] != -1 & enemy.pos_d[1][i] != -1) {
                    if (mass.mass[enemy.pos_d[0][i] + 1][enemy.pos_d[1][i]].equals(" ")) {
                        if (enemy.blue_num + enemy.red_num <= 3) {
                            Available_index[tmp] = i; // それらを配列に記録してく
                            tmp++;
                        } else if (i != enemy.keeper_index[0] & i != enemy.keeper_index[1]) {
                            Available_index[tmp] = i; // それらを配列に記録してく
                            tmp++;
                        }
                    }
                }
            }
            int index;
            if (tmp == 1) {
                index = 0;
            } else {
                index = random.nextInt(tmp);
            }

            // 選んだ駒に対して、横にも移動できる場合は1/6の確率で横に移動する
            if (mass.mass[enemy.pos_d[0][Available_index[index]]][enemy.pos_d[1][Available_index[index]] - 1]
                    .equals(" ") & horizontal_rand == 0) {
                move_enemy(Available_index[index], 3, self, enemy, mass);
                flag = 1;
            } else if (mass.mass[enemy.pos_d[0][Available_index[index]]][enemy.pos_d[1][Available_index[index]]
                    + 1].equals(" ") & horizontal_rand == 0) {
                move_enemy(Available_index[index], 2, self, enemy, mass);
                flag = 1;
            } else { // 5/6の確率で前方に移動する
                move_enemy(Available_index[index], 0, self, enemy, mass);
                flag = 1;
            }
        }
    }

    // エネミーの駒を動かす関数
    static void move_enemy(int index, int dir, make_ghost self, make_ghost enemy, mass_data mass) {
        int dir_y = 0;
        int dir_x = 0;
        switch (dir) {
            case 0:
                dir_y = 1;
                dir_x = 0;
                break;
            case 1:
                dir_y = -1;
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
        if (enemy.pos_d[0][index] != -1 & enemy.pos_d[1][index] != -1) {
            for (int i = 0; i < 8; i++) {
                if (enemy.pos_d[0][index] + dir_y == self.pos_d[0][i] & enemy.pos_d[1][index]
                        + dir_x == self.pos_d[1][i]) {
                    self.pos_d[0][i] = -1;
                    self.pos_d[1][i] = -1;
                    enemy.value_blue[i] = -1000.0;
                }
            }
            if (mass.mass[enemy.pos_d[0][index] + dir_y][enemy.pos_d[1][index]
                    + dir_x].equals(piece[1])) {
                self.blue_num--;
                enemy.get[1]++;
            } else if (mass.mass[enemy.pos_d[0][index] + dir_y][enemy.pos_d[1][index]
                    + dir_x].equals(piece[2])) {
                self.red_num--;
                enemy.get[2]++;
            }
            mass.mass[enemy.pos_d[0][index] + dir_y][enemy.pos_d[1][index]
                    + dir_x] = mass.mass[enemy.pos_d[0][index]][enemy.pos_d[1][index]];
            mass.mass[enemy.pos_d[0][index]][enemy.pos_d[1][index]] = " ";
            enemy.pos_d[0][index] = enemy.pos_d[0][index] + dir_y;
            enemy.pos_d[1][index] = enemy.pos_d[1][index] + dir_x;
        }
    }

    // (y,x)の周りにプレイヤーが何体いるかを返す関数
    static int min_contact_number(int y, int x, make_ghost enemy, mass_data mass) {
        int count = 0;
        for (int i = 0; i < 4; i++) {
            if (i < 2) {
                if (mass.mass[y + (int) Math.pow(-1, i)][x]
                        .equals(piece[1])
                        | mass.mass[y + (int) Math.pow(-1, i)][x]
                                .equals(piece[2])) {
                    count++;
                }
            } else {
                if (mass.mass[y][x + (int) Math.pow(-1, i)]
                        .equals(piece[1])
                        | mass.mass[y][x + (int) Math.pow(-1, i)]
                                .equals(piece[2])) {
                    count++;
                }
            }
        }
        return count;
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

    // 指定した座標に１か２があれば、その駒の青らしさを返す関数
    static double return_value_blue(int y, int x, mass_data mass, make_ghost self, make_ghost enemy) {
        int i;
        if (y != -1 & x != -1) {
            if (mass.mass[y][x].equals(piece[1]) | mass.mass[y][x].equals(piece[2])) {
                for (i = 0; i < 8; i++) {
                    if (self.pos_d[0][i] == y & self.pos_d[1][i] == x) {
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

    // 盤面作成
    static class mass_data {
        String[][] mass = new String[8][8];

        void set_mass() {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (i == 0 | i == 7 | j == 0 | j == 7) {
                        mass[i][j] = piece[1];
                    } else {
                        mass[i][j] = " ";
                    }
                }
            }
        }
    }

    // プレイヤーおよびエネミーの情報作成
    static class make_ghost {
        int blue_num = 4; // 盤面にいる自分の青い幽霊(1のコマ)の数
        int red_num = 4; // 盤面にいる自分の赤い幽霊(2のコマ)の数
        int[][] pos_d = new int[2][8]; // 各駒の座標を記録する (0～3が"1"、1～7が"2")
        int[][] pre_pos_d = new int[2][8]; // 移動処理の1つ前の座標を記録する

        int[] con_num = new int[8]; // 各駒に隣接している敵駒の数
        int[] pre_con_num = new int[8]; // 移動処理の1つ前に隣接していた敵駒の数
        int[] get = { 0, 0, 0 }; // 獲得した敵駒の数を記録する ([0]は使用しない)

        double[] mass_value_blue = new double[8]; // エネミーのみ使用 各プレイヤー駒の位置による青らしさを記録する
        double[] move_value_blue = new double[8]; // エネミーのみ使用 各プレイヤー駒の移動による青らしさを記録する
        double[] value_blue = new double[8]; // エネミーのみ使用 各プレイヤー駒の青らしさ (位置+移動) を記録する
        int[] keeper_index = new int[2]; // エネミーのみ使用 キーパーのインデックスを記録 (1,2)が[0]で、(1,5)が[1]

        // 接敵数を更新する関数
        void contact_update(int isSlef, mass_data mass) {
            int tmp = 0;
            if (isSlef == 0) {
                tmp = 1;
            }
            for (int i = 0; i < 8; i++) {
                if (pos_d[0][i] != -1 & pos_d[1][i] != -1) {
                    pre_con_num[i] = con_num[i];
                    con_num[i] = 0;
                    for (int j = 0; j < 4; j++) {
                        if (j < 2) {
                            if (mass.mass[pos_d[0][i] + (int) Math.pow(-1, j)][pos_d[1][i]]
                                    .equals(piece[tmp])
                                    | mass.mass[pos_d[0][i] + (int) Math.pow(-1, j)][pos_d[1][i]]
                                            .equals(piece[tmp * 2])) {
                                con_num[i]++;
                            }
                        } else {
                            if (mass.mass[pos_d[0][i] + (int) Math.pow(-1, j)][pos_d[1][i]]
                                    .equals(piece[tmp])
                                    | mass.mass[pos_d[0][i] + (int) Math.pow(-1, j)][pos_d[1][i]]
                                            .equals(piece[tmp * 2])) {
                                con_num[i]++;
                            }
                        }
                    }
                }
            }
        }

        // エネミーのみ使用 エネミーの初期配置を行う関数
        void set(mass_data mass) {
            int k = 0;
            int l = 4;

            Random random = new Random();
            int rand = random.nextInt(100);
            for (int n = 1; n < 3; n++) {
                for (int m = 2; m < 6; m++) {
                    mass.mass[n][m] = piece[0];

                    if (rand < 25) {
                        if ((n == 1) & (m != 5) | (m == 5) & (n == 2)) {
                            pos_d[0][l] = n;
                            pos_d[1][l] = m;
                            l++;
                        } else {
                            pos_d[0][k] = n;
                            pos_d[1][k] = m;
                            k++;
                        }
                    } else if (rand >= 25 & rand < 50) {
                        if ((n == 1) & (m != 5) | (m == 5) & (n == 2)) {
                            pos_d[0][k] = n;
                            pos_d[1][k] = m;
                            k++;
                        } else {
                            pos_d[0][l] = n;
                            pos_d[1][l] = m;
                            l++;
                        }
                    } else if (rand >= 51 & rand < 75) {
                        if ((n == 1 & m % 2 == 0) | (n == 2 & m % 2 == 1)) {
                            pos_d[0][k] = n;
                            pos_d[1][k] = m;
                            k++;
                        } else if ((n == 1 & m % 2 == 1) | (n == 2 & m % 2 == 0)) {
                            pos_d[0][l] = n;
                            pos_d[1][l] = m;
                            l++;
                        }
                    } else if (rand >= 76 & rand < 100) {
                        if (n == 1) {
                            if (m < 4) {
                                pos_d[0][k] = n;
                                pos_d[1][k] = m;
                                k++;
                            } else {
                                pos_d[0][l] = n;
                                pos_d[1][l] = m;
                                l++;
                            }
                        } else {
                            if (m < 4) {
                                pos_d[0][l] = n;
                                pos_d[1][l] = m;
                                l++;
                            } else {
                                pos_d[0][k] = n;
                                pos_d[1][k] = m;
                                k++;
                            }
                        }
                    }
                }
            }
        }

        // エネミーのみ使用 キーパーのインデックスを求める関数
        void set_keeper() {
            for (int i = 0; i < 8; i++) {
                if (pos_d[0][i] == 1) {
                    if (pos_d[1][i] == 2) {
                        keeper_index[0] = i;
                    } else if (pos_d[1][i] == 5) {
                        keeper_index[1] = i;
                    }
                }
            }
        }

        // エネミーのみ使用 プレイヤーの位置による青らしさを更新する関数
        void update_value_byPosition() {
            for (int i = 0; i < 8; i++) {
                if (pos_d[0][i] > 2 & (pos_d[1][i] == 3) | (pos_d[1][i] == 4)) {
                    mass_value_blue[i] = 0.0;
                } else if (pos_d[0][i] > 2 & (pos_d[1][i] == 2) | (pos_d[1][i] == 5)) {
                    mass_value_blue[i] = 0.1;
                } else if (pos_d[0][i] > 2 & (pos_d[1][i] == 1) | (pos_d[1][i] == 6)) {
                    mass_value_blue[i] = 0.4;
                } else if (pos_d[0][i] == 2) {
                    mass_value_blue[i] = 1.0;
                } else if (pos_d[0][i] == 1 & (pos_d[1][i] == 3)
                        | (pos_d[1][i] == 4)) {
                    mass_value_blue[i] = 5.0;
                } else if (pos_d[0][i] == 1 & (pos_d[1][i] == 2)
                        | (pos_d[1][i] == 5)) {
                    mass_value_blue[i] = 5.1;
                } else if (pos_d[0][i] == 1 & (pos_d[1][i] == 1)
                        | (pos_d[1][i] == 6)) {
                    mass_value_blue[i] = 5.4;
                }
            }
        }

        // エネミーのみ使用 プレイヤーの移動による青らしさを更新する関数
        void update_value_byMoving(make_ghost self) {
            for (int i = 0; i < 8; i++) {
                if (self.pre_pos_d[0][i] - self.pos_d[0][i] == 1) {
                    if (self.con_num[i] - self.pre_con_num[i] > 0) {
                        if (self.pos_d[0][i] < 3 & (self.pos_d[1][i] == 1 | self.pos_d[1][i] == 6)) {
                            move_value_blue[i] += 2.5;
                        } else {
                            move_value_blue[i] -= 1.5;
                        }
                    } else if (self.con_num[i] - self.pre_con_num[i] == 0) {
                        move_value_blue[i] += 4.0;
                    }
                } else if (self.pre_pos_d[1][i] - self.pos_d[1][i] == 1
                        | self.pre_pos_d[1][i] - self.pos_d[1][i] == -1) {
                    if (self.con_num[i] - self.pre_con_num[i] > 0) {
                        move_value_blue[i] -= 1.0;
                    } else {
                        move_value_blue[i] += 1.5;
                    }
                } else if (self.pre_pos_d[0][i] - self.pos_d[0][i] == -1) {
                    if (self.con_num[i] - self.pre_con_num[i] == 0) {
                        move_value_blue[i] += 1.5;
                    }
                } else if (self.con_num[i] == 0 & self.pre_con_num[i] == 0) {
                    if (self.pos_d[0][i] < 3 & (self.pos_d[1][i] == 1 | self.pos_d[1][i] == 6)) {
                        move_value_blue[i] += 10.0;
                    }
                } else if (self.pre_con_num[i] >= 1) {
                    if (self.pre_pos_d[0][i] - self.pos_d[0][i] == 0
                            & self.pre_pos_d[1][i] - self.pos_d[1][i] == 0) {
                        move_value_blue[i] -= 1.2;
                    }
                }
            }
        }

        // エネミーのみ使用 総合的な青らしさを更新する関数
        void update_value_Total() {
            for (int i = 0; i < 8; i++) {
                value_blue[i] = mass_value_blue[i] + move_value_blue[i];
            }
        }
    }
}
