package hw.java_core.stringBuilder;

import java.util.ArrayList;
import java.util.List;

public class UndoStringBuilder {
  private final StringBuilder builder;
  private final List<String> history;
  private int currentIndex;

  public UndoStringBuilder() {
    this.builder = new StringBuilder();
    this.history = new ArrayList<>();
    history.add("");
    this.currentIndex = 0;
  }

  private void saveSnapshot() {
    history.subList(currentIndex + 1, history.size()).clear();
    history.add(builder.toString());
    currentIndex++;
  }

  public UndoStringBuilder append(String value){
    builder.append(value);
    saveSnapshot();
    return this;
  }

  public UndoStringBuilder insert(int offset, String value){
    builder.insert(offset, value);
    saveSnapshot();
    return this;
  }

  public UndoStringBuilder delete(int start, int end){
    builder.delete(start, end);
    saveSnapshot();
    return this;
  }

  public UndoStringBuilder deleteCharAt(int index){
    builder.deleteCharAt(index);
    saveSnapshot();
    return this;
  }

  public UndoStringBuilder replace(int start, int end, String value){
    builder.replace(start, end, value);
    saveSnapshot();
    return this;
  }

  public UndoStringBuilder reverse(){
    builder.reverse();
    saveSnapshot();
    return this;
  }

  public UndoStringBuilder setCharAt(int index, char value){
    builder.setCharAt(index, value);
    saveSnapshot();
    return this;
  }

  public UndoStringBuilder setLength(int newLength){
    builder.setLength(newLength);
    saveSnapshot();
    return this;
  }

  public UndoStringBuilder undo(){
    if(currentIndex > 0){
      currentIndex--;
      String previousString = history.get(currentIndex);
      builder.setLength(0);
      builder.append(previousString);
      return this;
    }
    return this;
  }

  @Override
  public String toString() {
    return builder.toString();
  }
}
