class boardCreate {
    byte[][] b = new byte[6][7];

    public boardCreate() {
        b = new byte[][]{
                {0, 0, 0, 0, 0, 0, 0,},
                {0, 0, 0, 0, 0, 0, 0,},
                {0, 0, 0, 0, 0, 0, 0,},
                {0, 0, 0, 0, 0, 0, 0,},
                {0, 0, 0, 0, 0, 0, 0,},
                {0, 0, 0, 0, 0, 0, 0,},
        };
    }


    //places piece on matrix
    public boolean Moving(int cols, int playerPiece) {
        if (!legMov(cols)) {
            System.out.println("Invalid");
            return false;
        }
        for (int i = 5; i >= 0; --i) {
            if (b[i][cols] == 0) {
                b[i][cols] = (byte) playerPiece;
                return true;
            }
        }
        return false;
    }

    public void printBoard() {
        System.out.println();
        for (int i = 0; i <= 5; ++i) {
            for (int j = 0; j <= 6; ++j) {
                System.out.print(b[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void retractMov(int cols) {
        for (int i = 0; i <= 5; ++i) {
            if (b[i][cols] != 0) {
                b[i][cols] = 0;
                break;
            }
        }
    }

    //checks for legal moves
    public boolean legMov(int cols) {
        return b[0][cols] == 0;
    }
}
