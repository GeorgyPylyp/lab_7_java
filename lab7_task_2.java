import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class lab7_task_2 {

    static abstract class MetroStation {
        private String name;
        private int yearOpened;

        public MetroStation(String name, int yearOpened) {
            this.name = name;
            this.yearOpened = yearOpened;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getYearOpened() {
            return yearOpened;
        }

        public void setYearOpened(int yearOpened) {
            this.yearOpened = yearOpened;
        }

        @Override
        public String toString() {
            return String.format("%-20s %-10d", name, yearOpened);
        }

        public static String getTableHeader() {
            return String.format("%-20s %-10s", "Назва", "Рік відкриття");
        }
    }

    static class Hour extends MetroStation {
        private int passengersCount;
        private String comments;
        private String hour;

        public Hour(String name, int yearOpened, int passengersCount, String comments, String hour) {
            super(name, yearOpened);
            this.passengersCount = passengersCount;
            this.comments = comments;
            this.hour = hour;
        }

        public int getPassengersCount() {
            return passengersCount;
        }

        public void setPassengersCount(int passengersCount) {
            this.passengersCount = passengersCount;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public String getHour() {
            return hour;
        }

        public void setHour(String hour) {
            this.hour = hour;
        }

        @Override
        public String toString() {
            return String.format("%-20s %-10d %-10d %-20s %-5s", getName(), getYearOpened(), passengersCount, comments, hour);
        }

        public static String getTableHeader() {
            return String.format("%-20s %-10s %-10s %-20s %-5s",
                    "Назва", "Рік відкриття", "Кількість пасажирів", "Коментарі", "Година");
        }

        // Реалізація методів завдання
        static class MetroStationDatabase {
            private List<Hour> hours;

            public MetroStationDatabase() {
                hours = new ArrayList<>();
            }

            public void addHour(Hour hour) {
                hours.add(hour);
                saveToFile();
            }

            public List<Hour> getHours() {
                return hours;
            }

            public void editHour(String name, Hour newHour) {
                for (int i = 0; i < hours.size(); i++) {
                    if (hours.get(i).getName().equalsIgnoreCase(name)) {
                        hours.set(i, newHour);
                        saveToFile();
                        return;
                    }
                }
                System.out.println("Станція з такою назвою не знайдена.");
            }

            public void deleteHour(String name) {
                for (int i = 0; i < hours.size(); i++) {
                    if (hours.get(i).getName().equalsIgnoreCase(name)) {
                        hours.remove(i);
                        saveToFile();
                        return;
                    }
                }
                System.out.println("Станція з такою назвою не знайдена.");
            }

            public void displayHours() {
                System.out.println(Hour.getTableHeader());
                System.out.println("-------------------- ---------- ---------- -------------------- -----");
                for (Hour hour : hours) {
                    System.out.println(hour);
                }
            }

            public Hour searchHourByName(String name) {
                for (Hour hour : hours) {
                    if (hour.getName().equalsIgnoreCase(name)) {
                        return hour;
                    }
                }
                return null;
            }

            public void sortHoursByParameter(String parameter) {
                switch (parameter.toLowerCase()) {
                    case "назва":
                        Collections.sort(hours, Comparator.comparing(Hour::getName));
                        break;
                    case "рік відкриття":
                        Collections.sort(hours, Comparator.comparing(Hour::getYearOpened));
                        break;
                    case "кількість пасажирів":
                        Collections.sort(hours, Comparator.comparing(Hour::getPassengersCount));
                        break;
                    case "година":
                        Collections.sort(hours, Comparator.comparing(Hour::getHour));
                        break;
                    default:
                        System.out.println("Невідомий параметр для сортування.");
                }
            }

            public void totalPassengers() {
                int total = hours.stream().mapToInt(Hour::getPassengersCount).sum();
                System.out.println("Сумарна кількість пасажирів: " + total);
            }

            public void hourWithLeastPassengers() {
                Hour min = Collections.min(hours, Comparator.comparingInt(Hour::getPassengersCount));
                System.out.println("Година з найменшою кількістю пасажирів: " + min.getName() + " (" + min.getPassengersCount() + ")");
            }

            public void hourWithLongestComment() {
                Hour max = Collections.max(hours, Comparator.comparingInt(h -> h.getComments().split("\\s+").length));
                System.out.println("Година з найбільшою кількістю слів у коментарі: " + max.getName() + " (" + max.getComments() + ")");
            }

            private void saveToFile() {
                try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("metro_stations.txt"), StandardCharsets.UTF_8))) {
                    for (Hour hour : hours) {
                        writer.println(hour.getName() + "," + hour.getYearOpened() + "," +
                                hour.getPassengersCount() + "," + hour.getComments() + "," + hour.getHour());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void loadFromFile() {
                File file = new File("metro_stations.txt");
                if (!file.exists()) {
                    System.out.println("Файл metro_stations.txt не знайдено. Створюється новий файл.");
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        System.err.println("Помилка при створенні файлу: " + e.getMessage());
                    }
                    return;
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length < 5) {
                            System.err.println("Невірний формат рядка: " + line);
                            continue;
                        }
                        String name = parts[0];
                        int yearOpened = Integer.parseInt(parts[1]);
                        int passengersCount = Integer.parseInt(parts[2]);
                        String comments = parts[3];
                        String hour = parts[4];
                        Hour hourObj = new Hour(name, yearOpened, passengersCount, comments, hour);
                        hours.add(hourObj);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            // Налаштування System.out для використання UTF-8
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in, "UTF-8");
        Hour.MetroStationDatabase metroStationDatabase = new Hour.MetroStationDatabase();
        metroStationDatabase.loadFromFile();

        while (true) {
            System.out.println("\nМеню:");
            System.out.println("1. Додати станцію");
            System.out.println("2. Редагувати станцію");
            System.out.println("3. Видалити годину");
            System.out.println("4. Показати всі години");
            System.out.println("5. Пошук години за назвою станції");
            System.out.println("6. Сортування годин");
            System.out.println("7. Загальна кількість пасажирів");
            System.out.println("8. Година з найменшою кількістю пасажирів");
            System.out.println("9. Година з найбільшою кількістю слів у коментарі");
            System.out.println("10. Вихід");
            System.out.print("Оберіть опцію: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addHour(scanner, metroStationDatabase);
                    break;
                case "2":
                    editHour(scanner, metroStationDatabase);
                    break;
                case "3":
                    deleteHour(scanner, metroStationDatabase);
                    break;
                case "4":
                    metroStationDatabase.displayHours();
                    break;
                case "5":
                    searchHour(scanner, metroStationDatabase);
                    break;
                case "6":
                    sortHours(scanner, metroStationDatabase);
                    break;
                case "7":
                    metroStationDatabase.totalPassengers();
                    break;
                case "8":
                    metroStationDatabase.hourWithLeastPassengers();
                    break;
                case "9":
                    metroStationDatabase.hourWithLongestComment();
                    break;
                case "10":
                    System.out.println("Завершення програми.");
                    return;
                default:
                    System.out.println("Невірний вибір.");
            }
        }
    }

    private static void addHour(Scanner scanner, Hour.MetroStationDatabase metroStationDatabase) {
        System.out.print("Назва станції: ");
        String name = scanner.nextLine();

        System.out.print("Рік відкриття: ");
        int yearOpened = Integer.parseInt(scanner.nextLine());

        System.out.print("Кількість пасажирів: ");
        int passengersCount = Integer.parseInt(scanner.nextLine());

        System.out.print("Коментарі: ");
        String comments = scanner.nextLine();

        System.out.print("Година: ");
        String hour = scanner.nextLine();

        Hour hourObj = new Hour(name, yearOpened, passengersCount, comments, hour);
        metroStationDatabase.addHour(hourObj);
        System.out.println("Годину успішно додано.");
    }

    private static void editHour(Scanner scanner, Hour.MetroStationDatabase metroStationDatabase) {
        System.out.print("Введіть назву станції, яку потрібно редагувати: ");
        String name = scanner.nextLine();

        Hour existingHour = metroStationDatabase.searchHourByName(name);
        if (existingHour != null) {
            System.out.print("Нова назва станції: ");
            String newName = scanner.nextLine();

            System.out.print("Новий рік відкриття: ");
            int newYearOpened = Integer.parseInt(scanner.nextLine());

            System.out.print("Нова кількість пасажирів: ");
            int newPassengersCount = Integer.parseInt(scanner.nextLine());

            System.out.print("Нові коментарі: ");
            String newComments = scanner.nextLine();

            System.out.print("Нова година: ");
            String newHour = scanner.nextLine();

            Hour newHourObj = new Hour(newName, newYearOpened, newPassengersCount, newComments, newHour);
            metroStationDatabase.editHour(name, newHourObj);
            System.out.println("Годину успішно відредаговано.");
        } else {
            System.out.println("Станція з такою назвою не знайдена.");
        }
    }

    private static void deleteHour(Scanner scanner, Hour.MetroStationDatabase metroStationDatabase) {
        System.out.print("Введіть назву станції, яку потрібно видалити: ");
        String name = scanner.nextLine();

        metroStationDatabase.deleteHour(name);
        System.out.println("Годину успішно видалено.");
    }

    private static void searchHour(Scanner scanner, Hour.MetroStationDatabase metroStationDatabase) {
        System.out.print("Введіть назву станції для пошуку: ");
        String name = scanner.nextLine();

        Hour foundHour = metroStationDatabase.searchHourByName(name);
        if (foundHour != null) {
            System.out.println("Знайдено станцію: " + foundHour);
        } else {
            System.out.println("Станція з такою назвою не знайдена.");
        }
    }

    private static void sortHours(Scanner scanner, Hour.MetroStationDatabase metroStationDatabase) {
        System.out.print("Сортувати за полем (назва/рік відкриття/кількість пасажирів/година): ");
        String parameter = scanner.nextLine();
        metroStationDatabase.sortHoursByParameter(parameter);
        metroStationDatabase.displayHours();
    }
}

