package edu.berkeley.cs186.database.table;

import edu.berkeley.cs186.database.TestUtils;
import edu.berkeley.cs186.database.StudentTest;
import edu.berkeley.cs186.database.datatypes.DataType;
import edu.berkeley.cs186.database.datatypes.IntDataType;
import edu.berkeley.cs186.database.datatypes.StringDataType;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestSchema {
    @Category(StudentTest.class)
    @Test
    public void testSchemaRetrieveBool() {
      Schema schema = TestUtils.createSchemaOfBool();

      Record input = TestUtils.createRecordOfBool();
      byte[] encoded = schema.encode(input);
      Record decoded = schema.decode(encoded);

      assertEquals(input, decoded);
    }
    
    @Category(StudentTest.class)
    @Test
    public void testSchemaRetrieveInts() {
      Schema schema = TestUtils.createSchemaWithTwoInts();

      Record input = TestUtils.createRecordOfTwoInts();
      byte[] encoded = schema.encode(input);
      Record decoded = schema.decode(encoded);

      assertEquals(input, decoded);
    }
    
    @Category(StudentTest.class)
    @Test
    public void testSchemaRetrieveString() {
      Schema schema = TestUtils.createSchemaOfString(5);

      Record input = TestUtils.createRecordOfString();
      byte[] encoded = schema.encode(input);
      Record decoded = schema.decode(encoded);

      assertEquals(input, decoded);
    }
    
  @Test
  public void testSchemaRetrieve() {
    Schema schema = TestUtils.createSchemaWithAllTypes();

    Record input = TestUtils.createRecordWithAllTypes();
    System.out.println("test");
    byte[] encoded = schema.encode(input);
    System.out.println("test1");

    Record decoded = schema.decode(encoded);
    System.out.println("test2");


    assertEquals(input, decoded);
  }

  @Test
  public void testValidRecord() {
    Schema schema = TestUtils.createSchemaWithAllTypes();
    Record input = TestUtils.createRecordWithAllTypes();

    try {
      Record output = schema.verify(input.getValues());
      assertEquals(input, output);
    } catch (SchemaException se) {
      fail();
    }
  }

  @Test(expected = SchemaException.class)
  public void testInvalidRecordLength() throws SchemaException {
    Schema schema = TestUtils.createSchemaWithAllTypes();
    schema.verify(new ArrayList<DataType>());
  }

  @Test(expected = SchemaException.class)
  public void testInvalidFields() throws SchemaException {
    Schema schema = TestUtils.createSchemaWithAllTypes();
    List<DataType> values = new ArrayList<DataType>();

    values.add(new StringDataType("abcde", 5));
    values.add(new IntDataType(10));

    schema.verify(values);
  }

  @Test(expected = SchemaException.class)
  public void testInvalidStringLength() throws SchemaException {
    Schema schema = TestUtils.createSchemaWithAllTypes();
    Record input = TestUtils.createRecordWithAllTypes();

    input.getValues().set(2, new StringDataType("abcdef", 6));

    schema.verify(input.getValues());
  }
}
