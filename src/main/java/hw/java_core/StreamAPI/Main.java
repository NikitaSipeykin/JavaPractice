package hw.java_core.StreamAPI;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

  public static void main(String[] args) {
    new Main().test();
  }

  void test() {
    var denSheet = new WorkSheet("Денис", List.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY, DayOfWeek.SUNDAY));
    var benSheet = new WorkSheet("Бен", List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY));
    var lisSheet = new WorkSheet("Лиза", List.of(DayOfWeek.THURSDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
    var sheets = List.of(denSheet, benSheet, lisSheet);
     sheets.stream()
         .flatMap(sheet -> sheet.getWorkDays().stream())
         .collect(Collectors.groupingBy(day -> day, Collectors.counting()))
         .entrySet().stream().filter(value -> value.getValue() > 1)
         .map(Map.Entry::getKey)
         .forEach(System.out::println);
  }

  static class WorkSheet {
    String name;
    List<DayOfWeek> workDays;

    public WorkSheet(String name, List<DayOfWeek> workDays) {
      this.name = name;
      this.workDays = workDays;
    }

    public String getName() {
      return name;
    }

    public List<DayOfWeek> getWorkDays() {
      return workDays;
    }
  }

  enum DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
  }
}
