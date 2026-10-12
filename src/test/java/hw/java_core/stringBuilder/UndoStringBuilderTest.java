
package hw.java_core.stringBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UndoStringBuilderTest {

  private UndoStringBuilder builder;

  @BeforeEach
  void setUp() {
    builder = new UndoStringBuilder();
  }

  @Test
  void appendShouldAddTextToEnd() {
    builder.append("Hello").append(" world");

    assertEquals("Hello world", builder.toString());
  }

  @Test
  void insertShouldAddTextAtSpecifiedPosition() {
    builder.append("Helo");
    builder.insert(3, "l");

    assertEquals("Hello", builder.toString());
  }

  @Test
  void deleteShouldRemoveSpecifiedRange() {
    builder.append("Hello world");
    builder.delete(5, 11);

    assertEquals("Hello", builder.toString());
  }

  @Test
  void deleteCharAtShouldRemoveCharacter() {
    builder.append("Hello");
    builder.deleteCharAt(1);

    assertEquals("Hllo", builder.toString());
  }

  @Test
  void replaceShouldReplaceSpecifiedRange() {
    builder.append("Hello world");
    builder.replace(6, 11, "Java");

    assertEquals("Hello Java", builder.toString());
  }

  @Test
  void reverseShouldReverseText() {
    builder.append("Hello");
    builder.reverse();

    assertEquals("olleH", builder.toString());
  }

  @Test
  void setCharAtShouldReplaceCharacter() {
    builder.append("Hello");
    builder.setCharAt(1, 'a');

    assertEquals("Hallo", builder.toString());
  }

  @Test
  void setLengthShouldShortenText() {
    builder.append("Hello world");
    builder.setLength(5);

    assertEquals("Hello", builder.toString());
  }

  @Test
  void setLengthShouldExpandTextWithNullCharacters() {
    builder.append("Hi");
    builder.setLength(4);

    assertEquals("Hi\u0000\u0000", builder.toString());
  }

  @Test
  void undoShouldRestorePreviousState() {
    builder.append("A");
    builder.append("B");
    builder.append("C");

    builder.undo();

    assertEquals("AB", builder.toString());

    builder.undo();

    assertEquals("A", builder.toString());

    builder.undo();

    assertEquals("", builder.toString());
  }

  @Test
  void undoAtInitialStateShouldDoNothing() {
    builder.undo();

    assertEquals("", builder.toString());
  }

  @Test
  void newMutationAfterUndoShouldDiscardFutureSnapshots() {
    builder.append("A");
    builder.append("B");
    builder.append("C");

    builder.undo();

    assertEquals("AB", builder.toString());

    builder.append("D");

    assertEquals("ABD", builder.toString());

    builder.undo();

    assertEquals("AB", builder.toString());

    builder.undo();

    assertEquals("A", builder.toString());
  }

  @Test
  void failedMutationShouldNotCreateSnapshot() {
    builder.append("Hello");

    assertThrows(
        IndexOutOfBoundsException.class,
        () -> builder.insert(100, "!")
    );

    assertEquals("Hello", builder.toString());

    builder.undo();

    assertEquals("", builder.toString());
  }

  @Test
  void mutatingMethodsShouldSupportMethodChaining() {
    builder.append("Helo")
        .insert(3, "l")
        .replace(0, 1, "h")
        .reverse();

    assertEquals("olleh", builder.toString());
  }
}
