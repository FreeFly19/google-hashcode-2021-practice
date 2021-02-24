import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    static PrintStream printStream;
    static List<Pizza> pizzaPool = new ArrayList<>();
    static List<Team> teams = new ArrayList<>();

    public static void main(String[] args) throws IOException {
//        printStream = new PrintStream("b_out");
        printStream = System.out;

//        var sc = new Scanner(new FileInputStream("b_little_bit_of_everything.in"));
        var sc = new Scanner(System.in);
        int M = sc.nextInt();
        int T2 = sc.nextInt();
        int T3 = sc.nextInt();
        int T4 = sc.nextInt();


        for (int i = 0; i < T2; i++) {
            teams.add(new Team(teams.size(), 2));
        }
        for (int i = 0; i < T3; i++) {
            teams.add(new Team(teams.size(), 3));
        }
        for (int i = 0; i < T4; i++) {
            teams.add(new Team(teams.size(), 4));
        }

        for (int i = 0; i < M; i++) {
            Pizza pizza = new Pizza();
            pizza.id = i;
            int number = sc.nextInt();
            for (int j = 0; j < number; j++) {
                pizza.ingredients.add(sc.next());
            }
            pizzaPool.add(pizza);
        }

        for (int i = 0; i < teams.size() && !pizzaPool.isEmpty(); i++) {
            Team team = teams.get(i);
            team.pizzas.add(pizzaPool.remove(0));

            while(team.pizzas.size() != team.capacity && !pizzaPool.isEmpty()) {
                int maxJ = Integer.MIN_VALUE;
                int maxScore = Integer.MIN_VALUE;
                for (int j = 0; j < pizzaPool.size(); j++) {
                    int score = team.scoreWith(pizzaPool.get(j));
                    if (score > maxScore) {
                        maxJ = j;
                        maxScore = score;
                    }
                }
                team.pizzas.add(pizzaPool.remove(maxJ));
            }
        }

        List<Team> res = teams.stream().filter(t -> t.capacity == t.pizzas.size()).collect(Collectors.toList());

        printStream.println(res.size());
        for (Team re : res) {
            printStream.print(re.pizzas.size());
            for (Pizza pizza : re.pizzas) {
                printStream.print(" " + pizza.id);
            }
            printStream.println();
        }
    }

    static class Pizza {
        int id;
        Set<String> ingredients = new HashSet<>();
    }

    static class Team {
        int id;
        int capacity;
        List<Pizza> pizzas = new ArrayList<>();

        public Team(int id, int capacity) {
            this.id = id;
            this.capacity = capacity;
        }

        int score() {
            Set<String> ingredients = new HashSet<>();
            for (Pizza pizza : pizzas) {
                ingredients.addAll(pizza.ingredients);
            }
            return ingredients.size() * ingredients.size();
        }

        int scoreWith(Pizza p) {
            Set<String> ingredients = new HashSet<>();
            int ingredientTotal = 0;
            for (Pizza pizza : pizzas) {
                ingredients.addAll(pizza.ingredients);
                ingredientTotal += pizza.ingredients.size();
            }
            ingredients.addAll(p.ingredients);
            ingredientTotal += p.ingredients.size();

            return ingredients.size() * ingredients.size() - ingredientTotal * ingredientTotal;
        }
    }

}
