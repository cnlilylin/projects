package db61b;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class Testers {

    @Test
    public void testTableIterator() {
        String[] titles = {"height", "weight"};
        Table test1 = new Table("dog", titles);
        assertEquals(0, test1.columnIndex("height"));
        assertEquals(-1, test1.columnIndex("hahaha"));
        TableIterator iter1 = test1.tableIterator();
        assertEquals(test1, iter1.table());
        test1.add(new Row(new String[] {"10", "20"}));
        test1.add(new Row(new String[] {"20", "40"}));
        test1.add(new Row(new String[] {"30", "60"}));
        assertEquals(2, test1.numColumns());
        assertEquals(true, iter1.hasRow());
        Row row1 = iter1.next();
        iter1.reset();
        assertEquals(row1, iter1.next());
        while (iter1.hasRow()) {
            iter1.next();
        }
        TableIterator iterDup = test1.tableIterator();
        assertEquals(true, iterDup.hasRow());
    }

    @Test
    public void testColumn() {
        String[] titles1 = {"height", "weight", "name", "age"};
        String[] titles2 = {"color", "fur length", "lives", "purr"};
        ArrayList<TableIterator> iterators = new ArrayList<>();
        Table test1 = new Table("dog", titles1);
        Table test2 = new Table("cat", titles2);
        test1.add(new Row(new String[] {"10", "20", "Adam", "1"}));
        test1.add(new Row(new String[] {"20", "40", "Bill", "2"}));
        test1.add(new Row(new String[] {"30", "60", "Charles", "3"}));
        test2.add(new Row(new String[] {"red", "3", "9", "meow"}));
        test2.add(new Row(new String[] {"white", "4", "3", "meoww"}));
        test2.add(new Row(new String[] {"rainbow", "6", "2", "meowww"}));
        Column col1 = new Column(test1, "age");
        Column col2 = new Column(test2, "lives");
        TableIterator iter1 = test1.tableIterator();
        TableIterator iter2 = test2.tableIterator();
        iterators.add(iter1);
        iterators.add(iter2);
        col1.resolve(iterators);
        col2.resolve(iterators);
        assertEquals(false, col1.value() == null);
        assertEquals(false, col2.value() == null);
        test1.writeTable("dog");
    }
}
