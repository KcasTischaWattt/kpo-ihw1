import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.Stack;

public class Main {

    /**
     * Сама доска
     */
    static Board board = new Board();

    /**
     * Стэк досок для отмены хода
     */
    static Stack<Board> boards = new Stack<>();

    /**
     * Список доступных ходов
     */
    static ArrayList<Coords> possibleMovesList = new ArrayList<>();

    /**
     * Лучший результат за сессию
     */
    static int BestResult = 0;

    /**
     * Сам main
     */
    public static void main(String[] args) {
        try {
            printInterface();
            boards.push(new Board(board));
            chooseMode();
        } catch (Exception e) {
            System.out.println("Что-то пошло не так. Перезапустите программу");
            System.out.println("Возможно вы ввели не цифру в поле, где требовалась цифра");
        }

    }

    /**
     * Выбор режима игры
     */
    private static void chooseMode() {
        Scanner in = new Scanner(System.in);
        int input = in.nextInt();
        if (Objects.equals(input, 1)) {
            startGameAgainstComputer();
        } else {
            startDualGame();
        }
    }

    /**
     * Логика очерёдности подов в pvp режиме
     */
    private static void startDualGame() {
        boolean flag = true;
        do {
            if (canMove(Colour.BLACK)) {
                makeMove(Colour.BLACK);
            } else if (canMove(Colour.WHITE)) {
                System.out.println("Чёрные не могут сходить и пропускают ход.");
            } else {
                flag = false;
            }
            if (canMove(Colour.WHITE)) {
                makeMove(Colour.WHITE);
            } else if (canMove(Colour.BLACK)) {
                System.out.println("Чёрные не могут сходить и пропускают ход.");
            } else {
                flag = false;
            }
        } while (flag);
        printResult();
    }

