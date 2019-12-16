package com.arman.mapapp;

import com.google.android.gms.maps.model.LatLng;
import java.io.PrintStream;

public class BinaryTree {

    private TreeNode<LatLng> root;
    private int size;

    public BinaryTree() {
        root = null;
        size = 0;
    }

    public TreeNode<LatLng> getRoot() {
        return root;
    }

    public void toString(PrintStream p) {
        print(root,p,false);
    }

    public void toString(PrintStream p, boolean printKeys) {
        print(root,p,printKeys);
    }

    public void insert(LatLng key) throws DuplicateException {
        root = insert(root,key);
        size++;
    }

    public void delete(LatLng key) {
        root = delete(root, key);
        size--;
    }

    private void print(TreeNode<LatLng> n, PrintStream p, boolean printKeys) {
        if (printKeys) {
            if (n == null) return;

            print(n.getLeft(), p, printKeys);
            p.println(n.getKey());
            print(n.getRight(), p, printKeys);
        }
    }

    private TreeNode<LatLng> insert(TreeNode<LatLng> n, LatLng key) throws DuplicateException {
        if (n == null) {
            return new TreeNode<LatLng>(key,null,null);
        }

        if (n.getKey().equals(key)) {
            throw new DuplicateException();
        }

        if ((key.longitude+key.latitude) < (n.getKey().longitude+n.getKey().latitude)) {
            n.setLeft( insert(n.getLeft(), key) );
            return n;
        }
        else {
            n.setRight( insert(n.getRight(), key) );
            return n;
        }
    }

    private TreeNode<LatLng> delete(TreeNode<LatLng> n, LatLng key) {
        if (n == null) {
            return null;
        }

        //if true, this is the node to delete, handle children.
        if (key.equals(n.getKey())) {
            //if n doesn't have any children, return null
            if (n.getLeft() == null && n.getRight() == null) {
                return null;
            }

            //if n doesn't have any left children, but has right...
            if (n.getLeft() == null) {
                return n.getRight();
            }

            //if n doesn't have any right children, but has left...
            if (n.getRight() == null) {
                return n.getLeft();
            }

            //if n has both right and left children,
            //replace the key value of the node to delete with
            //an appropriate value to keep both trees intact.
            LatLng val = smallest(n.getRight());
            n.setKey(val);
            n.setRight( delete(n.getRight(), val));
            return n;
        }

        else if ((key.longitude+key.latitude) < (n.getKey().longitude+n.getKey().latitude)) {
            n.setLeft( delete(n.getLeft(), key));
            return n;
        }
        else {
            n.setRight( delete(n.getRight(), key));
            return n;
        }
    }

    static public LatLng smallest(TreeNode<LatLng> n) {
        if (n.getLeft() == null) {
            return n.getKey();
        }
        else {
            return smallest(n.getLeft());
        }
    }

    static public LatLng largest(TreeNode<LatLng> n) {
        if (n.getRight() == null) {
            return n.getKey();
        }
        else {
            return largest(n.getRight());
        }
    }

    public int size() {
        return size;
    }
}

class TreeNode<LatLng> {

    private LatLng key;
    private TreeNode<LatLng> left;
    private TreeNode<LatLng> right;

    public TreeNode(LatLng key, TreeNode<LatLng> left, TreeNode<LatLng> right) {
        this.key = key;
        this.left = left;
        this.right = right;
    }

    public LatLng getKey() { return this.key; }
    public TreeNode<LatLng> getLeft() { return this.left; }
    public TreeNode<LatLng> getRight() { return this.right; }

    public void setKey(LatLng nKey) { this.key = nKey; }
    public void setLeft(TreeNode<LatLng> nLeft) { this.left = nLeft; }
    public void setRight(TreeNode<LatLng> nRight) { this.right = nRight; }

}