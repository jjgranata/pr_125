
import java.util.Scanner;

public class boardAI {
    private boardCreate b;
    private Scanner s;
    private int nextLocate = -1;
    private int depth = 9;

    public boardAI(boardCreate b) {
        this.b = b;
        s = new Scanner(System.in);
    }

    //Allow enemy to move
    public void enemyMove() {
        System.out.println("Your move (1-7): ");
        int move = s.nextInt();
        while (move < 1 || move > 7 || !b.legMov(move - 1)) {
            System.out.println("Invalid move.\n\nYour move (1-7): ");
            move = s.nextInt();
        }

        //Assume 2 is the opponent
        b.Moving(move - 1, (byte) 2);
    }

    public int getenemyMove() {
        nextLocate = -1;
        min_maxAlg(0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return nextLocate;
    }

    int calcUtility(int enemyScore, int moveValues) {
        int totalScore = 4 - moveValues;
        if (enemyScore == 0) return 0;
        else if (enemyScore == 1) return 1 * totalScore;
        else if (enemyScore == 2) return 10 * totalScore;
        else if (enemyScore == 3) return 100 * totalScore;
        else return 1000;
    }

    public int min_maxAlg(int nodeDepth, int totTurns, int alph, int bet) {

        if (bet <= alph) {
            if (totTurns == 1) return Integer.MAX_VALUE;
            else return Integer.MIN_VALUE;
        }
        int finalResult = results(b);

        if (finalResult == 1) return Integer.MAX_VALUE / 2;
        else if (finalResult == 2) return Integer.MIN_VALUE / 2;
        else if (finalResult == 0) return 0;

        if (nodeDepth == this.depth) return boardEval(b);

        int maxScore = Integer.MIN_VALUE, minScore = Integer.MAX_VALUE;

        for (int j = 0; j <= 6; ++j) {

            int currentScore = 0;

            if (!b.legMov(j)) continue;

            if (totTurns == 1) {
                b.Moving(j, 1);
                currentScore = min_maxAlg(nodeDepth + 1, 2, alph, bet);

                if (nodeDepth == 0) {
                    System.out.println("Score in area " + j + " = " + currentScore);
                    if (currentScore > maxScore) nextLocate = j;
                    if (currentScore == Integer.MAX_VALUE / 2) {
                        b.retractMov(j);
                        break;
                    }
                }

                maxScore = Math.max(currentScore, maxScore);

                alph = Math.max(currentScore, alph);
            } else if (totTurns == 2) {
                b.Moving(j, 2);
                currentScore = min_maxAlg(nodeDepth + 1, 1, alph, bet);
                minScore = Math.min(currentScore, minScore);

                bet = Math.min(currentScore, bet);
            }
            b.retractMov(j);
            if (currentScore == Integer.MAX_VALUE || currentScore == Integer.MIN_VALUE) break;
        }
        return totTurns == 1 ? maxScore : minScore;
    }

    public void Menu() {
        //int humanMove = -1;
        Scanner scan = new Scanner(System.in);
        System.out.println("Should the player take the first turn? (yes/no) ");
        String answer = scan.next().trim();

        if (answer.equalsIgnoreCase("yes")) enemyMove();
        b.printBoard();
        b.Moving(3, 1);
        b.printBoard();
        while (true) {
            enemyMove();
            b.printBoard();

            int gameResult = results(b);
            if (gameResult == 1) {
                System.out.println("You lose");
                break;
            } else if (gameResult == 2) {
                System.out.println("You Win");
                break;
            } else if (gameResult == 0) {
                System.out.println("Draw Game");
                break;
            }

            b.Moving(getenemyMove(), 1);
            b.printBoard();
            gameResult = results(b);
            if (gameResult == 1) {
                System.out.println("AI won");
                break;
            } else if (gameResult == 2) {
                System.out.println("Player Won");
                break;
            } else if (gameResult == 0) {
                System.out.println("Draw Game");
                break;
            }
        }

    }

    //Evaluate b favorableness for AI
    public int boardEval(boardCreate b) {

        int enemyScore = 1;
        int finalScore = 0;
        int blankArea = 0;
        int kap = 0, totalMoves = 0;
        for (int i = 5; i >= 0; --i) {
            for (int j = 0; j <= 6; ++j) {

                if (b.b[i][j] == 0 || b.b[i][j] == 2) continue;

                if (j <= 3) {
                    for (kap = 1; kap < 4; ++kap) {
                        if (b.b[i][j + kap] == 1) enemyScore++;
                        else if (b.b[i][j + kap] == 2) {
                            enemyScore = 0;
                            blankArea = 0;
                            break;
                        } else blankArea++;
                    }

                    totalMoves = 0;
                    if (blankArea > 0)
                        for (int c = 1; c < 4; ++c) {
                            int column = j + c;
                            for (int m = i; m <= 5; m++) {
                                if (b.b[m][column] == 0) totalMoves++;
                                else break;
                            }
                        }

                    if (totalMoves != 0) finalScore += calcUtility(enemyScore, totalMoves);
                    enemyScore = 1;
                    blankArea = 0;
                }

                if (i >= 3) {
                    for (kap = 1; kap < 4; ++kap) {
                        if (b.b[i - kap][j] == 1) enemyScore++;
                        else if (b.b[i - kap][j] == 2) {
                            enemyScore = 0;
                            break;
                        }
                    }
                    totalMoves = 0;

                    if (enemyScore > 0) {
                        int column = j;
                        for (int m = i - kap + 1; m <= i - 1; m++) {
                            if (b.b[m][column] == 0) totalMoves++;
                            else break;
                        }
                    }
                    if (totalMoves != 0) finalScore += calcUtility(enemyScore, totalMoves);
                    enemyScore = 1;
                    blankArea = 0;
                }

                if (j >= 3) {
                    for (kap = 1; kap < 4; ++kap) {
                        if (b.b[i][j - kap] == 1) enemyScore++;
                        else if (b.b[i][j - kap] == 2) {
                            enemyScore = 0;
                            blankArea = 0;
                            break;
                        } else blankArea++;
                    }
                    totalMoves = 0;
                    if (blankArea > 0)
                        for (int c = 1; c < 4; ++c) {
                            int column = j - c;
                            for (int m = i; m <= 5; m++) {
                                if (b.b[m][column] == 0) totalMoves++;
                                else break;
                            }
                        }

                    if (totalMoves != 0) finalScore += calcUtility(enemyScore, totalMoves);
                    enemyScore = 1;
                    blankArea = 0;
                }

                if (j <= 3 && i >= 3) {
                    for (kap = 1; kap < 4; ++kap) {
                        if (b.b[i - kap][j + kap] == 1) enemyScore++;
                        else if (b.b[i - kap][j + kap] == 2) {
                            enemyScore = 0;
                            blankArea = 0;
                            break;
                        } else blankArea++;
                    }
                    totalMoves = 0;
                    if (blankArea > 0) {
                        for (int c = 1; c < 4; ++c) {
                            int column = j + c, row = i - c;
                            for (int m = row; m <= 5; ++m) {
                                if (b.b[m][column] == 0) totalMoves++;
                                else if (b.b[m][column] == 1) ;
                                else break;
                            }
                        }
                        if (totalMoves != 0) finalScore += calcUtility(enemyScore, totalMoves);
                        enemyScore = 1;
                        blankArea = 0;
                    }
                }

                if (i >= 3 && j >= 3) {
                    for (kap = 1; kap < 4; ++kap) {
                        if (b.b[i - kap][j - kap] == 1) enemyScore++;
                        else if (b.b[i - kap][j - kap] == 2) {
                            enemyScore = 0;
                            blankArea = 0;
                            break;
                        } else blankArea++;
                    }
                    totalMoves = 0;
                    if (blankArea > 0) {
                        for (int c = 1; c < 4; ++c) {
                            int column = j - c, row = i - c;
                            for (int m = row; m <= 5; ++m) {
                                if (b.b[m][column] == 0) totalMoves++;
                                else if (b.b[m][column] == 1) ;
                                else break;
                            }
                        }
                        if (totalMoves != 0) finalScore += calcUtility(enemyScore, totalMoves);
                        enemyScore = 1;
                        blankArea = 0;
                    }
                }
            }
        }
        return finalScore;
    }

    //checks for conflicts with player move
    public int results(boardCreate b) {
        int enemyScore = 0, playerScore = 0;
        for (int row = 5; row >= 0; --row) {
            for (int col = 0; col <= 6; ++col) {
                if (b.b[row][col] == 0) continue;

                //Checking positions on right for conflict
                if (col <= 3) {
                    for (int kap = 0; kap < 4; ++kap) {
                        if (b.b[row][col + kap] == 1) enemyScore++;
                        else if (b.b[row][col + kap] == 2) playerScore++;
                        else break;
                    }
                    if (enemyScore == 4) return 1;
                    else if (playerScore == 4) return 2;
                    enemyScore = 0;
                    playerScore = 0;
                }

                //Checking upward cells
                if (row >= 3) {
                    for (int k = 0; k < 4; ++k) {
                        if (b.b[row - k][col] == 1) enemyScore++;
                        else if (b.b[row - k][col] == 2) playerScore++;
                        else break;
                    }
                    if (enemyScore == 4) return 1;
                    else if (playerScore == 4) return 2;
                    enemyScore = 0;
                    playerScore = 0;
                }

                //Checking northeast diagonal
                if (col <= 3 && row >= 3) {
                    for (int k = 0; k < 4; ++k) {
                        if (b.b[row - k][col + k] == 1) enemyScore++;
                        else if (b.b[row - k][col + k] == 2) playerScore++;
                        else break;
                    }
                    if (enemyScore == 4) return 1;
                    else if (playerScore == 4) return 2;
                    enemyScore = 0;
                    playerScore = 0;
                }

                //Checking northwest diagonal
                if (col >= 3 && row >= 3) {
                    for (int k = 0; k < 4; ++k) {
                        if (b.b[row - k][col - k] == 1) enemyScore++;
                        else if (b.b[row - k][col - k] == 2) playerScore++;
                        else break;
                    }
                    if (enemyScore == 4) return 1;
                    else if (playerScore == 4) return 2;
                    enemyScore = 0;
                    playerScore = 0;
                }
            }
        }

        for (int j = 0; j < 7; ++j) {
            //check game to see that it hasn't finished
            if (b.b[0][j] == 0) return -1;
        }
        //draw game
        return 0;
    }

    public static void main(String[] args) {
        boardCreate b = new boardCreate();
        boardAI ai = new boardAI(b);
        ai.Menu();
    }
}