    /**
     * Проверка на то, есть ли доступные ходы
     *
     * @param turn Цвет стороны
     * @return true, если есть доступные ходы. Иначе false
     */
    private static boolean canMove(Colour turn) {
        board.clearAllDirections();
        possibleMovesList.clear();
        Colour opp = Objects.equals(turn, Colour.WHITE) ? Colour.BLACK : Colour.WHITE;
        int sum = 0;
        // Проверка идёт для всех фишек на поле в 8 направлений
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (Objects.equals(board.getPiece(i, j).getColour(), Colour.EMPTY)) {
                    if (i > 1 && Objects.equals(board.getPiece(i - 1, j).getColour(), opp)) {
                        board.check(i, j, -1, 0, 0, turn, opp);
                    }
                    if (i < 6 && Objects.equals(board.getPiece(i + 1, j).getColour(), opp)) {
                        board.check(i, j, 1, 0, 1, turn, opp);
                    }
                    if (j > 1 && Objects.equals(board.getPiece(i, j - 1).getColour(), opp)) {
                        board.check(i, j, 0, -1, 2, turn, opp);
                    }
                    if (j < 6 && Objects.equals(board.getPiece(i, j + 1).getColour(), opp)) {
                        board.check(i, j, 0, 1, 3, turn, opp);
                    }
                    if (i > 1 && j > 1 && Objects.equals(board.getPiece(i - 1, j - 1).getColour(), opp)) {
                        board.check(i, j, -1, -1, 4, turn, opp);
                    }
                    if (i < 6 && j > 1 && Objects.equals(board.getPiece(i + 1, j - 1).getColour(), opp)) {
                        board.check(i, j, 1, -1, 5, turn, opp);
                    }
                    if (i > 1 && j < 6 && Objects.equals(board.getPiece(i - 1, j + 1).getColour(), opp)) {
                        board.check(i, j, -1, 1, 6, turn, opp);
                    }
                    if (i < 6 && j < 6 && Objects.equals(board.getPiece(i + 1, j + 1).getColour(), opp)) {
                        board.check(i, j, 1, 1, 7, turn, opp);
                    }
                }
                // В конце проверки высчитывается количество фишек, которые можно захватить соответствующим ходом
                if (Objects.equals(board.getPiece(i, j).getColour(), Colour.POSSIBLE_MOVE)) {
                    possibleMovesList.add(new Coords(i, j));
                    sum += board.getPiece(i, j).getSumDirections();
                }
            }
        }
        // Если можно захватить хотя бы 1 фишку на поле, то можно сделать ход
        return sum != 0;
    }

    /**
     * Логика действий при совершении хода игроком
     *
     * @param col Цвет хода
     */
    private static void makeMove(Colour col) {
        printPossibleMoves();
        System.out.println("Введите свой ход:");
        boolean isPossible;
        String input;
        // Обработка введённого хода и проверка его на корректность
        do {
            Scanner in = new Scanner(System.in);
            input = in.next();
            isPossible = checkIfMoveIsInPossibleMovesList(input);
            if (Objects.equals(input, "undo")) {
                if (undo()) {
                    return;
                }
                continue;
            }
            if (!isPossible) {
                System.out.println("Неверный ход! Повторите попытку");
            }
        } while (!isPossible);
        board.changeColours(input);
        board.setPiece(input, new Piece(col));
        board.clearAllDirections();
        board.clearAllPossibleMoves();
        possibleMovesList.clear();
        saveBoard();
    }

    /**
     * Отмена хода
     *
     * @return true, если можно отменить ход. false, если это начальная позиция
     */
    private static boolean undo() {
        if (boards.size() > 1) {
            boards.pop();
            board = new Board(boards.peek());
            return true;
        }
        System.out.println("Это начальная позиция!");
        return false;
    }

    /**
     * Проверяет, есть ли ход в списке доступных ходов
     *
     * @param input Совершаемый ход
     * @return true, если можно сделать введённый ход. Иначе false
     */
    private static boolean checkIfMoveIsInPossibleMovesList(String input) {
        for (var a : possibleMovesList) {
            if (a.equals(input)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Вывод доступных ходов
     */
    private static void printPossibleMoves() {
        System.out.println(board);
        System.out.println("Возможные ходы:");
        possibleMovesList.forEach(System.out::println);
    }

    /**
     * Логика действий при игре pve
     */
    private static void startGameAgainstComputer() {
        printChoosingColorMenu();
        Colour colour = chooseColor();
        boolean flag = true;
        do {
            if (colour == Colour.BLACK) {
                if (canMove(Colour.BLACK)) {
                    makeMove(Colour.BLACK);
                    if (canMove(Colour.WHITE)) {
                        makeComputerMove(Colour.WHITE);
                    } else {
                        System.out.println("Белые не могут сходить и пропускают ход.");
                    }
                } else if (canMove(Colour.WHITE)) {
                    System.out.println("Чёрные не могут сходить и пропускают ход.");
                    makeComputerMove(Colour.WHITE);
                } else {
                    flag = false;
                }
            } else {
                if (canMove(Colour.BLACK)) {
                    makeComputerMove(Colour.BLACK);
                    if (canMove(Colour.WHITE)) {
                        makeMove(Colour.WHITE);
                    } else {
                        System.out.println("Белые не могут сходить и пропускают ход.");
                    }
                } else if (canMove(Colour.WHITE)) {
                    System.out.println("Чёрные не могут сходить и пропускают ход.");
                    makeMove(Colour.WHITE);
                } else {
                    flag = false;
                }
            }
        } while (flag);
        printResultAgainstComputer(colour);
    }

    /**
     * Выбор цвета для игрока
     *
     * @return Выбранный игроком цвет
     */
    private static Colour chooseColor() {
        Scanner in = new Scanner(System.in);
        int input = in.nextInt();
        if (Objects.equals(input, 1)) {
            return Colour.WHITE;
        } else {
            return Colour.BLACK;
        }
    }

    /**
     * Вывод меню выбора цвета
     */
    private static void printChoosingColorMenu() {
        System.out.println("""
                Выбери цвет:
                1) Белый
                2) Чёрный""");
    }

    /**
     * Логика при совершении хода компьютером
     *
     * @param colour Цвет, которым играет компьютер
     */
    private static void makeComputerMove(Colour colour) {
        System.out.println("Ход компьютера");
        System.out.println(board);
        if (!possibleMovesList.isEmpty()) {
            double bestValue = 0;
            Coords bestCord = possibleMovesList.get(0);
            // Доступные клетки вычисляются заранее - теперь надо лишь оценить ценность каждой из них
            for (var c : possibleMovesList) {
                double temp = evaluate(c.x(), c.y(), colour);
                if (temp > bestValue) {
                    bestValue = temp;
                    bestCord = c;
                }
            }
            board.changeColours(bestCord);
            board.setPiece(bestCord.x(), bestCord.y(), new Piece(colour));
            System.out.println("Компьютер ставит фишку на " + bestCord);
            board.clearAllDirections();
            board.clearAllPossibleMoves();
            possibleMovesList.clear();
        }
    }

    /**
     * Расчёт ценности клетки
     *
     * @param i      Первая координата клетки
     * @param j      Вторая координата клетки
     * @param colour Цвет, которпым играет компьютер
     * @return Ценность клетки
     */
    private static double evaluate(int i, int j, Colour colour) {
        Colour opposite = Objects.equals(colour, Colour.WHITE) ? Colour.BLACK : Colour.WHITE;
        double res = 0;
        // Вычисление прямой ценности занимаемой клетки
        if (i == 0 || j == 0 || i == 7 || j == 7) {
            res += 0.4;
            if ((i == 0 && j == 0) || (i == 7 && j == 7) ||
                    (i == 0 && j == 7) || (i == 7 && j == 0)) {
                res += 0.4;
            }
        }
        // Вычисление ценности захватываемых фишек путём анализа ситуации вокруг клетки во все 8 сторон
        for (int l = -1; l <= 1; l++) {
            for (int n = -1; n <= 1; n++) {
                if (!(l == 0 && n == 0)) {
                    res += board.evaluateDirection(i, j, l, n, colour, opposite);
                }
            }
        }
        return res;
    }

    /**
     * Сохранение доски
     */
    private static void saveBoard() {
        boards.push(new Board(board));
    }

    /**
     * Вывод интерфейса на печать
     */
    public static void printInterface() {
        System.out.print("""
                Выберите режим игры:\s
                1) Компьютер\s
                2) Два игрока\s
                Лучший результат за сегодня:\040""");
        System.out.println(BestResult);
        System.out.println("Чтобы отменить ход, надо ввести undo");

    }


    /**
     * Вывод результата игры в pvp режиме
     */
    private static void printResult() {
        if (board.getWhites() > board.getBlacks()) {
            System.out.println("Белые победили. Счёт: " + board.getWhites() + "-" + board.getBlacks());
        } else if (board.getWhites() < board.getBlacks()) {
            System.out.println("Чёрные победили. Счёт: " + board.getWhites() + "-" + board.getBlacks());
        } else {
            System.out.println("Ничья. Счёт: " + board.getWhites() + "-" + board.getBlacks());
        }
    }

    /**
     * Вывод результата игры в pve режиме
     *
     * @param colour Цвет игрока
     */
    private static void printResultAgainstComputer(Colour colour) {
        printResult();
        if (colour == Colour.WHITE) {
            if (BestResult < board.getWhites()) {
                BestResult = board.getWhites();
                System.out.println("Новый рекорд: " + BestResult);
            }
        } else {
            if (BestResult > board.getBlacks()) {
                BestResult = board.getBlacks();
                System.out.println("Новый рекорд: " + BestResult);
            }
        }
    }
}