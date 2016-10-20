package edu.berkeley.cs186.database.index;

import edu.berkeley.cs186.database.datatypes.DataType;
import edu.berkeley.cs186.database.io.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * A B+ tree inner node. An inner node header contains the page number of the
 * parent node (or -1 if no parent exists), and the page number of the first
 * child node (or -1 if no child exists). An inner node contains InnerEntry's.
 * Note that an inner node can have duplicate keys if a key spans multiple leaf
 * pages.
 *
 * Inherits all the properties of a BPlusNode.
 */
public class InnerNode extends BPlusNode {

  public InnerNode(BPlusTree tree) {
    super(tree, false);
    getPage().writeByte(0, (byte) 0);
    setFirstChild(-1);
    setParent(-1);
  }
  
  public InnerNode(BPlusTree tree, int pageNum) {
    super(tree, pageNum, false);
    if (getPage().readByte(0) != (byte) 0) {
      throw new BPlusTreeException("Page is not Inner Node!");
    }
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  public int getFirstChild() {
    return getPage().readInt(5);
  }
  
  public void setFirstChild(int val) {
    getPage().writeInt(5, val);
  }
  
  @Override
  protected int numLeaves() {
      List<BEntry> ents = this.getAllValidEntries();
      int sum=0;
      for (BEntry ent : ents) {
          int pageNum=ent.getPageNum();
          BPlusNode node = BPlusNode.getBPlusNode(this.getTree(), pageNum);
          sum+=node.numLeaves();
      }
      int firstChild=this.getFirstChild();
      if (firstChild!=-1) {
          BPlusNode firstNode = BPlusNode.getBPlusNode(this.getTree(), firstChild);
          sum+=firstNode.numLeaves();
      }
      return sum;
  }

  /**
   * See BPlusNode#locateLeaf documentation.
   */
  @Override
  public LeafNode locateLeaf(DataType key, boolean findFirst) {
    //TODO: Implement Me!!
      
      List<BEntry> ents = this.getAllValidEntries();
      BEntry prev=ents.get(0);
      if (prev.getKey().compareTo(key)>0) {
          BPlusNode child = BPlusNode.getBPlusNode(this.getTree(), this.getFirstChild());
          return child.locateLeaf(key, findFirst);
      }
      for (int i=1;i<ents.size();i++) {
          BEntry ent=ents.get(i);
          DataType entryKey = ent.getKey();
          int cmp = key.compareTo(entryKey);
          if (cmp<0) {
              BPlusNode child = BPlusNode.getBPlusNode(this.getTree(), prev.getPageNum());
              return child.locateLeaf(key, findFirst);
          }
          prev=ent;
      } 
      return BPlusNode.getBPlusNode(this.getTree(), ents.get(ents.size()-1).getPageNum()).locateLeaf(key, findFirst);
  }

  /**
   * Splits this node and pushes up the middle key. Note that we split this node
   * immediately after it becomes full rather than when trying to insert an
   * entry into a full node. Thus a full inner node of 2d entries will be split
   * into a left node with d entries and a right node with d-1 entries, with the
   * middle key pushed up.
   */
  @Override
  public void splitNode() {
    //TODO: Implement me!!
      List<BEntry> left=new ArrayList<BEntry>();
      InnerNode right=new InnerNode(this.getTree());

      BEntry middle = null;
      List<BEntry> ents=this.getAllValidEntries();
      for (int i = 0;i<ents.size();i++) {
          if (i<numEntries/2) {
              left.add(this.getAllValidEntries().get(i));
              continue;
          }
          if (i>numEntries/2) {
              right.insertBEntry(ents.get(i));
              continue;
          }
          middle=this.getAllValidEntries().get(i);
      }
      
      //getting the parent
      InnerNode parentNode=null;
      if (!this.isRoot()) {
          parentNode=(InnerNode)BPlusNode.getBPlusNode(this.getTree(),this.getParent());
      } else {
          parentNode=new InnerNode(this.getTree());
          this.getTree().updateRoot(parentNode.getPageNum());
          parentNode.setFirstChild(this.getPageNum());
      }
      //setting parents
      this.setParent(parentNode.getPageNum());
      right.setParent(parentNode.getPageNum());
      
      //creating and inserting new entry to parent
      BEntry newEnt=new InnerEntry(middle.getKey(),right.getPageNum());
      //if the pushed up key becomes the smallest entry
        if (parentNode.getAllValidEntries().size()>0&&
                middle.getKey().compareTo(parentNode.getAllValidEntries().get(0).getKey())<0){
            newEnt=new InnerEntry(middle.getKey(),parentNode.getFirstChild());
            parentNode.setFirstChild(this.getPageNum());
        } 
        parentNode.insertBEntry(newEnt);
        
        
        //Finalizing changes in new split nodes
        right.setFirstChild(middle.getPageNum());
        this.overwriteBNodeEntries(left);
        

  }
}
