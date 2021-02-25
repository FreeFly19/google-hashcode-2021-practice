import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    static PrintStream printStream;
    static List<Pizza> pizzaPool = new ArrayList<>();
    static boolean[] takenPizza;
    static List<Team> teams = new ArrayList<>();

    public static void main(String[] args) throws IOException {
//        printStream = new PrintStream("output/b.out");
//        var sc = new Scanner(new FileInputStream("input/b.in"));
        printStream = System.out;
        var sc = new Scanner(System.in);

        int M = sc.nextInt();
        int T2 = sc.nextInt();
        int T3 = sc.nextInt();
        int T4 = sc.nextInt();

        takenPizza = new boolean[M];

        for (int i = 0; i < T4; i++) {
            teams.add(new Team(teams.size(), 4));
        }
        for (int i = 0; i < T3; i++) {
            teams.add(new Team(teams.size(), 3));
        }
        for (int i = 0; i < T2; i++) {
            teams.add(new Team(teams.size(), 2));
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
            while(team.pizzas.size() != team.capacity && !pizzaPool.isEmpty()) {
                team.pizzas.add(pizzaPool.remove(0));
            }
        }

        List<Team> res = teams.stream().filter(t -> t.capacity == t.pizzas.size()).collect(Collectors.toList());

        int optiomizations = 10000;

        for (int iters = 0; iters < optiomizations; iters++) {
            List<Integer> teamIds = Stream.iterate(0, i -> i < res.size(), i -> i + 1)
                    .collect(Collectors.toList());

            Collections.shuffle(teamIds);

            List<Pair> teamPairs = new ArrayList<>();

            for (int i = 0; i + 1 < teamIds.size(); i+=2) {
                teamPairs.add(new Pair(teamIds.get(i), teamIds.get(i + 1)));
            }
            teamPairs.parallelStream()
                    .forEach(tp -> {
                        Team t1 = res.get(tp.first);
                        Team t2 = res.get(tp.second);
                        for (int i = 0; i < t1.pizzas.size(); i++) {
                            var t1Pizza = t1.pizzas.get(i);
                            var t1Ingredients = t1.pizzas.stream()
                                    .filter(p -> p.id != t1Pizza.id)
                                    .flatMap(p -> p.ingredients.stream())
                                    .collect(Collectors.toSet());
                            for (int j = 0; j < t2.pizzas.size(); j++) {
                                var t2Pizza = t2.pizzas.get(j);
                                var t1Ingreds = new HashSet<>(t1Ingredients);
                                t1Ingreds.addAll(t2Pizza.ingredients);
                                var newT1Score = t1Ingreds.size() * t1Ingreds.size();

                                var t2Ingredients = Stream.concat(
                                        t2.pizzas.stream().filter(p -> p.id != t2Pizza.id),
                                        Stream.of(t1Pizza)
                                )
                                        .flatMap(p -> p.ingredients.stream())
                                        .collect(Collectors.toSet());
                                var newT2Score = t2Ingredients.size() * t2Ingredients.size();

                                if (t1.score() + t2.score() < newT1Score + newT2Score) {
                                    var p = t1.pizzas.set(i, t2Pizza);
                                    t2.pizzas.set(j, p);
                                }
                            }
                        }
                    });
        }


        printStream.println(res.size());
        for (Team re : res) {
            printStream.print(re.pizzas.size());
            for (Pizza pizza : re.pizzas) {
                printStream.print(" " + pizza.id);
            }
            printStream.println();
        }
    }

    static class Pair {
        final int first, second;

        Pair(int first, int second) {
            this.first = first;
            this.second = second;
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
