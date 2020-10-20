import java.util.Arrays;

public class ArrayBag<T> implements BagInterface<T> {
    private T[] bag;
    private static final int DEFAULT_CAPACITY = 25;
    private int numberOfEntries;
    private boolean integrityOK = false;
    private static final int MAX_CAPACITY = 10000;

    public ArrayBag(){                               // Default constructor
        this(DEFAULT_CAPACITY);
    }//end constructor

    public ArrayBag(int capacity){                   // Constructor with integer input
        if(capacity <= MAX_CAPACITY){
            // memory allocation for array
            @SuppressWarnings("unchecked")               // Must suppress warnings because of type casting issue
            T[] tempBag = (T[]) new Object[capacity];
            // data field init
            bag = tempBag;
            numberOfEntries = 0;
            integrityOK = true;
        }
        else
            throw new IllegalStateException("Attempt to create a bag whose capacity exceeds allowed maximum.");
    }//end constructor

    /** Gets the current number of entries in this bag.
     * @return The integer number of entries currently in this bag. */
    @Override
    public int getCurrentSize() {                    // Returns the size of the bag
        return numberOfEntries;
    }// end getCurrentSize

    /** Sees whether this bag is empty.
     @return True if this bag is empty, or false if not. */
    @Override
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }// end isEmpty

    /** Adds a new entry to this bag.
     @param newEntry The object to be added as a new entry.
     @return True. */
    @Override
    public boolean add(T newEntry) {                 // Adds a newEntry into the bag
        checkIntegrity();
        boolean result = true;
        if(isFull()){
            doubleCapacity();
        }
        bag[numberOfEntries] = newEntry;
        numberOfEntries++;

        return result;
    } // end add

    @Override
    public T remove() {
        checkIntegrity();
        T result = removeEntry(numberOfEntries -1);
        return result;
    }// end remove

    @Override
    public boolean remove(T anEntry) {
        checkIntegrity();
        int index = getIndexOf(anEntry);
        T result = removeEntry(index);
        return anEntry.equals((result));
    }// end remove

    @Override
    public void clear() {
        while(!isEmpty()){
            remove();
        }
    }// end clear

    @Override
    public int getFrequencyOf(T anEntry) {
        checkIntegrity();
        int counter = 0;

        for(int index = 0; index < numberOfEntries; index++){
            if(anEntry.equals(bag[index])){
                counter++;
            }
        }
        return counter;
    }// end getFrequencyOf

    @Override
    public boolean contains(T anEntry) {
        checkIntegrity();
        return getIndexOf(anEntry) > -1;
    }// end contains

    @Override
    public BagInterface<T> union(BagInterface<T> aBag) {
        int bagNum = this.getCurrentSize() + aBag.getCurrentSize();
        BagInterface<T> bagUnion = new ArrayBag(bagNum);
        T[] tempBag = aBag.toArray();
        int j = 0;
        for(int i = 0; i < bagNum; i++){
            if(this.getCurrentSize() > i){
                bagUnion.add(this.bag[i]);   // Populates new bag with bag1 first
            }
            else{
                bagUnion.add(tempBag[j]);    // Then adds the contents of bag2 after that.
                j++;
            }
        }
        return bagUnion;                     // Returns unioned bag
    }

    @Override
    public BagInterface<T> intersection(BagInterface<T> aBag) {
        int bagNum;
        int start = 0;
        if(this.getCurrentSize() < aBag.getCurrentSize())    // Determining the smallest bag to initialize bagIntersect with its size
            bagNum = this.getCurrentSize();
        else
            bagNum = aBag.getCurrentSize();
        BagInterface<T> bagIntersect = new ArrayBag(bagNum);
        BagInterface<T> bagTemp1 = new ArrayBag(this.getCurrentSize());
        BagInterface<T> bagTemp2 = new ArrayBag(aBag.getCurrentSize());
        T[] tempBag1 = this.toArray();
        T[] tempBag2 = aBag.toArray();

        for(int i = 0; i < this.getCurrentSize(); i++){     // Populates the temp variable for bag1
            bagTemp1.add(tempBag1[i]);
        }
        for(int i = 0; i < aBag.getCurrentSize(); i++){     // Populates the temp variable for bag2
            bagTemp2.add(tempBag2[i]);
        }

        for(int i = start; i < bagTemp1.getCurrentSize(); i++) {
            for (int j = start; j < bagTemp2.getCurrentSize(); j++) {
                if (tempBag1[i] == tempBag2[j]) {
                    bagIntersect.add(tempBag1[i]);          // Add value to intersect bag.

                    bagTemp1.remove(tempBag1[i]);           // Remove temp value from bagTemp1 so it is not re-compared
                    tempBag1 = bagTemp1.toArray();          // Reset array variable to new bag1 array

                    bagTemp2.remove(tempBag2[j]);           // Remove temp value from bagTemp2 so it is not re-compared
                    tempBag2 = bagTemp2.toArray();          // Reset array variable to new bag2 array

                    i = start;                              // Reset to beginning of new array1
                    j = start;                              // Reset to beginning of new array2
                }
            }
        }
        return bagIntersect;
    }

    @Override
    public BagInterface<T> difference(BagInterface<T> aBag) {
        int bagNum = this.getCurrentSize();
        BagInterface<T> bagDiff = new ArrayBag(bagNum);
        BagInterface<T> bagTemp = new ArrayBag(aBag.getCurrentSize());

        T[] tempArray = aBag.toArray();
        for(int i = 0; i < aBag.getCurrentSize(); i++){   // Populates the temporary bag
            bagTemp.add(tempArray[i]);
        }
        for(int i = 0; i < this.getCurrentSize(); i++){
            if(!bagTemp.contains(this.bag[i])){
                bagDiff.add(this.bag[i]);                 // Adds element to bagDiff if contents at that index are not the same
            }
            else
                bagTemp.remove(this.bag[i]);              /* If contents are the same, don't add anything to bagDiff and delete reference from the temp bag so
                                                           * it is not re-compared*/
        }
        return bagDiff;
    }

    @Override
    public T[] toArray() {
        // the cast is safe because the new array contains null entries
        @SuppressWarnings("unchecked")
        T[] result = (T[]) new Object[numberOfEntries];
        for(int index = 0; index < numberOfEntries; index++){
            result[index] = bag[index];
        }
        return result;
    }// end toArray

    @Override
    public void displayBag(){
        System.out.print("{ ");
        for(int i = 0; i < this.getCurrentSize(); i++){
            System.out.print(this.bag[i]);
            if(i == this.getCurrentSize()-1)
                break;
            System.out.print(", ");
        }
        System.out.println(" }");
    }

    // Throws an exception if this oject is not initialized
    private void checkIntegrity(){
        if(!integrityOK)
            throw new SecurityException("ArrayBag object is corrupt.");
    }// end checkIntegrity

    private int getIndexOf(T anEntry){
        int where = -1;
        boolean found = false;
        int index = 0;

        while(!found && (index < numberOfEntries)){
            if(anEntry.equals(bag[index])){
                found = true;
                where = index;
            }
            index++;
        }
        return where;
    }// end getIndexOf

    private T removeEntry(int index){
        T result = null;

        if(!isEmpty() && (index >=0)){
            result = bag[index];
            bag[index] = bag[numberOfEntries -1];
            numberOfEntries--;
        }
        return result;
    }// end removeEntry

    private boolean isFull(){                       // Returns true or false if bag is full
        return numberOfEntries == bag.length;
    }// end isFull

    /**Doubles the size of the array bag.
     * Precondition: checkIntegrity has been called.
     */
    private void doubleCapacity(){
        int newLength = 2*bag.length;
        checkCapacity(newLength);
        bag = Arrays.copyOf(bag, newLength);
    }// end doubleCapacity

    /**Throws an exception if the client requests a capacit that is too large.
     * @param capacity the amount spaces in the array
     */
    private void checkCapacity(int capacity){
        if(capacity > MAX_CAPACITY){
            throw new IllegalStateException("Attempt to create a bag whose"+
                                            "capacity exceeds allowed "+
                                            "maximum of "+MAX_CAPACITY);
        }// end checkCapacity
    }
}
