package edu.berkeley.cs186.database.index;

import edu.berkeley.cs186.database.datatypes.DataType;
import edu.berkeley.cs186.database.io.Page;
import edu.berkeley.cs186.database.table.RecordID;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * A B+ tree leaf node. A leaf node header contains the page number of the
 * parent node (or -1 if no parent exists), the page number of the previous leaf
 * node (or -1 if no previous leaf exists), and the page number of the next leaf
 * node (or -1 if no next leaf exists). A leaf node contains LeafEntry's.
 *
 * Inherits all the properties of a BPlusNode.
 */
public class LeafNode extends BPlusNode {

  public LeafNode(BPlusTree tree) {
    super(tree, true);
    getPage().writeByte(0, (byte) 1);
    setPrevLeaf(-1);
    setParent(-1);
    setNextLeaf(-1);
  }
  
  public LeafNode(BPlusTree tree, int pageNum) {
    super(tree, pageNum, true);
    if (getPage().readByte(0) != (byte) 1) {
      throw new BPlusTreeException("Page is not Leaf Node!");
    }
  }

  @Override
  public boolean isLeaf() {
    return true;
  }

  /**
   * See BPlusNode#locateLeaf documentation.
   */
  @Override
  public LeafNode locateLeaf(DataType key, boolean findFirst) {
    //TODO: Implement Me!!
      if (findFirst) {
          //trace previous nodes to see if until curKey<key
          if (this.getPrevLeaf()!=-1) {
              LeafNode prevNode=(LeafNode)BPlusNode.getBPlusNode(this.getTree(), this.getPrevLeaf());
              Iterator<RecordID> iter=prevNode.scanForKey(key);
              if (iter.hasNext()) {
                  return prevNode.locateLeaf(key, findFirst);
              }
          } return this;
      } 
      else {
          if (this.getNextLeaf()!=-1) {
              LeafNode nextNode=(LeafNode)BPlusNode.getBPlusNode(this.getTree(), this.getNextLeaf());
              Iterator<RecordID> iter=nextNode.scanForKey(key);
              if (iter.hasNext()) {
                  return nextNode.locateLeaf(key, findFirst);
              } return this;
          } return this;
      }
      
  }

  /**
   * Splits this node and copies up the middle key. Note that we split this node
   * immediately after it becomes full rather than when trying to insert an
   * entry into a full node. Thus a full leaf node of 2d entries will be split
   * into a left node with d entries and a right node with d entries, with the
   * leftmost key of the right node copied up.
   */
  @Override
  public void splitNode() {
    //TODO: Implement Me!!

      List<BEntry> left=new ArrayList<BEntry>();
      LeafNode right=new LeafNode(this.getTree());

      BEntry middle = null;
      List<BEntry> ents=this.getAllValidEntries();
      for (int i = 0;i<ents.size();i++) {
          if (i<numEntries/2) {
              left.add(ents.get(i));
          } else {
              if (i==numEntries/2) {
                  middle=ents.get(i);
              }
              right.insertBEntry(ents.get(i));
          }
      }      
      //getting parent
      InnerNode parentNode=null;
      if (!this.isRoot()) {
          int parentPage=this.getParent();
          parentNode=(InnerNode)BPlusNode.getBPlusNode(this.getTree(),parentPage);
      } else {
          parentNode=new InnerNode(this.getTree());
          //updating root,assigning firstchild
          this.getTree().updateRoot(parentNode.getPageNum());
          parentNode.setFirstChild(this.getPageNum());
      }      
      //updating root, parent pointers, firstpagenum and next pointers if necessary.
      
      //setting parents
      this.setParent(parentNode.getPageNum());
      right.setParent(parentNode.getPageNum());
      
      BEntry newEnt=new InnerEntry(middle.getKey(),right.getPageNum());
//      if the pushed up key becomes the smallest entry
      if (parentNode.getAllValidEntries().size()>0&&
          middle.getKey().compareTo(parentNode.getAllValidEntries().get(0).getKey())<0){
          newEnt=new InnerEntry(middle.getKey(),parentNode.getFirstChild());
          parentNode.setFirstChild(this.getPageNum());
      } 
      parentNode.insertBEntry(newEnt);

      
      //setting splitted parents' as the new parent node
      this.overwriteBNodeEntries(left);
      //setting the pointers from leafnodes to each other
      if (this.getNextLeaf()!=-1) {
          LeafNode nextNode=(LeafNode)BPlusNode.getBPlusNode(this.getTree(), this.getNextLeaf());
          nextNode.setPrevLeaf(right.getPageNum());
      }
      right.setPrevLeaf(this.getPageNum());
      
      //setting next pointers
      right.setNextLeaf(this.getNextLeaf());
      this.setNextLeaf(right.getPageNum());
      
  }
  
  public int getPrevLeaf() {
    return getPage().readInt(5);
  }

  public int getNextLeaf() {
    return getPage().readInt(9);
  }
  
  public void setPrevLeaf(int val) {
    getPage().writeInt(5, val);
  }

  public void setNextLeaf(int val) {
    getPage().writeInt(9, val);
  }
  
  @Override
  protected int numLeaves() {
      return this.getAllValidEntries().size();
  }

  /**
   * Creates an iterator of RecordID's for all entries in this node.
   *
   * @return an iterator of RecordID's
   */
  public Iterator<RecordID> scan() {
    List<BEntry> validEntries = getAllValidEntries();
    List<RecordID> rids = new ArrayList<RecordID>();

    for (BEntry le : validEntries) {
      rids.add(le.getRecordID());
    }

    return rids.iterator();
  }

  /**
   * Creates an iterator of RecordID's whose keys are greater than or equal to
   * the given start value key.
   *
   * @param startValue the start value key
   * @return an iterator of RecordID's
   */
  public Iterator<RecordID> scanFrom(DataType startValue) {
    List<BEntry> validEntries = getAllValidEntries();
    List<RecordID> rids = new ArrayList<RecordID>();

    for (BEntry le : validEntries) {
      if (startValue.compareTo(le.getKey()) < 1) { 
        rids.add(le.getRecordID());
      }
    }
    return rids.iterator();
  }

  /**
   * Creates an iterator of RecordID's that correspond to the given key.
   *
   * @param key the search key
   * @return an iterator of RecordID's
   */
  public Iterator<RecordID> scanForKey(DataType key) {
    List<BEntry> validEntries = getAllValidEntries();
    List<RecordID> rids = new ArrayList<RecordID>();

    for (BEntry le : validEntries) {
      if (key.compareTo(le.getKey()) == 0) { 
        rids.add(le.getRecordID());
      }
    }
    return rids.iterator();
  }
}
